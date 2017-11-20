package com.gionee.autotest.field;

import android.app.Application;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.DBManager;
import com.gionee.autotest.field.util.Constant;

import java.io.File;

/**
 * Created by viking on 11/7/17.
 *
 * Application main entry point
 */
public class FieldApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //init preference
        Preference.initName(Constant.PREF_NAME);
        DBManager.initAllDB(getApplicationContext());
        makeDirects();//创建文件夹
    }

    /**
     * 创建文件夹
     */
    private void makeDirects() {

        File DIR_DATA_RESET = new File(Constant.DIR_DATA_RESET);
        if (!DIR_DATA_RESET.exists()){
            DIR_DATA_RESET.mkdirs();
        }

    }
}
