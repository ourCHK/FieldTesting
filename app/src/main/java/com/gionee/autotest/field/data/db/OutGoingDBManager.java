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
        try {
            helper.writeCallData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        try {
            helper.clearTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<OutGoingCallResult> getReportBean(int batchId) {
        try {
            return helper.getCallBean(batchId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static ArrayList<String> getAllBatch() {
        try {
            return helper.getAllBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static int getLastBatch() {
        try {
            return helper.getLastBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
