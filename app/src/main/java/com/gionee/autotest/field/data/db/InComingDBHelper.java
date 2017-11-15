package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.common.call.CallMonitorResult;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingBatch;
import com.gionee.autotest.field.util.Constant.InComingDB.InComingData;

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
        return mDb.insert(InComingBatch.NAME, null, cv);
    }

    public synchronized void writeData(CallMonitorResult data) {
        ContentValues cv = new ContentValues();
        cv.put(InComingData.BATCH_ID, data.batchId);
        cv.put(InComingData.NUMBER, data.number);
        cv.put(InComingData.RESULT, data.result ? 1 : 0);
        cv.put(InComingData.TEST_INDEX, data.testIndex);
        cv.put(InComingData.TIME, data.time);
        cv.put(InComingData.FAIL_MSG, data.failMsg);
        mDb.insert(InComingData.NAME, null, cv);
    }

    public InComingReportBean getReportBean(int batchId) {
        Cursor           query      = mDb.rawQuery("select * from " + InComingBatch.NAME + " where " + InComingBatch._ID + " =" + batchId, null);
        CallMonitorParam testParams = new CallMonitorParam();
        while (query.moveToNext()) {
            try {
                int isAutoReject       = query.getInt(query.getColumnIndex(InComingBatch.AUTO_REJECT));
                int rejectTime         = query.getInt(query.getColumnIndex(InComingBatch.AUTO_REJECT_TIME));
                int isAutoAnswer       = query.getInt(query.getColumnIndex(InComingBatch.AUTO_ANSWER));
                int isAutoAnswerHangup = query.getInt(query.getColumnIndex(InComingBatch.AUTO_ANSWER_HANGUP));
                int answerHangupTime   = query.getInt(query.getColumnIndex(InComingBatch.ANSWER_HANGUP_TIME));
                int gapTime            = query.getInt(query.getColumnIndex(InComingBatch.GAP_TIME));
                testParams = new CallMonitorParam(isAutoReject == 1, rejectTime, isAutoAnswer == 1, isAutoAnswerHangup == 1, answerHangupTime, gapTime);
            } catch (Exception e) {
//                Log.i(e.toString());
            }
        }
        Cursor    cursor = mDb.rawQuery("select * from " + InComingData.NAME + " where " + InComingData.BATCH_ID + " =" + batchId, null);
        ArrayList list   = new ArrayList<CallMonitorResult>();
        while (cursor.moveToNext()) {
            try {
                long   number    = cursor.getLong(cursor.getColumnIndex(InComingData.NUMBER));
                int    testIndex = cursor.getInt(cursor.getColumnIndex(InComingData.TEST_INDEX));
                int    result    = cursor.getInt(cursor.getColumnIndex(InComingData.RESULT));
                String time      = cursor.getString(cursor.getColumnIndex(InComingData.TIME));
                String failMsg   = cursor.getString(cursor.getColumnIndex(InComingData.FAIL_MSG));
                list.add(new CallMonitorResult(batchId, number, result == 1, testIndex, failMsg, time));
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
                type += "/${mContext.getString(R.string.hangup)}";
            }
        } else {
            type = mContext.getString(R.string.noOP);
        }
        return new InComingReportBean(testParams, list, type);
    }

    public int getLastBatch() {
        Cursor rawQuery  = mDb.rawQuery("select * from " + InComingBatch.NAME + " where " + InComingBatch._ID + " in (select max(" + InComingBatch._ID + ") from " + InComingBatch.NAME + ")", null);
        int    lastBatch = 0;
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
        Cursor            rawQuery = mDb.rawQuery("select " + InComingBatch._ID + " from " + InComingBatch.NAME, null);
        ArrayList<String> list     = new ArrayList<>();
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
}
