package com.gionee.autotest.field.ui.outgoing.model;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;

import io.reactivex.functions.Consumer;

public class OutGoingModel {


    private Context mContext;

    private boolean cancel    = false;
    private boolean isTesting = false;

    public OutGoingModel(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("StaticFieldLeak")
    private void addBatch(CallParam p, final Consumer<Long> c) {
        new AsyncTask<CallParam, Void, Long>() {
            @Override
            protected Long doInBackground(CallParam... params) {
                return OutGoingDBManager.addBatch(params[0]);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                if (c != null) {
                    try {
                        c.accept(aLong);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute(p);
    }

    public void start(final CallParam p) {
        addBatch(p, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                startTest(p);
            }
        });
    }

    public void stop() {
        cancel = true;
    }

    public boolean isTesting() {
        return isTesting;
    }

    private void startTest(CallParam p) {
        isTesting = true;
        new CallTask(mContext, p, new CallTask.Callback() {
            @Override
            public void call(CallParam param, OutGoingCallResult value) {
                OutGoingDBManager.writeData(value);
            }

            @Override
            public void finish(CallParam param) {
                Toast.makeText(mContext, "测试完成", Toast.LENGTH_SHORT).show();
                isTesting = false;
            }

            @Override
            public boolean cancel() {
                return cancel;
            }
        }).execute();
    }
}
