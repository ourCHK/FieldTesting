package com.gionee.autotest.field.ui.data_stability;



public class VerifyWebViewTest implements CallBack {
    private MyWebView webView;
    private WebViewUtil webViewUtil;
    private String url;
    private int verifyCount;
    private CallBack c;
    private int verifyIndex = 0;

    public VerifyWebViewTest(MyWebView webView, String url,int verifyCount, CallBack c) {
        this.webView = webView;
        webViewUtil = new WebViewUtil(webView);
        this.url = url;
        this.verifyCount = verifyCount;
        this.c = c;
    }

    public void test() {
        webViewUtil.load(url, this);
    }

    @Override
    public void todo(Object o) {
        WebViewUtil.WebViewResult result = (WebViewUtil.WebViewResult) o;
        if (!result.result) {
            if (verifyIndex < 5) {
                test();
            } else {
                if (c != null) {
                    c.todo(result);
                }
            }
        } else {
            if (c != null) {
                c.todo(result);
            }
        }
    }
}
