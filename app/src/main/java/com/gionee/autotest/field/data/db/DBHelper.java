package com.gionee.autotest.field.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingBatch;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingData;

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
                + InComingData.BATCH_ID + " Integer," + InComingData.NUMBER + " Long,"
                + InComingData.RESULT + " Integer," + InComingData.TEST_INDEX + " Integer,"
                + InComingData.FAIL_MSG + " text," + InComingData.TIME + " TIMESTAMP(14))");
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
        // Recreates the database with a new version
        onCreate(db);
    }
}
