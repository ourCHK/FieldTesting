package com.gionee.autotest.field.ui.throughput.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME ="WifiProperty";
    public static final String TABLE_NAME ="WifiProperty";
    private static final String DATABASE_CREATE =
            "create table WifiProperty(_id INTEGER PRIMARY KEY AUTOINCREMENT,serial INTEGER,start text,type text,averageSpeed text,web text,useTime text,times Integer,speed text,way text,time TEXT(10))";

    public DatabaseHelper(Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //初次使用软件时生成数据库表
    public void onCreate(SQLiteDatabase db) {
//        Helper.i("onCreate sql");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    //升级软件时更新数据库结构
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists WifiProperty");
        onCreate(db);
    }
}
