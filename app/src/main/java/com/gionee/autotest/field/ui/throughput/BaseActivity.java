package com.gionee.autotest.field.ui.throughput;

/*
 *  @项目名：  AutoMonitorWifi 
 *  @包名：    com.gionee.automonitorwifi
 *  @文件名:   BaseActivity
 *  @创建者:   gionee
 *  @创建时间:  2017/1/3 16:49
 *  @描述：    base activity
 */


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.gionee.autotest.field.R;
import com.gionee.autotest.field.ui.throughput.Util.ExportTask;
import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.util.Util;

import java.io.File;

import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROE_RESULT_PATH;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROR_FILE_NAME;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FILE_NAME;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ISLOADING;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.RESULT_PATH;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private Helper mHelper;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLogo();
        mHelper = new Helper();
        util = new Util();
        super.onCreate(savedInstanceState);
    }

    private void setLogo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    /**
     * view init
     */
    protected abstract void initViews();

    /**
     * data init
     */
    protected void initDatas() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_throughput, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                about();
                break;
            case R.id.menu_error:
                error();
                break;
            case R.id.menu_result:
                result();
                break;
            case R.id.menu_goto:
                new ExportTask(this).execute();
                break;
            case R.id.menu_clear:
                if (ISLOADING) {
                    Toast.makeText(this, "正在下载中...请勿进行删除结果操作", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("提示");
                    dialog.setMessage("确定要清空数据?");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Helper.delAllFiles(RESULT_PATH);
                            Helper.delAllFiles(ERROE_RESULT_PATH);
                            onClearData();
                            Toast.makeText(BaseActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void onClearData() {

    }

    /**
     * menu - about
     */
    public void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getResources().getString(R.string.throughputtest));
        builder.setView(AboutView.getAboutView(this, "刘思琦"));
        builder.setPositiveButton("确定", null);
        builder.show();
    }


    public void result() {
//        Intent intent = new Intent(this, ResultActivity.class);
        Intent intent = new Intent(this, ResultStartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        PackageManager packageManager =getPackageManager();
//        Intent intent=packageManager.getLaunchIntentForPackage("com.gionee.filemanager");
//        startActivity(intent);
    }


    public void error() {
        File file = new File(ERROR_FILE_NAME);
        if (file.exists()) {
            util.openExcelByIntent(this, ERROR_FILE_NAME);
        }else {
            Toast.makeText(this,"未发现文件", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("确定要退出应用?");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
    }


}
