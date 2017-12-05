package com.gionee.autotest.field.ui.call_loss_ratio;

import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

public interface CallLossRatioContract {

    interface View extends BaseView {

        void updateViews();

        void setParams(CallParam lastParams);

        CallParam getUserParams();

        void showDialog(String message);

        void updateCallRate(String s);
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
    }
}
