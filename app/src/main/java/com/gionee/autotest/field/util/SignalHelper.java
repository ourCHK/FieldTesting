package com.gionee.autotest.field.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.IntDef;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gionee.autotest.common.FLog;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viking on 11/13/17.
 *
 * Use this class to obtain signal information
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public final class SignalHelper {

    public static final int                        SIM_CARD_0  = 0 ;
    public static final int                        SIM_CARD_1  = 1 ;

    private WeakReference<Context>                  mContext ;
    private TelephonyManager                        mTelephonyManager;
    private SubscriptionManager                     mSubscriptionManager;

    private SimStateReceiver                        mSimStateReceiver;

    private int                                     mSubId0 = -1 ;
    private int                                     mSubId1 = -1 ;

    private Sim1SignalStrengthsListener             mSim1SignalListener ;
    private Sim2SignalStrengthsListener             mSim2SignalListener ;

    private SimSignalInfo                           mSim1SignalInfo ;
    private SimSignalInfo                           mSim2SignalInfo ;

    private List<SignalStateListener>               mSignalStateListeners ;

    private SignalHelper(Context context){
        Log.i(Constant.TAG, "enter SignalHelper...") ;
        this.mContext               = new WeakReference<>(context) ;
        mSim1SignalInfo             = new SimSignalInfo() ;
        mSim2SignalInfo             = new SimSignalInfo() ;
        mSignalStateListeners       = new ArrayList<>() ;
        mSubscriptionManager        = SubscriptionManager.from(context);
        mTelephonyManager           = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        initSIMListeners() ;
        mSimStateReceiver           = new SimStateReceiver();
        IntentFilter intentFilter   = new IntentFilter();
        intentFilter.addAction(SimStateReceiver.ACTION_SIM_STATE_CHANGED);
        if (mContext.get() != null){
            Log.i(Constant.TAG, "register mSimStateReceiver") ;
            mContext.get().registerReceiver(mSimStateReceiver, intentFilter);
        }
    }

    private String getSimOperator(int simId){
        String operator = "N/A" ;
        try {
            Method method   = TelephonyManager.class.getMethod("getSimOperatorName", int.class);
            operator =  (String) method.invoke(mTelephonyManager, simId);
            if (operator != null && operator.equals("CMCC")){
                operator = "中国移动" ;
            }
            if (operator == null || "".equals(operator)){
                operator = "N/A" ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return operator ;
    }

    private static volatile SignalHelper sInstance ;

    /*************************************************************************************
     *
     * PUBLIC methods
     *
     *************************************************************************************/

    /**
     * use this interface to provider signal information
     */
    public interface SignalStateListener{

        void onSimStateChanged(boolean sim1Exist, boolean sim2Exist) ;

        void onSignalStrengthsChanged(int simId, SimSignalInfo signalInfo) ;
    }

    /**
     * Singleton SignalHelper instance
     * @return singleton instance
     */
    public static SignalHelper getInstance(Context context){
        Log.i(Constant.TAG, "enter getInstance") ;
        if (null == sInstance) {
            synchronized (SignalHelper.class) {
                if (null == sInstance) {
                    Log.i(Constant.TAG, "enter getInstance create new one...") ;
                    sInstance = new SignalHelper(context);
                }
            }
        }
        return sInstance;
    }

    @IntDef({SIM_CARD_0, SIM_CARD_1})
    @Retention(RetentionPolicy.SOURCE)
    @interface SIMID{}

    public SimSignalInfo getSimSignalInfo(@SIMID int subId){
        if (subId == mSubId0){
            return mSim1SignalInfo ;
        }else if (subId == mSubId1){
            return mSim2SignalInfo ;
        }
        return null ;
    }

    public boolean isSimExist(@SIMID int simId){
        if (simId == SIM_CARD_0){
            return mSim1SignalInfo != null && mSim1SignalInfo.mIsActive ;
        }
        return mSim2SignalInfo != null && mSim2SignalInfo.mIsActive ;
    }

    /**
     * release all resources when all done
     */
    public void destroy(){
        Log.i(Constant.TAG, "destroy...") ;
        //if sim1 signal listener not null, unregister it
        if (null != mSim1SignalListener) {
            Log.i(Constant.TAG, "destroy unregister mSim1SignalListener...") ;
            mTelephonyManager.listen(mSim1SignalListener, PhoneStateListener.LISTEN_NONE);
            mSim1SignalListener = null ;
        }

        //if sim1 signal listener not null, unregister it
        if (null != mSim2SignalListener) {
            Log.i(Constant.TAG, "destroy unregister mSim2SignalListener...") ;
            mTelephonyManager.listen(mSim2SignalListener, PhoneStateListener.LISTEN_NONE);
            mSim2SignalListener = null ;
        }

        // if sim state receiver not null, unregister it
        if (mSimStateReceiver != null && mContext.get() != null){
            Log.i(Constant.TAG, "destroy unregister mSimStateReceiver...") ;
            try {
                mContext.get().unregisterReceiver(mSimStateReceiver);
            }catch (Exception e){}
            mSimStateReceiver = null ;
        }

        //clear sim state listeners
        if (mSignalStateListeners != null){
            Log.i(Constant.TAG, "destroy clear mSignalStateListeners...") ;
            mSignalStateListeners.clear();
            mSignalStateListeners = null ;
        }
        //set instance to null
        Log.i(Constant.TAG, "destroy set sInstance to null...") ;
        sInstance = null ;
    }

    public void registerSimStateListener(SignalStateListener listener){
        Log.i(Constant.TAG, "registerSimStateListener" );
        if (listener == null) return ;
        Log.i(Constant.TAG, "registerSimStateListener not null" );
        if (mSignalStateListeners.contains(listener)) return ;
        Log.i(Constant.TAG, "registerSimStateListener not contained" );
        mSignalStateListeners.add(listener) ;
        Log.i(Constant.TAG, "registerSimStateListener added" );
    }

    public void unregisterSimStateListener(SignalStateListener listener){
        Log.i(Constant.TAG, "unregisterSimStateListener" );
        if (listener == null) return ;
        Log.i(Constant.TAG, "unregisterSimStateListener not null" );
        if (!mSignalStateListeners.contains(listener)) return ;
        Log.i(Constant.TAG, "unregisterSimStateListener contained" );
        mSignalStateListeners.remove(listener) ;
        Log.i(Constant.TAG, "unregisterSimStateListener removed" );
    }

    /*************************************************************************************
     *
     * Inner methods
     *
     *************************************************************************************/

    private void initSIMListeners(){
        Log.i(Constant.TAG, "init sim listeners...") ;
        registerSimSignalStrengths(SIM_CARD_0);
        registerSimSignalStrengths(SIM_CARD_1);
    }

    private void registerSimSignalStrengths(int simId) {
        Log.i(Constant.TAG, "registerSimSignalStrengths : " + simId) ;
        if (simId == SIM_CARD_0) {
            SubscriptionInfo sub0 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(SIM_CARD_0);
            if (sub0 != null && null == mSim1SignalListener) {
                mSubId0 = sub0.getSubscriptionId();
                mSim1SignalListener = new Sim1SignalStrengthsListener(mSubId0);
                Log.i(Constant.TAG, "init sim listeners mSubId0 : " + mSubId0) ;
            }
            mTelephonyManager.listen(mSim1SignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } else if (simId == SIM_CARD_1) {
            SubscriptionInfo sub1 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(SIM_CARD_1);
            if (sub1 != null && null == mSim2SignalListener) {
                mSubId1 = sub1.getSubscriptionId();
                mSim2SignalListener = new Sim2SignalStrengthsListener(mSubId1);
                Log.i(Constant.TAG, "init sim listeners mSubId1 : " + mSubId1) ;
            }
            mTelephonyManager.listen(mSim2SignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    private void unregisterSimSignalStrengths(int simId) {
        Log.i(Constant.TAG, "unregisterSimSignalStrengths : " + simId) ;
        if (simId == SIM_CARD_0) {
            mSim1SignalInfo.mIsActive = false;
            mSim1SignalInfo.mLevel = 0;
            if (null != mSim1SignalListener) {
                mTelephonyManager.listen(mSim1SignalListener, PhoneStateListener.LISTEN_NONE);
            }
        } else if (simId == SIM_CARD_1) {
            mSim2SignalInfo.mIsActive = false;
            mSim2SignalInfo.mLevel = 0;
            if (null != mSim2SignalListener) {
                mTelephonyManager.listen(mSim2SignalListener, PhoneStateListener.LISTEN_NONE);
            }
        }
    }

    private class Sim1SignalStrengthsListener extends PhoneStateListener {

        Sim1SignalStrengthsListener(int subId) {
            super();
            setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.i(Constant.TAG, "sim1 signal strengths changed...") ;
            mSim1SignalInfo.mSignal = String.valueOf(getSignal(signalStrength, SIM_CARD_0));
            mSim1SignalInfo.mNetType = getNetTypeName(getNetworkType(mSubId0));
            mSim1SignalInfo.mLevel = getSignalStrengthsLevel(signalStrength);
            mSim1SignalInfo.mOperator = getSimOperator(mSubId0) ;
            notifySignalChanged(SIM_CARD_0, mSim1SignalInfo);
        }
    }

    private class Sim2SignalStrengthsListener extends PhoneStateListener {

        Sim2SignalStrengthsListener(int subId) {
            super();
            setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Log.i(Constant.TAG, "sim2 signal strengths changed...") ;
            mSim2SignalInfo.mSignal = String.valueOf(getSignal(signalStrength, SIM_CARD_1));
            mSim2SignalInfo.mNetType = getNetTypeName(getNetworkType(mSubId1));
            mSim2SignalInfo.mLevel = getSignalStrengthsLevel(signalStrength);
            mSim2SignalInfo.mOperator = getSimOperator(mSubId1) ;
            notifySignalChanged(SIM_CARD_1, mSim2SignalInfo);
        }
    }

    class SimStateReceiver extends BroadcastReceiver {
        private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SIM_STATE_CHANGED.equals(intent.getAction())) {
                Log.i(Constant.TAG, "SimStateReceiver state changed...") ;
                mSim1SignalInfo.mIsActive = isSimCardExist(SIM_CARD_0)
                        && null != mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(SIM_CARD_0);
                mSim2SignalInfo.mIsActive = isSimCardExist(SIM_CARD_1)
                        && null != mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(SIM_CARD_1);

                mSim1SignalInfo.mLevel = 0;
                mSim2SignalInfo.mLevel = 0;
                mSim1SignalInfo.mNetType = getNetTypeName(getNetworkType(mSubId0));
                mSim2SignalInfo.mNetType = getNetTypeName(getNetworkType(mSubId1));
                mSim1SignalInfo.mOperator = getSimOperator(mSubId0) ;
                mSim2SignalInfo.mOperator = getSimOperator(mSubId1) ;

                if (mSim1SignalInfo.mIsActive) {
                    registerSimSignalStrengths(SIM_CARD_0);
                } else {
                    unregisterSimSignalStrengths(SIM_CARD_0);
                    mSim1SignalInfo.mSignal = "0";
                    mSim1SignalInfo.mNetType = "N/A";
                }
                if (mSim2SignalInfo.mIsActive) {
                    registerSimSignalStrengths(SIM_CARD_1);
                } else {
                    unregisterSimSignalStrengths(SIM_CARD_1);
                    mSim2SignalInfo.mSignal = "0";
                    mSim2SignalInfo.mNetType = "N/A";
                }
                notifyStateChanged(mSim1SignalInfo.mIsActive, mSim2SignalInfo.mIsActive);
            }
        }
    }

    private void notifyStateChanged(boolean isSim1Exist, boolean isSim2Exist) {
        if (null != mSignalStateListeners && !mSignalStateListeners.isEmpty()) {
            for (int i = 0; i < mSignalStateListeners.size(); i++) {
                SignalStateListener listener = mSignalStateListeners.get(i);
                if (null != listener) {
                    listener.onSimStateChanged(isSim1Exist, isSim2Exist);
                }
            }
        }
    }

    private void notifySignalChanged(int simId, SimSignalInfo signalInfo) {
        if (null != mSignalStateListeners && !mSignalStateListeners.isEmpty()) {
            for (int i = 0; i < mSignalStateListeners.size(); i++) {
                SignalStateListener listener = mSignalStateListeners.get(i);
                if (null != listener) {
                    listener.onSignalStrengthsChanged(simId, signalInfo);
                }
            }
        }
    }

    /********************************************************************
     *
     * Utility methods for signal information
     *
     ********************************************************************/

    private boolean isSimCardExist(int simId) {
        boolean isSimCardExist = false;
        try {
            Method method   = TelephonyManager.class.getMethod("getSimState", int.class);
            int    simState = (Integer) method.invoke(mTelephonyManager, simId);
            if (TelephonyManager.SIM_STATE_READY == simState) {
                isSimCardExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSimCardExist;
    }

    private int getSignalStrengthsLevel(SignalStrength signalStrength) {
        int level = -1;
        try {
            Method levelMethod = SignalStrength.class.getDeclaredMethod("getLevel");
            level = (int) levelMethod.invoke(signalStrength);
        } catch (Exception e) {
            FLog.i(e.toString());
        }
        return level;
    }

    private int getNetworkType(int simId){
        int type = -1 ;
        try {
            Method method   = TelephonyManager.class.getMethod("getNetworkType", int.class);
            return (int) method.invoke(mTelephonyManager, simId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return type ;
    }

    private static final String GN5001S = "GN5001S";

    private int getSignal(SignalStrength signalStrength, int simId){
        int    signal;
        String operator = getOperator().trim();
        if (operator.contains("中国电信") && operator.contains(",")
                && android.os.Build.MODEL.equals(GN5001S)){
            String[] operators = operator.split(",");
            if (operators[simId].contains("中国电信")){
                signal = getLteDbm(signalStrength);
            }else{
                signal = getDbm(signalStrength);
            }
        }else if (operator.contains("中国电信") && !operator.contains(",")
                && android.os.Build.MODEL.equals(GN5001S)){
            signal = getLteDbm(signalStrength);
        }else{
            signal = getDbm(signalStrength);
        }
        return signal;
    }

    private int getLteDbm(SignalStrength signalStrength){
        int signal = -1 ;
        try {
            @SuppressLint("PrivateApi") Method levelMethod = SignalStrength.class.getDeclaredMethod("getLteDbm");
            signal = (int) levelMethod.invoke(signalStrength);
        }catch (Exception e){
            e.printStackTrace();
        }
        return signal ;
    }

    private int getDbm(SignalStrength signalStrength){
        int signal = -1 ;
        try {
            @SuppressLint("PrivateApi") Method levelMethod = SignalStrength.class.getDeclaredMethod("getDbm");
            signal = (int) levelMethod.invoke(signalStrength);
        }catch (Exception e){
            e.printStackTrace();
        }
        return signal ;
    }

    private String getNetTypeName(int netType){
        Log.i(Constant.TAG, "getNetTypeName : " + netType) ;
        String netTypeName ;
        switch(netType){
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                netTypeName = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                netTypeName = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                netTypeName = "4G";
                break;
            default:
                netTypeName = "N/A";
        }
        return netTypeName ;
    }

    private String getOperator(){
        String value = "";
        try {
            @SuppressLint("PrivateApi") Class<?> c   = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            value = (String)(get.invoke(null, "gsm.sim.operator.alpha" ));
        } catch (Exception e) {
            e.printStackTrace();
            FLog.i(e.toString());
        }
        return value;
    }

    private void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Field getAccessibleField(final Object obj, final String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException("object can't be null");
        }

        if (fieldName == null || fieldName.length() <= 0) {
            throw new IllegalArgumentException("fieldName can't be blank");
        }

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                ||!Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
