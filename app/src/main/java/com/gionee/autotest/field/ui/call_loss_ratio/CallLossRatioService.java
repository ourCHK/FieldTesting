package com.gionee.autotest.field.ui.call_loss_ratio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.CallLossRatioDBManager;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.call.DisConnectCallFilter;
import com.gionee.autotest.field.util.call.DisConnectInfo;
import com.gionee.autotest.field.util.call.DisConnectListener;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;


public class CallLossRatioService extends Service implements DisConnectListener {

    private CallParam params;
    private DisConnectCallFilter filter;
    private Uri AUTHORITY_URI = Uri.parse("content://gionee.calllog");
    private Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "callsjoindataview");// calls join data view
    private DisConnectInfo info;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Timer timer = null;
    private TelephonyManager mTm = null;
    private int cycleIndex = 0;
    private int numberIndex = 0;
    private boolean isCalled = false;
    private MyListener myListener = null;
    private OutGoingCallResult callBean = new OutGoingCallResult();

    @SuppressLint("ServiceCast")
    @Override
    public void onCreate() {
        super.onCreate();
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myListener = new MyListener();
        startListener();
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent nfIntent = new Intent(this, CallLossRatioActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setContentTitle("呼损率")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("测试中")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(522, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String paramsJson = intent.getStringExtra("params");
        if (paramsJson != null) {
            cycleIndex = 0;
            this.params = new Gson().fromJson(paramsJson, CallParam.class);
            startCycle();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startCycle() {
        Log.i(Constant.TAG, "开始第" + (cycleIndex + 1) + "轮测试");
        numberIndex = 0;
        startCall();
    }

    private void startCall() {
        Log.i(Constant.TAG, "开始拨打第" + (numberIndex + 1) + "个号码");
        String currentNumber = params.numbers[numberIndex];
        callBean = new OutGoingCallResult().setNumber(currentNumber);
        try {
            dial(currentNumber);
            callBean.setDialTime(TimeUtil.getTime()).setBatchId(params.id).setCycleIndex(cycleIndex);
            info = new DisConnectInfo();
            Log.i(Constant.TAG, "拔号=" + TimeUtil.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        cycleIndex = 0;
        numberIndex = 0;
        try {
            if (timer != null) timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mTm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                new CallUtil(this).getITelephony().endCall();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cancelListener();
    }


    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    SystemClock.sleep(3000);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    callBean.setResult(info.code == -1);
                    callBean.setCode(info.code);
                    if (!callBean.result) {
                        callBean.setSimNetInfo(CallLossRatioUtil.getSimNetInfo(getApplicationContext()));
                    }
                    Log.i(Constant.TAG, "写入测试结果");
                    CallLossRatioDBManager.writeData(callBean);
                    goTest();
                }
            }.execute();
        }
    };

    private void dial(String number) {
        Log.i(Constant.TAG, "拨号" + number);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有拨号权限", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }

    public void goTest() {
        if (numberIndex < params.numbers.length - 1) {
            numberIndex++;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startCall();
                }

            }, 10 * 1000);
        } else {
            if (cycleIndex < params.cycle - 1) {
                cycleIndex++;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startCycle();
                    }
                }, params.call_time_sum * 1000L);
            } else {
                CallLossRatioUtil.isTest = false;
                Log.i(Constant.TAG, "测试完成，发送结束广播");
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("CallLossRatioUpdateViews"));
                stopSelf();
            }
        }
    }


    private void startListener() {
        getContentResolver().registerContentObserver(CONTENT_URI, false, contentObserver);
        filter = new DisConnectCallFilter(this);
        mTm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void cancelListener() {
        if (contentObserver != null) {
            getContentResolver().unregisterContentObserver(contentObserver);
        }
        if (filter != null) {
            filter.cancel(true);
        }
        if (myListener != null) {
            mTm.listen(myListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onChanged(DisConnectInfo info) {
        this.info = info;
        if (!CallLossRatioUtil.isTest || !isCalled) return;
        isCalled = false;
        callBean.setOffHookTime(TimeUtil.getTime(info.callTimeStart, "yyyy-MM-dd HH:mm:ss"));
        callBean.setHangUpTime(TimeUtil.getTime(info.callTimeStart + info.callDuration, "yyyy-MM-dd HH:mm:ss"));
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (!CallLossRatioUtil.isTest) return;
            Log.i(Constant.TAG, "state=" + state);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(Constant.TAG, "state_ringing");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(Constant.TAG, "state_offHook");
//                    isCalled = true;
//                    callBean.offHookTime = TimeUtil.getTime();
//                    Log.i(Constant.TAG, "接通=" + TimeUtil.getTime() + ",号码=" + incomingNumber);
//                    AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    if (mAudioManager != null) {
//                        mAudioManager.setSpeakerphoneOn(params.is_speaker_on);
//                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(Constant.TAG, "state_idle");
                    break;
            }
        }

    }
}
