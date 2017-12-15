package com.gionee.autotest.field.util;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.data.db.model.AppList;
import com.gionee.autotest.field.views.NoticeInfoDialog;
import com.gionee.autotest.field.views.StandardDialog;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

/**
 * Created by viking on 11/7/17.
 * <p>
 * Utility method for application
 */

public class Util {
    public static <T> T[] toArray(List<T> list, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static AppList parseAppJsonFile(Context ctx) {
        try {
            StringBuilder     content     = new StringBuilder();
            InputStream       inputStream = ctx.getResources().getAssets().open("apps.json");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader    bufReader   = new BufferedReader(inputReader);
            String            line;
            while ((line = bufReader.readLine()) != null) {
                content.append(line);
            }
            if (!content.toString().equals("")) {
                return new Gson().fromJson(content.toString(), AppList.class);
            }
        } catch (Exception e) {
            Log.i(Constant.TAG, "Parsing apps.json has an exception ： " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static final Comparator<App> APP_COMPARATOR = new Comparator<App>() {
        private final Collator mCollator = Collator.getInstance();

        @Override
        public int compare(App obj1, App obj2) {
            String key1 = obj1.getKey() + "";
            String key2 = obj2.getKey() + "";
            return mCollator.compare(key1, key2);
        }
    };

    public static ProgressDialog showLoadingDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
/*        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }*/
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            if (message != null) {
                progressDialog.setMessage(message);
            }
        } else {
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            if (message != null) {
                progressDialog.setMessage(message);
            }
            progressDialog.show();
        }

        return progressDialog;
    }

    public static void showFinishDialog(final Context context, final String path) {
/*        new NoticeInfoDialog(context)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info_outline_white_36dp)
                .setTitle(R.string.finished_title)
                .setMessage(message)
                .show();*/
        new StandardDialog(context)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info_outline_white_36dp)
                .setTitle(R.string.finished_title)
                .setMessage(String.format(context.getString(R.string.export_signal_success), path))
                .setPositiveButton(R.string.open_msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openExcelByIntent(context, path);
                    }
                })
                .setNegativeButton(R.string.cancel_msg, null)
                .show();
    }

    public static void showNoticeDialog(Context context) {
        new NoticeInfoDialog(context)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.ic_info_outline_white_36dp)
                .setMessage(R.string.info_message)
                .show();
    }

    public static void openExcelByIntent(Context context, String excelPath) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri uri = Uri.fromFile(new File(excelPath));
            Uri uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", new File(excelPath));
            intent.setDataAndType(uri, "application/vnd.ms-excel");
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.go_to_file_manager, Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean isSystemPermit() {
        return Process.myUid() == Process.SYSTEM_UID;
    }
}
