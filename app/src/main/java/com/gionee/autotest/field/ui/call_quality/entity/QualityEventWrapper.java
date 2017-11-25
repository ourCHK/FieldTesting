package com.gionee.autotest.field.ui.call_quality.entity;

import java.util.List;

/**
 * Created by viking on 11/24/17.
 */

public class QualityEventWrapper extends BaseEvent {

    private int phone_num ;

    private int quality_type ;

    private int event_type ;

    private List<SimSignalInfoWrapper> signals ;

    public QualityEventWrapper(int phone_num, int quality_type, int event_type) {
        this.phone_num = phone_num;
        this.quality_type = quality_type;
        this.event_type = event_type;
    }

    public int getPhone_num() {
        return phone_num;
    }

    public int getQuality_type() {
        return quality_type;
    }

    public int getEvent_type() {
        return event_type;
    }

    public boolean isNextRoundEvent(){
        return getPhone_num() == CallQualityConstant.NEXT_ROUND ;
    }

    public List<SimSignalInfoWrapper> getSignals() {
        return signals;
    }

    public void setSignals(List<SimSignalInfoWrapper> signals) {
        this.signals = signals;
    }
}
