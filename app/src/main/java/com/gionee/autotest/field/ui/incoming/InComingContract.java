package com.gionee.autotest.field.ui.incoming;

import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.field.ui.base.BaseView;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface InComingContract {

    interface View extends BaseView {

    }

    interface Presenter {

        void startMonitor(CallMonitorParam callMonitorParam);
    }
}
