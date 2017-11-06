package com.gionee.autotest.field.ui.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import butterknife.Unbinder;

/**
 * Created by viking on 11/6/17.
 *
 * all activity should extend this class
 */

public abstract class BaseActivity extends AppCompatActivity{

    private Unbinder mUnbinder ;

    @LayoutRes
    protected abstract int layoutResId();


    @MenuRes
    protected int menuResId(){
        return 0 ;
    }

    /**
     * register a new Unbinder for view injection
     * @param mUnbinder Unbinder instance to register
     */
    protected void setUnBinder(Unbinder mUnbinder){
        this.mUnbinder = mUnbinder ;
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
}
