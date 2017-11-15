package com.gionee.autotest.field.data.db;

import android.content.Context;

/**
 * Created by viking on 11/7/17.
 * <p>
 * Provider interface to create or delete
 */

public final class DBManager {

    /**
     * init all database
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public static void initAllDB(Context context) {
        AppsDBManager.initDatabase(context);
        InComingDBManager.init(context);
    }

    /**
     * delete all database
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public static void deleteAllDB(Context context) {
        AppsDBManager.deleteDatabase();
        InComingDBManager.delete();
    }

}
