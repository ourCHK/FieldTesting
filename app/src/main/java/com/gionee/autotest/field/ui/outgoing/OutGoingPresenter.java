package com.gionee.autotest.field.ui.outgoing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.OutGoingDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.ui.outgoing.model.CallRateTask;
import com.gionee.autotest.field.ui.outgoing.model.OutGoingModel;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by viking on 11/13/17.
 * <p>
 * Presenter for signal
 */

class OutGoingPresenter extends BasePresenter<OutGoingContract.View> implements OutGoingContract.Presenter {
    private Context mContext;
    private OutGoingModel outGoingModel;
    private MyReceiver myReceiver;

    OutGoingPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void initialize(Bundle extras) {
        super.initialize(extras);
        getView().setParams(getLastParams());
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver, new IntentFilter("AutoCallUpdateViews"));
        outGoingModel = new OutGoingModel(mContext);
        obtainCallRate();
    }

    public void obtainCallRate() {
        new CallRateTask(mContext, new CallBack() {
            @Override
            public void call(Object o) {
                Float f = (float) o;
                getView().updateCallRate("接通率:" + f * 100 + "%");
            }
        }).execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CallParam getLastParams() {
        String callParams = Preference.getString(mContext, "outGoingParams", "");
        if (callParams == null || "".equals(callParams)) {
            Log.i("gionee.os.autotest", "null");
            return new CallParam();
        } else {
            Log.i("gionee.os.autotest", "non null");
            return new Gson().fromJson(callParams, CallParam.class);
        }
    }

    @Override
    public void startCallTest() {
        try {
            OutGoingUtil.isTest = true;
            CallParam p = getView().getUserParams();
            Preference.putString(mContext, "outGoingParams", new Gson().toJson(p));
            outGoingModel.start(p);
            mContext.startService(new Intent(mContext, SignalMonitorService.class));
        } catch (Exception e) {
            outGoingModel.stop();
        }
    }

    @Override
    public void handleStartBtnClicked() {
        if (OutGoingUtil.isTest) {
            outGoingModel.stop();
        } else {
            startCallTest();
        }
        getView().updateViews();
    }

    @SuppressLint("StaticFieldLeak")
    public void exportExcelFile() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ArrayList<ArrayList<OutGoingCallResult>> results = new ArrayList<>();
                ArrayList<String> allBatch = OutGoingDBManager.getAllBatch();
                for (String batch : allBatch) {
                    ArrayList<OutGoingCallResult> reportBean = OutGoingDBManager.getReportBean(Integer.parseInt(batch));
                    results.add(reportBean);
                }
                OutGoingUtil.writeBook(Constant.OUT_GOING_EXCEL_PATH, results);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                DialogHelper.create(mContext, "导出成功", "导出成功:" + Constant.OUT_GOING_EXCEL_PATH + ",立即打开?", new DialogHelper.OnBeforeCreate() {
                    @Override
                    public void setOther(AlertDialog.Builder builder) {
                        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Util.openExcelByIntent(mContext, Constant.OUT_GOING_EXCEL_PATH);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                    }
                }).show();
            }

        }.execute();
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getView().updateViews();
        }
    }

}
