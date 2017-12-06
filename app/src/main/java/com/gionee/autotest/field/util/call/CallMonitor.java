package com.gionee.autotest.field.util.call;


import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.common.call.Instrument;
import com.gionee.autotest.field.util.Constant;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallMonitor extends PhoneStateListener {
    private Context mContext;
    private TelephonyManager mTm;
    private long lastTime_Coming = -1;
    private int testIndex = 0;
    private boolean isWaiting = false;
    private Timer timer = new Timer();
    private CallUtil callUtil;
    private CallMonitorParam params;
    private MonitorListener monitorListener;
    private CallMonitorResult result;
    private Uri AUTHORITY_URI = Uri.parse("content://gionee.calllog");
    private Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "callsjoindataview");// calls join data view


    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            CallLogUtil.CallLogBean lastCallLog = CallLogUtil.getLastCallLog(mContext);
            result.setResult(lastCallLog.duration > 0 && lastCallLog.type == CallLog.Calls.INCOMING_TYPE);
            result.setTime(TimeUtil.getTime());
            if (monitorListener != null) {
                Log.i(Constant.TAG, "通话记录添加" + result.toString());
                monitorListener.onChanged(result.clone());
                result = new CallMonitorResult();
            }
            testIndex++;
        }
    };
    private boolean run = false;

    public CallMonitor(Context mContext, CallMonitorParam params) {
        this.mContext = mContext;
        this.params = params;
        callUtil = new CallUtil(mContext);
    }

    public void setMonitorListener(MonitorListener monitorListener) {
        this.monitorListener = monitorListener;
    }

    public void startMonitor() {
        run = true;
        Log.i(Constant.TAG, "开始监听");
        getTelephonyManager().listen(this, PhoneStateListener.LISTEN_CALL_STATE);
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, false, contentObserver);
    }

    public void cancel() {
        run = false;
        Log.i(Constant.TAG, "取消监听");
        if (isWaiting) {
            try {
                timer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            mContext.getContentResolver().unregisterContentObserver(contentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getTelephonyManager().listen(this, PhoneStateListener.LISTEN_NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TelephonyManager getTelephonyManager() {
        if (mTm == null) {
            mTm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        }
        return mTm;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(Constant.TAG, "来电:" + incomingNumber);
                try {
                    result = new CallMonitorResult();
                    result.setIndex(testIndex).setNumber(incomingNumber).setRingingTime(TimeUtil.getTime());
                    if (params.isAutoReject && run) {
                        endCall(params.autoRejectTime);
                    } else {
                        if (params.isAutoAnswer && run) {
                            new AutoAnswerThread(mContext).start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(Constant.TAG, "接通了:" + incomingNumber);
                result.setOffHookTime(TimeUtil.getTime());
                if (params.isAnswerHangup&&run) endCall(params.answerHangUptime);
                break;

            default:
                break;
        }
    }


    private void endCall(int timeout) {
        timer = new Timer();
        isWaiting = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Log.i(Constant.TAG, "操作挂断");
                    callUtil.getITelephony().endCall();
                    result.setHangUpTime(TimeUtil.getTime());
                    Instrument.clickKey(KeyEvent.KEYCODE_POWER);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                isWaiting = false;
            }
        }, timeout * 1000);
    }

    public interface MonitorListener {

        void onChanged(CallMonitorResult testData);
    }

}
