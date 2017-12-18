package com.gionee.autotest.field.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.ui.network_switch.model.IToast;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.google.gson.Gson;

import java.lang.reflect.Method;


public class SimUtil {

    private IToast toast;
    private Context context;

    public SimUtil(IToast toast, Context context) {
        this.toast = toast;
        this.context = context;
    }

    public class SimState {
        public int sim1State = TelephonyManager.SIM_STATE_ABSENT;
        public int sim2State = TelephonyManager.SIM_STATE_ABSENT;

        public SimState(int sim1State, int sim2State) {
            this.sim1State = sim1State;
            this.sim2State = sim2State;
        }
    }

    public SimState getSimStateBefore() {
        int sim1State = Preference.getInt(context, "sim1State", TelephonyManager.SIM_STATE_ABSENT);
        int sim2State = Preference.getInt(context, "sim2State", TelephonyManager.SIM_STATE_ABSENT);
        return new SimState(sim1State, sim2State);
    }


    public boolean hasSimReady() {
        SimState simStateBefore = getSimStateBefore();
        return NetworkSwitchUtil.isSimReady(simStateBefore.sim1State) || NetworkSwitchUtil.isSimReady(simStateBefore.sim2State);
    }

    public void waitForNetWorkOperator() {
        if (!hasSimReady()) return;
        SimState simStateBefore = getSimStateBefore();
        waitSim1Ready(simStateBefore.sim1State);
        waitSim2Ready(simStateBefore.sim2State);
        waitSim1Operator(simStateBefore.sim1State);
        waitSim2Operator(simStateBefore.sim2State);
    }

    public void waitSim1Ready(int sim1State_before) {
        if (sim1State_before == TelephonyManager.SIM_STATE_READY) {
            for (int i = 0; i < 5; i++) {
                NetworkSwitchUtil.assertInterrupted(5.1);
                if (NetworkSwitchUtil.getSim1State() == sim1State_before) {
                    break;
                } else {
                    Log.i(Constant.TAG, "等待sim卡1READY");
                    NetworkSwitchUtil.sleep(2000);
                }
            }
        }
    }

    public void waitSim2Ready(int sim2State_before) {
        if (sim2State_before == TelephonyManager.SIM_STATE_READY) {
            for (int i = 0; i < 5; i++) {
                NetworkSwitchUtil.assertInterrupted(5.2);
                if (NetworkSwitchUtil.getSim2State() == sim2State_before) {
                    break;
                } else {
                    Log.i(Constant.TAG, i + "等待sim卡2READY");
                    NetworkSwitchUtil.sleep(2000);
                }
            }
        }
    }

    public void waitSim2Operator(int sim2State_before) {
        boolean isSim2ready_before = sim2State_before == TelephonyManager.SIM_STATE_READY;
        if (isSim2ready_before) {
            for (int i = 0; i < Constant.CHECK_WAIT_TIME / 5000; i++) {
                NetworkSwitchUtil.assertInterrupted(7);
                if (NetworkSwitchUtil.isSim2Ready()) {
                    break;
                } else {
                    Log.i(Constant.TAG, i + "等待sim卡2注册");
                    toast.toast("等待sim卡2注册");
                    SystemClock.sleep(5000);
                }
            }
        }
    }

    public void waitSim1Operator(int sim1State_before) {
        boolean isSim1ready_before = sim1State_before == TelephonyManager.SIM_STATE_READY;
        if (isSim1ready_before) {
            for (int i = 0; i < Constant.CHECK_WAIT_TIME / 5000; i++) {
                NetworkSwitchUtil.assertInterrupted(6);
                if (NetworkSwitchUtil.isSim1Ready()) {
                    break;
                } else {
                    Log.i(Constant.TAG, i + "等待sim卡1注册");
                    toast.toast("等待sim卡1注册");
                    NetworkSwitchUtil.sleep(5000);
                }
            }
        }
    }

    public ReadSimResult checkReadSim(SimState simState_before) {
        boolean check_readSim = Preference.getBoolean(context, "checkBox_readSim", true);
        String sim1State_now = "卡1NA";
        String sim2State_now = "卡2NA";
        if (check_readSim) {
            if (simState_before.sim1State == TelephonyManager.SIM_STATE_READY) {
                sim1State_now = NetworkSwitchUtil.getSim1State() == TelephonyManager.SIM_STATE_READY ? "卡1识卡" : "卡1不识卡";
            }
            if (simState_before.sim2State == TelephonyManager.SIM_STATE_READY) {
                sim2State_now = NetworkSwitchUtil.getSim2State() == TelephonyManager.SIM_STATE_READY ? "卡2识卡" : "卡2不识卡";
            }
        }
        return new ReadSimResult(sim1State_now, sim2State_now);
    }

    public SignNetWorkResult checkSignNetwork(SimState simState_before) {
        String current_SimName_1 = "卡1NA";
        String current_SimName_2 = "卡2NA";
        boolean check_SignNetwork = Preference.getBoolean(context, "checkBox_SignNetwork", true);
        if (check_SignNetwork) {
            String sim1Name = NetworkSwitchUtil.getSim1Name();
            String sim2Name = NetworkSwitchUtil.getSim2Name();
            Log.i(Constant.TAG, "sim1:" + sim1Name + "sim2:" + sim2Name);
            if (simState_before.sim1State == TelephonyManager.SIM_STATE_READY) {
                SimSignalInfo sim1Info = getSimNetInfo(1);
                String netType = sim1Info == null ? "NA" : sim1Info.mNetType;
                current_SimName_1 = (sim1Name.equals("") || sim1Name.isEmpty()) ? "卡1失败" : "卡1 " + netType;
            }
            if (simState_before.sim2State == TelephonyManager.SIM_STATE_READY) {
                SimSignalInfo sim2Info = getSimNetInfo(2);
                String netType = sim2Info == null ? "NA" : sim2Info.mNetType;
                current_SimName_2 = (sim2Name.equals("") || sim2Name.isEmpty()) ? "卡2失败" : "卡2 " + netType;
            }
        }
        return new SignNetWorkResult(current_SimName_1, current_SimName_2);
    }

    public String checkIsNet(Context context) {
        boolean check_isNet = Preference.getBoolean(context, "checkBox_isNet", true);
        Log.i(Constant.TAG, "check_isNet" + check_isNet);
        if (!check_isNet) {
            Log.i(Constant.TAG, "不检查isNet");
        }
        NetworkSwitchUtil.setWifiEnable(context, false);
        int defaultDataSubId = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            defaultDataSubId = NetworkSwitchUtil.getDefaultDataSubId(context);
        }
        Log.i(Constant.TAG, "default subId:" + defaultDataSubId);
        NetworkSwitchUtil.setDataEnable(context, defaultDataSubId, true);
        String current_isNetwork;
        if (NetworkSwitchUtil.waitForMobileNetwork(context, Constant.CHECK_WAIT_TIME / 2)) {
            NetworkSwitchUtil.sleep(5000);
            current_isNetwork = NetworkSwitchUtil.isNetworkAvailable(context) ? "通过" : "失败";
        } else {
            Log.i(Constant.TAG, "当前没有使用数据网络");
            current_isNetwork = "失败";
        }
        return current_isNetwork;
    }

    public SimSignalInfo getSimNetInfo(int simId) {
        SignalHelper helper = SignalHelper.getInstance(context);
        return helper == null ? null : helper.getSimSignalInfo(simId);
    }

    public TestResult getResult(Context context) {
        SimUtil.SimState simState = getSimStateBefore();
        waitForNetWorkOperator();
        ReadSimResult readSimResult = checkReadSim(simState);
        SignNetWorkResult signNetWorkResult = checkSignNetwork(simState);
        String isNetResult = checkIsNet(context);
        return new TestResult(readSimResult, signNetWorkResult, isNetResult);
    }

    public NetworkSwitchResult getNetworkSwitchResult(Context context) {
        TestResult testResult = getResult(context);
        int simId = Preference.getInt(context, "SimId", 0);
        int defaultDataSubId = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            defaultDataSubId = NetworkSwitchUtil.getDefaultDataSubId(context);
        }
        Log.i(Constant.TAG, "default subId:" + defaultDataSubId);
        if (simId == 0) simId = defaultDataSubId;
        String result = (testResult.signNetWorkResult.current_simName_1.equals("")
                || testResult.signNetWorkResult.current_simName_2.equals("")
                || testResult.readSimResult.sim2State_now.equals("卡2不识卡")
                || testResult.readSimResult.sim1State_now.equals("卡1不识卡")
                || testResult.isNetResult.equals("失败")) ? "失败" : "通过";
        SimSignalInfo simNetInfo = getSimNetInfo(simId);
        String SimId_String = Preference.getBoolean(context, "checkBox_Switch_Sim", true) ? "卡" + simId : "";
        String current_isSwitched = Preference.getString(context, "current_isSwitched", "NA");
        Preference.putString(context, "current_isSwitched", "NA");
        NetworkSwitchResult networkSwitchResult = new NetworkSwitchResult().setReadSim_1(testResult.readSimResult.sim1State_now).setReadSim_2(testResult.readSimResult.sim2State_now).setSignNetwork_1(testResult.signNetWorkResult.current_simName_1).setSignNetwork_2(testResult.signNetWorkResult.current_simName_2).setIsNet(SimId_String + testResult.isNetResult).setResult(result).setTest_time(TimeUtil.getTime("yyyy-MM-dd HH:mm:ss")).setIsSwitched(current_isSwitched);
        if (simNetInfo != null) {
            networkSwitchResult.setSimNetOperator(simNetInfo.mOperator).setSimLevel(simNetInfo.mLevel).setSimNetType(simNetInfo.mNetType).setSimSignal(simNetInfo.mSignal);
        }
        return networkSwitchResult;
    }

    public class TestResult {
        public ReadSimResult readSimResult;
        public SignNetWorkResult signNetWorkResult;
        public String isNetResult = "NA";

        public TestResult(ReadSimResult readSimResult, SignNetWorkResult signNetWorkResult, String isNetResult) {

            this.readSimResult = readSimResult;
            this.signNetWorkResult = signNetWorkResult;
            this.isNetResult = isNetResult;
        }
    }

    public class ReadSimResult {

        public String sim1State_now = "卡1NA";
        public String sim2State_now = "卡2NA";

        public ReadSimResult(String sim1State_now, String sim2State_now) {

            this.sim1State_now = sim1State_now;
            this.sim2State_now = sim2State_now;
        }
    }

    public class SignNetWorkResult {

        public String current_simName_1 = "卡1NA";
        public String current_simName_2 = "卡2NA";

        public SignNetWorkResult(String current_simName_1, String current_simName_2) {

            this.current_simName_1 = current_simName_1;
            this.current_simName_2 = current_simName_2;
        }
    }

    public static int getDefaultDataSubId() {
        try {
            Class<?> aClass = Class.forName("android.telephony.SubscriptionManager");
            Method getDefaultDataSubId = aClass.getMethod("getDefaultDataSubId");
            return (int) getDefaultDataSubId.invoke(null);
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return SubscriptionManager.getDefaultDataSubscriptionId();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return -1;
    }

    /**
     * 获取默认发短信的SIM卡
     *
     * @return
     */
    public static int getDefaultSmsSubId() {

        try {
            Class<?> aClass = Class.forName("android.telephony.SubscriptionManager");
            Method getDefaultDataSubId = aClass.getMethod("getDefaultSmsSubId");
            return (int) getDefaultDataSubId.invoke(null);
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return SubscriptionManager.getDefaultSmsSubscriptionId();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return -1;
    }
}
