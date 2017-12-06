package com.gionee.autotest.field.ui.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;
import com.gionee.autotest.field.util.Constant;

import butterknife.BindView;

/**
 * Created by viking on 11/20/17.
 *
 * entry for about
 */

public class AboutActivity extends BaseActivity implements AboutContract.View{

    @BindView(R.id.about_logo)
    ImageView mLogo ;

    @BindView(R.id.about_version)
    TextView mInfo ;

    AboutPresenter aboutPresenter ;

    private static final String EXTRA_SHOW_LOGO = "show_logo" ;
    private static final String EXTRA_MESSAGE   = "about_msg" ;

    public static Intent getAboutIntent(Context context, String message, boolean showLogo){
        Intent about = new Intent(context, AboutActivity.class) ;
        about.putExtra(EXTRA_SHOW_LOGO, showLogo) ;
        about.putExtra(EXTRA_MESSAGE, message) ;
        return about ;
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        aboutPresenter = new AboutPresenter() ;
        super.presenter = aboutPresenter ;
        aboutPresenter.onAttach(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showLogo = getIntent().getBooleanExtra(EXTRA_SHOW_LOGO, true) ;
        String  message  = getIntent().getStringExtra(EXTRA_MESSAGE) ;
        if (!showLogo){
            mLogo.setVisibility(View.GONE);
        }
        mInfo.setText(message != null ? message : "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aboutPresenter.onDetach();
    }
}
