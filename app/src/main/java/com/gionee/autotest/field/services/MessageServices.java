package com.gionee.autotest.field.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.FLog;
import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.MessageHelper;
import com.gionee.autotest.field.util.SignalHelper;
import com.gionee.autotest.field.util.SimUtil;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xhk on 2017/11/24.
 */

public class MessageServices extends Service {

    private static final String TAG = "MessageServices";
    private Receiverlistner receiverlistner;
    private Backlistner backlistner;

//    private PendingIntent sendIntent;
//    private PendingIntent backIntent;


    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播
        receiverlistner = new Receiverlistner();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Constant.SMS_SEND_ACTION);
        registerReceiver(receiverlistner, intentFilter1);

        backlistner = new Backlistner();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Constant.DELIVERED_SMS_ACTION);
        registerReceiver(backlistner, intentFilter2);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
            //获取报告名字
                String message_presentation_name = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                //创建测试报告
                MessageHelper.exportExcel(Constant.DIR_MESSAGE + message_presentation_name);
                Bundle bundle = intent.getExtras();
                int message_type = bundle.getInt("message_type");
                String phone = Preference.getString(getApplicationContext(), Constant.PREF_KEY_MESSAGE_PHONE, "");
                Log.i(TAG, "message_type = " + message_type + ",phone = " + phone);
                long current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);

                SmsManager sms = SmsManager.getDefault();
                // 创建ACTION常数的Intent，作为PendingIntent的参数
                Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);
                Intent deliverIntent = new Intent(Constant.DELIVERED_SMS_ACTION);

                SendIt.putExtra("message", current_cycle);
                deliverIntent.putExtra("message", current_cycle);

                Log.i(TAG, "current_cycle = " + current_cycle);
                // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
                PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycle, SendIt, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent backIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycle, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // 发送短信
                sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + current_cycle, SendPendIt, backIntent);

                Preference.putString(getApplicationContext(), Constant.MESSAGE_CENTEXT, phone + ",深圳市金立通信设备有限公司，" + current_cycle);
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    public class Receiverlistner extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取短信状态
            switch (getResultCode()) {
                // 短信发送成功
                case RESULT_OK:
                    long message = intent.getLongExtra("message", -1);
                    Log.i(TAG, "发送信息成功=" + message);
//                    Log.i(TAG, "发送信息成功");
//                    FLog.i("发送信息成功");
//                    Toast.makeText(context, "短信内容："+Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, ""), Toast.LENGTH_SHORT).show();
                    String message_presentation_name = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                    String string = Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, "");

                    int subId = SimUtil.getDefaultSmsSubId();
                    SimSignalInfo simSignalInfo = SignalHelper.getInstance(context).getSimSignalInfo(subId);
                    if (simSignalInfo != null) {
                        MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name), new String[]{MessageHelper.getTimeDatas(), string, "成功","失败", simSignalInfo.mOperator, simSignalInfo.mNetType, simSignalInfo.mLevel + "", simSignalInfo.mSignal});
                    }else{
                        MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name), new String[]{MessageHelper.getTimeDatas(), string, "成功","失败", null, null, null + "", null});
                    }

                    long current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                    long message_interval = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_INTERVAL, 1);
                    if (current_cycle == message_interval) {

                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);

//                     //发送测试完成广播
                        sendBroadcast(new Intent(Constant.MESSAGE_RECEIVER));

                    } else {

                        Preference.putLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, (current_cycle + 1));
                        long current_cycles = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                        String phone = Preference.getString(context, Constant.PREF_KEY_MESSAGE_PHONE, "");
                        SmsManager sms = SmsManager.getDefault();
                        // 创建ACTION常数的Intent，作为PendingIntent的参数
                        Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);
                        Intent deliverIntent = new Intent(Constant.DELIVERED_SMS_ACTION);
                        SendIt.putExtra("message", current_cycles);
                        deliverIntent.putExtra("message", current_cycles);

                        Log.i(TAG, "current_cycle = " + current_cycles);

                        // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
                        PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycles, SendIt, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent backIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycles, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        // 发送短信
                        sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + current_cycles, SendPendIt, backIntent);

                    }

                    break;
                // 短信发送不成功
                default:
//                    Toast.makeText(context, "短信发送失败:"+Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, ""), Toast.LENGTH_SHORT).show();
                    String message_presentation_name1 = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                    String string1 = Preference.getString(getApplicationContext(), Constant.MESSAGE_CENTEXT, "");
                    int subIds = SimUtil.getDefaultSmsSubId();
                    SimSignalInfo simSignalInfos = SignalHelper.getInstance(context).getSimSignalInfo(subIds);
                    if (simSignalInfos != null) {
                        MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name1), new String[]{MessageHelper.getTimeDatas(), string1, "失败","失败",simSignalInfos.mOperator, simSignalInfos.mNetType, simSignalInfos.mLevel + "", simSignalInfos.mSignal});
                    }else{
                        MessageHelper.addExcel(new File(Constant.DIR_MESSAGE + message_presentation_name1), new String[]{MessageHelper.getTimeDatas(), string1, "失败","失败", null, null, null + "", null});
                    }

                    long current_cycle1 = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                    long message_interval1 = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_INTERVAL, 1);
                    if (current_cycle1 == message_interval1) {

                        Preference.putBoolean(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);

//                      //发送测试完成广播
                        sendBroadcast(new Intent(Constant.MESSAGE_RECEIVER));

                    } else {

                        Preference.putLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, (current_cycle1 + 1));
                        long current_cycle1s = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_MESSAGE_DATA_COLLECT_CURRENT_CYCLE, 1);
                        String phone = Preference.getString(context, Constant.PREF_KEY_MESSAGE_PHONE, "");
                        SmsManager sms = SmsManager.getDefault();
                        // 创建ACTION常数的Intent，作为PendingIntent的参数
                        Intent SendIt = new Intent(Constant.SMS_SEND_ACTION);
                        Intent deliverIntent = new Intent(Constant.DELIVERED_SMS_ACTION);
                        SendIt.putExtra("message", current_cycle1s);
                        deliverIntent.putExtra("message", current_cycle1s);

                        Log.i(TAG, "current_cycle1 = " + current_cycle1s);

                        // 接收消息传送后的广播信息SendPendIt，作为信息发送sendTextMessage方法的sentIntent参数
                        PendingIntent SendPendIt = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycle1s, SendIt, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent backIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) current_cycle1s, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        // 发送短信
                        sms.sendTextMessage(phone, null, phone + ",深圳市金立通信设备有限公司，" + current_cycle1s, SendPendIt, backIntent);

                    }


                    break;

            }
        }
    }

    public class Backlistner extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // 获取短信状态
            switch (getResultCode()){
                case RESULT_OK:
                    String message_presentation_name1 = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");

                    long message = intent.getLongExtra("message", -1);
                    Log.i(TAG, "接受信息成功=" + message);
                    Log.i(TAG, "接受信息成功");
                    MessageHelper.modifyExcel(Constant.DIR_MESSAGE + message_presentation_name1,(int) message,"成功");
//                    Toast.makeText(context, "收信人已经成功接收", Toast.LENGTH_SHORT).show();

                    break;
                default:

                    String message_presentation_name11 = Preference.getString(getApplicationContext(), Constant.MESSAGE_PRESENTATION_NAME, "");
                    long message1 = intent.getLongExtra("message", -1);
                    Log.i(TAG, "接受信息失败=" + message1);
                    Log.i(TAG, "接受信息失败");
                    MessageHelper.modifyExcel(Constant.DIR_MESSAGE + message_presentation_name11,(int) message1,"失败");
                    Toast.makeText(context, "收信人失败", Toast.LENGTH_SHORT).show();

                    break;

            }

        }
    }


}
