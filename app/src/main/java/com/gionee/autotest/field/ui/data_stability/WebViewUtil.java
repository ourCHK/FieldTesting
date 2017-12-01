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

import com.gionee.autotest.field.FieldApplication;
import com.gionee.autotest.field.ui.signal.entity.SimSignalInfo;
import com.gionee.autotest.field.util.DataStabilityUtil;
import com.gionee.autotest.field.util.SignalHelper;


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

            SimSignalInfo simSignalInfo1 = SignalHelper.getInstance(FieldApplication.getContext()).getSimSignalInfo(SignalHelper.SIM_CARD_0);
            SimSignalInfo simSignalInfo2 = SignalHelper.getInstance(FieldApplication.getContext()).getSimSignalInfo(SignalHelper.SIM_CARD_1);
            if (simSignalInfo1 != null){ //获取Sim1卡信息
                DataStabilityUtil.i(simSignalInfo1.toString());
                webViewResult.setActive1(simSignalInfo1.mIsActive);
                webViewResult.setLevel1(simSignalInfo1.mLevel);
                webViewResult.setNetType1(simSignalInfo1.mNetType);
                webViewResult.setSignal1(simSignalInfo1.mSignal);
                if (simSignalInfo1.mOperator.contains("CMCC"))
                    webViewResult.setOperator1("移动");
                else if(simSignalInfo1.mOperator.contains("UNICOM"))
                    webViewResult.setOperator1("联通");
                else
                    webViewResult.setOperator1("电信");
            } else {
                DataStabilityUtil.i("无法获取Sim1信息,请检查是否有插入Sim卡");
            }

            if (simSignalInfo2 != null){ //获取Sim2卡信息
                DataStabilityUtil.i(simSignalInfo2.toString());
                webViewResult.setActive2(simSignalInfo2.mIsActive);
                webViewResult.setLevel2(simSignalInfo2.mLevel);
                webViewResult.setNetType2(simSignalInfo2.mNetType);
                webViewResult.setSignal2(simSignalInfo2.mSignal);
                if (simSignalInfo2.mOperator.contains("CMCC"))
                    webViewResult.setOperator2("移动");
                else if(simSignalInfo2.mOperator.contains("UNICOM"))
                    webViewResult.setOperator2("联通");
                else
                    webViewResult.setOperator2("电信");
            } else {
                DataStabilityUtil.i("无法获取Sim2信息,请检查是否有插入Sim卡");
            }
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

        public boolean isActive1 = false;
        public int level1 = -1;
        public String netType1 = "N/A";
        public String signal1 = "N/A";
        public String operator1 = "N/A";

        public boolean isActive2 = false;
        public int level2 = -1;
        public String netType2 = "N/A";
        public String signal2 = "N/A";
        public String operator2 = "N/A";



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

        public void setActive1(boolean active1) {
            isActive1 = active1;
        }

        public void setLevel1(int level1) {
            this.level1 = level1;
        }

        public void setNetType1(String netType1) {
            this.netType1 = netType1;
        }

        public void setSignal1(String signal1) {
            this.signal1 = signal1;
        }

        public void setOperator1(String operator1) {
            this.operator1 = operator1;
        }


        public boolean isActive2() {
            return isActive2;
        }

        public void setActive2(boolean active2) {
            isActive2 = active2;
        }

        public int getLevel2() {
            return level2;
        }

        public void setLevel2(int level2) {
            this.level2 = level2;
        }

        public String getNetType2() {
            return netType2;
        }

        public void setNetType2(String netType2) {
            this.netType2 = netType2;
        }

        public String getSignal2() {
            return signal2;
        }

        public void setSignal2(String signal2) {
            this.signal2 = signal2;
        }

        public String getOperator2() {
            return operator2;
        }

        public void setOperator2(String operator2) {
            this.operator2 = operator2;
        }
    }
}
