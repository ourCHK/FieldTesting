package com.gionee.autotest.field.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingBatch;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingData;
import com.gionee.autotest.field.util.Constant.OutGoingDB.OutGoingBatch;
import com.gionee.autotest.field.util.Constant.OutGoingDB.OutGoingData;

/**
 * Created by viking on 11/7/17.
 * <p>
 * Create or delete DB operations
 */

class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase mDb;

    DBHelper(Context context) {
        // calls the super constructor, requesting the default cursor factory.
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        this.mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(Constant.TAG, "enter DBHelper onCreate");
        db.execSQL("CREATE TABLE " + Constant.APPDB.TABLE_NAME + " ("
                + Constant.APPDB._ID + " INTEGER PRIMARY KEY,"
                + Constant.APPDB.COLUMN_NAME_KEY + " INTEGER,"
                + Constant.APPDB.COLUMN_NAME_LABEL + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_ICON + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_ACTIVITY + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_INSTALLED + " INTEGER"
                + ");");
        db.execSQL("create table " + InComingBatch.NAME + "(" + InComingBatch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InComingBatch.AUTO_ANSWER + " Integer," + InComingBatch.AUTO_ANSWER_HANGUP + " Integer,"
                + InComingBatch.ANSWER_HANGUP_TIME + " Integer," + InComingBatch.AUTO_REJECT + " Integer,"
                + InComingBatch.AUTO_REJECT_TIME + " Integer," + InComingBatch.GAP_TIME + " Integer,"
                + InComingBatch.TIME + " TIMESTAMP(14))");
        db.execSQL("create table " + InComingData.NAME + "(" + InComingData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InComingData.BATCH_ID + " Integer," + InComingData.NUMBER + " text,"
                + InComingData.RESULT + " Integer," + InComingData.TEST_INDEX + " Integer,"
                + InComingData.FAIL_MSG + " text," + InComingData.TIME + " TIMESTAMP(14))");
        db.execSQL("create table " + OutGoingBatch.NAME + "(" + OutGoingBatch._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OutGoingBatch.NUMBERS + " text," + OutGoingBatch.CYCLE + " Integer," + OutGoingBatch.GAP_TIME + " Integer,"
                + OutGoingBatch.CALL_TIME + " Integer," + OutGoingBatch.CALL_TIME_SUM + " Integer,"
                + OutGoingBatch.IS_SPEAKER_ON + " Integer," + OutGoingBatch.VERIFY_COUNT + " Integer," + OutGoingBatch.TIME + " TIMESTAMP(14))");
        db.execSQL("create table " + OutGoingData.NAME + "(" + OutGoingData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + OutGoingData.BATCH_ID + " Long," + OutGoingData.CYCLE_INDEX + " Long," + OutGoingData.NUMBER + " Long,"
                + OutGoingData.DIAL_TIME + " TIMESTAMP(14)," + OutGoingData.OFF_HOOK_TIME + " TIMESTAMP(14),"
                + OutGoingData.HANG_UP_TIME + " TIMESTAMP(14)," + OutGoingData.RESULT + " Integer," +OutGoingData.IS_VERIFY+" Integer,"+OutGoingData.SIM_NET_INFO+" text,"+ OutGoingData.TIME + " TIMESTAMP(14))");
        db.execSQL("create table networkSwitch(_id INTEGER PRIMARY KEY AUTOINCREMENT,resultFileName text,result text,time TIMESTAMP(14))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.i(Constant.TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + Constant.APPDB.TABLE_NAME);
        db.execSQL("drop table if exists " + InComingBatch.NAME);
        db.execSQL("drop table if exists " + InComingData.NAME);
        db.execSQL("drop table if exists " + OutGoingBatch.NAME);
        db.execSQL("drop table if exists " + OutGoingData.NAME);
        db.execSQL("drop table if exists networkSwitch");
        // Recreates the database with a new version
        onCreate(db);
    }
}
