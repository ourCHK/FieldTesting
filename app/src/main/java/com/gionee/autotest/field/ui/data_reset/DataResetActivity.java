package com.gionee.autotest.field.ui.data_reset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xhk on 2017/11/15.
 */

public class DataResetActivity extends BaseActivity implements DataResetContract.View{

    @BindView(R.id.bt_start_testing)
    Button bt_start_testing;

    @BindView(R.id.bt_stop_testing)
    Button bt_stop_testing;

    @BindView(R.id.et_frequency)
    EditText et_frequency;

    private LocalReceiver localReceiver;


    private DataResetPresenter mDataResetPresenter;

    @Override
    protected int layoutResId() {
        return R.layout.activity_data_reset;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mDataResetPresenter = new DataResetPresenter(getApplicationContext()) ;
        super.presenter = mDataResetPresenter ;
        mDataResetPresenter.onAttach(this);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.DATA_RESET_RECEIVER);
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);

    }

    @Override
    protected int menuResId() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.help:
                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AboutActivity.class));
                return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_start_testing)
    void onDataResetStartClicked(){
        mDataResetPresenter.isIntervalValid(et_frequency.getText().toString()) ;
    }

    @OnClick(R.id.bt_stop_testing)
    void onDataResetStopClicked(){
        Toast.makeText(this, R.string.stop_testing, Toast.LENGTH_SHORT).show();
        mDataResetPresenter.unregisterDataResetListener();
    }

    @Override
    public void setDefaultInterval(String time) {
        et_frequency.setText(time);
    }

    @Override
    public void showFrequencyError() {
        Toast.makeText(getApplicationContext(), R.string.cycle_erro, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showStartToast() {
        Toast.makeText(this, R.string.start_testing, Toast.LENGTH_SHORT).show();
        mDataResetPresenter.registerDataResetListener(et_frequency.getText().toString());
    }

    @Override
    public void setStartButtonVisibility(boolean visibility) {
        bt_start_testing.setEnabled(visibility);
    }

    @Override
    public void setStopButtonVisibility(boolean visibility) {
        bt_stop_testing.setEnabled(visibility);
    }

    class LocalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"测试完成",Toast.LENGTH_SHORT).show();
            bt_start_testing.setEnabled(true);
            bt_stop_testing.setEnabled(false);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }
}
