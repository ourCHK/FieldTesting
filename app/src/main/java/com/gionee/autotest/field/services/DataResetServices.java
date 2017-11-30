package com.gionee.autotest.field.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.gionee.autotest.common.FLog;
import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.ShellUtil;
import com.gionee.autotest.common.YUtils;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataResetHelper;
import com.gionee.autotest.field.util.SignalHelper;
import com.gionee.autotest.field.util.SimUtil;
import com.gionee.autotest.field.util.Util;

import java.io.File;

/**
 * Created by xhk on 2017/11/17.
 */

public class DataResetServices extends Service {

    private ShellUtil.CommandResult commandResult;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取报告名字
        String data_reset_presentation_name = Preference.getString(getApplicationContext(), Constant.DATA_RESET_PRESENTATION_NAME, "");
        //添加标题
        DataResetHelper.exportExcel(Constant.DIR_DATA_RESET + data_reset_presentation_name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false)) {
                    long data_reset_interval = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_INTERVAL, 1);
                    long data_reset_current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1);
                    if (data_reset_interval == data_reset_current_cycle) {

                        FLog.i("data_reset_interval=" + data_reset_interval + ",data_reset_current_cycle=" + data_reset_current_cycle);
                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_RUNNING, false);
                        //发送测试完成广播
                        sendBroadcast(new Intent(Constant.DATA_RESET_RECEIVER));

                    } else {
                        String data_reset_presentation_name = Preference.getString(getApplicationContext(), Constant.DATA_RESET_PRESENTATION_NAME, "");
                        //关闭wifi
                        YUtils.WiFiwitch(getApplicationContext(), false);
                        SystemClock.sleep(1000);
                        //开启数据
                        YUtils.setMobileDataState(getApplicationContext(), true);
                        SystemClock.sleep(1000);

                        ShellUtil.execCommand("ping -c 1 www.baidu.com", false);
                        SystemClock.sleep(1000);

                        //关闭数据
                        YUtils.setMobileDataState(getApplicationContext(), false);
                        SystemClock.sleep(1000);

                        ShellUtil.execCommand("ping -c 1 www.baidu.com", false);
                        SystemClock.sleep(1000);

                        //开启数据
                        YUtils.setMobileDataState(getApplicationContext(), true);
                        String start_time = DataResetHelper.getTimeDatas();

                        ShellUtil.CommandResult commandResult = ShellUtil.execCommand("ping -c 1 www.baidu.com", false);
                        int result = commandResult.result;
                        if (result != 0) {
                            //失败
                            FLog.i("resulterrs2=" + result);
                            FLog.i("resulterrs22=" + commandResult.errorMsg);

                                getPing(getApplicationContext(), start_time);

                        } else {
                            //成功
                            FLog.i("resultsuree1=" + result);
                            FLog.i("resultsuree11=" + commandResult.successMsg);

                            int subId = SimUtil.getDefaultDataSubId();
                            SimSignalInfo simSignalInfo = SignalHelper.getInstance(getApplicationContext()).getSimSignalInfo(subId);
                            if (simSignalInfo != null) {

                                DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                        start_time, DataResetHelper.getTimeDatas(), "成功", simSignalInfo.mOperator, simSignalInfo.mNetType, simSignalInfo.mLevel + "", simSignalInfo.mSignal
                                });

                                Preference.putLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                                FLog.i("data_reset_current_cycle=" + Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                                SystemClock.sleep(1000);
                            } else {
                                DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                        start_time, DataResetHelper.getTimeDatas(), "成功", null, null, null + "", null
                                });

                                Preference.putLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                                FLog.i("data_reset_current_cycle=" + Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                                SystemClock.sleep(1000);
                            }

                        }
                    }

                }
            }
        }).start();

    }


    private static void getPing(Context context, String start_time) {


        if (!DataResetHelper.getTimeDifference(start_time, DataResetHelper.getTimeDatas())) {

            ShellUtil.CommandResult commandResults = ShellUtil.execCommand("ping -c 1 www.baidu.com", false);

            if (commandResults.result != 0) {

                getPing(context, start_time);

            }else{
                String data_reset_presentation_name = Preference.getString(context, Constant.DATA_RESET_PRESENTATION_NAME, "");
                long data_reset_current_cycle = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1);

                int subId = SimUtil.getDefaultDataSubId();
                SimSignalInfo simSignalInfo = SignalHelper.getInstance(context).getSimSignalInfo(subId);

                DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                        start_time, DataResetHelper.getTimeDatas(), "成功", simSignalInfo.mOperator, simSignalInfo.mNetType, simSignalInfo.mLevel + "", simSignalInfo.mSignal
                });

                Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                FLog.i("data_reset_current_cycle=" + Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                SystemClock.sleep(1000);
            }


        } else {

            String data_reset_presentation_name = Preference.getString(context, Constant.DATA_RESET_PRESENTATION_NAME, "");

            long data_reset_current_cycle = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1);

            long aLong1 = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE, -1);
            Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE, (aLong1+1));
            long aLong2 = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE, -1);

            long aLong3 = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES, -1);

            if (aLong2==aLong1){

                ShellUtil.CommandResult commandResults = ShellUtil.execCommand("ping -c 1 www.baidu.com", false);
                if (commandResults.result!=0){
                    int subId = SimUtil.getDefaultDataSubId();
                    SimSignalInfo simSignalInfo = SignalHelper.getInstance(context).getSimSignalInfo(subId);
                    if (simSignalInfo!=null){
                        DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                start_time, DataResetHelper.getTimeDatas(), "失败", simSignalInfo.mOperator, simSignalInfo.mNetType, simSignalInfo.mLevel + "", simSignalInfo.mSignal
                        });

                        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                        FLog.i("data_reset_current_cycle=" + Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                        SystemClock.sleep(1000);
                    }else{
                        DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                start_time, DataResetHelper.getTimeDatas(), "失败", null, null, null+"", null
                        });

                        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                        FLog.i("data_reset_current_cycle=" + Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                        SystemClock.sleep(1000);
                    }

                }else{

                    int subId = SimUtil.getDefaultDataSubId();
                    SimSignalInfo simSignalInfo = SignalHelper.getInstance(context).getSimSignalInfo(subId);
                    if (simSignalInfo!=null){
                        DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                start_time, DataResetHelper.getTimeDatas(), "成功", simSignalInfo.mOperator, simSignalInfo.mNetType, simSignalInfo.mLevel + "", simSignalInfo.mSignal
                        });

                        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                        FLog.i("data_reset_current_cycle=" + Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                        SystemClock.sleep(1000);
                    }else{
                        DataResetHelper.addExcel(new File(Constant.DIR_DATA_RESET + data_reset_presentation_name), new String[]{
                                start_time, DataResetHelper.getTimeDatas(), "成功", null, null, null+"", null
                        });

                        Preference.putLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, data_reset_current_cycle + 1);
                        FLog.i("data_reset_current_cycle=" + Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1));
                        SystemClock.sleep(1000);
                    }

                    long aLong4 = Preference.getLong(context, Constant.PREF_KEY_DATA_RESET_RETEST_TIMES, -1);
                    Preference.putLong(context,Constant.PREF_KEY_DATA_RESET_RETEST_TIMES_CURRENT_CYCLE,aLong4);
                }

            }else{
                getPing(context, DataResetHelper.getTimeDatas());
            }

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
