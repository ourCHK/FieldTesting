package com.gionee.autotest.field.ui.network_switch;

import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchParam;

/**
 * Created by viking on 11/13/17.
 * <p>
 * interface for signal
 */

interface NetworkSwitchContract {

    interface View extends BaseView {

        void updateParams(NetworkSwitchParam param);

        void updateViews();
    }

    interface Presenter {

        void showReport();

        void clearAllReport();

        void exportExcelFile();

        NetworkSwitchParam getLastParams();

        void startTest(NetworkSwitchParam inputParam);
    }
}
