package com.gionee.autotest.field.ui.outgoing.model;


import android.content.Context;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;

public class OutGoingModel {


    private Context mContext;

    public OutGoingModel(Context mContext) {
        this.mContext = mContext;
    }

    public void start(CallParam p) {
        new CallTask(mContext, p, new CallTask.Callback() {
            @Override
            public void call(CallParam param, OutGoingCallResult value) {

            }

            @Override
            public void finish(CallParam param) {

            }

            @Override
            public boolean cancel() {
                return false;
            }
        }).execute();
    }
}
