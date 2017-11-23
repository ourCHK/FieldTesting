package com.gionee.autotest.field.ui.network_switch.model;

public class NetworkSwitchResult {
    public boolean isShow_Test_Time = false;
    public String  SignNetwork_1    = "";
    public String  SignNetwork_2    = "";
    public String  readSim_1        = "";
    public String  readSim_2        = "";
    public String  isNet            = "";
    public String  result           = "";
    public String  test_time        = "";
    public String  isSwitched       = "";

    public NetworkSwitchResult setShow_Test_Time(boolean show_Test_Time) {
        isShow_Test_Time = show_Test_Time;
        return this;
    }

    public NetworkSwitchResult setSignNetwork_1(String signNetwork_1) {
        SignNetwork_1 = signNetwork_1;
        return this;
    }

    public NetworkSwitchResult setSignNetwork_2(String signNetwork_2) {
        SignNetwork_2 = signNetwork_2;
        return this;
    }

    public NetworkSwitchResult setReadSim_1(String readSim_1) {
        this.readSim_1 = readSim_1;
        return this;
    }

    public NetworkSwitchResult setReadSim_2(String readSim_2) {
        this.readSim_2 = readSim_2;
        return this;
    }

    public NetworkSwitchResult setIsNet(String isNet) {
        this.isNet = isNet;
        return this;
    }

    public NetworkSwitchResult setResult(String result) {
        this.result = result;
        return this;
    }

    public NetworkSwitchResult setTest_time(String test_time) {
        this.test_time = test_time;
        return this;
    }

    public NetworkSwitchResult setIsSwitched(String isSwitched) {
        this.isSwitched = isSwitched;
        return this;
    }
}
