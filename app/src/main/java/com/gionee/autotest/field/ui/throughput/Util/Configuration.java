package com.gionee.autotest.field.ui.throughput.Util;

import android.os.Environment;

import com.gionee.autotest.field.util.Constant;

import java.io.File;

public class Configuration {
    public static final String TEN_DOWNLOAD_URL="http://otadl.gionee.com/synth/res/release/com.gionee.amisystem/20170227/o92lc/Amigo_CarefreeLauncher.apk";
    public static final String TWENTY_DOWNLOAD_URL="http://otadl.gionee.com/ota/res/ota/2380/8619/GBL7553A02_B_update_amigo3.5.8_T2158_amigo3.5.7_T2141.zip";
    public static final String FIFTY_DOWNLOAD_URL="http://otadl.gionee.com/ota/res/ota/2369/8568/GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.6_T3640.zip ";
    public static final String ONE_HUNDRED_DOWNLOAD_URL="http://otadl.gionee.com/ota/res/ota/2379/8615/GBL7523L03_A_update_amigo3.1.10_T2603_amigo3.1.6_T2573.zip ";
    public static final String TWO_HUNDRED_DOWNLOAD_URL="http://otadl.gionee.com/ota/res/ota/2369/8571/GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.1_T3553.zip";
    public static final String FRTY_FOUR_DOWNLOAD_URL="http://it.gionee.com/download/soft/office/pdf/Adobe%20Reader_9.4.0.195.exe";
    public static final String EIGHTY_EIGHT_DOWNLOAD_URL="http://it.gionee.com/download/soft/virtual/VirtualBox-4.1.8-75467-Win.exe";
    public static final String FOUR_HUNDRED_DOWNLOAD_URL="http://it.gionee.com/download/soft/virtual/VMware-workstation-full-9.0.2-1031769.exe";
    public static final String SIX_HUNDRED_DOWNLOAD_URL="http://it.gionee.com/download/soft/system/ubuntu-10.04.1-desktop-amd64.iso";
    public static final String EIGHT_HUNDRED_DOWNLOAD_URL="http://it.gionee.com/download/soft/office/office2010/office2010.rar";
    public static final String TWO_TWo_HUNDRED_DOWNLOAD_URL="http://it.gionee.com/download/soft/ghost/P8H61_Winxp.GHO";
//    public static final String TEST_UPLOAD_URL="http://otadl.gionee.com/synth/res/release/com.gionee.amisystem/20170227/o92lc/";
    public static final String TEST_UPLOAD_URL="https://p-admin.gionee.com/otadm/";
    public static final String TEN_FILE_NAME="Amigo_CarefreeLauncher.apk";
    public static final String TWENTY_FILE_NAME="GBL7553A02_B_update_amigo3.5.8_T2158_amigo3.5.7_T2141.zip";
    public static final String FIFTY_FILE_NAME="GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.6_T3640.zip";
    public static final String ONE_HUNDRED_FILE_NAME="GBL7523L03_A_update_amigo3.1.10_T2603_amigo3.1.6_T2573.zip";
    public static final String TWO_HUNDRED_FILE_NAME="GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.1_T3553.zip";
    public static final String  TEN_UPLOAD_FILENAME="/sdcard/Amigo_CarefreeLauncher.apk";
    public static final String  TWENTY_UPLOAD_FILENAME="/sdcard/GBL7553A02_B_update_amigo3.5.8_T2158_amigo3.5.7_T2141.zip";
    public static final String  FIFTY_UPLOAD_FILENAME="/sdcard/GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.6_T3640.zip";
    public static final String  ONE_HUNDRED_UPLOAD_FILENAME="/sdcard/GBL7523L03_A_update_amigo3.1.10_T2603_amigo3.1.6_T2573.zip";
    public static final String  TWO_HUNDRED_UPLOAD_FILENAME="/sdcard/GBL7359L01_A_update_amigo3.1.11_T3667_amigo3.1.1_T3553.zip";
    public static final String RESULT_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+ Constant.HOME+File.separator+"throughput";
    public static final String ERROE_RESULT_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+ Constant.HOME+File.separator+"throughputError";
    public static final String FILE_NAME=RESULT_PATH+ File.separator+"吞吐率.xls";
    public static final String ERROR_FILE_NAME=ERROE_RESULT_PATH+ File.separator+"吞吐率失败详情.xls";
    public static final String FILE_NAME_LOOK="内部存储器"+File.separator+Constant.HOME+File.separator+"throughput"+ File.separator+"吞吐率.xls";
    public static final String FILE_PATH=RESULT_PATH+ File.separator;
    public static final int SET_VIEWS_ENABLES = 1;
    public static final int NET_LINK_TIMEOUT=0;
    public static final int INITIALIZE_VIEW=2;
    public static final int DISSMISS=3;
    public static final int SHOWSECIAL=4;
    public static boolean isGioneeWeb = false;
    public static boolean  ISLOADING = false;
    public static boolean  WAIT_STOP = false;
    public static boolean  HAS_ERROR = false;

}
