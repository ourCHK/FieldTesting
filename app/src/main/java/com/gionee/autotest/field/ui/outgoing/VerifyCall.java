package com.gionee.autotest.field.ui.outgoing;


import android.content.Context;
import android.util.Log;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.call.CallLogUtil;
import com.gionee.autotest.field.util.call.CallResult;

import java.util.Timer;
import java.util.TimerTask;

public class VerifyCall implements CallLogUtil.CallLogListener {
    private Context context;
    private int callIndex = 0;
    private CallLogUtil callLogUtil;
    private String currentNumber;
    private CallParam params;
    private int cycleIndex;
    private CallBack callBack;
    private Timer timer;
    private boolean run = false;

    public VerifyCall(Context context, String number, CallParam params, int cycleIndex, CallBack callBack) {
        this.currentNumber = number;
        this.context = context;
        this.params = params;
        this.cycleIndex = cycleIndex;
        this.callBack = callBack;
        callLogUtil = new CallLogUtil();
        callLogUtil.setCallLogListener(context, this);
    }

    public void start() {
        run = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callLogUtil.start(context, currentNumber);
            }
        }, params.call_time_sum * 1000);
    }

    public void cancel() {
        run = false;
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onChanged(CallResult callbean) {
        OutGoingCallResult result = OutGoingCallResult.parse(callbean);
        result.setBatchId(params.id).setCycleIndex(cycleIndex);
        result.setVerify(true);
        if (!result.result) {
            result.setSimNetInfo(OutGoingUtil.getSimNetInfo(context));
        }
        Log.i(Constant.TAG, "写入第" + (callIndex + 1) + "次复测结果");
        OutGoingDBManager.writeData(result);
        if (callIndex < params.verifyCount - 1&&run) {
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
