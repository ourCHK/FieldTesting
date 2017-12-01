package com.gionee.autotest.field.ui.throughput;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.ui.throughput.Util.DialogHelper;
import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.util.Util;

import static android.R.layout.simple_spinner_item;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.EIGHTY_EIGHT_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.EIGHT_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROE_RESULT_PATH;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ERROR_FILE_NAME;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FIFTY_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FOUR_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.FRTY_FOUR_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.INITIALIZE_VIEW;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ISLOADING;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.NET_LINK_TIMEOUT;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.ONE_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.SET_VIEWS_ENABLES;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.SIX_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.TEN_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.TEST_UPLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.TWENTY_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.TWO_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.TWO_TWo_HUNDRED_DOWNLOAD_URL;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.WAIT_STOP;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.isGioneeWeb;
import static com.gionee.autotest.field.ui.throughput.Util.Helper.isNetWorkEnable;

public class MainActivity extends BaseActivity implements View.OnClickListener, IMain, AdapterView.OnItemSelectedListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Spinner mDownloadSystem, mWebSystem, mSize;
    private String mDownStr, mWebStr, mSizeStr, uri, fileName, mDownTimesStr, mWaitTimeStr, down;
    private EditText mDownTimes, mWaitTime;
    private int mDownTimesInt, mWaitTimeInt;
    private ProgressBar mTenProgress;
    private MainPresenter mPresenter;
    private Button mStart;
    private TextView mTV, mShow;
    private String[] d = {"上传", "下载"};
    private String[] w_1 = {"内网", "外网"};
    private String[] w_2 = {"外网"};
    private String[] s_1 = {"10M", "20M", "50M", "100M", "200M"};
    private String[] s_2 = {"44MB", "88MB", "430MB", "686MB", "800MB", "2.73GB"};
    private Map<String, String> mMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throughput);
        verifyStoragePermissions(this);
        startSignalCollectService();
        initViews();
        mPresenter = new MainPresenter(this);
    }

    @Override
    public void onClick(View v) {
//        if (isWifiEnabled(this)) {
        Helper.i("当前网络的状态是否可用" + isNetWorkEnable(this));
        if (isNetWorkEnable(this)) {
            if (checkView()) {
                switch (v.getId()) {
                    case R.id.start:
                        Helper.i("Isloading的值为：" + ISLOADING);
                        if (!ISLOADING) {
                            ISLOADING = true;
                            down = (String) mDownloadSystem.getSelectedItem();
                            String size = (String) mSize.getSelectedItem();
                            if (mWebStr.equals("内网")) {
                                isGioneeWeb = true;
                            }
                            String start = Helper.getTime();
                            Preference.putString("start", start);
                            Preference.putInt("serial", mDownTimesInt);
                            mPresenter.download(uri, fileName, size, down, mWebStr, mWaitTimeInt, mDownTimesInt);
                            mStart.setText("停止测试");
                            break;
                        } else {
                            if (WAIT_STOP) {
                                if (down.equals("下载")) {
                                    File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + fileName);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    Helper.i("是否删除成功" + !file.exists());
                                }
                            }
                            ISLOADING = false;
                            mStart.setText("开始测试");
                            Process.killProcess(Process.myPid());

                        }
                        updateViews();
//                        updateViews(false);
                }
            }
        } else {
            DialogHelper.dialog(this, "提醒", "请连接一个可用网络（WiFi或者数据连接）");
        }
    }

    public void updateViews() {
        if (mStart != null) {
            mStart.setText(ISLOADING ? "停止测试" : "开始测试");
        }
    }

//    //更改按钮状态
//    public void updateViews(boolean isEnable) {
//        mStart.setEnabled(isEnable);
//    }

    //实时判断有没有SD卡权限
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    //初始化控件
    public void initViews() {
        ISLOADING = false;
        WAIT_STOP = false;
        mShow = (TextView) findViewById(R.id.show);
        mDownTimes = (EditText) findViewById(R.id.download_times);
        mWaitTime = (EditText) findViewById(R.id.wait_time);
        mDownTimes.setText(Preference.getInt("downTime", 1) + "");
        mDownTimes.requestFocus();
        mWaitTime.setText(Preference.getInt("waitTime", 0) + "");
        mTenProgress = (ProgressBar) findViewById(R.id.ten_progress);
        mMap.put("10M", TEN_DOWNLOAD_URL);
        mMap.put("20M", TWENTY_DOWNLOAD_URL);
        mMap.put("50M", FIFTY_DOWNLOAD_URL);
        mMap.put("100M", ONE_HUNDRED_DOWNLOAD_URL);
        mMap.put("200M", TWO_HUNDRED_DOWNLOAD_URL);
        mMap.put("44MB", FRTY_FOUR_DOWNLOAD_URL);
        mMap.put("88MB", EIGHTY_EIGHT_DOWNLOAD_URL);
        mMap.put("430MB", FOUR_HUNDRED_DOWNLOAD_URL);
        mMap.put("686MB", SIX_HUNDRED_DOWNLOAD_URL);
        mMap.put("800MB", EIGHT_HUNDRED_DOWNLOAD_URL);
        mMap.put("2.73GB", TWO_TWo_HUNDRED_DOWNLOAD_URL);
        mStart = (Button) findViewById(R.id.start);
        mStart.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, simple_spinner_item, d);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        mDownloadSystem = (Spinner) findViewById(R.id.download_system);
        mWebSystem = (Spinner) findViewById(R.id.web_system);
        mTV = (TextView) findViewById(R.id.textView);
        mSize = (Spinner) findViewById(R.id.size);
        mDownloadSystem.setAdapter(adapter);
        mDownloadSystem.setOnItemSelectedListener(this);
        mWebSystem.setOnItemSelectedListener(this);
        mSize.setOnItemSelectedListener(this);
        mWebSystem.setEnabled(false);
        mSize.setEnabled(false);
        updateViews();
    }

    public boolean checkView() {
        mDownTimesStr = mDownTimes.getText().toString().trim();
        mWaitTimeStr = mWaitTime.getText().toString().trim();
        if (mDownTimesStr.equals("") || mWaitTimeStr.equals("")) {
            Toast.makeText(this, "请检查参数设置，填写内容是否为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDownTimesInt = Integer.parseInt(mDownTimesStr);
        mWaitTimeInt = Integer.parseInt(mWaitTimeStr);
        Preference.putInt("downTime", mDownTimesInt);
        Preference.putInt("waitTime", mWaitTimeInt);
        if (mDownTimesInt < 1 || mWaitTimeInt < 0) {
            Toast.makeText(this, "请输入正确的数据", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public Handler h = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_VIEWS_ENABLES:
//                    updateViews(true);
                    updateViews();
                    break;
                case NET_LINK_TIMEOUT:
                    dialogNetEnable();
                    break;
                case INITIALIZE_VIEW:
                    restoreViews();
                    break;
            }
        }
    };

    //还原界面设置
    private void restoreViews() {
        ISLOADING=false;
        updateViews();
        ProgressBar[] progress = {mTenProgress};
        for (int i = 0; i < progress.length; i++) {
            progress[i].setProgress(0);
        }
        TextView[] view = {mTV};
        for (int i = 0; i < view.length; i++) {
            view[i].setText(0 + " s");
        }
        Helper.i("当前WAIT_SHOP的状态为："+WAIT_STOP);
        if (WAIT_STOP) {
            Helper.i("当前状态为："+down);
            if (down.equals("下载")) {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + fileName);
                if (file.exists()) {
                    file.delete();
                }
                Helper.i("是否删除成功" + !file.exists());
            }
        }
        WAIT_STOP = false;
    }

    @Override
    void onClearData() {
        restoreViews();
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
//                Process.killProcess(Process.myPid());
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();

    }

    private static boolean mIsShow = false;
    @Override
    public void dialogNetEnable() {
        if (mIsShow)return;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setCancelable(false);
                dialog.setTitle("提醒");
                dialog.setMessage("网络不可用，导致测试中断，请过后重新进行测试");
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int newTime = Preference.getInt("newTime");
                        mShow.setText("第"+newTime+"次测试时，由于网络原因导致测试停止");
                        restoreViews();
//                        updateViews(true);
                        mIsShow = false;
                    }
                });
                dialog.show();
                mIsShow = true;
            }
        });
    }

    @Override
    public void updateUseTimeTv(String text) {
        if (text == null) {
            return;
        }
        mTV.setText(text);
    }

    @Override
    public void showSecial(String secial) {
        mShow.setText(secial);
    }

    @Override
    public void updateProgressBar(int i) {
        mTenProgress.setProgress(i);
    }

    @Override
    public void showDialog(String tips, String msg) {
        DialogHelper.dialog(this, tips, msg);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setViewEnabled() {
        h.sendEmptyMessage(SET_VIEWS_ENABLES);
    }

    @Override
    public void setInitializeView() {
        h.sendEmptyMessage(INITIALIZE_VIEW);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.download_system:
                mDownStr = (String) mDownloadSystem.getSelectedItem();
                mWebSystem.setEnabled(true);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, w_1);
                if (mDownStr.equals("上传")) {
                    adapter1 = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, w_2);
                }
                adapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                mWebSystem.setAdapter(adapter1);
                break;
            case R.id.web_system:
                mWebStr = (String) mWebSystem.getSelectedItem();
                mSize.setEnabled(true);
                ArrayAdapter<String> adapterSize = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, s_1);
                if (mWebStr.equals("内网") && mDownStr.equals("下载")) {
                    adapterSize = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, s_2);
                }
                adapterSize.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                mSize.setAdapter(adapterSize);
                break;
            case R.id.size:
                mSizeStr = (String) mSize.getSelectedItem();
                if (mDownStr.equals("下载")) {
                    uri = mMap.get(mSizeStr);
                    String[] split = uri.split("/");
                    fileName = split[split.length - 1].trim();
                    Helper.i("打断后的个数为：" + split.length + ", 文件名称为：" + split[split.length - 1]);
                } else {
                    uri = TEST_UPLOAD_URL;
                    String[] split = mMap.get(mSizeStr).trim().split("/");
                    fileName = split[split.length - 1].trim();
                }
                Helper.i("uri的地址为：" + uri + ",下载的资源名称为：" + fileName);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void startSignalCollectService(){
        Intent signalService = new Intent (this, SignalMonitorService.class);
        startService(signalService);
    }
}
