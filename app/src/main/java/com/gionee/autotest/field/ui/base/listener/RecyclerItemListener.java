package com.gionee.autotest.field.ui.base.listener;

public interface RecyclerItemListener<T> {

    void onItemClick(T t, int position);

    void onItemLongClick(T t, int position);
}