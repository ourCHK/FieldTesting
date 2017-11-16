package com.gionee.autotest.field.ui.incoming;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gionee.autotest.common.call.CallMonitor;
import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.incoming.model.InComingCall;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class InComingPresenter extends BasePresenter<BaseView> implements InComingContract.Presenter, CallMonitor.MonitorListener {
    private final InComingCall inComingCall;
    private       Context      mContext;
    private       long         batchId;
    private       CallMonitor  callMonitor;

    InComingPresenter(Context context) {
        mContext = context;
        inComingCall = new InComingCall();
    }

    private InComingContract.ReportView getReportView() {
        return (InComingContract.ReportView) getView();
    }

    private InComingContract.View getMainView() {
        return (InComingContract.View) getView();
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
        if (callMonitor != null) {
            callMonitor.cancel();
        }
    }

    @Override
    public void showReport() {
        Intent intent = new Intent(mContext, InComingReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void clearAllReport() {
        inComingCall.clearAllReport();
    }

    @Override
    public void exportExcelFile() {

    }

    @Override
    public void updateBatchList() {
        inComingCall.getBatchList(new Consumer<ArrayList<String>>() {
            @Override
            public void accept(ArrayList<String> strings) throws Exception {
                getReportView().updateBatchList(strings);
            }
        });
    }

    @Override
    public void insertListData(int i) {
        inComingCall.insertListData(i, new Consumer<InComingReportBean>() {
            @Override
            public void accept(InComingReportBean inComingReportBean) throws Exception {
                getReportView().insertListData(inComingReportBean);
            }
        });
    }

    @Override
    public void onInComing(CallMonitorResult callMonitorResult) {
        callMonitorResult.batchId = batchId;
        inComingCall.writeData(callMonitorResult);
    }

    @Override
    public void initialize(Bundle extras) {
    }
}
