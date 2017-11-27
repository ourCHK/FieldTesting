package com.gionee.autotest.field.ui.throughput.Util;


import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog.Builder;

import com.gionee.autotest.field.R;


public class DialogHelper {

    public static AlertDialog create(Context context, String title, String msg, OnBeforeCreate before) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setIcon(R.mipmap.ic_launcher).setCancelable(false);
        if (msg != null) {
            builder.setMessage(msg);
        }
        before.setOther(builder);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setWindowAnimations(R.style.AppTheme);
        return alertDialog;
    }

    public interface OnBeforeCreate {
         void setOther(AlertDialog.Builder builder);
    }

    public static void dialog(Context context,String title,String content){
        Builder dialog = new Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("了解", null);
        dialog.show();
    }


}
