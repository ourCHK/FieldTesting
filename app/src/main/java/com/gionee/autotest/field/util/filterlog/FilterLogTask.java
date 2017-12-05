package com.gionee.autotest.field.util.filterlog;

import android.os.AsyncTask;
import android.util.Log;

import com.gionee.autotest.field.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public abstract class FilterLogTask extends AsyncTask<Void, String, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        Process process = null;
        BufferedReader reader = null;
        boolean ok = true;
        try {
            RuntimeHelper.clearLogcatBuffer();
            process = LogcatHelper.getLogcatProcess(LogcatHelper.BUFFER_MAIN);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);
            Log.i(Constant.TAG, "开始记录Log");
            while (!isCancelled()) {
                final String line = reader.readLine();
                if (isContainNeed(line)) {
                    operationFilterLog(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            ok = false;
        } finally {
            if (process != null) {
                RuntimeHelper.destroy(process);
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ok;
    }


    protected abstract boolean isContainNeed(String s);

    protected abstract void operationFilterLog(String s);
}
