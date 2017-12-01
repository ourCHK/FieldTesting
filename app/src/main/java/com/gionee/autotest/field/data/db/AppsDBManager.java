package com.gionee.autotest.field.data.db;

import android.content.Context;
import android.util.Log;

import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.util.Constant;

import java.util.List;

/**
 * Created by viking on 11/7/17.
 *
 * use this class to manager or operate all apps
 */

public class AppsDBManager {

    private static AppsDBHelper sDataBaseHelper ;

    static void initDatabase(Context context){
        createDataBase(context);
    }

    private static void createDataBase(Context context) {
        if (sDataBaseHelper == null) {
            Log.i(Constant.TAG, "init AppsDBManager");
            sDataBaseHelper = new AppsDBHelper(context);
        }
    }

    public static boolean insertApps(List<App> apps){
        return sDataBaseHelper.insertApps(apps) ;
    }

    public static List<App> fetchAllApps(boolean installed){
        return sDataBaseHelper.fetchAllApps(installed) ;
    }

    public static boolean updateApp(int key, boolean installed){
        return sDataBaseHelper.updateApp(key, installed) ;
    }

    public static boolean deleteDatabase(){
        return sDataBaseHelper.deleteAllApp() ;
    }

}
