package com.gionee.autotest.field.ui.network_switch.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.gionee.autotest.field.data.db.NetworkSwitchDBManager;
import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.NetworkSwitchUtil;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * weisc
 * 17-11-30
 */

public class ExcelUtil {
    public static boolean isTest = false;
    private static final String[] headLine = {"轮次", "识卡状态", "驻网状态", "能否上网", "切换SIM卡", "网络运营商", "信号类型", "信号格数", "信号强度", "操作时间", "测试结果"};

    public static void writeBook(String filePath, ArrayList<ArrayList<NetworkSwitchResult>> list) {
        WritableWorkbook workBook = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            workBook = Workbook.createWorkbook(file);
            for (int i = 0; i < list.size(); i++) {
                WritableSheet sheet = workBook.createSheet("第" + (i + 1) + "批次", i);
                ArrayList<NetworkSwitchResult> results = list.get(i);
                writeSheet(sheet, results);
                sheet.addCell(new Label(0, results.size() + 2, NetworkSwitchUtil.getSumContent(results)));
            }
            workBook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void writeFailedBook(String filePath, String resultFileName, ArrayList<NetworkSwitchResult> results) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        WritableWorkbook workBook = null;
        try {
            if (!file.exists()) {
                workBook = Workbook.createWorkbook(file);
            } else {
                Workbook wb = Workbook.getWorkbook(file);
                workBook = Workbook.createWorkbook(file, wb);
            }
            int sheetNum = workBook.getNumberOfSheets();
            WritableSheet sheet = workBook.createSheet(resultFileName, sheetNum + 1);
            writeSheet(sheet, results);
            workBook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public static void exportReport(final Consumer<Integer> c) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                ArrayList<ArrayList<NetworkSwitchResult>> results = new ArrayList<>();
                ArrayList<String> resultFileNameList = NetworkSwitchDBManager.getResultFileNameList();
                for (String fileName : resultFileNameList) {
                    ArrayList<NetworkSwitchResult> reportBean = NetworkSwitchDBManager.getNetworkSwitchResultList(fileName);
                    results.add(reportBean);
                }
                ExcelUtil.writeBook(Constant.NETWORK_SWITCH_EXCEL_PATH, results);
                return results.size();
            }


            @Override
            protected void onPostExecute(Integer size) {
                super.onPostExecute(size);
                if (c != null) {
                    try {
                        c.accept(size);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void exportFailedReport(String resultFileName, ArrayList<NetworkSwitchResult> failedResults) {
        final String mResultFileName = resultFileName;
        final ArrayList<NetworkSwitchResult> results = failedResults;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ExcelUtil.writeFailedBook(Constant.NETWORK_SWITCH_FAILED_EXCEL_PATH, mResultFileName, results);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

        }.execute();
    }

    private static void writeSheet(WritableSheet sheet, ArrayList<NetworkSwitchResult> results) throws WriteException {
        CellView cellView = new CellView();
        cellView.setAutosize(true);
        for (int c = 0; c < headLine.length; c++) {
            sheet.addCell(new Label(c, 0, headLine[c], getTitleFormat()));
            sheet.setColumnView(c, cellView);
        }
        int roundRow = 1;
        for (int j = 0; j < results.size(); j++) {
            NetworkSwitchResult result = results.get(j);
            sheet.addCell(new Label(0, roundRow + j, roundRow + j + "", getContentFormat(result)));
            sheet.addCell(new Label(1, roundRow + j, result.readSim_1 + "，" + result.readSim_2, getContentFormat(result)));
            sheet.addCell(new Label(2, roundRow + j, result.SignNetwork_1 + "，" + result.SignNetwork_2, getContentFormat(result)));
            sheet.addCell(new Label(3, roundRow + j, result.isNet, getContentFormat(result)));
            sheet.addCell(new Label(4, roundRow + j, result.isSwitched, getContentFormat(result)));
            sheet.addCell(new Label(5, roundRow + j, result.simNetOperator, getContentFormat(result)));
            sheet.addCell(new Label(6, roundRow + j, result.simNetType, getContentFormat(result)));
            sheet.addCell(new Label(7, roundRow + j, result.simLevel + "", getContentFormat(result)));
            sheet.addCell(new Label(8, roundRow + j, result.simSignal + "dbm", getContentFormat(result)));
            sheet.addCell(new Label(9, roundRow + j, result.test_time, getContentFormat(result)));
            sheet.addCell(new Label(10, roundRow + j, result.result, getContentFormat(result)));
        }
    }

    @NonNull
    private static WritableCellFormat getContentFormat(NetworkSwitchResult result) throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        if (result.result.equals("失败")) {
            bold.setColour(Colour.RED);
        }
        WritableCellFormat contentFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        contentFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        contentFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return contentFormat;
    }

    @NonNull
    private static WritableCellFormat getTitleFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat titleFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        titleFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return titleFormat;
    }
}
