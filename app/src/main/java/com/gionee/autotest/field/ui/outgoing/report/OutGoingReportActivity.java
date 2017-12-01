package com.gionee.autotest.field.ui.outgoing.report;


import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.outgoing.OutGoingContract;
import com.gionee.autotest.field.ui.outgoing.OutGoingPresenter;


import butterknife.BindView;

public class OutGoingReportActivity extends BaseActivity implements OutGoingContract.ReportView {
    @BindView(R.id.out_going_report__view)
    ExpandableListView mReportView;
    @BindView(R.id.out_going_report_Spinner)
    Spinner mReportSpinner;


    @Override
    protected int layoutResId() {
        return R.layout.layout_outgoing_report;
    }

    @Override
    protected void initializePresenter() {
        OutGoingPresenter mReportPresenter= new OutGoingPresenter(this);
        super.presenter = mReportPresenter;
        mReportPresenter.onAttach(this);
    }

    @Override
    public ExpandableListView getListView() {
        return mReportView;
    }

    @Override
    public Spinner getSpinner() {
        return mReportSpinner;
    }

}


