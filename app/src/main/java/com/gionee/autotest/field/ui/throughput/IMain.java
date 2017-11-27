package com.gionee.autotest.field.ui.throughput;

import android.content.Context;

public interface IMain {
    Context getContext();

    void setViewEnabled();

    void dialogNetEnable();

    void updateUseTimeTv(String text);

    void updateProgressBar(int i);

    void showDialog(String tips, String msg);

    void setInitializeView();

    void showSecial(String secial);
}
