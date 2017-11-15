package com.gionee.autotest.field.ui.incoming.model;


import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.data.db.InComingDBManager;

public class InComingCall {
    @SuppressLint("StaticFieldLeak")
    public void writeData(CallMonitorResult callMonitorResult) {
        new AsyncTask<CallMonitorResult, Void, Void>() {
            @Override
            protected Void doInBackground(CallMonitorResult... data) {
                InComingDBManager.writeData(data[0]);
                return null;
            }
        }.execute(callMonitorResult);
    }
}
