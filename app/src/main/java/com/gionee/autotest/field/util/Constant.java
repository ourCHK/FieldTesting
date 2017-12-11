package com.gionee.autotest.field.util;

import android.os.Environment;
import android.provider.BaseColumns;

import java.io.File;

/**
 * Created by viking on 11/6/17.
 * <p>
 * Constants for widely used by this application
 */

public class Constant {

    //application tag for all log print
    public static final String TAG = "FieldTesting";

    //for splash screen time out
    public static int SPLASH_TIME_OUT = 1000;
    public static final String HOME = "field";
    public static final String SIGNAL_DIR = "signal";
    public static final String SIGNAL_DATA_NAME = "signal_data.txt";
    public static final String EXPORT_SIGNAL_DATA_NAME = "signal_%s.xls";
    public static final String CALL_QUALITY_HOME = "call_quality";
    public static final String CALL_QUALITY_LAST_TIME = "call_quality_last_time";
    public static final String CALL_QUALITY_DATA_NAME = "call_quality_data.txt";
    public static final String EXPORT_CALL_QUALITY_DATA_NAME = "call_quality_%s.xls";

    public static final String TIME_STAMP_DIR_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    public static final String SEPARATOR = "::";

    //Preferences constants
    public static final String THROUGHPUT_RUNING = "throughput_runing";
    public static final String PREF_KEY_FIRST_LAUNCH = "first_launch";
    public static final String PREF_KEY_FIRST_NOTICE_LAUNCH = "first_notice_launch";
    public static final String PREF_KEY_APP_VERSION = "app_version";
    public static final String PREF_KEY_SIGNAL_INTERVAL = "signal_interval";
    public static final String PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING = "signal_data_collect_running";
    public static final String PREF_KEY_SIGNAL_DATA_COLLECT = "signal_data_collect";
    public static final String PREF_KEY_SIGNAL_DATA_DISCOLLECT = "signal_data_discollect";
    public static final String PREF_KEY_MONITOR_SIGNAL = "monitor_signal";
    public static final String PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL = "manual_stop_monitor_signal";

    public static final String PREF_KEY_CALL_QUALITY_FIRST_ROUND = "call_quality_first_round";
    public static final String PREF_KEY_CALL_QUALITY_RUNNING = "call_quality_running";

    public static final String PREF_KEY_DATA_RESET_INTERVAL = "data_reset_interval";
    public static final String PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE = "data_reset_data_collect_current_cycle";

    public static final String PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING = "data_reset_data_collect_running";
    public static final String DATA_RESET_PRESENTATION_NAME = "data_reset_presentation_name";

    public static final String PREF_KEY_DATA_RESET_RETEST_TIMES = "data_reset_retest_times";
    public static final String PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE = "data_reset_retest_times_current_cycle";

    public static final String DATA_RESET_RECEIVER = "com.gionee.autotest.field.data.reset.receiver"; //数据重激活完成广播

    public static final String DATA_RESET_EACH_RECEIVER = "com.gionee.autotest.field.data.reset.each.receiver"; //数据重激活每轮测试完成广播
    public static final String DATA_RESET_SUCCESS_NUMBER = "data_reset_success_number"; //数据重激活成功次数
    public static final String DATA_RESET_FAILURE_NUMBER = "data_reset_failure_number"; //数据重激活失败次数

//    public static final String DATA_RESET_TOTAL_NUMBER_TESTS = "data_reset_total_number_tests"; //测试总数
    public static final String DATA_RESET_RETEST_FAILURE_TIMES = "data_reset_retest_failure_times"; //数据重激活复测失败次数
    public static final String DATA_RESET_RETEST_SUCCESS_TIMES = "data_reset_retest_success_times"; //数据重激活复测成功次数

    public static final String PREF_KEY_MESSAGE_INTERVAL = "message_interval";
    public static final String PREF_KEY_MESSAGE_PHONE = "message_phone";
    public static final String PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING = "message_data_collect_running";
    public static final String PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE = "message_data_collect_current_cycle";
    public static final String MESSAGE_PRESENTATION_NAME = "message_presentation_name";

    public static final String MESSAGE_CENTEXT = "message_centext";

    public static final String MESSAGE_RECEIVER = "com.gionee.autotest.field.message.receiver"; //信息发送完成广播

    // 自定义ACTION常数作为Intent Filter识别常数
    public static final String SMS_SEND_ACTION = "message_sms_send_action";

    // 自定义ACTION常数作为Intent Filter识别常数
    public static final String DELIVERED_SMS_ACTION = "message_delivered_sms_action";


    public static final String PREF_NAME = "field_prefs";

    //for DB constants
    public static final String DATABASE_NAME = "field.db";
    public static final int DATABASE_VERSION = 1;

    //SD
    // --------------应用缓存文件基本信息-----------------------
    public static final String PATH_SD = Environment.getExternalStorageDirectory() + File.separator + HOME + File.separator;

    public static final String DIR_DATA_RESET = PATH_SD + "data_reset/";
    public static final String DIR_MESSAGE = PATH_SD + "message/";
    // outgoing
    public static final String DIR_OUT_GOING = PATH_SD + "outgoing/";
    public static final String OUT_GOING_EXCEL_PATH = DIR_OUT_GOING + "outGoingCallRecord.xls";

    //callLossRatio
    public static final String DIR_CALL_LOSS_RATIO = PATH_SD + "callLossRatio/";
    public static final String CALL_LOSS_RATIO_EXCEL_PATH = DIR_CALL_LOSS_RATIO + "CallLossRatioRecord.xls";

    //incoming
    public static final String DIR_INCOMING = PATH_SD + "incoming/";
    public static final String INCOMING_EXCEL_PATH = DIR_INCOMING + "incomingCallRecord.xls";

    //network_switch
    public static final String DIR_NETWORK_SWITCH = PATH_SD + "network_switch/";
    public static final String NETWORK_SWITCH_EXCEL_PATH = DIR_NETWORK_SWITCH + "networkSwitchRecord.xls";
    public static final String NETWORK_SWITCH_FAILED_EXCEL_PATH = DIR_NETWORK_SWITCH + "networkSwitchFailedRecord.xls";

    //dataStability
    public static final String DIR_DATA_STABILITY = PATH_SD+"data_stability";
    public static final String DATA_STABILITY_EXCEL_PATH = DIR_DATA_STABILITY+"dataStability.xls";

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

        public static final String COLUMN_NAME_REQUIRE_SYS_PERM = "app_require_sys_perm";

    }

    public static final class InComingDB implements BaseColumns {
        private InComingDB() {

        }

        public static final class InComingData implements BaseColumns {
            public static final String NUMBER = "number";
            public static final String BATCH_ID = "batch_id";
            public static final String RESULT = "result";
            public static final String TEST_INDEX = "testIndex";
            public static final String TIME = "time";
            public static final String FAIL_MSG = "failMsg";
            public static final String NAME = "table_data";
        }

        public static final class InComingBatch implements BaseColumns {
            public static final String AUTO_REJECT = "autoReject";
            public static final String AUTO_REJECT_TIME = "autoRejectTime";
            public static final String AUTO_ANSWER = "autoAnswer";
            public static final String AUTO_ANSWER_HANGUP = "autoAnswerHangup";
            public static final String ANSWER_HANGUP_TIME = "answerHangupTime";
            public static final String GAP_TIME = "gapTime";
            public static final String IS_HANG_UP_PRESS_POWER = "isHangUpPressPower";
            public static final String TIME = "time";
            public static final String NAME = "table_batch";
        }
    }

    public static final class OutGoingDB implements BaseColumns {

        public static final class OutGoingBatch implements BaseColumns {
            public static final String NUMBERS = "numbers";
            public static final String CYCLE = "cycle";
            public static final String GAP_TIME = "gapTime";
            public static final String CALL_TIME = "callTime";
            public static final String CALL_TIME_SUM = "callTimeSum";
            public static final String IS_SPEAKER_ON = "isSpeakerOn";
            public static final String TIME = "time";
            public static final String NAME = "table_batch_out";
            public static final String VERIFY_COUNT = "verifyCount";
        }

        public static final class OutGoingData implements BaseColumns {
            public static final String BATCH_ID = "batch_id";
            public static final String CYCLE_INDEX = "cycleIndex";
            public static final String NUMBER = "number";
            public static final String DIAL_TIME = "dialTime";
            public static final String OFF_HOOK_TIME = "offHookTime";
            public static final String HANG_UP_TIME = "hangUpTime";
            public static final String RESULT = "result";
            public static final String TIME = "time";
            public static final String IS_VERIFY = "isVerify";
            public static final String SIM_NET_INFO = "simNetInfo";
            public static final String NAME = "out_going_data";
        }


    }
    public static final class CallLossRatioDB implements BaseColumns {

        public static final class CallLossRatioBatch implements BaseColumns {
            public static final String NUMBERS = "numbers";
            public static final String CYCLE = "cycle";
            public static final String GAP_TIME = "gapTime";
            public static final String CALL_TIME = "callTime";
            public static final String CALL_TIME_SUM = "callTimeSum";
            public static final String IS_SPEAKER_ON = "isSpeakerOn";
            public static final String TIME = "time";
            public static final String NAME = "callLossRatioBatch";
            public static final String VERIFY_COUNT = "verifyCount";
        }

        public static final class CallLossRatioData implements BaseColumns {
            public static final String BATCH_ID = "batch_id";
            public static final String CYCLE_INDEX = "cycleIndex";
            public static final String NUMBER = "number";
            public static final String DIAL_TIME = "dialTime";
            public static final String OFF_HOOK_TIME = "offHookTime";
            public static final String HANG_UP_TIME = "hangUpTime";
            public static final String RESULT = "result";
            public static final String TIME = "time";
            public static final String IS_VERIFY = "isVerify";
            public static final String SIM_NET_INFO = "simNetInfo";
            public static final String CODE = "code";
            public static final String NAME = "call_loss_ratio_data";
        }


    }
    //network switch actions
    /**
     * 停止测试广播
     */
    public static final String ACTION_STOP_TEST = "action_AutoSwitchSimCardTest.stop_test";
    /**
     * 切换sim卡广播
     */
    public static final String ACTION_SWITCH_SIM_FINISH = "action_AutoSwitchSimCardTest.switch_sim_finish";
    /**
     * 等待时间
     */
    public static final int CHECK_WAIT_TIME = 60000;
}
