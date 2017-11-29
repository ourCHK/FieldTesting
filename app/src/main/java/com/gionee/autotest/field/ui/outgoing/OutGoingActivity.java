package com.gionee.autotest.field.ui.outgoing;


import android.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gionee.autotest.common.About;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.outgoing.model.CallParam;
import com.gionee.autotest.field.util.DialogHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class OutGoingActivity extends BaseActivity implements OutGoingContract.View {

    @BindView(R.id.number_et)
    EditText mNumberET;
    @BindView(R.id.cycle_et)
    EditText mCycleET;
    @BindView(R.id.gap_time_et)
    EditText mGapTimeET;
    @BindView(R.id.call_time_et)
    EditText mCallTimeET;
    @BindView(R.id.call_time_sum_et)
    EditText mCallTimeSumET;
    @BindView(R.id.is_speaker_phone_open)
    CheckBox mIsSpeakerPhoneOpenCB;

    @BindView(R.id.out_going_start)
    Button mStartBtn;
    @BindView(R.id.callRateET)
    TextView mCallRateET;

    private OutGoingPresenter mOutGoingPresenter;

    @OnClick(R.id.out_going_start)
    public void OutGoingStartClicked() {
        mOutGoingPresenter.handleStartBtnClicked();
    }

    @Override
    public void updateViews() {
        mStartBtn.setText(OutGoingUtil.isTest ? "停止测试" : "开始测试");
        setViewEnabled(OutGoingUtil.isTest, mNumberET, mCycleET, mGapTimeET, mCallTimeET, mCallTimeSumET, mIsSpeakerPhoneOpenCB);
    }

    private void setViewEnabled(boolean testing, View... v) {
        for (View view : v) {
            view.setEnabled(testing);
        }
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_outgoing;
    }

    @Override
    protected void initializePresenter() {
        mOutGoingPresenter = new OutGoingPresenter(this);
        super.presenter = mOutGoingPresenter;
        mOutGoingPresenter.onAttach(this);
    }


    @Override
    public void setParams(CallParam p) {
        mNumberET.setText(p.number);
        mCycleET.setText(String.valueOf(p.cycle));
        mGapTimeET.setText(String.valueOf(p.gap_time));
        mCallTimeET.setText(String.valueOf(p.call_time));
        mCallTimeSumET.setText(String.valueOf(p.call_time_sum));
        mIsSpeakerPhoneOpenCB.setChecked(p.is_speaker_on);
    }

    @Override
    public CallParam getUserParams() {
        String   number      = mNumberET.getText().toString().trim();
        int      count       = Integer.parseInt(mCycleET.getText().toString().trim());
        int      gapTime     = Integer.parseInt(mGapTimeET.getText().toString().trim());
        int      callTime    = Integer.parseInt(mCallTimeET.getText().toString().trim());
        int      callTimeSum = Integer.parseInt(mCallTimeSumET.getText().toString().trim());
        boolean  isSpeakOn   = mIsSpeakerPhoneOpenCB.isChecked();
        String[] numbers     = new String[0];
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
                .setGap_time(gapTime).setIs_speaker_on(isSpeakOn);
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
        mCallRateET.setText(s);
    }

    @Override
    protected int menuResId() {
        return R.menu.outgoing_menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.show_report:
//                mOutGoingPresenter.showReport();
//                break;
//            case R.id.clear_report:
//                mOutGoingPresenter.clearAllReport();
//                break;
            case R.id.export_excel:
                mOutGoingPresenter.exportExcelFile();
                break;
            case R.id.action_abouts:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
