package com.gionee.autotest.field;

import android.app.Application;
import android.content.Context;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.DBManager;
import com.gionee.autotest.field.ui.throughput.Util.Configuration;
import com.gionee.autotest.field.util.Constant;

import java.io.File;

/**
 * Created by viking on 11/7/17.
 *
 * Application main entry point
 */
public class FieldApplication extends Application{

    public static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //init preference
        context=this.getApplicationContext();
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

        File DIR_MESSAGE = new File(Constant.DIR_MESSAGE);
        if (!DIR_MESSAGE.exists()){
            DIR_MESSAGE.mkdirs();
        }

        File RESULT_PATH = new File(Configuration.RESULT_PATH);
        if (!RESULT_PATH.exists()){
            RESULT_PATH.mkdirs();
        }


    }

    public static Context getContext() {
        return context;
    }
}
