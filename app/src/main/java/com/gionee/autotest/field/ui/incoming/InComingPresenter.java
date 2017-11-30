package com.gionee.autotest.field.ui.incoming;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gionee.autotest.common.call.CallMonitor;
import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.incoming.model.InComingCall;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.Preference;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class InComingPresenter extends BasePresenter<BaseView> implements InComingContract.Presenter {
    private Context mContext;

    InComingPresenter(Context context) {
        mContext = context;
    }

    private InComingContract.ReportView getReportView() {
        return (InComingContract.ReportView) getView();
    }

    private InComingContract.View getMainView() {
        return (InComingContract.View) getView();
    }

    @Override
    public void startMonitor(CallMonitorParam callMonitorParam) {
        long batchId = InComingDBManager.addBatch(callMonitorParam);
        String paramJson = new Gson().toJson(callMonitorParam);
        Preference.putString(mContext, "inComingParams", paramJson);
        Intent intent = new Intent(mContext, InComingService.class);
        intent.putExtra("batchID", batchId);
        intent.putExtra("params", paramJson);
        mContext.startService(intent);
    }

    @Override
    public void stopMonitor() {
        mContext.stopService(new Intent(mContext, InComingService.class));
    }

    @Override
    public void showReport() {
        Intent intent = new Intent(mContext, InComingReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void clearAllReport() {
        InComingCall.clearAllReport();
    }

    @Override
    public void exportExcelFile() {
        InComingCall.exportExcelFile(new Consumer<Integer>() {
            @Override
            public void accept(Integer aVoid) throws Exception {
                if (aVoid == 0) {
                    Toast.makeText(mContext, "无报告记录", Toast.LENGTH_LONG).show();
                } else {
                    DialogHelper.create(mContext, "报告导出成功", "导出到:" + Constant.INCOMING_EXCEL_PATH + ",立即打开?", new DialogHelper.OnBeforeCreate() {
                        @Override
                        public void setOther(AlertDialog.Builder builder) {
                            builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Util.openExcelByIntent(mContext, Constant.INCOMING_EXCEL_PATH);
                                }
                            });
                            builder.setNegativeButton("取消", null);
                        }
                    }).show();
                }
            }
        });
    }

    @Override
    public void updateBatchList() {
        InComingCall.getBatchList(new Consumer<ArrayList<String>>() {
            @Override
            public void accept(ArrayList<String> strings) throws Exception {
                getReportView().updateBatch(strings);
            }
        });
    }

    @Override
    public void insertListData(int i) {
        InComingCall.insertListData(i, new Consumer<InComingReportBean>() {
            @Override
            public void accept(InComingReportBean inComingReportBean) throws Exception {
                getReportView().updateListData(inComingReportBean);
            }
        });
    }


    @Override
    public void initialize(Bundle extras) {
        if (getView() instanceof InComingContract.View) {
            getMainView().setParams(getLastParams());
        } else {

        }
    }

    private CallMonitorParam getLastParams() {
        String inComingParams = Preference.getString(mContext, "inComingParams", "");
        if (!"".equals(inComingParams)) {
            try {
                return new Gson().fromJson(inComingParams, CallMonitorParam.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return new CallMonitorParam().setAutoAnswer(true).setAnswerHangup(true).setAnswerHangUptime(5);
    }
}
