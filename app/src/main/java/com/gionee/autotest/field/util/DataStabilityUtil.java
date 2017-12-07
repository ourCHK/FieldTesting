package com.gionee.autotest.field.util;


import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.data_stability.CallBack;

import java.util.Timer;
import java.util.TimerTask;

public class DataStabilityUtil {
    public static boolean isTest = false;

    public static void i(String text) {
        Log.i("gionee.os.autotest", text);

    }

    public static void callAfterTest(final Context context, final CallBack c) {
        final CallUtil callUtil = new CallUtil(context);
        callUtil.call("10086");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    callUtil.getITelephony().endCall();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 15 * 1000);
        final CallLogUtil callLogUtil = new CallLogUtil();
        callLogUtil.setCallLogListener(context, new CallLogUtil.CallLogListener() {
            @Override
            public void onChanged(OutGoingCallResult lastCallLog) {
                try {
                    callLogUtil.cancelListen(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (c != null) {
                    c.todo(lastCallLog);
                }
            }
        });
    }

}
