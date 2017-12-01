package com.gionee.autotest.field.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.gionee.autotest.field.ui.network_switch.model.SwitchTestTask;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.NotificationHelper;
import com.gionee.autotest.field.util.Preference;
import com.gionee.autotest.field.util.SignalHelper;
import com.google.gson.Gson;

public class NetworkSwitchService extends Service implements INetworkSwitchService {
    private MyReceiver stopReceiver;
    private static final int NOTIFICATION_ID = 1101;
    private NotificationHelper notificationHelper;
    private SwitchTestTask switchTestTask;
    private NetworkSwitchParam networkSwitchParam;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(Constant.TAG, "TestService:onCreate");
        notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.set(NOTIFICATION_ID);
        registerReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Constant.TAG, "TestService:onStartCommand");
        String params = intent.getStringExtra("params");
        if (!TextUtils.isEmpty(params)) {
            networkSwitchParam = new Gson().fromJson(params, NetworkSwitchParam.class);
            switchTestTask = new SwitchTestTask(this);
            startSignalService();
            switchTestTask.execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constant.TAG, "TestService:onDestroy");
        if (stopReceiver != null) {
            unregisterReceiver(stopReceiver);
            stopReceiver = null;
        }
        notificationHelper.cancelAll();
        destroySignalService();
    }

    private void registerReceiver() {
        stopReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_SWITCH_SIM_FINISH);
        filter.addAction(Constant.ACTION_STOP_TEST);
        registerReceiver(stopReceiver, filter);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.i(Constant.TAG, "接收到广播为：" + action);
                if (action != null) {
                    switch (action) {
                        case Constant.ACTION_STOP_TEST:
                            Log.i(Constant.TAG, "接收到停止测试广播");
                            if (switchTestTask != null) {
                                switchTestTask.stopTestThread();
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.i(Constant.TAG, e.getMessage());
            }
        }
    }

    @Override
    public void notifyRefreshView() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("updateView_autoSwitchSimCard"));
    }

    @Override
    public NetworkSwitchParam getParams() {
        return networkSwitchParam;
    }

    public void destroySignalService() {
        SignalHelper.getInstance(this).destroy();
    }

    private void startSignalService() {
        Intent signalService = new Intent(this, SignalMonitorService.class);
        startService(signalService);
    }

}
