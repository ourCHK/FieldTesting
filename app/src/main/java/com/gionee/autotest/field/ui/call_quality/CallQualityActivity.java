package com.gionee.autotest.field.ui.call_quality;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Viking on 2017/11/22.
 *
 * ui for call quality
 */

public class CallQualityActivity extends BaseActivity implements CallQualityContract.View {

    @BindView(R.id.phone_num)
    EditText phoneNum ;
    @BindView(R.id.phone_num_other)
    EditText phoneNumO ;

    @BindView(R.id.quality_continue_single)
    Button btnContinue ;
    @BindView(R.id.quality_continue_multi)
    Button btnContinueMulti ;
    @BindView(R.id.quality_continue_single_other)
    Button btnContinueO ;
    @BindView(R.id.quality_continue_multi_other)
    Button btnContinueMultiO ;

    @BindView(R.id.quality_low_vol_single)
    Button btnLowVol ;
    @BindView(R.id.quality_low_vol_multi)
    Button btnLowVolMulti ;
    @BindView(R.id.quality_low_vol_single_other)
    Button btnLowVolO ;
    @BindView(R.id.quality_low_vol_multi_other)
    Button btnLowVolMultiO ;

    @BindView(R.id.quality_noise_single)
    Button btnNoise ;
    @BindView(R.id.quality_noise_multi)
    Button btnNoiseMulti ;
    @BindView(R.id.quality_noise_single_other)
    Button btnNoiseO ;
    @BindView(R.id.quality_noise_multi_other)
    Button btnNoiseMultiO ;

    @BindView(R.id.quality_loop_single)
    Button btnLoop ;
    @BindView(R.id.quality_loop_multi)
    Button btnLoopMulti ;
    @BindView(R.id.quality_loop_single_other)
    Button btnLoopO ;
    @BindView(R.id.quality_loop_multi_other)
    Button btnLoopMultiO ;

    @BindView(R.id.quality_no_vol_single)
    Button btnNoVol ;
    @BindView(R.id.quality_no_vol_multi)
    Button btnNoVolMulti ;
    @BindView(R.id.quality_no_vol_single_other)
    Button btnNoVolO ;
    @BindView(R.id.quality_no_vol_multi_other)
    Button btnNoVolMultiO ;

    @BindView(R.id.quality_lose_vol_single)
    Button btnLoseVol ;
    @BindView(R.id.quality_lose_vol_multi)
    Button btnLoseVolMulti ;
    @BindView(R.id.quality_lose_vol_single_other)
    Button btnLoseVolO ;
    @BindView(R.id.quality_lose_vol_multi_other)
    Button btnLoseVolMultiO ;

    @BindView(R.id.quality_current_single)
    Button btnCurrent ;
    @BindView(R.id.quality_current_multi)
    Button btnCurrentMulti ;
    @BindView(R.id.quality_current_single_other)
    Button btnCurrentO ;
    @BindView(R.id.quality_current_multi_other)
    Button btnCurrentMultiO ;

    @BindView(R.id.quality_hao_single)
    Button btnHao ;
    @BindView(R.id.quality_hao_multi)
    Button btnHaoMulti ;
    @BindView(R.id.quality_hao_single_other)
    Button btnHaoO ;
    @BindView(R.id.quality_hao_multi_other)
    Button btnHaoMultiO ;

    @BindView(R.id.quality_lose_call_single)
    Button btnLoseCall ;
    @BindView(R.id.quality_lose_call_multi)
    Button btnLoseCallMulti ;
    @BindView(R.id.quality_lose_call_single_other)
    Button btnLoseCallO ;
    @BindView(R.id.quality_lose_call_multi_other)
    Button btnLoseCallMultiO ;

    @BindView(R.id.call_quality_start)
    Button btnStart ;
    @BindView(R.id.call_quality_end)
    Button btnStop ;

    private View[] mViews ;

    private CallQualityPresenter mPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_call_quality;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mPresenter = new CallQualityPresenter(getApplicationContext()) ;
        super.presenter = mPresenter ;
        mPresenter.onAttach(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViews = new View[]{
                btnContinueMulti, btnContinueMultiO,
                btnLowVolMulti, btnLowVolMultiO,
                btnNoiseMulti, btnNoiseMultiO,
                btnLoopMulti, btnLoopMultiO,
                btnNoVolMulti, btnNoVolMultiO,
                btnLoseVolMulti, btnLoseVolMultiO,
                btnCurrentMulti, btnCurrentMultiO,
                btnHaoMulti, btnHaoMultiO,
                btnLoseCallMulti, btnLoseCallMultiO

        } ;
        setEnableState(mViews, false) ;
        setEnableState(new View[]{btnStop}, false) ;
    }

    private void setEnableState(View view, boolean enable) {
        view.setEnabled(enable);
    }

    private void setEnableState(View[] views, boolean enable) {
        for (View view : views){
            setEnableState(view, enable) ;
        }
    }

    @OnClick({R.id.quality_continue_single, R.id.quality_continue_single_other,
        R.id.quality_low_vol_single, R.id.quality_low_vol_single_other,
        R.id.quality_noise_single, R.id.quality_noise_single_other,
        R.id.quality_loop_single, R.id.quality_loop_single_other,
        R.id.quality_no_vol_single, R.id.quality_no_vol_single_other,
        R.id.quality_lose_vol_single, R.id.quality_lose_vol_single_other,
        R.id.quality_current_single, R.id.quality_current_single_other,
        R.id.quality_hao_single, R.id.quality_hao_single_other,
        R.id.quality_lose_call_single, R.id.quality_lose_call_single_other})
    void onSingleButtonClicked(View view){
        mPresenter.checkRunningSingle(view);
    }

    @OnClick({R.id.quality_continue_multi, R.id.quality_continue_multi_other,
            R.id.quality_low_vol_multi, R.id.quality_low_vol_multi_other,
            R.id.quality_noise_multi, R.id.quality_noise_multi_other,
            R.id.quality_loop_multi, R.id.quality_loop_multi_other,
            R.id.quality_no_vol_multi, R.id.quality_no_vol_multi_other,
            R.id.quality_lose_vol_multi, R.id.quality_lose_vol_multi_other,
            R.id.quality_current_multi, R.id.quality_current_multi_other,
            R.id.quality_hao_multi, R.id.quality_hao_multi_other,
            R.id.quality_lose_call_multi, R.id.quality_lose_call_multi_other})
    void onMultiButtonClicked(View view){
        mPresenter.checkRunningMulti(view);
    }

    @OnClick(R.id.call_quality_start)
    void onStartClicked(View view){
        mPresenter.onStartClicked(phoneNum.getText().toString(), phoneNumO.getText().toString());
    }

    @OnClick(R.id.call_quality_end)
    void onStopClicked(View view){
        mPresenter.onStopClicked();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    public void showErrorPhoneNumMsg() {
        Toast.makeText(this, R.string.call_quality_error_phone_num, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startWithSuccessfully() {
        //set start to next round
        btnStart.setText(R.string.quality_next_round);
        setEnableState(btnStop, true);
    }

    @Override
    public void stopWithSuccessfully() {
        btnStart.setText(R.string.start_testing);
        setEnableState(btnStart, true);
        setEnableState(btnStop, false);
        setEnableState(mViews, false);
    }

    @Override
    public void onNextRoundClicked() {
        setEnableState(mViews, false);
    }

    @Override
    public void showNotRunningMsg() {
        Toast.makeText(this, R.string.call_quality_not_running, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSingleClicked(View view) {
        switch (view.getId()){
            case R.id.quality_continue_single:
                setEnableState(btnContinueMulti, true);
                break ;
            case R.id.quality_continue_single_other:
                setEnableState(btnContinueMultiO, true);
                break ;
            case R.id.quality_low_vol_single:
                setEnableState(btnLowVolMulti, true);
                break ;
            case R.id.quality_low_vol_single_other:
                setEnableState(btnLowVolMultiO, true);
                break ;
            case R.id.quality_noise_single:
                setEnableState(btnNoiseMulti, true);
                break ;
            case R.id.quality_noise_single_other:
                setEnableState(btnNoiseMultiO, true);
                break ;
            case R.id.quality_loop_single:
                setEnableState(btnLoopMulti, true);
                break ;
            case R.id.quality_loop_single_other:
                setEnableState(btnLoopMultiO, true);
                break ;
            case R.id.quality_no_vol_single:
                setEnableState(btnNoVolMulti, true);
                break ;
            case R.id.quality_no_vol_single_other:
                setEnableState(btnNoVolMultiO, true);
                break ;
            case R.id.quality_lose_vol_single:
                setEnableState(btnLoseVolMulti, true);
                break ;
            case R.id.quality_lose_vol_single_other:
                setEnableState(btnLoseVolMultiO, true);
                break ;
            case R.id.quality_current_single:
                setEnableState(btnCurrentMulti, true);
                break ;
            case R.id.quality_current_single_other:
                setEnableState(btnCurrentMultiO, true);
                break ;
            case R.id.quality_hao_single:
                setEnableState(btnHaoMulti, true);
                break ;
            case R.id.quality_hao_single_other:
                setEnableState(btnHaoMultiO, true);
                break ;
            case R.id.quality_lose_call_single:
                setEnableState(btnLoseCallMulti, true);
                break ;
            case R.id.quality_lose_call_single_other:
                setEnableState(btnLoseCallMultiO, true);
                break ;
        }
    }

    @Override
    public void onMultiClicked(View view) {
        switch (view.getId()){
            case R.id.quality_continue_multi:
                setEnableState(btnContinueMulti, false);
                break ;
            case R.id.quality_continue_multi_other:
                setEnableState(btnContinueMultiO, false);
                break ;
            case R.id.quality_low_vol_multi:
                setEnableState(btnLowVolMulti, false);
                break ;
            case R.id.quality_low_vol_multi_other:
                setEnableState(btnLowVolMultiO, false);
                break ;
            case R.id.quality_noise_multi:
                setEnableState(btnNoiseMulti, false);
                break ;
            case R.id.quality_noise_multi_other:
                setEnableState(btnNoiseMultiO, false);
                break ;
            case R.id.quality_loop_multi:
                setEnableState(btnLoopMulti, false);
                break ;
            case R.id.quality_loop_multi_other:
                setEnableState(btnLoopMultiO, false);
                break ;
            case R.id.quality_no_vol_multi:
                setEnableState(btnNoVolMulti, false);
                break ;
            case R.id.quality_no_vol_multi_other:
                setEnableState(btnNoVolMultiO, false);
                break ;
            case R.id.quality_lose_vol_multi:
                setEnableState(btnLoseVolMulti, false);
                break ;
            case R.id.quality_lose_vol_multi_other:
                setEnableState(btnLoseVolMultiO, false);
                break ;
            case R.id.quality_current_multi:
                setEnableState(btnCurrentMulti, false);
                break ;
            case R.id.quality_current_multi_other:
                setEnableState(btnCurrentMultiO, false);
                break ;
            case R.id.quality_hao_multi:
                setEnableState(btnHaoMulti, false);
                break ;
            case R.id.quality_hao_multi_other:
                setEnableState(btnHaoMultiO, false);
                break ;
            case R.id.quality_lose_call_multi:
                setEnableState(btnLoseCallMulti, false);
                break ;
            case R.id.quality_lose_call_multi_other:
                setEnableState(btnLoseCallMultiO, false);
                break ;
        }
    }
}
