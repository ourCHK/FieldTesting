package com.gionee.autotest.field.ui.call_quality;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

import butterknife.BindView;

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
        mPresenter = new CallQualityPresenter() ;
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

    private void setEnableState(View[] views, boolean enable) {
        for (View view : views){
            view.setEnabled(enable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }
}
