package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Constant.CallLossRatioDB.CallLossRatioBatch;
import com.gionee.autotest.field.util.Constant.CallLossRatioDB.CallLossRatioData;

import java.util.ArrayList;

public class CallLossRatioDBHelper extends DBHelper {
    private Context mContext;

    CallLossRatioDBHelper(Context context) {
        super(context);
        this.mContext = context;
    }

    public long addBatch(CallParam params) {
        ContentValues cv = new ContentValues();
        cv.put(CallLossRatioBatch.NUMBERS, params.number);
        cv.put(CallLossRatioBatch.CYCLE, params.cycle);
        cv.put(CallLossRatioBatch.CALL_TIME, params.call_time);
        cv.put(CallLossRatioBatch.CALL_TIME_SUM, params.call_time_sum);
        cv.put(CallLossRatioBatch.GAP_TIME, params.gap_time);
        cv.put(CallLossRatioBatch.IS_SPEAKER_ON, params.is_speaker_on);
        return mDb.insert(CallLossRatioBatch.NAME, null, cv);
    }

    public long writeCallData(OutGoingCallResult result) {
        ContentValues cv = new ContentValues();
        cv.put(CallLossRatioData.BATCH_ID, result.batchId);
        cv.put(CallLossRatioData.CYCLE_INDEX, result.cycleIndex);
        cv.put(CallLossRatioData.NUMBER, result.number);
        cv.put(CallLossRatioData.DIAL_TIME, result.dialTime);
        cv.put(CallLossRatioData.OFF_HOOK_TIME, result.offHookTime);
        cv.put(CallLossRatioData.HANG_UP_TIME, result.hangUpTime);
        cv.put(CallLossRatioData.RESULT, result.result ? 1 : 0);
        cv.put(CallLossRatioData.TIME, result.time);
        cv.put(CallLossRatioData.IS_VERIFY, result.isVerify ? 1 : 0);
        cv.put(CallLossRatioData.SIM_NET_INFO, result.simNetInfo);
        cv.put(CallLossRatioData.CODE, result.code);
        Log.i(Constant.TAG, "writeCallData=" + result.toString());
        return mDb.insert(CallLossRatioData.NAME, null, cv);
    }

    public ArrayList getBatchs() {
        Cursor query = mDb.rawQuery("select * from " + CallLossRatioBatch.NAME, null);
        ArrayList batchs = new ArrayList<CallParam>();
        if (query.getCount() == 0) return batchs;
        while (query.moveToNext()) {
            try {
                long id = query.getLong(query.getColumnIndex(CallLossRatioBatch._ID));
                String numbers = query.getString(query.getColumnIndex(CallLossRatioBatch.NUMBERS));
                int cycle = query.getInt(query.getColumnIndex(CallLossRatioBatch.CYCLE));
                int call_time = query.getInt(query.getColumnIndex(CallLossRatioBatch.CALL_TIME));
                int call_time_sum = query.getInt(query.getColumnIndex(CallLossRatioBatch.CALL_TIME_SUM));
                int gap_time = query.getInt(query.getColumnIndex(CallLossRatioBatch.GAP_TIME));
                int is_speaker_on = query.getInt(query.getColumnIndex(CallLossRatioBatch.IS_SPEAKER_ON));
                int verify_count = query.getInt(query.getColumnIndex(CallLossRatioBatch.VERIFY_COUNT));
                batchs.add(new CallParam(id, numbers, new String[0], cycle, call_time, call_time_sum, gap_time, is_speaker_on == 1, verify_count));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batchs;
    }

    public int getLastBatch() {
        Cursor rawQuery = mDb.rawQuery("select * from " + CallLossRatioBatch.NAME + " where " + CallLossRatioBatch._ID + " in (select max(" + CallLossRatioBatch._ID + ") from " + CallLossRatioBatch.NAME + ")", null);
        int lastBatch = 0;
        while (rawQuery.moveToNext()) {
            try {
                lastBatch = rawQuery.getInt(rawQuery.getColumnIndex(CallLossRatioBatch._ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lastBatch;
    }

    public ArrayList<String> getAllBatch() {
        Cursor rawQuery = mDb.rawQuery("select " + CallLossRatioBatch._ID + " from " + CallLossRatioBatch.NAME, null);
        ArrayList<String> list = new ArrayList();
        while (rawQuery.moveToNext()) {
            try {
                int batch = rawQuery.getInt(rawQuery.getColumnIndex(CallLossRatioBatch._ID));
                list.add(batch + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public ArrayList<OutGoingCallResult> getCallBean(long batchId) {
        Cursor cursor = mDb.rawQuery("select * from " + CallLossRatioData.NAME + " where " + CallLossRatioData.BATCH_ID + " =" + batchId + "", null);
        ArrayList<OutGoingCallResult> calls = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            Log.i(Constant.TAG,"cursor is null");
            return calls;
        }
        while (cursor.moveToNext()) {
            try {
                int cycleIndex = cursor.getInt(cursor.getColumnIndex(CallLossRatioData.CYCLE_INDEX));
                String number = cursor.getString(cursor.getColumnIndex(CallLossRatioData.NUMBER));
                String dialTime = cursor.getString(cursor.getColumnIndex(CallLossRatioData.DIAL_TIME));
                String offHookTime = cursor.getString(cursor.getColumnIndex(CallLossRatioData.OFF_HOOK_TIME));
                String hangUpTime = cursor.getString(cursor.getColumnIndex(CallLossRatioData.HANG_UP_TIME));
                int result = cursor.getInt(cursor.getColumnIndex(CallLossRatioData.RESULT));
                int isVerify = cursor.getInt(cursor.getColumnIndex(CallLossRatioData.IS_VERIFY));
                String simNetInfo = cursor.getString(cursor.getColumnIndex(CallLossRatioData.SIM_NET_INFO));
                int code = cursor.getInt(cursor.getColumnIndex(CallLossRatioData.CODE));
                String time = cursor.getString(cursor.getColumnIndex(CallLossRatioData.TIME));
                calls.add(new OutGoingCallResult().setVerify(isVerify == 1).setBatchId(batchId).setCycleIndex(cycleIndex).setNumber(number).setDialTime(dialTime).setCode(code).
                        setOffHookTime(offHookTime).setHangUpTime(hangUpTime).setResult(result == 1).setTime(time).setSimNetInfo(simNetInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return calls;
    }

    public void deleteTable(String table_name) {
        try {
            mDb.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        try {
            mDb.delete(CallLossRatioBatch.NAME, null, null);
            mDb.delete(CallLossRatioData.NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
