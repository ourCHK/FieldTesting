package com.gionee.autotest.field.ui.data_stability.report;


import android.os.AsyncTask;

import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.gionee.autotest.field.ui.data_stability.WebViewResultSum;
import com.gionee.autotest.field.util.DataStabilityUtil;

import java.util.ArrayList;
import java.util.HashMap;


class ReportPresenter {
    private IReportView              mView;
    private HashMap<String, Integer> mUids;
    private ArrayList<String>        mBatchs;

    ReportPresenter(IReportView iReportView) {
        this.mView = iReportView;
        mUids = new HashMap<>();
        mBatchs = new ArrayList<>();
    }

    HashMap<String, Integer> getUids() {
        return mUids;
    }

    void showReport(int i) {
        int batchId = Integer.parseInt(mBatchs.get(i));
        DataStabilityUtil.i("展示 ="+batchId+"的报告");
        new ReportTask().execute(batchId);
    }

    ArrayList<String> getBatchs() {
        return mBatchs;
    }

    void updateBatchs() {
        DatabaseUtil db  = new DatabaseUtil(mView.getContext());
        ArrayList<String> ids = db.getIds();
        db.close();
        mBatchs.clear();
        mBatchs.addAll(ids);
        mView.getSpinnerAdapter().notifyDataSetChanged();
        if (ids.size() == 0) {
            return;
        }
        try {
            mView.setSelection_Spinner(ids.size() - 1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private class ReportTask extends AsyncTask<Integer, Integer, ArrayList<WebViewResultSum>> {

        @Override
        protected ArrayList<WebViewResultSum> doInBackground(Integer... ids) {
            DatabaseUtil db = new DatabaseUtil(mView.getContext());
            return db.getResult(ids[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
            super.onProgressUpdate(params);
        }

        @Override
        protected void onPostExecute(ArrayList<WebViewResultSum> list) {
            super.onPostExecute(list);
            DataStabilityUtil.i("报告"+list.size());
            mView.updateListViewData(list);
        }

    }
}
