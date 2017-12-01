package com.gionee.autotest.field.ui.throughput.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;

import com.gionee.autotest.field.R;

import java.io.File;
import java.util.ArrayList;

import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FILE_NAME;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FILE_NAME_LOOK;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.RESULT_PATH;
import static com.gionee.autotest.field.ui.throughput.Util.FileUtil.getExcelFileIntent;

public class ExportTask extends AsyncTask<Integer, Void, Void> {
    private static Context mContext;
    private ArrayList<String> data;

    public ExportTask(Context context) {
        this.mContext = context;

    }

    private ProgressDialog dialog;
    public static boolean isExported = true;


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.exportReports));
        dialog.setMessage("正在导出，请稍候……");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setIndeterminate(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showExportResultDialog();
            }
        });
        dialog.getWindow().setWindowAnimations(R.style.AppTheme);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Integer... params) {
        File file = new File(RESULT_PATH, FILE_NAME);
        if (!file.exists()) {
            boolean mkdirs = isExported = file.mkdirs();
            Helper.i("创建" + (mkdirs ? "成功" : "失败"));
            if (!mkdirs) {
                return null;
            }
        }
        isExported = JExcelUtil.exportExcel(new File(FILE_NAME), "success");
        SystemClock.sleep(1000);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

    public static void showExportResultDialog() {
        DialogHelper.create(mContext, "结果存储路径", isExported ? FILE_NAME_LOOK : "导出失败", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(android.app.AlertDialog.Builder builder) {
                boolean is = Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
              Helper.i("if判断的结果是 -- "+is+"&&"+isExported);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && isExported) {
                    builder.setPositiveButton("打开", listener);
                }
                builder.setNegativeButton("确定", null);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        Intent excelFileIntent =getExcelFileIntent(new File(FILE_NAME));
                        mContext.startActivity(excelFileIntent);
                    } catch (Exception e) {
                        Helper.i(e.toString());
                    }
                }
            };
        }).show();
    }
}
