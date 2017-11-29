package com.gionee.autotest.field.ui.data_stability;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;


public class MyWebView extends WebView {

    private OnLoadFinishListener mOnLoadFinishListener;

    public interface OnLoadFinishListener {
        public void onLoadFinish();
    }

    private boolean isRendered = false;

    public MyWebView(Context context) {
        super(context);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRendered) {
            isRendered = getContentHeight() > 10;
            if (mOnLoadFinishListener != null) {
                mOnLoadFinishListener.onLoadFinish();
            }
        }
    }

    public void setOnLoadFinishListener(OnLoadFinishListener onLoadFinishListener) {
        this.mOnLoadFinishListener = onLoadFinishListener;
    }
}
