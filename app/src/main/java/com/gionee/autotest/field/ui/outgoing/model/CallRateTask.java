package com.gionee.autotest.field.ui.outgoing.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.CallBack;

import java.util.ArrayList;

public class CallRateTask extends AsyncTask<Void, Void, Float> {
    @SuppressLint("StaticFieldLeak")
    private Context  context;
    private CallBack callBack;

    public CallRateTask(Context context, CallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    protected Float doInBackground(Void... voids) {
        int                 lastBatch    = OutGoingDBManager.getLastBatch();
        ArrayList<OutGoingCallResult> reportBean = OutGoingDBManager.getReportBean(lastBatch);
        Log.i("gionee.os.autotest",reportBean.size()+"");
        if (reportBean.size()==0){
            return 0f;
        }
        ArrayList<OutGoingCallResult>  callBeans    = new ArrayList<>();
        for (OutGoingCallResult bean : callBeans) {
                if (!bean.isVerify) {
                    callBeans.add(bean);
            }
        }
        if (callBeans.size()==0){
            return 0f;
        }
        int success = 0;
        for (OutGoingCallResult callBean : callBeans) {
            if (callBean.result) {
                success++;
            }
        }
        return (float) success / (float) callBeans.size();
    }

    @Override
    protected void onPostExecute(Float callRate) {
        super.onPostExecute(callRate);
        if (callBack != null) {
            callBack.call(callRate);
        }
    }
}
