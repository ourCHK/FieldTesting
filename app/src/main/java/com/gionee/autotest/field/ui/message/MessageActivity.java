package com.gionee.autotest.field.ui.message;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.information.SMSUtils;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.data_reset.DataResetActivity;
import com.gionee.autotest.field.ui.data_reset.DataResetContract;
import com.gionee.autotest.field.ui.data_reset.DataResetPresentationActivity;
import com.gionee.autotest.field.ui.data_reset.DataResetPresenter;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataResetHelper;
import com.gionee.autotest.field.util.DialogHelper;
import com.gionee.autotest.field.util.RegexUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xhk on 2017/11/15.
 */

public class MessageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, MessageContract.View {

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

//    @Override
//    protected boolean isDisplayHomeUpEnabled() {
//        return true;
//    }

    @Override
    protected int menuResId() {
        return R.menu.menu_message;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message_about:
                startActivity(AboutActivity.getAboutIntent(this, getString(R.string.data_reset_about_name), true));
                break;
            case R.id.message_test:
                ArrayList<File> dirFileXls = DataResetHelper.getDirFileXls(Constant.DIR_MESSAGE);
                if (dirFileXls.size()==0){
                    Toast.makeText(getApplicationContext(), R.string.data_reset_erro, Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(this,DataResetPresentationActivity.class);
                    intent.putExtra("dirFileXls",(Serializable)dirFileXls);
                    startActivity(intent);

                }

                break;

            case R.id.message_help:
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
                dialog.setTitle("说明");
                dialog.setMessage("测试报告保存在内部存储器/field/message下。");
                dialog.setIcon(R.mipmap.logo);
                dialog.setCancelable(true);
                dialog.setPositiveButton("取消", (android.content.DialogInterface.OnClickListener) null);
                dialog.show();

                break;
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
            Toast.makeText(context,"信息发送完成",Toast.LENGTH_SHORT).show();
            bt_message_start_testing.setEnabled(true);
            bt_message_stop_testing.setEnabled(false);

        }
    }

    @Override
    public void onBackPressed() {
        boolean aBoolean = Preference.getBoolean(MessageActivity.this, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);
        if (aBoolean) {

            DialogHelper.create(MessageActivity.this, "警告", "将退出到首页并停止测试", new DialogHelper.OnBeforeCreate() {
                @Override
                public void setOther(AlertDialog.Builder builder) {
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Preference.putBoolean(MessageActivity.this, Constant.PREF_KEY_MESSAGE_DATA_COLLECT_RUNNING, false);
                            finish();
                        }
                    }).setNegativeButton("取消", null);
                }
            }).show();

        }else{
            super.onBackPressed();
        }


    }
}
