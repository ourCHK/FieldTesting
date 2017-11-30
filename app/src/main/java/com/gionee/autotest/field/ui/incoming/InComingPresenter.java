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
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class InComingPresenter extends BasePresenter<BaseView> implements InComingContract.Presenter{
    private       Context      mContext;

    InComingPresenter(Context context) {
        mContext = context;
    }

    private InComingContract.ReportView getReportView() {
        return (InComingContract.ReportView) getView();
    }

    private InComingContract.View getMainView() {
        return (InComingContract.View) getView();
    }

    @Override
    public void startMonitor(CallMonitorParam callMonitorParam) {
        long batchId = InComingDBManager.addBatch(callMonitorParam);
        Intent intent = new Intent(mContext, InComingService.class);
        intent.putExtra("batchID",batchId);
        intent.putExtra("params",new Gson().toJson(callMonitorParam));
        mContext.startService(intent);
    }

    @Override
    public void stopMonitor() {
        mContext.stopService(new Intent(mContext,InComingService.class));
    }

    @Override
    public void showReport() {
        Intent intent = new Intent(mContext, InComingReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void clearAllReport() {
        InComingCall.clearAllReport();
    }

    @Override
    public void exportExcelFile() {

    }

    @Override
    public void updateBatchList() {
        InComingCall.getBatchList(new Consumer<ArrayList<String>>() {
            @Override
            public void accept(ArrayList<String> strings) throws Exception {
                getReportView().updateBatch(strings);
            }
        });
    }

    @Override
    public void insertListData(int i) {
        InComingCall.insertListData(i, new Consumer<InComingReportBean>() {
            @Override
            public void accept(InComingReportBean inComingReportBean) throws Exception {
                getReportView().updateListData(inComingReportBean);
            }
        });
    }


    @Override
    public void initialize(Bundle extras) {
    }

}
