package com.gionee.autotest.field.ui.data_stability;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.DatabaseHelper;
import com.gionee.autotest.field.data.db.DatabaseUtil;
import com.gionee.autotest.field.services.WebViewService;
import com.gionee.autotest.field.ui.data_stability.report.ReportActivity;
import com.gionee.autotest.field.util.Configurator;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

class MainAction {
    private Context mContext;
    private IMain   main;

    MainAction(IMain main) {
        this.main = main;
        this.mContext = main.getContext();
    }

    void startTest(DataParam p) {
        String paramJson = new Gson().toJson(p);
        Preference.putString(mContext, "lastParams", paramJson);
        Intent intent = new Intent(mContext, WebViewService.class);
        Configurator.getInstance().setParam(p);
        mContext.startService(intent);
    }

    DataParam getLastParam() {
        String lastParams = Preference.getString(mContext, "lastParams", "");
        if (lastParams.equals("")) {
            return new DataParam();
        }
        try {
            return new Gson().fromJson(lastParams, DataParam.class);
        } catch (JsonSyntaxException e) {
            return new DataParam();
        }
    }


    void showReportPage() {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    void stopTest() {
        mContext.stopService(new Intent(mContext, WebViewService.class));
    }

    @SuppressLint("StaticFieldLeak")
    void clearReport() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                databaseUtil.deleteTable(DatabaseHelper.TABLE_RESULT);
                databaseUtil.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(mContext, "清除报告成功", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    void openDataStabilityExcelFile() {
        Util.openExcelByIntent(mContext,Constant.DATA_STABILITY_EXCEL_PATH);
    }

    @SuppressLint("StaticFieldLeak")
    void exportDataStabilityExcelFile() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
                ArrayList<DataStabilityBean> dataStabilityBeans = databaseUtil.getDataStabilityBeans();
                return writeAllExcel(dataStabilityBeans,Constant.DATA_STABILITY_EXCEL_PATH);
            }

            @Override
            protected void onPostExecute(Boolean writeExcelResult) {
                super.onPostExecute(writeExcelResult);
                if (writeExcelResult)
                    Toast.makeText(mContext, "导出成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "导出失败", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }


    /**
     * 将数据输出至Excel文件
     * @param beans
     * @param filePath
     * @return 输出成功返回true,失败返回false
     */
    boolean writeAllExcel(ArrayList<DataStabilityBean> beans,String filePath) {
        WritableWorkbook workBook = null;
        try {
            File file  = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) file.delete();
            file.createNewFile();
            workBook = Workbook.createWorkbook(file);

            if (beans != null && beans.size()!= 0) {
                Gson gson = new Gson();
                WritableSheet sheet = workBook.createSheet("sheet",1);
                sheet.addCell(new Label(0,0,"id"));
                sheet.addCell(new Label(1,0,"batchId"));
                sheet.addCell(new Label(2,0,"testIndex"));
                sheet.addCell(new Label(3,0,"result"));
                sheet.addCell(new Label(4,0,"errorIndex"));
                sheet.addCell(new Label(5,0,"isActive1"));
                sheet.addCell(new Label(6,0,"NetType1"));
                sheet.addCell(new Label(7,0,"Level1"));
                sheet.addCell(new Label(8,0,"Signal1"));
                sheet.addCell(new Label(9,0,"运营商1"));
                sheet.addCell(new Label(10,0,"isActive2"));
                sheet.addCell(new Label(11,0,"NetType2"));
                sheet.addCell(new Label(12,0,"Level2"));
                sheet.addCell(new Label(13,0,"Signal2"));
                sheet.addCell(new Label(14,0,"运营商2"));

                int row = 1;
                for (DataStabilityBean dataStabilityBean:beans) {
                    sheet.addCell(new Label(0,row,dataStabilityBean.getId()+""));
                    sheet.addCell(new Label(1,row,dataStabilityBean.getBatchId()+""));
                    sheet.addCell(new Label(2,row,dataStabilityBean.getTestIndex()+""));
                    WebViewResultSum webViewResultSum = gson.fromJson(dataStabilityBean.result, WebViewResultSum.class);

                    if (webViewResultSum.result) {  //成功
                        sheet.addCell(new Label(3,row,"成功"));
                    } else {    //失败
                        sheet.addCell(new Label(3,row,"失败"));
                        ArrayList<WebViewResultSum.SimNetInfo> errorSimNetInfoList = webViewResultSum.errorSimNetInfoList;
                        if (errorSimNetInfoList != null && errorSimNetInfoList.size() != 0) {
                            row +=1;
                            for (WebViewResultSum.SimNetInfo errorSimNetInfo:errorSimNetInfoList) {
                                sheet.addCell(new Label(4,row,errorSimNetInfo.getIndex()+""));
                                sheet.addCell(new Label(5,row,errorSimNetInfo.ismIsActive1()+""));
                                sheet.addCell(new Label(6,row,errorSimNetInfo.getmNetType1()+""));
                                sheet.addCell(new Label(7,row,errorSimNetInfo.getmLevel1()+""));
                                sheet.addCell(new Label(8,row,errorSimNetInfo.getmSignal1()+""));
                                sheet.addCell(new Label(9,row,errorSimNetInfo.getmOperator1()+""));
                                sheet.addCell(new Label(10,row,errorSimNetInfo.ismIsActive2()+""));
                                sheet.addCell(new Label(11,row,errorSimNetInfo.getmNetType2()+""));
                                sheet.addCell(new Label(12,row,errorSimNetInfo.getmLevel2()+""));
                                sheet.addCell(new Label(13,row,errorSimNetInfo.getmSignal2()+""));
                                sheet.addCell(new Label(14,row,errorSimNetInfo.getmOperator2()+""));
                                row++;
                            }
                            row -=1;
                        }
                    }
                    row++;
                }

            }
            workBook.write();
            workBook.close();
            return true;
        }   catch (Exception e) {
            Log.i(Constant.TAG,e.toString());
            return false;
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    Log.i(Constant.TAG,e.toString());
                }

            }
        }
    }

}
