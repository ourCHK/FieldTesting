package com.gionee.autotest.field.util.call;

import android.os.Build;

import com.gionee.autotest.field.util.filterlog.FilterLogTask;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DisConnectCallFilter extends FilterLogTask {
    private int t = 0;
    private DisConnectInfo info;
    private Consumer<DisConnectInfo> c;

    public DisConnectCallFilter(Consumer<DisConnectInfo> c) {
        this.c = c;
    }

    @Override
    protected boolean isContainNeed(String s) {
        if (s.contains("Telecom : Adapter: disconnect call DisconnectCause")) {
            t = 0;
            return true;
        } else if (s.contains("Telecom : CallLogManager: Logging Calllog entry")) {
            t = 1;
            return true;
        }
        return false;
    }

    @Override
    protected void operationFilterLog(String s) {
        if (t == 0) {
            Pattern p = Pattern.compile("Code: \\((LOCAL|REMOTE|BUSY|ERROE)\\)");
            Matcher m = p.matcher(s);
            if (m.find()) {
                String group = m.group();
                info = new DisConnectInfo();
                info.setCode(DisConnectInfo.StringToCode(group));
            }
        } else if (t == 1) {
            if (s.contains(",")) {
                String[] split = s.split(",");
                long callTimeStart = Long.parseLong(split[5]);
                long duration = Long.parseLong(split[6]);
                int type = Integer.parseInt(split[4]);
                String number = split[2];
                info.setNumber(number).setType(type).setCallTimeStart(callTimeStart).setCallDuration(duration);
                if (c != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            c.accept(info.clone());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}