package com.gionee.autotest.field.ui.network_switch.model;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.services.INetworkSwitchService;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.NetworkSwitchUtil;
import com.gionee.autotest.field.util.SimUtil;

import java.util.function.Consumer;
import java.util.function.Function;


public class SwitchTest {
    private Context               context;
    private INetworkSwitchService iTestService;
    private IToast                toast;
    private NetworkSwitchParam            testParams;
    private Long                  testRound, currentTestRound;
    private SimUtil        simUtil;
//    private DatabaseUtil   db;
    private Consumer<Void> consumer;

    public SwitchTest(INetworkSwitchService iTestService, IToast toast, NetworkSwitchParam testParams) {
        this.context = iTestService.getContext();
        this.iTestService = iTestService;
        this.toast = toast;
        this.testParams = testParams;
    }

    private boolean isFirstTimes() {
        return currentTestRound == 0;
    }

    private boolean isLastTimes() {
        return currentTestRound == testRound;
    }

    public void start() {
        init();
        test();
    }

    private void init() {
        testRound = Preference.getLong(context,"testRound", 1L);
        currentTestRound = Preference.getLong(context,"currentTestRound", 0L);
        Log.i(Constant.TAG, "取出参数====testRound:" + testRound + "取出currentTestRound:" + currentTestRound);
        simUtil = new SimUtil(toast,context);
//        db = new DatabaseUtil(context);
    }

    private void test() {
        if (!isFirstTimes()) {
            Log.i(Constant.TAG, "判断屏幕是否上锁");
            Log.i(Constant.TAG, "=============准备检查测试结果===============");
            checkResultAndWrite();
        }
        if (!isLastTimes()) {
            currentTestRound++;
            Preference.putLong(context,"currentTestRound", currentTestRound);
            Log.i(Constant.TAG, "=====开始第 " + currentTestRound + "次测试开始====");
            NetworkSwitchUtil.assertInterrupted(1);
            testFlight_mode(1L);
            switchSim();
            testReboot();
        } else {
            Log.i(Constant.TAG, "======测试次数=总次数，无需测试。准备操作停止测试======");
            stopTestThread();
        }
    }

    public void stopTestThread() {
        Log.i(Constant.TAG, "===========处理停止操作=============");
        Preference.putBoolean(context,"isTest", false);
        Log.i(Constant.TAG, "更新测试次数为:" + testRound);
        Preference.putLong(context,"currentTestRound", testRound);
        if (consumer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                consumer.accept(null);
            }
        }
    }

    private void testFlight_mode(Long flight_mode_times) {
        if (!testParams.flightMode) {
            return;
        }
        NetworkSwitchUtil.waitSecondTime(toast, 5000, "切换飞行模式", 1.1);
//        Log.i(Constant.TAG, "操作飞行模式测试，次数为：" + flight_mode_times);
        for (int i = 0; i < flight_mode_times; i++) {
            NetworkSwitchUtil.assertInterrupted(2);
            setFlightMode(true);
            NetworkSwitchUtil.sleep(10000);
            setFlightMode(false);
            NetworkSwitchUtil.sleep(10000);
            NetworkSwitchUtil.assertInterrupted(2.1);
        }
        NetworkSwitchUtil.sleep(5000);
    }

    private boolean isAirPlaneModeOn() {
        try {
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setFlightMode(boolean isOpen) {
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, isOpen ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", isOpen);
        context.sendBroadcastAsUser(intent, android.os.Process.myUserHandle());
    }

    private void switchSim() {
        if (!testParams.isSwitchSim) {
            return;
        }
        if (isAirPlaneModeOn()) {
            setFlightMode(false);
        }
        int oldId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            oldId = NetworkSwitchUtil.getDefaultDataSubId(context);
        }
        NetworkSwitchUtil.waitSecondTime(toast, 5000, "切换Sim卡", 1.2);
//        Log.i(Constant.TAG, "====开始执行切换sim卡===");
        int newId = (oldId == 1) ? 2 : 1;
//        NetworkSwitchUtil.i("oldId " + oldId + "newId " + newId);
        NetworkSwitchUtil.setDefaultData(context, newId);
        NetworkSwitchUtil.setDefaultVoiceSubId(context, newId);
//        NetworkSwitchUtil.i("default " + NetworkSwitchUtil.getDefaultDataSubId(context));
        if (!NetworkSwitchUtil.isMobileDataOn(context, newId)) {
            NetworkSwitchUtil.setDataEnable(context, oldId, false);
            NetworkSwitchUtil.setDataEnable(context, newId, true);
        }
//        NetworkSwitchUtil.i("等待切换完成");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkSwitchUtil.sleep(2 * 60 * 1000, new Function<Void, Boolean>() {
                @Override
                public Boolean apply(Void aVoid) {
                    return NetworkSwitchUtil.isSim1Ready() && NetworkSwitchUtil.isSim2Ready();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Preference.putString(context,"current_isSwitched", (NetworkSwitchUtil.getDefaultDataSubId(context) == oldId) ? "失败" : "成功");
        }
    }

    private void testReboot() {
        if (testParams.reboot) {
            NetworkSwitchUtil.waitSecondTime(toast, 5000, "重启", 3);
            reboot();
        } else {
            NetworkSwitchUtil.sleep(testParams.isSwitchSim ? 20000 : 10000);
            test();
        }
    }


    private void reboot() {
        Intent reboot = new Intent(Intent.ACTION_REBOOT);
        reboot.putExtra("nowait", 1);
        reboot.putExtra("interval", 1);
        reboot.putExtra("window", 0);
        context.sendBroadcast(reboot);
    }

    private void checkResultAndWrite() {
        String        resultContent  = simUtil.getResultJson(context);
        String        resultFileName = Preference.getString(context,"resultFileName", "1");
        ContentValues cv             = new ContentValues();
//        cv.put(FILE_NAME, resultFileName);
//        cv.put(RESULT_CONTENT, resultContent);
//        db.insert(cv);
//        db.close();
        Log.i(Constant.TAG, "第" + currentTestRound + "次结果已输出");
        toast.toast("第" + currentTestRound + "次结果已输出");
    }

    public void setStopListener(Consumer<Void> consumer) {
        this.consumer = consumer;
    }
}