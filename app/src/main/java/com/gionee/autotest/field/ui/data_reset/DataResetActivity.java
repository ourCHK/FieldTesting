package com.gionee.autotest.field.ui.data_reset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.DataResetHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

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

    @BindView(R.id.et_retest_times)
    EditText et_retest_times;

    @BindView(R.id.tv_test_result)
    TextView tv_test_result;

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
        intentFilter.addAction(Constant.DATA_RESET_EACH_RECEIVER);
        localReceiver = new LocalReceiver();
        registerReceiver(localReceiver, intentFilter);

    }

    @Override
    protected int menuResId() {
        return R.menu.menu_data_reset;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.data_reset_about:
//                Toast.makeText(getApplicationContext(), R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.data_reset_test:

                ArrayList<File> dirFileXls = DataResetHelper.getDirFileXls(Constant.DIR_DATA_RESET);
                if (dirFileXls.size()==0){
                    Toast.makeText(getApplicationContext(), R.string.data_reset_erro, Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(this,DataResetPresentationActivity.class);
                    intent.putExtra("dirFileXls",(Serializable)dirFileXls);
                    startActivity(intent);

                }

                return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.bt_start_testing)
    void onDataResetStartClicked(){
        mDataResetPresenter.isIntervalValid(et_frequency.getText().toString(),et_retest_times.getText().toString()) ;
    }

    @OnClick(R.id.bt_stop_testing)
    void onDataResetStopClicked(){
        Toast.makeText(this, R.string.stop_testing, Toast.LENGTH_SHORT).show();
        mDataResetPresenter.unregisterDataResetListener();
    }

    @Override
    public void setDefaultInterval(String time,String retest_times) {
        et_frequency.setText(time);
        et_retest_times.setText(retest_times);

    }

    @Override
    public void showFrequencyError() {
        Toast.makeText(getApplicationContext(), R.string.cycle_erro, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showRetesTimesError() {
        Toast.makeText(getApplicationContext(), R.string.retest_erro, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showStartToast() {
        Toast.makeText(this, R.string.start_testing, Toast.LENGTH_SHORT).show();
        mDataResetPresenter.registerDataResetListener(et_frequency.getText().toString(),et_retest_times.getText().toString());
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
            switch (intent.getAction()){
                case Constant.DATA_RESET_RECEIVER:// 测试完成
                    Toast.makeText(context,"测试完成",Toast.LENGTH_SHORT).show();
                    bt_start_testing.setEnabled(true);
                    bt_stop_testing.setEnabled(false);

                    tv_test_result.setVisibility(View.VISIBLE);
                    long data_reset_success_number1 = Preference.getLong(getApplicationContext(), Constant.DATA_RESET_SUCCESS_NUMBER, 0);
                    long data_reset_failure_number1 = Preference.getLong(getApplicationContext(), Constant.DATA_RESET_FAILURE_NUMBER, 0);

                    String successRate1 = DataResetHelper.SuccessRate(data_reset_success_number1, data_reset_failure_number1);
                    String str1 = "测试完成\n 成功次数："+data_reset_success_number1+"次\n 失败次数："+data_reset_failure_number1+"次\n 成功率："+successRate1;
                    tv_test_result.setText(str1);
                    mDataResetPresenter.unregisterDataResetListener();

                    break;
                case Constant.DATA_RESET_EACH_RECEIVER: //每轮测试

                    tv_test_result.setVisibility(View.VISIBLE);
                    long data_reset_current_cycle = Preference.getLong(getApplicationContext(), Constant.PREF_KEY_DATA_RESET_DATA_COLLECT_CURRENT_CYCLE, 1);
                    long data_reset_success_number = Preference.getLong(getApplicationContext(), Constant.DATA_RESET_SUCCESS_NUMBER, 0);
                    long data_reset_failure_number = Preference.getLong(getApplicationContext(), Constant.DATA_RESET_FAILURE_NUMBER, 0);
                    long current_cycle = data_reset_current_cycle+1;
                    if (data_reset_success_number==0&&data_reset_failure_number==0){
                        String str2 = "第"+current_cycle+"轮测试\n 成功次数："+data_reset_success_number+"次\n 失败次数："+data_reset_failure_number+"次\n 成功率：0%" ;
                        tv_test_result.setText(str2);
                    }else{
                        String successRate = DataResetHelper.SuccessRate(data_reset_success_number, data_reset_failure_number);
                        String str = "第"+current_cycle+"轮测试\n 成功次数："+data_reset_success_number+"次\n 失败次数："+data_reset_failure_number+"次\n 成功率：" + successRate;
                        tv_test_result.setText(str);
                    }


                    break;

            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }
}
