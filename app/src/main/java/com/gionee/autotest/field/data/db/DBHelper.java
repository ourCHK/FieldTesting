package com.gionee.autotest.field.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gionee.autotest.field.util.Constant;

/**
 * Created by viking on 11/7/17.
 *
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
        Log.i(Constant.TAG, "enter DBHelper onCreate") ;
        db.execSQL("CREATE TABLE " + Constant.APPDB.TABLE_NAME + " ("
                + Constant.APPDB._ID + " INTEGER PRIMARY KEY,"
                + Constant.APPDB.COLUMN_NAME_KEY + " INTEGER,"
                + Constant.APPDB.COLUMN_NAME_LABEL + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_ICON + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_ACTIVITY + " TEXT,"
                + Constant.APPDB.COLUMN_NAME_INSTALLED + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logs that the database is being upgraded
        Log.i(Constant.TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS " + Constant.APPDB.TABLE_NAME);
        // Recreates the database with a new version
        onCreate(db);
    }
}
