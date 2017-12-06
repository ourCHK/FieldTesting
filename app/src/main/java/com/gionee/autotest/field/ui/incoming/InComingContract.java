package com.gionee.autotest.field.ui.incoming;

import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BaseView;
import com.gionee.autotest.field.util.call.CallMonitorParam;

import java.util.ArrayList;

/**
 * Created by viking on 11/13/17.
 *
 * interface for signal
 */

interface InComingContract {

    interface View extends BaseView {

        void updateViews();

        void setSumContent(String s);

        void setParams(CallMonitorParam lastParams);
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

        void openExcelFile();

        void updateSumContent();
    }
}
