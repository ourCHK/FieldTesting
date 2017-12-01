package com.gionee.autotest.field.ui.outgoing.model;


import com.gionee.autotest.field.data.db.model.OutGoingCallResult;

import java.util.ArrayList;

public class OutGoingReportCycle {
    public String sumString = "";
    public ArrayList<OutGoingCallResult> results = new ArrayList<>();

    public OutGoingReportCycle setSumString(String sumString) {
        this.sumString = sumString;
        return this;
    }

    public OutGoingReportCycle setResults(ArrayList<OutGoingCallResult> results) {
        this.results = results;
        return this;
    }

    public OutGoingReportCycle addResult(OutGoingCallResult result) {
        results.add(result);
        return this;
    }


}
