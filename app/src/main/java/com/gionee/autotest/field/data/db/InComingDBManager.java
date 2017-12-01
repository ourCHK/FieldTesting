package com.gionee.autotest.field.data.db;


import android.content.Context;

import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.util.call.CallMonitorParam;
import com.gionee.autotest.field.util.call.CallMonitorResult;

import java.util.ArrayList;

public class InComingDBManager {

    private static InComingDBHelper helper;

    static void init(Context context) {
        if (helper == null) {
            helper = new InComingDBHelper(context);
        }
    }

    public static long addBatch(CallMonitorParam param) {
       return helper.addBatch(param);
    }

    public static void writeData(CallMonitorResult data) {
        helper.writeData(data);
    }

    public static void delete() {
        helper.clearTable();
    }

    public static InComingReportBean getReportBean(int batchId){
      return  helper.getReportBean(batchId);
    }

    public static ArrayList<String> getAllBatch(){
      return  helper.getAllBatch();
    }

    public static int getLastBatch(){
        return helper.getLastBatch();
    }
}
