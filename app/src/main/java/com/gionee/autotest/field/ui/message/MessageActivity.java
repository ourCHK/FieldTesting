package com.gionee.autotest.field.ui.message;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gionee.autotest.common.information.SMSUtils;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xhk on 2017/11/15.
 */

public class MessageActivity extends BaseActivity {

    @BindView(R.id.rg_message_type)
    RadioGroup rg_message_type;

    @BindView(R.id.rg_sim)
    RadioGroup rg_sim;

    @BindView(R.id.bt_start_testing)
    Button bt_start_testing;

    @BindView(R.id.bt_stop_testing)
    Button bt_stop_testing;


    @Override
    protected int layoutResId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initializePresenter() {

    }

    @OnClick(R.id.bt_start_testing)
    void onDataResetStartClicked(){
        Toast.makeText(this, R.string.start_testing, Toast.LENGTH_SHORT).show();
        try {
            SMSUtils.setSIM(this,1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        mDataResetPresenter.isIntervalValid(et_frequency.getText().toString()) ;
    }

    @OnClick(R.id.bt_stop_testing)
    void onDataResetStopClicked(){
        Toast.makeText(this, R.string.stop_testing, Toast.LENGTH_SHORT).show();
        try {
            SMSUtils.setSIM(this,2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        mDataResetPresenter.unregisterDataResetListener();
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
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
                //                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.help:
                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                //                startActivity(new Intent(this, AboutActivity.class));
                return true ;
        }
        return super.onOptionsItemSelected(item);
    }
}
