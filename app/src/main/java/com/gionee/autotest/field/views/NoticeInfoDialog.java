package com.gionee.autotest.field.views;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Button;

import com.gionee.autotest.field.R;

/**
 * Created by viking on 11/27/17.
 *
 */
public class NoticeInfoDialog extends AbsDialog<NoticeInfoDialog> {

    private Button confirmButton;

    private int infoDialogId;

    public NoticeInfoDialog(Context context) {
        super(context);
    }

    public NoticeInfoDialog(Context context, int theme) {
        super(context, theme);
    }

    {
        confirmButton = findView(R.id.ld_btn_confirm);
        confirmButton.setOnClickListener(new ClickListenerDecorator(null, true));
        infoDialogId = -1;
    }

    public NoticeInfoDialog setConfirmButtonText(@StringRes int text) {
        return setConfirmButtonText(string(text));
    }

    public NoticeInfoDialog setConfirmButtonText(String text) {
        confirmButton.setText(text);
        return this;
    }

    public NoticeInfoDialog setConfirmButtonColor(int color) {
        confirmButton.setTextColor(color);
        return this;
    }

    @Override
    protected int getLayout() {
        return R.layout.notice_info;
    }

}
