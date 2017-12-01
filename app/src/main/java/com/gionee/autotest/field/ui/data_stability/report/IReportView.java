package com.gionee.autotest.field.ui.data_stability.report;


import android.content.Context;
import android.widget.ArrayAdapter;


import com.gionee.autotest.field.ui.data_stability.WebViewResultSum;

import java.util.ArrayList;

interface IReportView {

    Context getContext();

    void setSelection_Spinner(int selection);

    ArrayAdapter getSpinnerAdapter();

    boolean isFirstTime();

    boolean showTheNews();

    void updateListViewData(ArrayList<WebViewResultSum> list);
}
