package com.gionee.autotest.field.ui.network_switch.model;


public class NetworkSwitchParam {
    public  boolean flightMode;
    private boolean reboot;
    public  boolean isSwitchSim;
    public  long    testRound;
    public  boolean signNetwork;
    public  boolean readSim;
    public  boolean isNet;

    public NetworkSwitchParam() {
        this(true, true, true, 1L, true, true, true);
    }

    public NetworkSwitchParam(boolean flightMode, boolean reboot, boolean isSwitchSim, long testRound, boolean signNetwork, boolean readSim, boolean isNet) {
        this.flightMode = flightMode;
        this.reboot = reboot;
        this.isSwitchSim = isSwitchSim;
        this.testRound = testRound;
        this.signNetwork = signNetwork;
        this.readSim = readSim;
        this.isNet = isNet;
    }

    public NetworkSwitchParam setFlightMode(boolean flightMode) {
        this.flightMode = flightMode;
        return this;
    }

    public NetworkSwitchParam setSwitchSim(boolean switchSim) {
        isSwitchSim = switchSim;
        return this;
    }

    public NetworkSwitchParam setTestRound(int testRound) {
        this.testRound = testRound;
        return this;
    }

    public NetworkSwitchParam setSignNetwork(boolean signNetwork) {
        this.signNetwork = signNetwork;
        return this;
    }

    public NetworkSwitchParam setReadSim(boolean readSim) {
        this.readSim = readSim;
        return this;
    }

    public NetworkSwitchParam setNet(boolean net) {
        isNet = net;
        return this;
    }
}
