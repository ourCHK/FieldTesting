package com.gionee.autotest.field.ui.incoming;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.InComingReportBean;
import com.gionee.autotest.field.ui.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;

public class InComingReportActivity extends BaseActivity implements InComingContract.ReportView {

    @BindView(R.id.report_list)
    ListView report_list;
    @BindView(R.id.mySpinner)
    Spinner  mySpinner;
    private InComingReportAdapter reportAdapter;
    private ArrayAdapter          mSpinnerAdapter;
    private ArrayList<String> batchs = new ArrayList<>();
    private InComingPresenter mInComingPresenter;


    @Override
    protected int layoutResId() {
        return R.layout.incoming_report;
    }

    @Override
    protected void initializePresenter() {
        mInComingPresenter = new InComingPresenter(getApplicationContext());
        super.presenter = mInComingPresenter;
        mInComingPresenter.onAttach(this);

        reportAdapter = new InComingReportAdapter(getApplicationContext());
        report_list.setAdapter(reportAdapter);
        mSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, batchs);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(mSpinnerAdapter);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1)
                    mInComingPresenter.insertListData(Integer.parseInt(batchs.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mInComingPresenter.updateBatchList();
    }


    @Override
    public void updateBatchList(ArrayList<String> strings) {
        batchs.clear();
        batchs.addAll(strings);
        try {
            mSpinnerAdapter.notifyDataSetChanged();
            if (strings.size() != 0)
                mySpinner.setSelection(Integer.parseInt(strings.get(strings.size() - 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertListData(InComingReportBean inComingReportBean) {
        reportAdapter.updateData(inComingReportBean);
    }
}
