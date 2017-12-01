package com.gionee.autotest.field.util.call;

import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.gionee.autotest.common.call.Instrument;


public class AutoAnswerThread extends Thread {
    private final Context context;

    public AutoAnswerThread(Context context) {
        super();
        this.context = context;
    }

    public void run() {
        DisplayMetrics dm     = getDisplayMetrics(this.context);
        int            width  = dm.widthPixels;
        int            height = dm.heightPixels;
        SystemClock.sleep(4500L);
        Instrument.click((float) (481 * width / 720), (float) (143 * height / 1280));
        SystemClock.sleep(1500L);
        Log.i("ActivityThread", "swipe to the right");
        Instrument.swipe((float) (379 * width / 720), (float) (958 * height / 1280), (float) (591 * width / 720), (float) (958 * height / 1280), 1);
        Log.i("ActivityThread", "AutoAnswer start----");
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager  mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = null;
        try {
            Display defaultDisplay = mWindowManager.getDefaultDisplay();
            displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return displayMetrics;
    }

}
