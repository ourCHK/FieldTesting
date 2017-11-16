package com.gionee.autotest.field.ui.incoming.model;


import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.data.db.InComingDBManager;
import com.gionee.autotest.field.data.db.model.InComingReportBean;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class InComingCall {
    @SuppressLint("StaticFieldLeak")
    public void writeData(CallMonitorResult callMonitorResult) {
        new AsyncTask<CallMonitorResult, Void, Void>() {
            @Override
            protected Void doInBackground(CallMonitorResult... data) {
                InComingDBManager.writeData(data[0]);
                return null;
            }
        }.execute(callMonitorResult);
    }

    @SuppressLint("StaticFieldLeak")
    public void clearAllReport() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                InComingDBManager.delete();
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getBatchList(final Consumer<ArrayList<String>> c) {
        new AsyncTask<Void, Void, ArrayList<String>>() {

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                ArrayList<String> allBatch = InComingDBManager.getAllBatch();
                return allBatch;
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                try {
                    c.accept(strings);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertListData(final int batchId, final Consumer<InComingReportBean> c) {
        new AsyncTask<Integer, Void, InComingReportBean>() {

            @Override
            protected InComingReportBean doInBackground(Integer... voids) {
                return InComingDBManager.getReportBean(voids[0]);
            }

            @Override
            protected void onPostExecute(InComingReportBean inComingReportBean) {
                super.onPostExecute(inComingReportBean);
                try {
                    c.accept(inComingReportBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(batchId);
    }
}
