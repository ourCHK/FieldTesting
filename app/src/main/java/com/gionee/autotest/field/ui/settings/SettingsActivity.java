package com.gionee.autotest.field.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.base.BaseActivity;

/**
 * Created by viking on 11/20/17.
 *
 * entry for settings
 */

public class SettingsActivity extends BaseActivity implements SettingsContract.View{

    private SettingsPresenter mPresenter ;

    @Override
    protected int layoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected boolean isDisplayHomeUpEnabled() {
        return true;
    }

    @Override
    protected void initializePresenter() {
        mPresenter = new SettingsPresenter() ;
        super.presenter = mPresenter ;
        mPresenter.onAttach(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new SettingsFragment())
                .commit() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

}
