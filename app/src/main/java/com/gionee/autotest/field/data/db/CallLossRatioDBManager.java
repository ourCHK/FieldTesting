package com.gionee.autotest.field.data.db;


import android.content.Context;
import android.util.Log;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.Constant;

import java.util.ArrayList;

public class CallLossRatioDBManager {

    private static CallLossRatioDBHelper helper;

    static void init(Context context) {
        if (helper == null) {
            helper = new CallLossRatioDBHelper(context);
        }
    }

    public static long addBatch(CallParam param) {
        return helper.addBatch(param);
    }

    public static void writeData(OutGoingCallResult data) {
        try {
            helper.writeCallData(data);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(Constant.TAG,"writeData false");
        }
    }

    public static void delete() {
        helper.clearTable();
    }

    public static ArrayList<OutGoingCallResult> getReportBean(int batchId) {
        return helper.getCallBean(batchId);
    }

    public static ArrayList<String> getAllBatch() {
        return helper.getAllBatch();
    }

    public static int getLastBatch() {
        return helper.getLastBatch();
    }
}
