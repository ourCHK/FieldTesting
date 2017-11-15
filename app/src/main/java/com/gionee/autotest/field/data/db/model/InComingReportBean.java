package com.gionee.autotest.field.data.db.model;

import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.common.call.CallMonitorResult;

import java.util.ArrayList;

public class InComingReportBean {
    public final CallMonitorParam             param;
    public final ArrayList<CallMonitorResult> data;
    public final String                       type;

    public InComingReportBean(CallMonitorParam param, ArrayList<CallMonitorResult> data, String type) {

        this.param = param;
        this.data = data;
        this.type = type;
    }
}