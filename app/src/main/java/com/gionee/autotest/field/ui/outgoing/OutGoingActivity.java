package com.gionee.autotest.field.ui.outgoing;


import android.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.EditText;

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
    private OutGoingPresenter mOutGoingPresenter;

    @OnClick(R.id.out_going_start)
    public void OutGoingStartClicked() {
        mOutGoingPresenter.startCallTest();
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
        mCycleET.setText(p.cycle);
        mGapTimeET.setText(p.gap_time);
        mCallTimeET.setText(p.call_time);
        mCallTimeSumET.setText(p.call_time_sum);
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
                builder.setPositiveButton("确定",null);
            }
        }).show();
    }
}
