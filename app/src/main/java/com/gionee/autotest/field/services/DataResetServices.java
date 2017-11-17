package com.gionee.autotest.field.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.gionee.autotest.common.FLog;
import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.util.Constant;

/**
 * Created by xhk on 2017/11/17.
 */

public class DataResetServices extends Service {




    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false)){
                    long data_reset_interval = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_INTERVAL, 1);
                    long data_reset_current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1);
                    if (data_reset_interval == data_reset_current_cycle){

                        FLog.i("data_reset_interval="+data_reset_interval+",data_reset_current_cycle="+data_reset_current_cycle);
                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false);

                    }else{
                        Preference.putLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle+1);
                        FLog.i("data_reset_current_cycle="+Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                        SystemClock.sleep(1000);
                    }

                }
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
