package com.gionee.autotest.field.ui.outgoing;


import android.content.Context;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.CallLogUtil;

import java.util.Timer;
import java.util.TimerTask;

public class VerifyCall implements CallLogUtil.CallLogListener {
    private Context context;
    private int callIndex = 0;
    private CallLogUtil callLogUtil;
    private String currentNumber;
    private CallParam params;
    private CallBack callBack;
    private Timer timer;

    public VerifyCall(Context context, String number, CallParam params, CallBack callBack) {
        this.currentNumber = number;
        this.context = context;
        this.params = params;
        this.callBack = callBack;
        callLogUtil = new CallLogUtil();
        callLogUtil.setCallLogListener(context, this);
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callLogUtil.start(context, currentNumber);
            }
        }, params.call_time_sum * 1000);
    }

    @Override
    public void onChanged(OutGoingCallResult callbean) {
        callbean.setBatchId(params.id);
        callbean.setVerify(true);
        OutGoingDBManager.writeData(callbean);
        if (callIndex < params.verifyCount - 1) {
            callIndex++;
            start();
        } else {
            callIndex = 0;
            callLogUtil.cancelListen(context);
            if (callBack != null) {
                callBack.call(null);
            }
        }
    }

}
