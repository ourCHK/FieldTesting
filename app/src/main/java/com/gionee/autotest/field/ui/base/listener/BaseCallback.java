package com.gionee.autotest.field.ui.base.listener;

/**
 * Created by viking on 11/9/17.
 *
 * base callback for all success and failure situation
 */

public interface BaseCallback<T> {

    void onSuccess(T t);

    void onFail();
}
