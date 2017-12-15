package com.gionee.autotest.field.ui.incoming;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.incoming.model.InComingCall;
import com.gionee.autotest.field.ui.outgoing.CallBack;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.Preference;
import com.gionee.autotest.field.util.Util;
import com.gionee.autotest.field.util.call.CallMonitorParam;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

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
        InComingCall.isTest = true;
        long batchId = InComingDBManager.addBatch(callMonitorParam);
        String paramJson = new Gson().toJson(callMonitorParam);
        Preference.putString(mContext, "inComingParams", paramJson);
        Log.i(Constant.TAG, "batch=" + batchId + " params=" + callMonitorParam.toString());
        Intent intent = new Intent(mContext, InComingService.class);
        intent.putExtra("batchId", batchId);
        intent.putExtra("params", callMonitorParam);
        mContext.startService(intent);
        Log.i(Constant.TAG, "开始监听服务");
    }

    @Override
    public void stopMonitor() {
        InComingCall.isTest = false;
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
        DialogHelper.create(mContext, "警告", "确定要清除全部报告数据?", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InComingCall.clearAllReport(new Consumer<Void>() {
                            @Override
                            public void accept(Void aVoid) throws Exception {
                                Toast.makeText(mContext, "清除成功", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", null);
            }
        }).show();
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
    public void openExcelFile() {
        InComingCall.exportExcelFile(new Consumer<Integer>() {
            @Override
            public void accept(Integer aVoid) throws Exception {
                if (aVoid == 0) {
                    Toast.makeText(mContext, "无报告记录", Toast.LENGTH_LONG).show();
                } else {
                    Util.openExcelByIntent(mContext, Constant.INCOMING_EXCEL_PATH);
                }
            }
        });
    }

    @Override
    public void updateSumContent() {
        InComingCall.getBatchReportSum(new CallBack() {
            @Override
            public void call(Object o) {
                String sum = (String) o;
                getMainView().setSumContent(sum);
            }
        });
    }

    @Override
    public void showExitWarningDialog() {
        DialogHelper.create(mContext, "警告", "将退出到首页并停止测试", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopMonitor();
                        getMainView().updateViews();
                        getMainView().doFinish();
                    }
                }).setNegativeButton("取消", null);
            }
        }).show();
    }


    @Override
    public void initialize(Bundle extras) {
        if (getView() instanceof InComingContract.View) {
            getMainView().setParams(getLastParams());
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
