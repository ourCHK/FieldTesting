package com.gionee.autotest.field.ui.call_loss_ratio;


import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;

public class CallLossRatioReportActivity extends BaseActivity implements CallLossRatioContract.ReportView {
    @BindView(R.id.call_loss_ratio_report__view)
    ExpandableListView mReportView;
    @BindView(R.id.call_loss_ratio_report_Spinner)
    Spinner mReportSpinner;

    @Override
    protected int layoutResId() {
        return R.layout.layout_call_loss_ratio_report;
    }

    @Override
    protected void initializePresenter() {
        CallLossRatioPresenter mReportPresenter= new CallLossRatioPresenter(this);
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


