package com.gionee.autotest.field.ui.data_stability;


import android.content.Context;

public interface IWebViewActivity {
    Context getContext();

    void testFinish(WebViewResultSum webViewResultSum);
}
