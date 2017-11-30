package com.gionee.autotest.field.ui.data_reset;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Util;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by xhk on 2017/11/30.
 */

public class DataResetPresentationActivity extends BaseActivity implements OnCustomListener {

    @BindView(R.id.data_reset_recyclerView)
    RecyclerView data_reset_recyclerView;

    private DataResetPresentationAdapter mDataResetPresentationAdapter;
    private ArrayList<File> dirFileXls;

    @Override
    protected int layoutResId() {
        return R.layout.activity_data_reset_presentation;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        dirFileXls = (ArrayList<File>) getIntent().getSerializableExtra("dirFileXls");
        data_reset_recyclerView.setHasFixedSize(false);
        data_reset_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDataResetPresentationAdapter = new DataResetPresentationAdapter(this, dirFileXls);
        data_reset_recyclerView.setAdapter(mDataResetPresentationAdapter);
        mDataResetPresentationAdapter.setOnCustomListener(this);
    }

    @Override
    public void onCustomerListener(View v, int position) {
//        Toast.makeText(this,dirFileXls.get(position).getName(),Toast.LENGTH_SHORT).show();
        Util.openExcelByIntent(this,dirFileXls.get(position).getAbsolutePath());

    }
}
