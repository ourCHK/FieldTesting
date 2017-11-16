package com.gionee.autotest.field.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TimeUtils;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by viking on 11/14/17.
 *
 * service for signal monitor
 */

public class SignalMonitorService extends Service{

    private static final String SEPARATOR = "::" ;

    private static final int NOTIFICATION_ID = 111 ;

    private SignalHelper.SignalStateListener listener ;

    private long interval = -1 ;
    private Timer mTimer ;

    private SignalHelper.SimSignalInfo mSim1SignalInfo ;
    private SignalHelper.SimSignalInfo mSim2SignalInfo ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Constant.TAG, "enter SignalMonitorService onCreate") ;
        startForegroundService() ;
        fire() ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Constant.TAG, "enter SignalMonitorService onStartCommand") ;
        if (intent != null){
            //enable collecting data logic
            boolean isCollectingData = intent.getBooleanExtra(Constant.PREF_KEY_SIGNAL_DATA_COLLECT, false) ;
            if (isCollectingData){
                Log.i(Constant.TAG, "enter SignalMonitorService data collect") ;
                if (!Preference.getBoolean(getApplicationContext(), Constant.PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING)){
                    Log.i(Constant.TAG, "enter SignalMonitorService data collect thread start") ;
                    getArguments() ;
                    startCollectingData();
                    //set PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING to true
                    Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING, true) ;
                }
            }
            //disable collecting data logic
            boolean isDisableCollectingData = intent.getBooleanExtra(Constant.PREF_KEY_SIGNAL_DATA_DISCOLLECT, false) ;
            if (isDisableCollectingData){
                stopCollectingData();
                //set PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING to false
                Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_SIGNAL_DATA_COLLECT_RUNNING, false) ;
            }
        }
        return START_STICKY;
    }

    FileWriter fw = null ;
    BufferedWriter writer = null ;

    private void startCollectingData() {
        //make sure external storage usable
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constant.TAG, "external storage state not mounted...") ;
            return ;
        }
        //make sure signal directory exist
        File signal_dir = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.HOME, Constant.SIGNAL_DIR) ;
        if (!signal_dir.exists() && !signal_dir.mkdirs()){
            Log.i(Constant.TAG, "make signal directory failed...") ;
            return ;
        }
        File signal_data = new File(signal_dir, Constant.SIGNAL_DATA_NAME) ;
        if (signal_data.exists() && !signal_data.delete()){
            Log.i(Constant.TAG, "fail to delete exist signal data file...") ;
            return ;
        }
        try{
            if (!signal_data.createNewFile()){
                Log.i(Constant.TAG, "create signal data file fail...") ;
                return ;
            }
            fw = new FileWriter(signal_data, true) ;
            writer = new BufferedWriter(fw) ;
        }catch (IOException e){ e.printStackTrace();}

        if (mTimer != null){
            mTimer.cancel();
        }
        mTimer = new Timer() ;
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                String time = TimeUtil.getTime() ;
                if (mSim1SignalInfo != null && mSim1SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim0 fetched : " + mSim1SignalInfo.toString() ) ;
                    writeContentToFile(time, SignalHelper.SIM_CARD_0, mSim1SignalInfo) ;
                }
                if (mSim2SignalInfo != null && mSim2SignalInfo.mIsActive){
                    Log.i(Constant.TAG, "sim1 fetched : " + mSim2SignalInfo.toString() ) ;
                    writeContentToFile(time, SignalHelper.SIM_CARD_1, mSim2SignalInfo) ;
                }
            }
        } ;
        mTimer.schedule(mTask, interval * 1000, interval * 1000);
    }

    private void writeContentToFile(String time, @SignalHelper.SIMID int simId, SignalHelper.SimSignalInfo info){
        StringBuilder content = new StringBuilder() ;
        content.append(time) ;
        content.append(SEPARATOR) ;
        content.append(simId) ;
        content.append(SEPARATOR) ;
        content.append(info.mLevel) ;
        content.append(SEPARATOR) ;
        content.append(info.mNetType) ;
        content.append(SEPARATOR) ;
        content.append(info.mSignal) ;
        try {
            writer.write(content.toString());
            writer.newLine();
            writer.flush();
        }catch (IOException e){
            Log.i(Constant.TAG, "write sim0 info to file exception : " + e.getMessage()) ;
            e.printStackTrace();
        }
    }

    private void stopCollectingData(){
        //cancel Timer
        if (mTimer != null ){
            mTimer.cancel();
        }
        //close all stream
        if (fw != null){
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getArguments(){
        interval = Preference.getLong(this, Constant.PREF_KEY_SIGNAL_INTERVAL, 5) ;
        Log.i(Constant.TAG, "SignalMonitorService getArguments interval : " + interval) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopCollectingData();
        SignalHelper.getInstance(this).unregisterSimStateListener(listener);
//        SignalHelper.getInstance(this).destroy();
    }

    private void fire(){
        listener = new SignalHelper.SignalStateListener() {
            @Override
            public void onSimStateChanged(boolean sim1Exist, boolean sim2Exist) {
                Log.i(Constant.TAG, "onSimStateChanged : " + sim1Exist + " " + sim2Exist) ;
            }

            @Override
            public void onSignalStrengthsChanged(int simId, SignalHelper.SimSignalInfo signalInfo) {
                Log.i(Constant.TAG, "onSignalStrengthsChanged : simId " + simId + " " + signalInfo.toString()) ;
                if (simId == SignalHelper.SIM_CARD_0){
                    mSim1SignalInfo = signalInfo ;
                }else if (simId == SignalHelper.SIM_CARD_1){
                    mSim2SignalInfo = signalInfo ;
                }
            }
        } ;
        SignalHelper.getInstance(this).registerSimStateListener(listener);
    }

    private void startForegroundService(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(getString(R.string.signal_foreground_service_title));
        builder.setContentText(getString(R.string.signal_foreground_service_subtitle));
        startForeground(NOTIFICATION_ID, builder.build());
    }
}
