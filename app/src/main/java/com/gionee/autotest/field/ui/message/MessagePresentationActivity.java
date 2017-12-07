package com.gionee.autotest.field.ui.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.data_reset.OnCustomListener;
import com.gionee.autotest.field.util.Util;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by xhk on 2017/12/7.
 */

public class MessagePresentationActivity extends BaseActivity implements OnCustomListener {

    @BindView(R.id.message_recyclerView)
    RecyclerView message_recyclerView;

    private MessagetPresentationAdapter mMessagetPresentationAdapter;
    private ArrayList<File> dirFileXls;

    @Override
    protected int layoutResId() {
        return R.layout.activity_message_presentation;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {

        dirFileXls = (ArrayList<File>) getIntent().getSerializableExtra("dirFileXls");
        message_recyclerView.setHasFixedSize(false);
        message_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessagetPresentationAdapter = new MessagetPresentationAdapter(this, dirFileXls);
        message_recyclerView.setAdapter(mMessagetPresentationAdapter);
        mMessagetPresentationAdapter.setOnCustomListener(this);
    }

    @Override
    public void onCustomerListener(View v, int position) {
//        Toast.makeText(this,dirFileXls.get(position).getName(),Toast.LENGTH_SHORT).show();
        Util.openExcelByIntent(this,dirFileXls.get(position).getAbsolutePath());

    }
}
