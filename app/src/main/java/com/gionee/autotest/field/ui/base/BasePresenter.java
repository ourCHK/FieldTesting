package com.gionee.autotest.field.ui.base;

/**
 * Created by viking on 11/6/17.
 *
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also keeping a reference to the BaseView that can be accessed
 * from the children classed by calling getMvpView().
 */

public class BasePresenter<V extends BaseView> implements MvpPresenter<V> {

    private V mBaseView ;

    @Override
    public void onAttach(V view) {
        this.mBaseView = view ;
    }

    @Override
    public void onDetach() {
        this.mBaseView = null ;
    }

    public boolean isViewAttached(){
        return this.mBaseView != null ;
    }

    public V getMvpView(){
        return mBaseView ;
    }


}
