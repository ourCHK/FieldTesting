package com.gionee.autotest.field.util.call;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gionee.autotest.common.TimeUtil;


public class CallLogUtil {
    private Uri AUTHORITY_URI = Uri.parse("content://gionee.calllog");
    private Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "callsjoindataview");// calls join data view
    private boolean isCall = false;
    private MyListener myListener;
    private Context context;
    private CallLogUtil.CallLogListener logListener;
    private CallResult callBean = new CallResult();


    public void setCallLogListener(Context context, CallLogListener logListener) {
        this.context = context;
        this.logListener = logListener;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        myListener = new CallLogUtil.MyListener();
        tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
        context.getContentResolver().registerContentObserver(CONTENT_URI, false, contentObserver);
    }

    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!isCall) return;
            callBean.setHangUpTime(TimeUtil.getTime());
            if (logListener != null) {
                CallLogBean lastCallLog = getLastCallLog(context);
                callBean.setResult(lastCallLog.duration > 0 && lastCallLog.type == CallLog.Calls.OUTGOING_TYPE);
                logListener.onChanged(callBean.clone());
                callBean = new CallResult();
            }
        }
    };
    @SuppressLint("MissingPermission")
    public void start(Context context, String number) {
        Log.i("gionee.os.autotest", "拨号=" + number);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callBean = new CallResult();
        try {
            callBean.setNumber(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        callBean.setDialTime(TimeUtil.getTime());
    }

    public void cancelListen(Context context) {
        if (contentObserver != null) {
            context.getContentResolver().unregisterContentObserver(contentObserver);
        }
        if (myListener != null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(myListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public static CallLogBean getLastCallLog(Context context) {
        @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[]{CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DURATION// 通话时长
                        , CallLog.Calls.TYPE}// 通话类型
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        if (cursor == null) {
            return new CallLogBean();
        }
        cursor.moveToNext();
        String number = cursor.getString(0);
        int duration = cursor.getInt(1);
        int type = cursor.getInt(2);
        Log.i("gionee.os.autotest", "号码=" + number + "时长=" + duration + "类型=" + type);
        return new CallLogBean(number, duration, type);
    }

    private class MyListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isCall = true;
                    callBean.setOffHookTime(TimeUtil.getTime());
                    callBean.setNumber(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    break;

                default:
                    break;
            }
        }
    }

    public static class CallLogBean {
        public String number = "";
        public int duration = 0;
        public int type = 2;

        public CallLogBean() {
            this("", 0, 2);
        }

        public CallLogBean(String number, int duration, int type) {
            this.number = number;
            this.duration = duration;
            this.type = type;
        }
    }

    public interface CallLogListener {
        void onChanged(CallResult lastCallLog);
    }
}
