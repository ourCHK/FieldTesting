package com.gionee.autotest.field.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.autotest.field.ui.data_stability.WebViewResultSum;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DatabaseUtil {

    private Context mContext;
    private SQLiteDatabase db;
    public static final String RESULT_CONTENT = "result";

    public DatabaseUtil(Context context) {
        this.mContext = context;
        open();
    }

    private void open() {
        DatabaseHelper mDBHelper = new DatabaseHelper(mContext);
        db = mDBHelper.getReadableDatabase();
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    public long insert(String table_name, ContentValues values) {
        return db.insert(table_name, null, values);
    }

    public long insert(ContentValues values) {
        return insert(DatabaseHelper.TABLE_RESULT, values);
    }

    public Cursor rawQuery(String query, String[] selection) {
        return db.rawQuery(query, selection);
    }

    public void deleteTable(String table_name) {
        try {
            db.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getIds() {
        Cursor cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_RESULT, null);
        ArrayList<String> ids    = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return ids;
        }
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("batchId"));
            if (!ids.contains(id)) {
                ids.add(id);
            }
        }
        return ids;
    }


    public ArrayList<WebViewResultSum> getResult(long id) {
        Cursor cursor            = db.rawQuery("select * from " + DatabaseHelper.TABLE_RESULT + " where batchId=" + id, null);
        ArrayList<WebViewResultSum> webViewResultSums = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            DataStabilityUtil.i("cursor null");
            return webViewResultSums;
        }
        Gson gson = new Gson();
        while (cursor.moveToNext()) {
            int    testIndex  = cursor.getInt(cursor.getColumnIndex("testIndex"));
            String resultJson = cursor.getString(cursor.getColumnIndex("result"));
            webViewResultSums.add( gson.fromJson(resultJson, WebViewResultSum.class));
        }
        return webViewResultSums;
    }

    public void del(String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }

    public ArrayList<String> getResultList() {
        Cursor cursor  = db.rawQuery("select result from " + DatabaseHelper.TABLE_RESULT, null);
        ArrayList<String> results = new ArrayList<>();
        if (cursor == null) {
            return results;
        }
        while (cursor.moveToNext()) {
            String resultFileName = cursor.getString(cursor.getColumnIndex("resultFileName"));
            if (!results.contains(resultFileName)) {
                results.add(resultFileName);
            }
        }
        return results;
    }
}
