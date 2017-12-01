package com.gionee.autotest.field.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by viking on 11/6/17.
 *
 * all activity should extend this class
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseView{

    protected BasePresenter presenter;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Unbinder mUnbinder ;

    private ProgressDialog mProgressDialog;

    @LayoutRes
    protected abstract int layoutResId();

    @MenuRes
    protected int menuResId(){
        return 0 ;
    }

    protected abstract void initializePresenter();

    /**
     * register a new Unbinder for view injection
     * @param mUnbinder Unbinder instance to register
     */
    protected void setUnBinder(Unbinder mUnbinder){
        this.mUnbinder = mUnbinder ;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId());
        setUnBinder(ButterKnife.bind(this));
        initializePresenter();
        initializeToolbar();
        if (presenter != null) {
            presenter.initialize(getIntent().getExtras());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.finalizeView();
        }
    }

    protected void initializeToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(isDisplayHomeUpEnabled());
                ab.setDisplayShowTitleEnabled(isDisplayShowTitleEnabled());
            }
        }
    }

    protected boolean isDisplayHomeUpEnabled() {
        return false;
    }

    protected boolean isDisplayShowTitleEnabled() {
        return true;
    }

    @Override
    protected void onDestroy(){
        //unregister Unbinder before onDestroy
        if (mUnbinder != null){
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        if (menuResId() != 0) {
            getMenuInflater().inflate(menuResId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showLoading(String message) {
        hideLoading();
        mProgressDialog = Util.showLoadingDialog(this, message);
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }
}
