package com.gionee.autotest.field.ui.network_switch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.field.services.NetworkSwitchService;
import com.gionee.autotest.field.ui.network_switch.NetworkSwitchActivity;
import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.util.Constant;
import com.google.gson.Gson;


public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constant.TAG, "onReceive");
        boolean isTest = Preference.getBoolean(context, "isTest", false);
        if (isTest) {
            Toast.makeText(context, "开机完成", Toast.LENGTH_SHORT).show();
            //拉起主界面activity
            Intent activity_intent = new Intent(context, NetworkSwitchActivity.class);
            activity_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activity_intent);
            //拉起测试报务
            Intent mIntent = new Intent(context, NetworkSwitchService.class);
            String lastParams = Preference.getString(context, "lastParams");
            mIntent.putExtra("params", lastParams);
            context.startService(mIntent);
        }
    }

}

