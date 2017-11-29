package com.gionee.autotest.field.ui.data_stability.report;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.data_stability.WebViewResultSum;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.DirectionScrollUtil;

import java.util.ArrayList;


public class ReportActivity extends AppCompatActivity implements IReportView, DirectionScrollUtil.OnDirectionScrollListener {
    private ReportAdapter        mAdapter;
    private ListView mReport_list;
    private Spinner mBatchSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private boolean showTheNews = false;
    private ReportPresenter mPresenter;
    private boolean isFirstTime = false;
    private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report2);
        setLogo();
        mPresenter = new ReportPresenter(this);
        initViews();
        isFirstTime = true;
        DirectionScrollUtil.setDirectionScrollListener(mReport_list, ViewConfiguration.get(this).getScaledTouchSlop(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.updateBatchs();
    }

    private void initViews() {
        mAdapter = new ReportAdapter(this);
        mReport_list = (ListView) findViewById(R.id.mReport_List);
        mReport_list.setAdapter(mAdapter);
        mBatchSpinner = (Spinner) findViewById(R.id.batchSpinner);
        mBatchSpinner.setOnItemSelectedListener(spinnerListener);
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mPresenter.getBatchs());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBatchSpinner.setAdapter(mSpinnerAdapter);
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            DataStabilityUtil.i("选择了第" + i + "个");
            mPresenter.showReport(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            adapterView.setSelection(0);
        }
    };

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void setSelection_Spinner(int selection) {
        if (selection >= 0) {
            mBatchSpinner.setSelection(selection);
        }
    }

    @Override
    public ArrayAdapter getSpinnerAdapter() {
        return mSpinnerAdapter;
    }

    @Override
    public boolean isFirstTime() {
        return isFirstTime;
    }

    @Override
    public boolean showTheNews() {
        return showTheNews;
    }

    @Override
    public void updateListViewData(ArrayList<WebViewResultSum> list) {
        mAdapter.updateData(list);
    }

    private void setLogo() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.logo);
            actionBar.setTitle("报告");
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public void onScrollUp() {
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onScrollDown() {
        if (actionBar != null) {
            actionBar.show();
        }
    }
}
