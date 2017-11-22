package com.gionee.autotest.field.ui.outgoing;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.ui.outgoing.model.OutGoingModel;
import com.google.gson.Gson;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class OutGoingPresenter extends BasePresenter<OutGoingContract.View> implements OutGoingContract.Presenter {
    private Context       mContext;
    private OutGoingModel outGoingModel;

    OutGoingPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void initialize(Bundle extras) {
        getView().setParams(getLastParams());
    }

    public CallParam getLastParams() {
        String callParams = Preference.getString(mContext, "callParams");
        if (callParams == null || callParams.isEmpty()) {
            Log.i("gionee.os.autotest", "null");
            return new CallParam();
        } else {
            Log.i("gionee.os.autotest", "non null");
            return new Gson().fromJson(callParams, CallParam.class);
        }
    }

    @Override
    public void startCallTest() {
        CallParam p = getView().getUserParams();
        if ((p.call_time + p.gap_time) > (p.call_time_sum / p.numbers.length)) {
            String message = String.format(mContext.getString(R.string.TooShortTips, (p.call_time + p.gap_time) * p.numbers.length));
            getView().showDialog(message);
        } else {
            outGoingModel = new OutGoingModel(mContext);
            outGoingModel.start(p);
        }
    }

    @Override
    public void handleStartBtnClicked() {
        if (outGoingModel != null && outGoingModel.isTesting()) {
            outGoingModel.stop();
        } else if (outGoingModel == null || !outGoingModel.isTesting()) {
            startCallTest();
        }
        getView().updateViews(outGoingModel.isTesting());
    }

}
