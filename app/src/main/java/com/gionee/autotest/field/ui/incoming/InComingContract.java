package com.gionee.autotest.field.ui.incoming;

import com.gionee.autotest.common.call.CallMonitorParam;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BaseView;

import java.util.ArrayList;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface InComingContract {

    interface View extends BaseView {

    }

    interface ReportView extends BaseView{
        void updateBatch(ArrayList<String> strings);

        void updateListData(InComingReportBean inComingReportBean);
    }

    interface Presenter {

        void startMonitor(CallMonitorParam callMonitorParam);

        void stopMonitor();

        void showReport();

        void clearAllReport();

        void exportExcelFile();

        void updateBatchList();

        void insertListData(int i);
    }
}
