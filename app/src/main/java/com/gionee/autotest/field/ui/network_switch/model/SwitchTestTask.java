package com.gionee.autotest.field.ui.network_switch.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.field.services.INetworkSwitchService;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.WakeHelper;

public class SwitchTestTask extends AsyncTask<Void, String, Void> implements IToast {
    @SuppressLint("StaticFieldLeak")
    private Context               context;
    private WakeHelper            mWake;
    private INetworkSwitchService iTestService;
    private SwitchTest            switchTest;

    public SwitchTestTask(INetworkSwitchService iTestService) {
        this.iTestService = iTestService;
        this.context = iTestService.getContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mWake = new WakeHelper(context);
        mWake.getWakeLock().setReferenceCounted(false);
        mWake.getKeyguardLock().disableKeyguard();
        mWake.getWakeLock().acquire(60 * 60 * 1000L /*60 minutes*/);
        switchTest = new SwitchTest(iTestService, this, iTestService.getParams());
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            switchTest.start();
        } catch (InterruptException e) {
            Log.i(Constant.TAG,"打断异常出现!!!!!!!,停止测试");
            switchTest.stopTestThread();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mWake.getKeyguardLock().reenableKeyguard();
        mWake.getWakeLock().release();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values.length == 0) {
            return;
        }
        switch (values[0].isEmpty() ? "-1" : values[0]) {
            case "0":
                if (values.length > 0 && !values[1].isEmpty()) {
                    Toast.makeText(context, values[1] == null ? "" : values[1], Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void toast(String s) {
        publishProgress("0", s);
    }

    public void stopTestThread() {
        switchTest.stopTestThread();
    }
}
