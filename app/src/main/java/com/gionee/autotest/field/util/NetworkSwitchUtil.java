package com.gionee.autotest.field.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gionee.autotest.field.ui.network_switch.model.IToast;
import com.gionee.autotest.field.ui.network_switch.model.InterruptException;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

public class NetworkSwitchUtil {
    public static boolean isTest = false;

    /**
     * 获取sim卡数量
     *
     * @return create at 2016-08-09 22:35
     * @Author SONGYC
     */
    public static int getSimCount() {
        int count = 0;
        if (getSim2State() == TelephonyManager.SIM_STATE_READY && getSim1State() == TelephonyManager.SIM_STATE_READY) {
            count = 2;
        } else if (getSim1State() == TelephonyManager.SIM_STATE_READY || getSim2State() == TelephonyManager.SIM_STATE_READY) {
            count = 1;
        }
        return count;
    }

    public static int getSim2State() {
        String sim_state = MySystemProperties.get("gsm.sim.state");
        Log.i(Constant.TAG, "sim_state=" + sim_state);
        if (sim_state.contains(",") && !sim_state.endsWith(",")) {
            sim_state = sim_state.split(",")[1];
        }
        Log.i(Constant.TAG, "sim2State:" + sim_state);
        return sim_state.trim().equals("READY") ? TelephonyManager.SIM_STATE_READY : TelephonyManager.SIM_STATE_ABSENT;
    }

    public static String getSim1Name() {
        String sim1name = "";
        String name = MySystemProperties.get("gsm.sim.operator.default-name").trim();
        if (name.contains(",")) {
            if (!name.endsWith(",")) {
                sim1name = name.split(",")[0];
            }
        } else {
            if (getSim1State() == TelephonyManager.SIM_STATE_READY) {
                sim1name = name;
            }
        }
        return sim1name;
    }

    public static String getSim2Name() {
        String sim1name = "";
        String name = MySystemProperties.get("gsm.sim.operator.default-name").trim();
        if (name.contains(",")) {
            if (!name.endsWith(",")) {
                sim1name = name.split(",")[1];
            }
        } else {
            if (getSim2State() == TelephonyManager.SIM_STATE_READY) {
                sim1name = name;
            }
        }
        return sim1name;
    }

    public static boolean isSim1Ready() {
        return getSim1Name() != null && !Objects.equals(getSim1Name(), "");
    }

    public static boolean isSim2Ready() {
        return getSim2Name() != null && !Objects.equals(getSim2Name(), "");
    }

    public static int getSim1State() {
        String sim_state = MySystemProperties.get("gsm.sim.state");
        if (!Objects.equals(sim_state, "") && sim_state != null && sim_state.contains(",") && !sim_state.endsWith(",")) {
            sim_state = sim_state.split(",")[0];
        }
        Log.i(Constant.TAG, "sim1State:" + sim_state);
        sim_state = sim_state == null ? "" : sim_state;
        return sim_state.trim().equals("READY") ? TelephonyManager.SIM_STATE_READY : TelephonyManager.SIM_STATE_ABSENT;
    }

    public static boolean isSimReady(int simState) {
        return simState == TelephonyManager.SIM_STATE_READY;
    }

    public static void setWifiEnable(Context context, boolean isOpen) {
        @SuppressLint("WifiManagerPotentialLeak") WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Log.i(Constant.TAG, "设置wifi为:" + isOpen);
        mWifi.setWifiEnabled(isOpen);
    }

    public static void setDataEnable(Context context, int subid, boolean isEnable) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Method mSetMethod;
        try {
            mSetMethod = telephonyManager.getClass().getMethod("setDataEnabled", int.class, boolean.class);
            mSetMethod.invoke(telephonyManager, subid, isEnable);
        } catch (Exception e) {
            Log.i(Constant.TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean isMobileDataOn(Context context, int subId) {
        TelephonyManager mT = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isDataOn = false;
        try {
            Method get = mT.getClass().getMethod("getDataEnabled", int.class);
            isDataOn = (boolean) get.invoke(mT, subId);
        } catch (Exception e) {
            Log.i(Constant.TAG, e.getMessage());
            e.printStackTrace();
        }
        return isDataOn;
    }

    public static void setDefaultData(Context context, int id) {
        SubscriptionManager sm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            sm = SubscriptionManager.from(context);
        }
        try {
            Method set = sm.getClass().getMethod("setDefaultDataSubId", int.class);
            set.invoke(sm, id);
        } catch (Exception e) {
            Log.i(Constant.TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setDefaultVoiceSubId(Context context, int subId) {
        SubscriptionManager sm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            sm = SubscriptionManager.from(context);
        }
        try {
            Method set = sm.getClass().getMethod("setDefaultVoiceSubId", int.class);
            set.invoke(sm, subId);
        } catch (Exception e) {
            Log.i(Constant.TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getDefaultDataSubId(Context context) {
        SubscriptionManager sm = SubscriptionManager.from(context);
        int defaultKey;
        try {
            Method getDefault = sm.getClass().getMethod("getDefaultDataSubId");
            defaultKey = (int) getDefault.invoke(sm);
        } catch (Exception e) {
            Log.i(Constant.TAG, e.getMessage());
            e.printStackTrace();
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }
        return defaultKey;
    }

    /**
     * 检测网络是否连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        // 去进行判断网络是否连接
        NetworkInfo active = manager.getActiveNetworkInfo();
        return active != null && active.getType() == ConnectivityManager.TYPE_MOBILE && active.getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean waitForMobileNetwork(Context context, int timeout) {
        int times = 1;
        if (timeout > 2000) times = timeout / 2000;
        boolean result = false;
        for (int i = 0; i < times; i++) {
            if (isMobileNetwork(context)) {
                result = true;
            } else {
                SystemClock.sleep(1000);
            }
        }
        return result;
    }

    private static boolean isMobileNetwork(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        // 去进行判断网络是否连接
        NetworkInfo active = manager.getActiveNetworkInfo();
        return active != null && active.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        // 得到网络连接信息
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static void assertInterrupted(double sn) {
        if (!isTest) {
            Log.i(Constant.TAG, "检查网络线程被打断:-----------" + sn);
            throw new InterruptException();
        }
    }

    public static void sleep(int time) {
        long target = System.currentTimeMillis() + time;
        while (target > System.currentTimeMillis()) {
            SystemClock.sleep(10);
            assertInterrupted(0.1);
        }
    }

    public static void sleep(int time, Function<Void, Boolean> until) {
        long target = System.currentTimeMillis() + time;
        while (target > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (until.apply(null)) {
                    return;
                }
            }
            SystemClock.sleep(10);
            assertInterrupted(0.1);
        }
    }

    public static void waitSecondTime(IToast toast, int time, String msg, double sn) {
        toast.toast(time / 1000 + "秒后" + msg);
        Log.i(Constant.TAG, time / 1000 + "秒后" + msg);
        long targetTime = System.currentTimeMillis() + time;
        while (targetTime > System.currentTimeMillis()) {
            SystemClock.sleep(10);
            assertInterrupted(sn);
        }
    }

    /**
     * 获取当前日期
     *
     * @Author SONGYC
     * create at 2016-07-18 16:30
     */
    public static String getTimeForFilename() {
        return getCurrentTime("yyyyMMdd_HHmmss");
    }


    /**
     * 获取当前时间（传入格式）
     *
     * @return create at 2016-08-09 17:53
     * @Author SONGYC
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat mFormat = new SimpleDateFormat(format);
        return mFormat.format(new Date());
    }

    public static String getSumContent(ArrayList<NetworkSwitchResult> results) {
        int success = 0, fail = 0;
        for (int i = 0; i < results.size(); i++) {
            NetworkSwitchResult networkSwitchResult = results.get(i);
            if (networkSwitchResult.result.equals("通过")) {
                success++;
            } else {
                fail++;
            }
        }
        return "总测试" + results.size() + "次\n成功" + success + "次\n失败" + fail + "次\n成功率" + ((float) success / results.size()) + "%";
    }
}
