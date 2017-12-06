package com.gionee.autotest.field.ui.call_loss_ratio;


import android.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.outgoing.OutGoingUtil;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.DialogHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class CallLossRatioActivity extends BaseActivity implements CallLossRatioContract.View {

    @BindView(R.id.call_loss_ratio_number_et)
    EditText mNumberET;
    @BindView(R.id.call_loss_ratio_cycle_et)
    EditText mCycleET;
    @BindView(R.id.call_loss_ratio_gap_time_et)
    EditText mGapTimeET;
    @BindView(R.id.call_loss_ratio_call_time_et)
    EditText mCallTimeET;
    @BindView(R.id.call_loss_ratio_call_time_sum_et)
    EditText mCallTimeSumET;
    @BindView(R.id.call_loss_ratio_is_speaker_phone_open)
    CheckBox mIsSpeakerPhoneOpenCB;
    @BindView(R.id.call_loss_ratio_start)
    Button mStartBtn;
    @BindView(R.id.call_loss_ratio_callRateET)
    TextView mCallLossRatioET;
    @BindView(R.id.call_loss_ratio_verifyCount)
    EditText mVerifyCountET;

    private CallLossRatioPresenter mCallLossRatioPresenter;

    @OnClick(R.id.call_loss_ratio_start)
    public void CallLossRatioStartClicked() {
        mCallLossRatioPresenter.handleStartBtnClicked();
    }

    @Override
    public void updateViews() {
        try {
            mStartBtn.setText(OutGoingUtil.isTest ? "停止测试" : "开始测试");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setViewEnabled(!OutGoingUtil.isTest, mNumberET, mCycleET, mGapTimeET, mCallTimeET, mCallTimeSumET, mIsSpeakerPhoneOpenCB, mVerifyCountET);
    }

    private void setViewEnabled(boolean testing, View... v) {
        for (View view : v) {
            try {
                view.setEnabled(testing);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_calllossratio;
    }

    @Override
    protected void initializePresenter() {
        mCallLossRatioPresenter = new CallLossRatioPresenter(this);
        super.presenter = mCallLossRatioPresenter;
        mCallLossRatioPresenter.onAttach(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCallLossRatioPresenter.obtainCallRate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCallLossRatioPresenter.obtainCallRate();
    }

    @Override
    public void setParams(CallParam p) {
        mNumberET.setText(p.number);
        mCycleET.setText(String.valueOf(p.cycle));
        mGapTimeET.setText(String.valueOf(p.gap_time));
        mCallTimeET.setText(String.valueOf(p.call_time));
        mCallTimeSumET.setText(String.valueOf(p.call_time_sum));
        mIsSpeakerPhoneOpenCB.setChecked(p.is_speaker_on);
        mVerifyCountET.setText(String.valueOf(p.verifyCount));
    }

    @Override
    public CallParam getUserParams() {
        String number = mNumberET.getText().toString().trim();
        int count = Integer.parseInt(mCycleET.getText().toString().trim());
        int gapTime = Integer.parseInt(mGapTimeET.getText().toString().trim());
        int callTime = Integer.parseInt(mCallTimeET.getText().toString().trim());
        int callTimeSum = Integer.parseInt(mCallTimeSumET.getText().toString().trim());
        boolean isSpeakOn = mIsSpeakerPhoneOpenCB.isChecked();
        int verifyCount = Integer.parseInt(mVerifyCountET.getText().toString().trim());
        String[] numbers = new String[0];
        if (number.contains(",")) {
            try {
                numbers = number.split(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            numbers = new String[]{number};
        }
        return new CallParam().setNumber(number).setNumbers(numbers).setCall_time(callTime).setCall_time_sum(callTimeSum).setCycle(count)
                .setGap_time(gapTime).setIs_speaker_on(isSpeakOn).setVerifyCount(verifyCount);
    }

    @Override
    public void showDialog(String message) {
        DialogHelper.create(this, "提示", message, new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                builder.setPositiveButton("确定", null);
            }
        }).show();
    }

    @Override
    public void updateCallRate(String s) {
        mCallLossRatioET.setText(s);
    }

    @Override
    public void doFinish() {
        finish();
    }

    @Override
    protected int menuResId() {
        return R.menu.outgoing_menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_report:
                mCallLossRatioPresenter.showReport();
                break;
            case R.id.clear_report:
                mCallLossRatioPresenter.clearAllReport();
                break;
            case R.id.export_excel:
                mCallLossRatioPresenter.exportExcelFile();
                break;
            case R.id.open_excel:
                mCallLossRatioPresenter.openExcelFile();
                break;
            case R.id.action_abouts:
                startActivity(AboutActivity.getAboutIntent(this, getString(R.string.outgoing_about), true));
                break;
            case android.R.id.home:
                if (CallLossRatioUtil.isTest) {
                    mCallLossRatioPresenter.showExitWarningDialog();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (CallLossRatioUtil.isTest) {
            mCallLossRatioPresenter.showExitWarningDialog();
        } else {
            super.onBackPressed();
        }
    }
}
