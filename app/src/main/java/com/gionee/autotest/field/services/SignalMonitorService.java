package com.gionee.autotest.field.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.main.MainActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

/**
 * Created by viking on 11/14/17.
 *
 * service for signal monitor
 */

public class SignalMonitorService extends Service{

    private static final int NOTIFICATION_ID = 111 ;

    private SignalHelper.SignalStateListener listener ;

    private long interval = -1 ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getArguments() ;
        fire() ;

        return START_STICKY;
    }

    private void getArguments(){
        startForegroundService() ;
        interval = Preference.getLong(this, Constant.PREF_KEY_SIGNAL_INTERVAL, 5) ;
        Log.i(Constant.TAG, "SignalMonitorService getArguments interval : " + interval) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        SignalHelper.getInstance(this).unregisterSimStateListener(listener);
        SignalHelper.getInstance(this).destroy();
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
            }
        } ;
        SignalHelper.getInstance(this).registerSimStateListener(listener);
        //start a new thread
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
