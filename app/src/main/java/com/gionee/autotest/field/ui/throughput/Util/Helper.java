package com.gionee.autotest.field.ui.throughput.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import com.gionee.autotest.field.ui.throughput.Preference;
import com.gionee.autotest.field.ui.throughput.SQL.DatabaseUtil;
import com.gionee.autotest.field.util.Constant;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;


import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.gionee.autotest.field.ui.throughput.Util.Configuration.HAS_ERROR;

/**
 * Log工具
 *
 * @author siqiliu
 */
public class Helper {
    private static boolean isInput = true;
    private static final String TAG = "gionee.os.autotest";
    private static int level;
    /**
     * 输出i级别的Log
     *
     * @param s
     */
    public static void i(String s) {
        if (isInput) {
            Log.i(TAG, "wifiperformance==" + s);
        }
    }

    /**
     * 删除文件夹
     */
    public static void delAllFiles(String path) {
        File resultFile = new File(path);
        if (resultFile.exists()) {
            if (resultFile.isDirectory()) {
                File[] fileList = resultFile.listFiles();
                Log.i(TAG, "path: " + fileList.length);
                for (int i = 0; i < fileList.length; i++)
                    deleteFile(fileList[i]);
                resultFile.delete();
            } else {
                resultFile.delete();
            }
        }
    }

    /**
     * L
     * 删除文件
     *
     * @param screenshot
     */
    public static void deleteFile(File screenshot) {
        if (screenshot.exists()) {
            if (screenshot.isDirectory()) {
                File[] fileList = screenshot.listFiles();
                for (int i = 0; i < fileList.length; i++)
                    deleteFile(fileList[i]);
                screenshot.delete();
            } else {
                screenshot.delete();
            }
        }
    }

    /**
     * 保存下载文件
     *
     * @param response
     * @param fileName
     */
    public static void downFile(Context context, Response response, String fileName) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            is = response.body().byteStream();
            long total = response.body().contentLength();
            File file = new File(SDPath, fileName);
            if (file.exists()) {
                file.mkdir();
            }
            fos = new FileOutputStream(file);
            long sum = 0;

            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
//                    int progress = (int) (sum * 1.0f / total * 100);
            }
            fos.flush();
            Helper.i("文件下载成功");
            Preference.putBoolean(Constant.THROUGHPUT_RUNING,false);
        } catch (Exception e) {
            Helper.i("文件下载失败");
            Helper.i("error:" + e.toString());
            HAS_ERROR=true;
            Preference.putBoolean(Constant.THROUGHPUT_RUNING,false);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
            Preference.putBoolean(Constant.THROUGHPUT_RUNING,false);
        }
    }



    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        return getTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public static String formatTime(long date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date(date));// new Date()为获取当前系统时间
    }

    public static int getsecond(String time) {
//        String time = getTime();
        String[] bb = time.split(" ");
        String s = bb[1];
//        Helper.i("时间段为："+s);
        String[] aa = s.split(":");
        int hh = Integer.parseInt(aa[0]);
        int min = Integer.parseInt(aa[1]);
        int ss = Integer.parseInt(aa[2]);
        return ss + (min * 60) + (hh * 60 * 60);
    }
    public static int getMillisecond(String time) {
//        String time = getTime();
        String[] bb = time.split(" ");
        String s = bb[1];
//        Helper.i("时间段为："+s);
        String[] aa = s.split(":");
        int hh = Integer.parseInt(aa[0]);
        int min = Integer.parseInt(aa[1]);
        int ss = Integer.parseInt(aa[2]);
        int sss=Integer.parseInt(aa[3]);
        return (ss + (min * 60) + (hh * 60 * 60))*1000+sss;
    }
    //判断WiFi是否可用
    public static boolean isWifiEnabled(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    public static  boolean isNetWorkEnable(Context mContext){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        if(cm !=null){
            if(cm.getActiveNetworkInfo()!=null){
                return cm.getActiveNetworkInfo().isAvailable();
            }
        }
        return false;
    }
    public static boolean isNetwork() {
//        try {
//            URL url = new URL("http://www.baidu.com");
//            URLConnection connection = url.openConnection();
//            connection.setConnectTimeout(25000);
//            connection.connect();
//        } catch (Exception e) {
//            i("ERROR:"+e.getMessage());
//            return false;
//        }
        return true;
    }
    //定时器初始化
    public static Timer timerInitialize(Timer timer) {
        timer.cancel();
        timer.purge();
        return  timer = new Timer();
    }
  public static int wifiRssi(WifiManager wifiManager){
        return level =wifiManager.getConnectionInfo().getRssi();
    }


    public static ArrayList getNewList(Context context) {
        DatabaseUtil db = new DatabaseUtil(context);
        ArrayList<String> list = db.getstartContent();
        Set set = new HashSet();
        ArrayList newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        for (int i=0;i<newList.size();i++){
            Helper.i(newList.get(i)+"");
        }
        return newList;
    }




}

