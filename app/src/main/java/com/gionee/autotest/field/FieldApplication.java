package com.gionee.autotest.field;

import android.app.Application;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.DBManager;
import com.gionee.autotest.field.util.Constant;

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
    }
}
