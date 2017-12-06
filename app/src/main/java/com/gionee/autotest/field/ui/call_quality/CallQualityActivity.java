package com.gionee.autotest.field.ui.call_quality;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.common.TimeUtil;
import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.about.AboutActivity;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.ui.call_quality.entity.CallQualityConstant;
import com.gionee.autotest.field.ui.call_quality.model.ReportFile;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Util;
import com.gionee.autotest.field.views.ListDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    protected int menuResId() {
        return R.menu.menu_signal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                mPresenter.fetchResults();
                return true ;
            case R.id.about:
                startActivity(AboutActivity.getAboutIntent(this, getString(R.string.signal_about), true));
                return true;
            case android.R.id.home :
                handleBackPressedAction() ;
                return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBackPressedAction(){
        if (btnStop.isEnabled()){
            mPresenter.handleBackPressedAction(this);
        }else{
            super.onBackPressed();
        }
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

    @OnClick({R.id.quality_continue_single,
        R.id.quality_low_vol_single,
        R.id.quality_noise_single,
        R.id.quality_loop_single,
        R.id.quality_no_vol_single,
        R.id.quality_lose_vol_single,
        R.id.quality_current_single,
        R.id.quality_hao_single,
        R.id.quality_lose_call_single})
    void onSingleButtonClicked(View view){
        int quality_type = CallQualityConstant.QUALITY_TYPE_UNKNOWN ;
        switch (view.getId()){
            case R.id.quality_continue_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_CONTINUE ;
                break ;
            case R.id.quality_low_vol_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOW_VOL ;
                break ;
            case R.id.quality_noise_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_NOISE ;
                break ;
            case R.id.quality_loop_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOOP ;
                break ;
            case R.id.quality_no_vol_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_NO_VOL ;
                break ;
            case R.id.quality_lose_vol_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_VOL ;
                break ;
            case R.id.quality_current_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_CURRENT ;
                break ;
            case R.id.quality_hao_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_HAO ;
                break ;
            case R.id.quality_lose_call_single :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_CALL ;
                break ;
        }
        mPresenter.checkRunningSingle(view, CallQualityConstant.PHONE_NUM_1, quality_type, CallQualityConstant.EVENT_TYPE_SINGLE);
    }

    @OnClick({R.id.quality_continue_multi,
            R.id.quality_low_vol_multi,
            R.id.quality_noise_multi,
            R.id.quality_loop_multi,
            R.id.quality_no_vol_multi,
            R.id.quality_lose_vol_multi,
            R.id.quality_current_multi,
            R.id.quality_hao_multi,
            R.id.quality_lose_call_multi})
    void onMultiButtonClicked(View view){
        int quality_type = CallQualityConstant.QUALITY_TYPE_UNKNOWN ;
        switch (view.getId()){
            case R.id.quality_continue_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_CONTINUE ;
                break ;
            case R.id.quality_low_vol_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOW_VOL ;
                break ;
            case R.id.quality_noise_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_NOISE ;
                break ;
            case R.id.quality_loop_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOOP ;
                break ;
            case R.id.quality_no_vol_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_NO_VOL ;
                break ;
            case R.id.quality_lose_vol_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_VOL ;
                break ;
            case R.id.quality_current_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_CURRENT ;
                break ;
            case R.id.quality_hao_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_HAO ;
                break ;
            case R.id.quality_lose_call_multi :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_CALL ;
                break ;
        }
        mPresenter.checkRunningMulti(view, CallQualityConstant.PHONE_NUM_1, quality_type, CallQualityConstant.EVENT_TYPE_MULTI);
    }

    @OnClick({R.id.quality_continue_single_other,
            R.id.quality_low_vol_single_other,
            R.id.quality_noise_single_other,
            R.id.quality_loop_single_other,
            R.id.quality_no_vol_single_other,
            R.id.quality_lose_vol_single_other,
            R.id.quality_current_single_other,
            R.id.quality_hao_single_other,
            R.id.quality_lose_call_single_other})
    void onSingleOtherButtonClicked(View view){
        int quality_type = CallQualityConstant.QUALITY_TYPE_UNKNOWN ;
        switch (view.getId()){
            case R.id.quality_continue_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_CONTINUE ;
                break ;
            case R.id.quality_low_vol_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOW_VOL ;
                break ;
            case R.id.quality_noise_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_NOISE ;
                break ;
            case R.id.quality_loop_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOOP ;
                break ;
            case R.id.quality_no_vol_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_NO_VOL;
                break ;
            case R.id.quality_lose_vol_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_VOL ;
                break ;
            case R.id.quality_current_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_CURRENT ;
                break ;
            case R.id.quality_hao_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_HAO ;
                break ;
            case R.id.quality_lose_call_single_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_CALL ;
                break ;
        }
        mPresenter.checkRunningSingle(view, CallQualityConstant.PHONE_NUM_2, quality_type, CallQualityConstant.EVENT_TYPE_SINGLE);
    }

    @OnClick({R.id.quality_continue_multi_other,
            R.id.quality_low_vol_multi_other,
            R.id.quality_noise_multi_other,
            R.id.quality_loop_multi_other,
            R.id.quality_no_vol_multi_other,
            R.id.quality_lose_vol_multi_other,
            R.id.quality_current_multi_other,
            R.id.quality_hao_multi_other,
            R.id.quality_lose_call_multi_other})
    void onMultiOtherButtonClicked(View view){
        int quality_type = CallQualityConstant.QUALITY_TYPE_UNKNOWN ;
        switch (view.getId()){
            case R.id.quality_continue_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_CONTINUE ;
                break ;
            case R.id.quality_low_vol_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOW_VOL ;
                break ;
            case R.id.quality_noise_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_NOISE ;
                break ;
            case R.id.quality_loop_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOOP ;
                break ;
            case R.id.quality_no_vol_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_NO_VOL ;
                break ;
            case R.id.quality_lose_vol_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_VOL ;
                break ;
            case R.id.quality_current_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_CURRENT ;
                break ;
            case R.id.quality_hao_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_HAO ;
                break ;
            case R.id.quality_lose_call_multi_other :
                quality_type = CallQualityConstant.QUALITY_TYPE_LOSE_CALL ;
                break ;
        }
        mPresenter.checkRunningMulti(view, CallQualityConstant.PHONE_NUM_2, quality_type, CallQualityConstant.EVENT_TYPE_MULTI);
    }

    @OnClick(R.id.call_quality_start)
    void onStartClicked(){
        //FOR TEST ONLY!!!
/*        String quality_dir = Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME + File.separator + Constant.CALL_QUALITY_HOME;
        File target = new File(quality_dir, Constant.CALL_QUALITY_DATA_NAME) ;
        File destination = new File(quality_dir,
                String.format(Constant.EXPORT_CALL_QUALITY_DATA_NAME, TimeUtil.getTime("yyyy-MM-dd_HH-mm-ss"))) ;
        mPresenter.doExport(phoneNum.getText().toString(), phoneNumO.getText().toString(),
                target, destination);*/
        mPresenter.onStartClicked(phoneNum.getText().toString(), phoneNumO.getText().toString());
    }

    @OnClick(R.id.call_quality_end)
    void onStopClicked(){
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

        String quality_dir = Environment.getExternalStorageDirectory()
                + File.separator + Constant.HOME + File.separator + Constant.CALL_QUALITY_HOME
                + File.separator + Preference.getString(getApplicationContext(), Constant.CALL_QUALITY_LAST_TIME) ;
        File target = new File(quality_dir, Constant.CALL_QUALITY_DATA_NAME) ;
        File destination = new File(quality_dir,
                String.format(Constant.EXPORT_CALL_QUALITY_DATA_NAME, TimeUtil.getTime("yyyy-MM-dd_HH-mm-ss"))) ;
        Log.i(Constant.TAG, "call quality target path : " + target.getAbsolutePath()) ;
        Log.i(Constant.TAG, "call quality destination path : " + destination.getAbsolutePath()) ;
        mPresenter.doExport(phoneNum.getText().toString(), phoneNumO.getText().toString(),
                target, destination);
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

    @Override
    public void showExportErrorInformation(int errorCode) {
        switch (errorCode){
            case CallQualityContract.EXPORT_ERROR_CODE_NO_DATA :
                Toast.makeText(this, R.string.export_signal_error_no_data, Toast.LENGTH_SHORT).show();
                break ;
            case CallQualityContract.EXPORT_ERROR_CODE_FAIL_CREATE_DESTINATION_FILE:
                Toast.makeText(this, R.string.export_signal_error_create_excel_failure, Toast.LENGTH_SHORT).show();
                break ;
            case CallQualityContract.EXPORT_ERROR_CODE_FAILURE:
                Toast.makeText(this, R.string.export_signal_error_failure, Toast.LENGTH_SHORT).show();
                break ;
        }
    }

    @Override
    public void showExportSuccessInformation(String path) {
        Util.showFinishDialog(this, path);
    }

    @Override
    public void onBackPressed() {
        handleBackPressedAction() ;
    }

    @Override
    public void showEmptyReport() {
        Toast.makeText(this, R.string.empty_report_notice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showReport(final List<ReportFile> files) {
        final List<String> items = new ArrayList<>() ;
        for (ReportFile file : files){
            items.add(file.getDirectory()) ;
        }
        //show dialog
        new ListDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info_outline_white_36dp)
                .setTitle(R.string.choose_report_title)
                .setItems(items, new ListDialog.OnItemSelectedListener<String>(){

                    @Override
                    public void onItemSelected(int position, String item) {
                        String path = null ;
                        for (ReportFile file : files){
                            if (file.getDirectory().endsWith(item)){
                                path = file.getFilePath() ;
                            }
                        }
                        if (path == null || "".equals(path)){
                            Toast.makeText(CallQualityActivity.this, R.string.open_report_failure, Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        Util.openExcelByIntent(CallQualityActivity.this, path);
                    }
                })
                .show();
    }
}
