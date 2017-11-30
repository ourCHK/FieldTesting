package com.gionee.autotest.field.ui.outgoing;

import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface OutGoingContract {

    interface View extends BaseView {

        void updateViews();

        void setParams(CallParam lastParams);

        CallParam getUserParams();

        void showDialog(String message);

        void updateCallRate(String s);
    }


    interface Presenter {

        void startCallTest();

        void handleStartBtnClicked();

        void clearAllReport();
    }
}
