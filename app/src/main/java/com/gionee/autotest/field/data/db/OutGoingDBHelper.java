package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Constant.OutGoingDB.OutGoingBatch;
import com.gionee.autotest.field.util.Constant.OutGoingDB.OutGoingData;

import java.util.ArrayList;

public class OutGoingDBHelper extends DBHelper {
    private Context mContext;

    OutGoingDBHelper(Context context) {
        super(context);
        this.mContext = context;
    }

    public long addBatch(CallParam params) {
        ContentValues cv = new ContentValues();
        cv.put(OutGoingBatch.NUMBERS, params.number);
        cv.put(OutGoingBatch.CYCLE, params.cycle);
        cv.put(OutGoingBatch.CALL_TIME, params.call_time);
        cv.put(OutGoingBatch.CALL_TIME_SUM, params.call_time_sum);
        cv.put(OutGoingBatch.GAP_TIME, params.gap_time);
        cv.put(OutGoingBatch.IS_SPEAKER_ON, params.is_speaker_on);
        return mDb.insert(OutGoingBatch.NAME, null, cv);
    }

    public long writeCallData(OutGoingCallResult result) {
        ContentValues cv = new ContentValues();
        cv.put(OutGoingData.BATCH_ID, result.batchId);
        cv.put(OutGoingData.CYCLE_INDEX, result.cycleIndex);
        cv.put(OutGoingData.NUMBER, result.number);
        cv.put(OutGoingData.DIAL_TIME, result.dialTime);
        cv.put(OutGoingData.OFF_HOOK_TIME, result.offHookTime);
        cv.put(OutGoingData.HANG_UP_TIME, result.hangUpTime);
        cv.put(OutGoingData.RESULT, result.result ? 1 : 0);
        cv.put(OutGoingData.TIME, result.time);
        cv.put(OutGoingData.IS_VERIFY, result.isVerify?1:0);
        cv.put(OutGoingData.SIM_NET_INFO, result.simNetInfo);
        Log.i(Constant.TAG,"writeCallData="+result.toString());
        return mDb.insert(OutGoingData.NAME, null, cv);
    }

    public ArrayList getBatchs() {
        Cursor    query  = mDb.rawQuery("select * from " + OutGoingBatch.NAME, null);
        ArrayList batchs = new ArrayList<CallParam>();
        if (query.getCount() == 0) return batchs;
        while (query.moveToNext()) {
            try {
                long   id            = query.getLong(query.getColumnIndex(OutGoingBatch._ID));
                String numbers       = query.getString(query.getColumnIndex(OutGoingBatch.NUMBERS));
                int    cycle         = query.getInt(query.getColumnIndex(OutGoingBatch.CYCLE));
                int    call_time     = query.getInt(query.getColumnIndex(OutGoingBatch.CALL_TIME));
                int    call_time_sum = query.getInt(query.getColumnIndex(OutGoingBatch.CALL_TIME_SUM));
                int    gap_time      = query.getInt(query.getColumnIndex(OutGoingBatch.GAP_TIME));
                int    is_speaker_on = query.getInt(query.getColumnIndex(OutGoingBatch.IS_SPEAKER_ON));
                int    verify_count = query.getInt(query.getColumnIndex(OutGoingBatch.VERIFY_COUNT));
                batchs.add(new CallParam(id, numbers, new String[0], cycle, call_time, call_time_sum, gap_time, is_speaker_on == 1,verify_count));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batchs;
    }

    public int getLastBatch() {
        Cursor rawQuery  = mDb.rawQuery("select * from " + OutGoingBatch.NAME + " where " + OutGoingBatch._ID + " in (select max(" + OutGoingBatch._ID + ") from " + OutGoingBatch.NAME + ")", null);
        int   lastBatch = 0;
        while (rawQuery.moveToNext()) {
            try {
                lastBatch = rawQuery.getInt(rawQuery.getColumnIndex(OutGoingBatch._ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lastBatch;
    }

    public ArrayList<String> getAllBatch() {
        Cursor    rawQuery = mDb.rawQuery("select " + OutGoingBatch._ID + " from " + OutGoingBatch.NAME, null);
        ArrayList<String> list     = new ArrayList();
        while (rawQuery.moveToNext()) {
            try {
                int batch = rawQuery.getInt(rawQuery.getColumnIndex(OutGoingBatch._ID));
                list.add(batch + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public ArrayList<OutGoingCallResult> getCallBean(long batchId) {
        Cursor    cursor = mDb.rawQuery("select * from " + OutGoingData.NAME + " where " + OutGoingData.BATCH_ID + " =" + batchId + "", null);
        ArrayList<OutGoingCallResult> calls  = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) return calls;
        while (cursor.moveToNext()) {
            try {
                int    cycleIndex  = cursor.getInt(cursor.getColumnIndex(OutGoingData.CYCLE_INDEX));
                String   number      = cursor.getString(cursor.getColumnIndex(OutGoingData.NUMBER));
                String dialTime    = cursor.getString(cursor.getColumnIndex(OutGoingData.DIAL_TIME));
                String offHookTime = cursor.getString(cursor.getColumnIndex(OutGoingData.OFF_HOOK_TIME));
                String hangUpTime  = cursor.getString(cursor.getColumnIndex(OutGoingData.HANG_UP_TIME));
                int    result      = cursor.getInt(cursor.getColumnIndex(OutGoingData.RESULT));
                int    isVerify      = cursor.getInt(cursor.getColumnIndex(OutGoingData.IS_VERIFY));
                String    simNetInfo      = cursor.getString(cursor.getColumnIndex(OutGoingData.SIM_NET_INFO));
                String time        = cursor.getString(cursor.getColumnIndex(OutGoingData.TIME));
                calls.add(new OutGoingCallResult().setVerify(isVerify==1).setBatchId(batchId).setCycleIndex(cycleIndex).setNumber(number).setDialTime(dialTime).
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
            mDb.delete(OutGoingBatch.NAME, null, null);
            mDb.delete(OutGoingData.NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
