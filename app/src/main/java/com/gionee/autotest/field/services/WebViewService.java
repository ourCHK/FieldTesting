package com.gionee.autotest.field.services;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.gionee.autotest.field.ui.data_stability.CallBack;
import com.gionee.autotest.field.ui.data_stability.WebViewActivity;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.DataStabilityUtil;

import java.util.ArrayList;

public class WebViewService extends Service {

    private MyReceiver myReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Configurator.getInstance().setTestIndex(0);
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter("testFinish"));
    }

    private void goTestPage() {
        DataStabilityUtil.i("waitTime=" + Configurator.getInstance().param.waitTime + " batchId=" + Configurator.getInstance().batchId + " testIndex=" + Configurator.getInstance().testIndex);
        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTest();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTest() {
        DataStabilityUtil.i("开始测试");
        getNewBatchId(new CallBack() {
            @Override
            public void todo(Object o) {
                int batchId = (int) o;
                Configurator.getInstance().setBatchId(batchId);
                goTestPage();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void getNewBatchId(final CallBack callBack) {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                DatabaseUtil      db  = new DatabaseUtil(getApplicationContext());
                ArrayList<String> ids = db.getIds();
                db.close();
                if (ids.size() == 0) {
                    return 0;
                }
                String lastBatchStr = ids.get(ids.size() - 1);
                int    lastBatch    = 0;
                try {
                    lastBatch = Integer.parseInt(lastBatchStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return lastBatch;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (callBack != null) {
                    callBack.todo(integer + 1);
                }
            }
        }.execute();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Configurator.getInstance().setTestIndex(Configurator.getInstance().testIndex + 1);
            if (Configurator.getInstance().testIndex < Configurator.getInstance().param.testTimes) {
                goTestPage();
            } else {
                Configurator.getInstance().setTestIndex(0);
                DataStabilityUtil.isTest = false;
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("updateViews"));
                stopSelf();
            }
        }
    }

}
