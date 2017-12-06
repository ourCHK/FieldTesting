package com.gionee.autotest.field.ui.outgoing;

import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.gionee.autotest.field.data.db.model.OutGoingCallResult;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;

import java.util.ArrayList;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

public interface OutGoingContract {

    interface View extends BaseView {

        void updateViews();

        void setParams(CallParam lastParams);

        CallParam getUserParams();

        void showDialog(String message);

        void updateCallRate(String s);

        void doFinish();
    }

    interface ReportView extends BaseView{

        ExpandableListView getListView();

        Spinner getSpinner();

    }

    interface Presenter {

        void startCallTest();

        void handleStartBtnClicked();

        void clearAllReport();

        void showReport();

        void showExitWarningDialog();
    }
}
