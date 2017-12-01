package com.gionee.autotest.field.ui.data_stability;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.google.gson.Gson;

public class WriteResultTask extends AsyncTask<WebViewResultSum, Void, Void> {
    private Context context;
    private int              batchId;
    private int              textIndex;
    private CallBack callBack;
    private WebViewResultSum resultSum;

    public WriteResultTask(Context context, int batchId, int textIndex, CallBack callBack) {
        this.context = context;
        this.batchId = batchId;
        this.textIndex = textIndex;
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(WebViewResultSum... resultSums) {
        try {
            DatabaseUtil databaseUtil  = new DatabaseUtil(context);
            ContentValues contentValues = new ContentValues();
            resultSum = resultSums[0];
            contentValues.put("batchId", batchId);
            contentValues.put("testIndex", textIndex);
            contentValues.put("result", new Gson().toJson(resultSum));
            databaseUtil.insert(contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callBack != null) {
            callBack.todo(resultSum);
        }
    }
}
