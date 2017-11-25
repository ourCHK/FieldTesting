package com.gionee.autotest.field.ui.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gionee.autotest.common.information.SMSUtils;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.data_reset.DataResetActivity;
import com.gionee.autotest.field.ui.data_reset.DataResetContract;
import com.gionee.autotest.field.ui.data_reset.DataResetPresenter;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xhk on 2017/11/15.
 */

public class MessageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, DataResetContract.View, MessageContract.View {

    @BindView(R.id.rg_message_type)
    RadioGroup rg_message_type;

    @BindView(R.id.rg_sim)
    RadioGroup rg_sim;

    @BindView(R.id.bt_message_start_testing)
    Button bt_message_start_testing;

    @BindView(R.id.bt_message_stop_testing)
    Button bt_message_stop_testing;

    @BindView(R.id.rb_sms)
    RadioButton rb_sms;

    @BindView(R.id.rb_mms)
    RadioButton rb_mms;

    @BindView(R.id.rb_sim_1)
    RadioButton rb_sim_1;

    @BindView(R.id.rb_sim_2)
    RadioButton rb_sim_2;

    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.et_frequency)
    EditText et_frequency;

    private MessagePresenter mMessagePresenter;

    /**
     * 1,短信  2，彩信  0，没有选择
     */
    private int message_type;

    /**
     * 1,卡1 2,卡2 0，没有选择
     */
    private int sim;

    private LocalMessageReceiver localMessageReceiver;


    @Override
    protected int layoutResId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initializePresenter() {
        mMessagePresenter = new MessagePresenter(getApplicationContext()) ;
        super.presenter = mMessagePresenter ;
        mMessagePresenter.onAttach(this);
        rg_message_type.setOnCheckedChangeListener(this);
        rg_sim.setOnCheckedChangeListener(this);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.MESSAGE_RECEIVER);
        localMessageReceiver = new LocalMessageReceiver();
        registerReceiver(localMessageReceiver, intentFilter);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.bt_message_start_testing)
    void onDataResetStartClicked() {
        mMessagePresenter.isIntervalValid(message_type,sim,et_phone.getText(),et_frequency.getText().toString());
    }

    @OnClick(R.id.bt_message_stop_testing)
    void onDataResetStopClicked() {
        Toast.makeText(this, R.string.stop_testing, Toast.LENGTH_SHORT).show();
        mMessagePresenter.unregisterMessageListener();
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_sms:
                message_type = 1;
                break;
            case R.id.rb_mms:
                message_type = 2;
                break;
            case R.id.rb_sim_1:
                sim = 1;
                try {
                    SMSUtils.setSIM(this, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.rb_sim_2:
                sim = 2;
                try {
                    SMSUtils.setSIM(this, 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
        }

    }


    @Override
    public void setDefaultInterval(String time, String phone) {
        et_frequency.setText(time);
        et_phone.setText(phone);

    }

    @Override
    public void showMessageTypeError() {
        Toast.makeText(this, "请选择信息类型", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSimError() {
        Toast.makeText(this, "请选择sim卡", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPhoneError() {
        Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setDefaultInterval(String time) {

    }

    @Override
    public void showFrequencyError() {
        Toast.makeText(getApplicationContext(), R.string.cycle_erro, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showStartToast() {
        mMessagePresenter.registerMessageListener(et_phone.getText().toString(),et_frequency.getText().toString(),message_type);
    }

    @Override
    public void setStartButtonVisibility(boolean visibility) {
        bt_message_start_testing.setEnabled(visibility);
    }

    @Override
    public void setStopButtonVisibility(boolean visibility) {
        bt_message_stop_testing.setEnabled(visibility);
    }

    class LocalMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"测试完成",Toast.LENGTH_SHORT).show();
            bt_message_start_testing.setEnabled(true);
            bt_message_stop_testing.setEnabled(false);

        }
    }
}
