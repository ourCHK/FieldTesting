package com.gionee.autotest.field.ui.data_stability;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.util.DataStabilityUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WebViewActivity extends AppCompatActivity implements IWebViewActivity {

    private MyWebView     mWebView;
    private LinearLayout rootLayout;
    private WebViewAction webViewAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hookWebView();
        setContentView(R.layout.activity_web_view);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        mWebView = (MyWebView) findViewById(R.id.webView);
        webViewAction = new WebViewAction(this, mWebView);
        webViewAction.testLoad(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearCache(true);
        rootLayout.removeView(mWebView);
        mWebView.destroy();
        DataStabilityUtil.i("WebViewActivity onDestroy");
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void testFinish(WebViewResultSum webViewResultSum) {
//        setResult(2);
        finish();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("testFinish"));
    }

    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i("WebViewActivity","sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i("WebViewActivity","don't need to hook webView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
//                log.debug("sProviderInstance:{}", sProviderInstance);
                Log.i("WebViewActivity","sProviderInstance:{}");
                field.set("sProviderInstance", sProviderInstance);
            }
//            log.debug("Hook done!");
        } catch (Throwable e) {
//            log.error(e);
            Log.i("WebActivity","error:"+e);
        }
    }

}
