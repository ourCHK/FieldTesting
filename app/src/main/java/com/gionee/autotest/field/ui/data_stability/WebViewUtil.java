package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gionee.autotest.field.util.DataStabilityUtil;


public class WebViewUtil extends WebViewClient implements MyWebView.OnLoadFinishListener {

    private MyWebView mWebView;
    private CallBack callBack = null;
    private WebViewResult webViewResult;
    private boolean redirect = false;
    private boolean start    = false;

    public WebViewUtil(MyWebView webView) {
        mWebView = webView;
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(this);
        mWebView.setOnLoadFinishListener(this);
    }

    public void load(String urls, CallBack callBack) {
        if (callBack != null) {
            this.callBack = callBack;
        }
        webViewResult = new WebViewResult();
        redirect = false;
        mWebView.loadUrl(urls);
        start = true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
//        DataStabilityUtil.i("shouldOverrideUrlLoading=" + url);
        redirect = false;
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//        DataStabilityUtil.i("onPageStarted");
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
//        DataStabilityUtil.i("onPageFinished");
        if (start) {
            start = false;
            if (redirect) {
                redirect = false;
            } else {
                webViewResult.setResult(webViewResult.errorMsg.equals(""));
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        SystemClock.sleep(3000);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (callBack != null) {
                            callBack.todo(webViewResult);
                        }
                    }
                }.execute();

            }
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DataStabilityUtil.i("onReceivedError=" + error.getErrorCode() + "des=" + error.getDescription());
            webViewResult.setErrorCode(error.getErrorCode()).setErrorMsg(error.getDescription().toString());
        }
    }

    @Override
    public void onLoadFinish() {
//        DataStabilityUtil.i("onLoadFinish");
    }


    public class WebViewResult {
        public boolean result    = true;
        public int     errorCode = 0;
        public String  errorMsg  = "";

        public WebViewResult() {
        }

        public WebViewResult setResult(boolean result) {
            this.result = result;
            return this;
        }

        public WebViewResult setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public WebViewResult setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }
    }
}
