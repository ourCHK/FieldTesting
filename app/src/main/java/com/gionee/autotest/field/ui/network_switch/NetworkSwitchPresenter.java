package com.gionee.autotest.field.ui.network_switch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.services.NetworkSwitchService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.incoming.InComingReportActivity;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.NetworkSwitchUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
        Intent intent = new Intent(mContext, InComingReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void clearAllReport() {
    }

    @Override
    public void exportExcelFile() {

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
        Gson               gson       = new Gson();
        NetworkSwitchParam param      = new NetworkSwitchParam();
        String             lastParams = Preference.getString(mContext, "lastParams", "");
        if (null == lastParams || lastParams.equals("")) {
            try {
                param = gson.fromJson(lastParams, NetworkSwitchParam.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                param      = new NetworkSwitchParam();
            }
        }
        return param;
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
                startTest(inputParam);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                getView().toast(e.getMessage());
            }
        } else {
            Preference.putBoolean(mContext, "isTest", false);
            NetworkSwitchUtil.isTest = false;
            mContext.sendBroadcast(new Intent(Constant.ACTION_STOP_TEST));
        }
    }
}
