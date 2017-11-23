package com.gionee.autotest.field.ui.outgoing.model;


public class CallParam {

    public long id=0;
    public String   number        = "10086,10010";
    public String[] numbers       = new String[]{"10086", "10010"};
    public int       cycle         = 3;
    public int      call_time     = 10;
    public int      gap_time      = 20;
    public int      call_time_sum = 60;
    public boolean  is_speaker_on = false;

    public CallParam() {
        this(0,"10086,10010",new String[]{"10086","10010"},3,10,20,60,false);
    }

    public CallParam(long id, String number, String[] numbers, int cycle, int call_time, int gap_time, int call_time_sum, boolean is_speaker_on) {

        this.id = id;
        this.number = number;
        this.numbers = numbers;
        this.cycle = cycle;
        this.call_time = call_time;
        this.gap_time = gap_time;
        this.call_time_sum = call_time_sum;
        this.is_speaker_on = is_speaker_on;
    }

    public CallParam setId(long id) {
        this.id = id;
        return this;
    }

    public CallParam setNumber(String number) {
        this.number = number;
        return this;
    }

    public CallParam setNumbers(String[] numbers) {
        this.numbers = numbers;
        return this;
    }

    public CallParam setCycle(int cycle) {
        this.cycle = cycle;
        return this;
    }

    public CallParam setCall_time(int call_time) {
        this.call_time = call_time;
        return this;
    }

    public CallParam setGap_time(int gap_time) {
        this.gap_time = gap_time;
        return this;
    }

    public CallParam setCall_time_sum(int call_time_sum) {
        this.call_time_sum = call_time_sum;
        return this;
    }

    public CallParam setIs_speaker_on(boolean is_speaker_on) {
        this.is_speaker_on = is_speaker_on;
        return this;
    }
}
