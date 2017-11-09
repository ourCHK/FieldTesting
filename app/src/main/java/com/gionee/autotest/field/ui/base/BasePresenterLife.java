package com.gionee.autotest.field.ui.base;

import android.os.Bundle;

import com.gionee.autotest.field.ui.base.listener.PresenterLife;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by viking on 11/6/17.
 *
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also keeping a reference to the BaseView that can be accessed
 * from the children classed by calling getMvpView().
 */

public class BasePresenterLife<V extends BaseView> implements PresenterLife<V> {

    private WeakReference<V> mBaseView ;

    protected AtomicBoolean isViewAlive = new AtomicBoolean();

    @Override
    public void onAttach(V view) {
        this.mBaseView = new WeakReference<>(view) ;
    }

    @Override
    public void onDetach() {
        if (this.mBaseView != null){
            this.mBaseView.clear();
            this.mBaseView = null ;
        }
    }

    public boolean isViewAttached(){
        return this.mBaseView != null && this.mBaseView.get() != null ;
    }

    public V getView(){
        return mBaseView.get() ;
    }

    public void start() {
        isViewAlive.set(true);
    }

    public void finalizeView() {
        isViewAlive.set(false);
    }

    public void initialize(Bundle extras) {

    }

}
