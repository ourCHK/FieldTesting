package com.gionee.autotest.field.ui.throughput.SQL;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.autotest.field.ui.throughput.Util.Configuration;

import java.io.File;



class DatabaseContext extends ContextWrapper {

    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        String dbFile = Configuration.FILE_PATH + DatabaseHelper.DATABASE_NAME;
        if (!dbFile.endsWith(".db")) {
            dbFile += ".db";
        }
        File result = new File(dbFile);
        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
    }

    /*
     * this softVersion is called for android devices >= api-11. thank to @damccull
     * for fixing this.
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name, mode, factory);
    }

    /* this softVersion is called for android devices < api-11 */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), null);

    }
}
