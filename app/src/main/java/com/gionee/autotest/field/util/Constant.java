package com.gionee.autotest.field.util;

import android.provider.BaseColumns;

/**
 * Created by viking on 11/6/17.
 *
 * Constants for widely used by this application
 */

public class Constant {

    //application tag for all log print
    public static final String TAG                      = "FieldTesting" ;

    //for splash screen time out
    public static int SPLASH_TIME_OUT                   = 1000;
    public static final String HOME                     = "field" ;

    //Preferences constants
    public static final String PREF_KEY_FIRST_LAUNCH    = "first_launch" ;
    public static final String PREF_KEY_APP_VERSION     = "app_version"  ;

    public static final String PREF_NAME                = "field_prefs" ;

    //for DB constants
    public static final String DATABASE_NAME            = "field.db" ;
    public static final int    DATABASE_VERSION         = 1 ;

    public static final class APPDB implements BaseColumns {

        private APPDB(){}

        public static final String TABLE_NAME            = "app" ;

        public static final String ORDER_BY              = " _id DESC" ;

        /*
         * Column definitions
         */
        public static final String COLUMN_NAME_KEY       = "app_key" ;

        public static final String COLUMN_NAME_LABEL     = "app_label" ;

        public static final String COLUMN_NAME_ICON      = "app_icon" ;

        public static final String COLUMN_NAME_ACTIVITY  = "app_activity" ;

        public static final String COLUMN_NAME_INSTALLED = "app_installed" ;

    }
}
