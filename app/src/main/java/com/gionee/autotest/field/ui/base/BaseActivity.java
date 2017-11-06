package com.gionee.autotest.field.ui.base;

import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;

/**
 * Created by viking on 11/6/17.
 *
 * all activity should extend this class
 */

public abstract class BaseActivity extends AppCompatActivity{

    private Unbinder mUnbinder ;

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
}
