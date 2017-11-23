package com.gionee.autotest.field.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import static android.content.ContentValues.TAG;

public class WakeHelper {
    private        PowerManager                 mPm;
    private        KeyguardManager              mKeyguardManager;
    private        PowerManager.WakeLock        mScreenOnWakeLock;
    private static KeyguardManager.KeyguardLock mKeyguardLock;

    public WakeHelper(Context context) {
        mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mPm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mScreenOnWakeLock = mPm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("disableKeyguard");
        mScreenOnWakeLock.setReferenceCounted(false);
    }

    public PowerManager getManager() {
        return mPm;
    }

    public KeyguardManager.KeyguardLock getKeyguardLock() {
        return mKeyguardLock;
    }

    public PowerManager.WakeLock getWakeLock() {
        return mScreenOnWakeLock;
    }

}
