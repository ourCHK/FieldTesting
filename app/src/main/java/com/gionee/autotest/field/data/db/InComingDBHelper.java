package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingBatch;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingData;
import com.gionee.autotest.field.util.SimUtil;
import com.gionee.autotest.field.util.call.CallMonitorParam;
import com.gionee.autotest.field.util.call.CallMonitorResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

public class InComingDBHelper extends DBHelper {
    private Context mContext;

    InComingDBHelper(Context context) {
        super(context);
        this.mContext = context;
    }

    public long addBatch(CallMonitorParam params) {
        ContentValues cv = new ContentValues();
        cv.put(InComingBatch.AUTO_REJECT, params.isAutoReject ? 1 : 0);
        cv.put(InComingBatch.AUTO_REJECT_TIME, params.autoRejectTime);
        cv.put(InComingBatch.AUTO_ANSWER, params.isAutoAnswer ? 1 : 0);
        cv.put(InComingBatch.AUTO_ANSWER_HANGUP, params.isAnswerHangup ? 1 : 0);
        cv.put(InComingBatch.ANSWER_HANGUP_TIME, params.answerHangUptime);
        cv.put(InComingBatch.GAP_TIME, params.gapTime);
        cv.put(InComingBatch.IS_HANG_UP_PRESS_POWER, params.isAnswerHangup ? 1 : 0);
        return mDb.insert(InComingBatch.NAME, null, cv);
    }

    public synchronized void writeData(CallMonitorResult data) {
        ContentValues cv = new ContentValues();
        cv.put(InComingData.BATCH_ID, data.batchId);
        cv.put(InComingData.NUMBER, data.number);
        cv.put(InComingData.RESULT, data.result ? 1 : 0);
        cv.put(InComingData.TEST_INDEX, data.index);
        cv.put(InComingData.TIME, data.time);
        cv.put(InComingData.FAIL_MSG, data.failMsg);
        mDb.insert(InComingData.NAME, null, cv);
    }

    public InComingReportBean getReportBean(int batchId) {
        Cursor query = mDb.rawQuery("select * from " + InComingBatch.NAME + " where " + InComingBatch._ID + " =" + batchId, null);
        CallMonitorParam testParams = new CallMonitorParam();
        while (query.moveToNext()) {
            try {
                int isAutoReject = query.getInt(query.getColumnIndex(InComingBatch.AUTO_REJECT));
                int rejectTime = query.getInt(query.getColumnIndex(InComingBatch.AUTO_REJECT_TIME));
                int isAutoAnswer = query.getInt(query.getColumnIndex(InComingBatch.AUTO_ANSWER));
                int isAutoAnswerHangup = query.getInt(query.getColumnIndex(InComingBatch.AUTO_ANSWER_HANGUP));
                int answerHangupTime = query.getInt(query.getColumnIndex(InComingBatch.ANSWER_HANGUP_TIME));
                int gapTime = query.getInt(query.getColumnIndex(InComingBatch.GAP_TIME));
                int isHangUpPressPower = query.getInt(query.getColumnIndex(InComingBatch.IS_HANG_UP_PRESS_POWER));
                testParams = new CallMonitorParam(isAutoReject == 1, rejectTime, isAutoAnswer == 1, isAutoAnswerHangup == 1, answerHangupTime, gapTime, isHangUpPressPower == 1);
            } catch (Exception e) {
//                Log.i(e.toString());
            }
        }
        Gson gson = new Gson();
        Cursor cursor = mDb.rawQuery("select * from " + InComingData.NAME + " where " + InComingData.BATCH_ID + " =" + batchId, null);
        ArrayList list = new ArrayList<CallMonitorResult>();
        while (cursor.moveToNext()) {
            try {
                String number = cursor.getString(cursor.getColumnIndex(InComingData.NUMBER));
                int testIndex = cursor.getInt(cursor.getColumnIndex(InComingData.TEST_INDEX));
                int result = cursor.getInt(cursor.getColumnIndex(InComingData.RESULT));
                String time = cursor.getString(cursor.getColumnIndex(InComingData.TIME));
                String failMsg = cursor.getString(cursor.getColumnIndex(InComingData.FAIL_MSG));
                SimSignalInfo simSignalInfo = new SimSignalInfo();
                try {
                    simSignalInfo = gson.fromJson(failMsg, SimSignalInfo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                list.add(new CallMonitorResult(batchId, number, result == 1, testIndex, simSignalInfo.toString(), time));
            } catch (Exception e) {
//                i(e.toString())
            }
        }
        String type = "";
        if (testParams.isAutoReject) {
            type = mContext.getString(R.string.distinguish_text);
        } else if (testParams.isAutoAnswer) {
            type = mContext.getString(R.string.is_auto_answer);
            if (testParams.isAnswerHangup) {
                type += "/" + mContext.getString(R.string.hangup);
            }
        } else {
            type = mContext.getString(R.string.noOP);
        }
        return new InComingReportBean(testParams, list, type);
    }

    public int getLastBatch() {
        Cursor rawQuery = mDb.rawQuery("select * from " + InComingBatch.NAME + " where " + InComingBatch._ID + " in (select max(" + InComingBatch._ID + ") from " + InComingBatch.NAME + ")", null);
        int lastBatch = 0;
        while (rawQuery.moveToNext()) {
            try {
                lastBatch = rawQuery.getInt(rawQuery.getColumnIndex(InComingBatch._ID));
            } catch (Exception e) {
//                i(e.toString())
            }
        }
        return lastBatch;
    }

    public ArrayList<String> getAllBatch() {
        Cursor rawQuery = mDb.rawQuery("select " + InComingBatch._ID + " from " + InComingBatch.NAME, null);
        ArrayList<String> list = new ArrayList<>();
        while (rawQuery.moveToNext()) {
            try {
                String batch = rawQuery.getString(rawQuery.getColumnIndex(InComingBatch._ID));
                list.add(batch);
            } catch (Exception e) {
//                i(e.toString())
            }
        }
        return list;

    }

    public void deleteTable(String table_name) {
        try {
            mDb.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        mDb.delete(InComingBatch.NAME, null, null);
        mDb.delete(InComingData.NAME, null, null);
    }
}
