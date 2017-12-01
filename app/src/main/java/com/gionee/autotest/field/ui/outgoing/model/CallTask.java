package com.gionee.autotest.field.ui.outgoing.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.common.call.CallUtil;
import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.util.Constant;


public class CallTask extends AsyncTask<Void, Object, Void> {
    @SuppressLint("StaticFieldLeak")
    private       Context   mContext;
    private       CallParam param;
    private final CallUtil  callUtil;
    private       Callback  c;
    private int cycleIndex = 0;

    public CallTask(Context context, CallParam param, Callback c) {
        mContext = context;
        this.param = param;
        callUtil = new CallUtil(context);
        this.c = c;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (int i = 0; i < param.cycle; i++) {
                cycleIndex = i;
                long beginTime = System.currentTimeMillis();
                for (int j = 0; j < param.numbers.length; j++) {
                    if (j > 0) waitGapTime();
                    OutGoingCallResult result = call(j);
                    publishProgress(2, result);
                }
                waitUntilRoundFinish(beginTime);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OutGoingCallResult call(int callIndex) {
        OutGoingCallResult result = new OutGoingCallResult().setNumber((param.numbers[callIndex])).setCycleIndex(cycleIndex);
        try {
            callUtil.call(param.numbers[callIndex]);
            result.setDialTime(TimeUtil.getTime());
            Log.i(Constant.TAG, "拔号=" + TimeUtil.getTime());

            long wait_offHook_time = callUtil.waitOffHook();
            result.setOffHookTime(TimeUtil.getTime());
            Log.i(Constant.TAG, "接通=" + TimeUtil.getTime());

            callUtil.getAudioManager().setSpeakerphoneOn(param.is_speaker_on);

            waitHangUpTime(wait_offHook_time);
            result.setHangUpTime(TimeUtil.getTime());
            Log.i(Constant.TAG, "挂断=" + TimeUtil.getTime());

            try {
                callUtil.getITelephony().endCall();
                result.setResult(true);
            } catch (RemoteException e) {
                e.printStackTrace();
                result.setResult(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (values.length > 0 && values[0] instanceof Integer) {
            switch ((int) values[0]) {
                case 1:
                    if (values.length > 1) {
                        Toast.makeText(mContext, values[1].toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 2:
                    if (values.length > 1 && c != null) {
                        try {
                            c.call(param, (OutGoingCallResult) values[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (c != null) {
            c.finish(param);
        }
    }

    private void waitGapTime() {
        long targetTime = System.currentTimeMillis() + param.gap_time * 1000;
        while (System.currentTimeMillis() < targetTime) {
            SystemClock.sleep(10);
            if (!cancel()) {
                throw new RuntimeException("停止测试");
            }
        }
    }

    private void waitHangUpTime(long wait_offHook_time) {
        long targetTime = param.call_time * 1000 - wait_offHook_time + System.currentTimeMillis();
        while (System.currentTimeMillis() < targetTime) {
            SystemClock.sleep(1);
            if (!cancel()) {
                throw new RuntimeException("停止测试");
            }
        }
    }

    private void waitUntilRoundFinish(long beginTime) {
        long real_call_time = System.currentTimeMillis() - beginTime;
        long allGapTime     = param.gap_time * (param.numbers.length - 1) * 1000;
        long sleepTime      = param.call_time_sum * 1000 - real_call_time;
        Log.i(Constant.TAG, "call_time_sum:" + param.call_time_sum + "real_call_time:" + real_call_time + "gap_time:" + allGapTime + "sleepTime" + sleepTime);
        if (sleepTime > 0) {
            SystemClock.sleep(sleepTime);
        } else {
            publishProgress("1", "通话总时长小于每次通话时长总和");
        }
    }

    private boolean cancel() {
        return c != null && c.cancel();
    }

    public interface Callback {
        void call(CallParam param, OutGoingCallResult value);

        void finish(CallParam param);

        boolean cancel();
    }
}
