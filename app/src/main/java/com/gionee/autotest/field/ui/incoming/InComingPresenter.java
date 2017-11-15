package com.gionee.autotest.field.ui.incoming;

import android.content.Context;

import com.gionee.autotest.common.call.CallMonitor;
import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.incoming.model.InComingCall;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class InComingPresenter extends BasePresenter<InComingContract.View> implements InComingContract.Presenter, CallMonitor.MonitorListener {
    private final InComingCall inComingCall;
    private       Context      mContext;
    private       long         batchId;
    private       CallMonitor  callMonitor;

    InComingPresenter(Context context) {
        mContext = context;
        inComingCall = new InComingCall();
    }

    @Override
    public void startMonitor(CallMonitorParam callMonitorParam) {
        batchId = InComingDBManager.addBatch(callMonitorParam);
        callMonitor = new CallMonitor(mContext, callMonitorParam);
        callMonitor.setMonitorListener(this);
        callMonitor.startMonitor();
    }

    @Override
    public void stopMonitor() {
        if (callMonitor!=null) {
            callMonitor.cancel();
        }
    }

    @Override
    public void onInComing(CallMonitorResult callMonitorResult) {
        callMonitorResult.batchId = batchId;
        inComingCall.writeData(callMonitorResult);
    }
}
