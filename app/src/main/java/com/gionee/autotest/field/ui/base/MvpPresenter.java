package com.gionee.autotest.field.ui.base;

/**
 * Created by viking on 11/6/17.
 *
 * Every presenter in the app must either implement this interface or extend BasePresenter indicating
 * the BaseView type that wants to be attached with.
 */

public interface MvpPresenter<V extends BaseView> {

    void onAttach(V view) ;

    void onDetach() ;
}
