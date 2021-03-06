package com.gionee.autotest.field.util.call;

import android.os.Build;
import android.util.Log;

import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.filterlog.FilterLogTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DisConnectCallFilter extends FilterLogTask {
    private int t = 0;
    private DisConnectInfo info;
    private DisConnectListener listener;

    public DisConnectCallFilter(DisConnectListener listener) {
        this.listener = listener;
    }

    @Override
    protected boolean isContainNeed(String s) {
        if (s.contains("Telecom : Adapter: disconnect call DisconnectCause")) {
            t = 0;
            Log.i(Constant.TAG,"出现DisconnectCause");
            return true;
        } else if (s.contains("Telecom : CallLogManager: Logging Calllog entry")) {
            t = 1;
            Log.i(Constant.TAG,"出现entry");
            return true;
        }
        return false;
    }

    @Override
    protected void operationFilterLog(String s) {
        Log.i(Constant.TAG,"operationFilterLog");
        if (t == 0) {
            Log.i(Constant.TAG,"t=0 ");
            Pattern p = Pattern.compile("Code: \\((LOCAL|REMOTE|BUSY|ERROE)\\)");
            Matcher m = p.matcher(s);
            if (m.find()) {
                String group = m.group();
                info = new DisConnectInfo();
                info.setCode(DisConnectInfo.StringToCode(group));
            }
        } else if (t == 1) {
            Log.i(Constant.TAG,"t=1 ");
            if (s.contains(",")) {
                String[] split = s.split(",");
                long callTimeStart = Long.parseLong(split[5].trim());
                long duration = Long.parseLong(split[6].trim());
                int type = Integer.parseInt(split[4].trim());
                String number = split[2].trim();
                if (info==null)info=new DisConnectInfo();
                info.setNumber(number).setType(type).setCallTimeStart(callTimeStart).setCallDuration(duration);
                if (listener != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            listener.onChanged(info.clone());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}