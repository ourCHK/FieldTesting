package com.gionee.autotest.field.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gionee.autotest.field.util.DataStabilityUtil;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int    DATABASE_VERSION = 1;
    public static final  String DATABASE_NAME    = "dataStability";
    public static final  String TABLE_RESULT     = "webView";
    private static final String DATABASE_CREATE  = "create table webView(_id INTEGER PRIMARY KEY AUTOINCREMENT,batchId Integer,testIndex Integer,result text,time TIMESTAMP(14))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataStabilityUtil.i("onCreate sql");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists webView");
        onCreate(db);
    }
}
