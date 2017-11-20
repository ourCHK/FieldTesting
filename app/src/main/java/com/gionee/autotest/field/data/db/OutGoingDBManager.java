package com.gionee.autotest.field.data.db;


import android.content.Context;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;

import java.util.ArrayList;

public class OutGoingDBManager {

    private static OutGoingDBHelper helper;

    static void init(Context context) {
        if (helper == null) {
            helper = new OutGoingDBHelper(context);
        }
    }

    public static long addBatch(CallParam param) {
        return helper.addBatch(param);
    }

    public static void writeData(OutGoingCallResult data) {
        helper.writeCallData(data);
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