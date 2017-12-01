package com.gionee.autotest.field.ui.network_switch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.NetworkSwitchDBManager;
import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.services.NetworkSwitchService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.incoming.InComingReportActivity;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.gionee.autotest.field.ui.network_switch.util.ExcelUtil;
import com.gionee.autotest.field.ui.outgoing.OutGoingUtil;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.NetworkSwitchUtil;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.functions.Consumer;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class NetworkSwitchPresenter extends BasePresenter<NetworkSwitchActivity> implements NetworkSwitchContract.Presenter {
    private Context mContext;

    NetworkSwitchPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void showReport() {
        Intent intent = new Intent(mContext, NetworkSwitchReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void clearAllReport() {
        DialogHelper.create(mContext, "提示", "确定清除？", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkSwitchDBManager.deleteTable();
                        File report = new File(Constant.NETWORK_SWITCH_EXCEL_PATH);
                        File failedReport = new File(Constant.NETWORK_SWITCH_FAILED_EXCEL_PATH);
                        if (report.exists()) {
                            report.delete();
                        }
                        if (failedReport.exists()) {
                            failedReport.delete();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
            }
        }).show();

    }

    @Override
    public void exportExcelFile() {
        ExcelUtil.exportReport(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                DialogHelper.create(mContext, "导出成功", "导出成功:" + Constant.NETWORK_SWITCH_EXCEL_PATH + ",立即打开?", new DialogHelper.OnBeforeCreate() {
                    @Override
                    public void setOther(AlertDialog.Builder builder) {
                        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Util.openExcelByIntent(mContext, Constant.NETWORK_SWITCH_EXCEL_PATH);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                    }
                }).show();
            }
        });
    }

    @Override
    public void showFailedDetails() {
        File file=new File(Constant.NETWORK_SWITCH_FAILED_EXCEL_PATH);
        if(file.exists()){
            Util.openExcelByIntent(mContext, Constant.NETWORK_SWITCH_FAILED_EXCEL_PATH);
        }else{
            Toast.makeText(mContext, "无失败记录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        NetworkSwitchParam lastParams = getLastParams();
        getView().updateParams(lastParams);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getView().updateViews();
            }
        }, new IntentFilter("updateView_autoSwitchSimCard"));
    }

    @Override
    public NetworkSwitchParam getLastParams() {
        Gson gson = new Gson();
        NetworkSwitchParam param = new NetworkSwitchParam();
        String lastParams = Preference.getString(mContext, "lastParams", "");
        if (null == lastParams || lastParams.equals("")) {
            return param;
        }
        try {
            return gson.fromJson(lastParams, NetworkSwitchParam.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return param;
        }
    }

    @Override
    public void startTest(NetworkSwitchParam inputParam) {
        Intent intent = new Intent(mContext, NetworkSwitchService.class);
        intent.putExtra("params", new Gson().toJson(inputParam));
        mContext.startService(intent);
    }

    @Override
    public void handleClicked() {
        if (!Preference.getBoolean(mContext, "isTest", false)) {
            Preference.putBoolean(mContext, "isTest", true);
            NetworkSwitchUtil.isTest = true;
            try {
                NetworkSwitchParam inputParam = getView().getInputParam();
                getView().updateViews();
                saveParams();
                startTest(inputParam);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                getView().toast(e.getMessage());
            }
        } else {
            Preference.putBoolean(mContext, "isTest", false);
            Preference.putString(mContext, "ns_processText", "");
            NetworkSwitchUtil.isTest = false;
            getView().updateViews();
            mContext.sendBroadcast(new Intent(Constant.ACTION_STOP_TEST));
        }
    }

    //<weisc_add>
    private void saveParams() {
        Preference.putLong(mContext, "currentTestRound", 0L);
        Preference.putString(mContext, "resultFileName", NetworkSwitchUtil.getTimeForFilename());
        Preference.putInt(mContext, "sim1State", NetworkSwitchUtil.getSim1State());
        Preference.putInt(mContext, "sim2State", NetworkSwitchUtil.getSim1State());
    }

}
