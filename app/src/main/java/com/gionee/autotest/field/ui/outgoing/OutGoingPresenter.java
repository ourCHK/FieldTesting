package com.gionee.autotest.field.ui.outgoing;

import android.content.Context;
import android.os.Bundle;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.google.gson.Gson;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class OutGoingPresenter extends BasePresenter<OutGoingContract.View> implements OutGoingContract.Presenter{
    private       Context      mContext;

    OutGoingPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void initialize(Bundle extras) {
        getView().setParams(getLastParams());
    }

    public CallParam getLastParams() {
        String callParams = Preference.getString(mContext, "callParams");
        if (callParams==null||callParams.isEmpty()){
            return new CallParam();
        }else{
            return new Gson().fromJson(callParams, CallParam.class);
        }
    }
}
