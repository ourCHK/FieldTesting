package com.gionee.autotest.field.util;

import android.os.Environment;
import android.provider.BaseColumns;

/**
 * Created by viking on 11/6/17.
 * <p>
 * Constants for widely used by this application
 */

public class Constant {

    //application tag for all log print
    public static final String TAG = "FieldTesting";

    //for splash screen time out
    public static       int    SPLASH_TIME_OUT         = 1000;
    public static final String HOME                    = "field";
    public static final String SIGNAL_DIR              = "signal";
    public static final String SIGNAL_DATA_NAME        = "signal_data.txt";
    public static final String EXPORT_SIGNAL_DATA_NAME = "signal_%s.xls";

    public static final String CALL_QUALITY_HOME       = "call_quality" ;
    public static final String CALL_QUALITY_LAST_TIME  = "call_quality_last_time" ;
    public static final String CALL_QUALITY_DATA_NAME  = "call_quality_data.txt" ;
    public static final String EXPORT_CALL_QUALITY_DATA_NAME = "call_quality_%s.xls";

    public static final String TIME_STAMP_DIR_FORMAT   = "yyyy-MM-dd_HH-mm-ss" ;

    public static final String SEPARATOR               = "::" ;

    //Preferences constants
    public static final String PREF_KEY_FIRST_LAUNCH                = "first_launch";
    public static final String PREF_KEY_APP_VERSION                 = "app_version";
    public static final String PREF_KEY_SIGNAL_INTERVAL             = "signal_interval";
    public static final String PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING = "signal_data_collect_running";
    public static final String PREF_KEY_SIGNAL_DATA_COLLECT         = "signal_data_collect";
    public static final String PREF_KEY_SIGNAL_DATA_DISCOLLECT      = "signal_data_discollect";
    public static final String PREF_KEY_MONITOR_SIGNAL              = "monitor_signal";
    public static final String PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL  = "manual_stop_monitor_signal";

    public static final String PREF_KEY_CALL_QUALITY_FIRST_ROUND    = "call_quality_first_round" ;
    public static final String PREF_KEY_CALL_QUALITY_RUNNING        = "call_quality_running" ;

    public static final String PREF_KEY_DATA_RESET_INTERVAL = "data_reset_interval";
    public static final String PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING  = "data_reset_data_collect_running";
    public static final String PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE  = "data_reset_data_collect_current_cycle";
    public static final String DATA_RESET_PRESENTATION_NAME  = "data_reset_presentation_name";

    public static final String DATA_RESET_RECEIVER = "com.gionee.autotest.field.data.reset.receiver"; //数据重激活完成广播


    public static final String PREF_NAME = "field_prefs";

    //for DB constants
    public static final String DATABASE_NAME    = "field.db";
    public static final int    DATABASE_VERSION = 1;

    //SD
    // --------------应用缓存文件基本信息-----------------------
    public static final String PATH_SD = Environment.getExternalStorageDirectory() + "/field/";

    public static final String DIR_DATA_RESET = PATH_SD + "data_reset/";


    public static final class APPDB implements BaseColumns {

        private APPDB() {
        }

        public static final String TABLE_NAME = "app";

        public static final String ORDER_BY = " _id DESC";

        /*
         * Column definitions
         */
        public static final String COLUMN_NAME_KEY = "app_key";

        public static final String COLUMN_NAME_LABEL = "app_label";

        public static final String COLUMN_NAME_ICON = "app_icon";

        public static final String COLUMN_NAME_ACTIVITY = "app_activity";

        public static final String COLUMN_NAME_INSTALLED = "app_installed";

    }

    public static final class InComingDB implements BaseColumns {
        private InComingDB() {

        }

        public static final class InComingData implements BaseColumns {
            public static final String NUMBER     = "number";
            public static final String BATCH_ID   = "batch_id";
            public static final String RESULT     = "result";
            public static final String TEST_INDEX = "testIndex";
            public static final String TIME       = "time";
            public static final String FAIL_MSG   = "failMsg";
            public static final String NAME       = "table_data";
        }

        public static final class InComingBatch implements BaseColumns {
            public static final String AUTO_REJECT        = "autoReject";
            public static final String AUTO_REJECT_TIME   = "autoRejectTime";
            public static final String AUTO_ANSWER        = "autoAnswer";
            public static final String AUTO_ANSWER_HANGUP = "autoAnswerHangup";
            public static final String ANSWER_HANGUP_TIME = "answerHangupTime";
            public static final String GAP_TIME           = "gapTime";
            public static final String TIME               = "time";
            public static final String NAME               = "table_batch";
        }
    }

    public static final class OutGoingDB implements BaseColumns {

        public static final class OutGoingBatch implements BaseColumns {
            public static final String NUMBERS       = "numbers";
            public static final String CYCLE         = "cycle";
            public static final String GAP_TIME      = "gapTime";
            public static final String CALL_TIME     = "callTime";
            public static final String CALL_TIME_SUM = "callTimeSum";
            public static final String IS_SPEAKER_ON = "isSpeakerOn";
            public static final String TIME          = "time";
            public static final String NAME          = "table_batch_out";
        }

        public static final class OutGoingData implements BaseColumns {
            public static final String BATCH_ID      = "batch_id";
            public static final String CYCLE_INDEX   = "cycleIndex";
            public static final String NUMBER        = "number";
            public static final String DIAL_TIME     = "dialTime";
            public static final String OFF_HOOK_TIME = "offHookTime";
            public static final String HANG_UP_TIME  = "hangUpTime";
            public static final String RESULT        = "result";
            public static final String TIME          = "time";
            public static final String NAME          = "out_going_data";
        }


    }

    //network switch actions
    /**
     * 停止测试广播
     */
    public static final String  ACTION_STOP_TEST         = "action_AutoSwitchSimCardTest.stop_test";
    /**
     * 切换sim卡广播
     */
    public static final String  ACTION_SWITCH_SIM_FINISH = "action_AutoSwitchSimCardTest.switch_sim_finish";
    /**
     * 等待时间
     */
    public static final int     CHECK_WAIT_TIME          = 60000;
}
