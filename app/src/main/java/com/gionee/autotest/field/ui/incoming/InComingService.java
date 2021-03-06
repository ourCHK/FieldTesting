package com.gionee.autotest.field.ui.incoming;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.incoming.model.InComingCall;
import com.gionee.autotest.field.ui.outgoing.OutGoingActivity;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;
import com.gionee.autotest.field.util.SimUtil;
import com.gionee.autotest.field.util.call.CallMonitor;
import com.gionee.autotest.field.util.call.CallMonitorParam;
import com.gionee.autotest.field.util.call.CallMonitorResult;
import com.google.gson.Gson;

import java.io.Serializable;

public class InComingService extends Service implements CallMonitor.MonitorListener {

    private long        batchId;
    private CallMonitor callMonitor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, SignalMonitorService.class));
        Notification.Builder builder  = new Notification.Builder(this.getApplicationContext());
        Intent               nfIntent = new Intent(this, InComingActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setContentTitle("监听来电")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("运行中")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(521, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (callMonitor != null) {
            callMonitor.cancel();
        }
        InComingCall.exportExcelFile(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        batchId = intent.getLongExtra("batchId", -1);
        CallMonitorParam params = (CallMonitorParam) intent.getSerializableExtra("params");
        if (batchId != -1 && params != null) {
            callMonitor = new CallMonitor(this, params);
            callMonitor.setMonitorListener(this);
            callMonitor.startMonitor();
        } else {
            Log.i(Constant.TAG, "参数获取失败=" + params + " batchID=" + batchId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChanged(CallMonitorResult callMonitorResult) {
        callMonitorResult.batchId = batchId;
        if (!callMonitorResult.result) {
            try {
                SimSignalInfo simSignalInfo = SignalHelper.getInstance(this).getSimSignalInfo(SimUtil.getDefaultDataSubId());
                String        simNetInfo    = new Gson().toJson(simSignalInfo);
                callMonitorResult.setFailMsg(simNetInfo);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(Constant.TAG, "通话记录变更后，写入结果失败");
            }
            Log.i(Constant.TAG, "写入结果成功");
        }
        InComingCall.writeData(callMonitorResult);
    }
}
