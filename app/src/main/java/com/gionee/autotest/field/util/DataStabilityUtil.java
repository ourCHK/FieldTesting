package com.gionee.autotest.field.util;


import android.content.Context;
import android.util.Log;

import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.data_stability.CallBack;

public class DataStabilityUtil {
    public static boolean isTest = false;

    public static void i(String text) {
        Log.i("gionee.os.autotest", text);

    }

    public static void callAfterTest(final Context context, final CallBack c) {
        new CallUtil(context).call("10086");
        final CallLogUtil callLogUtil = new CallLogUtil();
        callLogUtil.setCallLogListener(context, new CallLogUtil.CallLogListener() {
            @Override
            public void onChanged(OutGoingCallResult lastCallLog) {
                callLogUtil.cancelListen(context);
                if (c != null) {
                    c.todo(lastCallLog);
                }
            }
        });
    }

}
