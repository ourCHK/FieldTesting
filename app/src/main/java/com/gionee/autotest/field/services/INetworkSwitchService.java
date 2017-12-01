package com.gionee.autotest.field.services;


import android.content.Context;

import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;

public interface INetworkSwitchService {

    Context getContext();

    void notifyRefreshView();

    void stopSelf();

    NetworkSwitchParam getParams();
}
