package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gionee.autotest.field.data.db.DatabaseHelper;
import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.gionee.autotest.field.services.WebViewService;
import com.gionee.autotest.field.ui.data_stability.report.ReportActivity;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.Preference;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class MainAction {
    private Context mContext;
    private IMain   main;

    MainAction(IMain main) {
        this.main = main;
        this.mContext = main.getContext();
    }

    void startTest(DataParam p) {
        String paramJson = new Gson().toJson(p);
        Preference.putString(mContext, "lastParams", paramJson);
        Intent intent = new Intent(mContext, WebViewService.class);
        Configurator.getInstance().setParam(p);
        mContext.startService(intent);
    }

    DataParam getLastParam() {
        String lastParams = Preference.getString(mContext, "lastParams", "");
        if (lastParams.equals("")) {
            return new DataParam();
        }
        try {
            return new Gson().fromJson(lastParams, DataParam.class);
        } catch (JsonSyntaxException e) {
            return new DataParam();
        }
    }


    void showReportPage() {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    void stopTest() {
        mContext.stopService(new Intent(mContext, WebViewService.class));
    }

    @SuppressLint("StaticFieldLeak")
    void clearReport() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                databaseUtil.deleteTable(DatabaseHelper.TABLE_RESULT);
                databaseUtil.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(mContext, "清除报告成功", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
