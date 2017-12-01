package com.gionee.autotest.field.ui.outgoing.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.OutGoingService;
import com.gionee.autotest.field.ui.outgoing.OutGoingUtil;
import com.google.gson.Gson;

import io.reactivex.functions.Consumer;

public class OutGoingModel {


    private Context mContext;

    private boolean isTesting = false;

    public OutGoingModel(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("StaticFieldLeak")
    private void addBatch(CallParam p, final Consumer<CallParam> c) {
        new AsyncTask<CallParam, Void, CallParam>() {
            @Override
            protected CallParam doInBackground(CallParam... params) {
                CallParam param = params[0];
                param.setId(OutGoingDBManager.addBatch(param));
                return param;
            }

            @Override
            protected void onPostExecute(CallParam p) {
                super.onPostExecute(p);
                if (c != null) {
                    try {
                        c.accept(p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(p);
    }

    public void start(final CallParam p) {
        addBatch(p, new Consumer<CallParam>() {
            @Override
            public void accept(CallParam p) throws Exception {
                startTest(p);
            }
        });
    }

    public void stop() {
        OutGoingUtil.isTest=false;
        mContext.stopService(new Intent(mContext, OutGoingService.class));
    }

    public boolean isTesting() {
        return isTesting;
    }

    private void startTest(CallParam p) {
        isTesting = true;
        mContext.startService(new Intent(mContext, OutGoingService.class).putExtra("params", new Gson().toJson(p)));
//        new CallTask(mContext, p, new CallTask.Callback() {
//            @Override
//            public void call(CallParam param, OutGoingCallResult value) {
//                OutGoingDBManager.writeData(value);
//            }
//
//            @Override
//            public void finish(CallParam param) {
//                Toast.makeText(mContext, "测试完成", Toast.LENGTH_SHORT).show();
//                isTesting = false;
//            }
//
//            @Override
//            public boolean cancel() {
//                return cancel;
//            }
//        }).execute();
    }
}
