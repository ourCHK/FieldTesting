package com.gionee.autotest.field.ui.outgoing.model;


import android.os.AsyncTask;
import android.util.Log;

import com.gionee.autotest.field.data.db.CallLossRatioDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.call_loss_ratio.CallLossRatioUtil;
import com.gionee.autotest.field.ui.outgoing.CallBack;
import com.gionee.autotest.field.util.Constant;

import java.util.ArrayList;

public class CallRateTask extends AsyncTask<Integer, Void, String> {
    private CallBack callBack;
    public static final int CALL_LOSS_RATIO = 1;
    public static final int OUT_GOING = 0;

    public CallRateTask(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected String doInBackground(Integer... model) {
        if (model[0] == OUT_GOING) {
            int lastBatch = CallLossRatioDBManager.getLastBatch();
            Log.i(Constant.TAG, "lastBatch=" + lastBatch);
            ArrayList<OutGoingCallResult> allCalls = CallLossRatioDBManager.getReportBean(lastBatch);
            Log.i(Constant.TAG, "CallRate reportBeanSize=" + allCalls.size());
            return CallLossRatioUtil.getSumString(allCalls);
        } else if(model[0]==CALL_LOSS_RATIO){
            int lastBatch = CallLossRatioDBManager.getLastBatch();
            Log.i(Constant.TAG, "lastBatch=" + lastBatch);
            ArrayList<OutGoingCallResult> allCalls = CallLossRatioDBManager.getReportBean(lastBatch);
            Log.i(Constant.TAG, "CallRate reportBeanSize=" + allCalls.size());
            return CallLossRatioUtil.getSumString(allCalls);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String callRate) {
        super.onPostExecute(callRate);
        if (callBack != null) {
            callBack.call(callRate);
        }
    }
}
