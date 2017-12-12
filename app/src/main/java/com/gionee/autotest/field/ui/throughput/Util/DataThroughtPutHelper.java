package com.gionee.autotest.field.ui.throughput.Util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gionee.autotest.field.FieldApplication;
import com.gionee.autotest.field.ui.throughput.SQL.DatabaseUtil;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by ljc on 17-12-12.
 */

public class DataThroughtPutHelper {
    /**
     * 获取时间
     *
     * @return
     */
    public static String getTimeData() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date data = new Date(time);
        return format.format(data);
    }

    /**
     * 创建xls标题
     *
     * @param path
     */
    public static void exportExcel(String path) {
        WritableWorkbook book = null;
        String[] firstLine = {"序号", "测试结果", "开始时间", "结束时间", "失败时间", "耗时(S)", "测试类型", "网络类型", "文件大小", "平均速率(KB/S)", "运营商", "网络类型", "信号格数", "信号强度"};
        int width[] = {10, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25};
        try {
            book = Workbook.createWorkbook(new File(path));
            //生成名为eccif的工作表，参数0表示第一页
            WritableSheet sheet = book.createSheet("吞吐率", 0);
            WritableFont nf = new WritableFont(WritableFont.createFont("宋体"), 10);
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            wcfN.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
            wcfN.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
            wcfN.setAlignment(Alignment.CENTRE);//左右居中
            // 水平对齐
            wcfN.setWrap(true);
            Helper.i("添加每列标题");
            for (int i = 0; i < firstLine.length; i++) {
                Label label = new Label(i, 0, firstLine[i], getTitleFormat());
                sheet.setColumnView(i, width[i]);
                sheet.addCell(label);
            }
            // 写入数据并关闭文件
            book.write();
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (book != null) {
                try {
                    book.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 追加excel
     *
     * @throws IOException
     * @throws BiffException
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void addExcel(File file, SpeedBean speedBean) {
        Workbook book = null;
//        int width[] = {20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
        Label content, content1, content2, content3, content4, content5, content6, content7, content8, content9, content10, content11, content12, content13;
        try {
            book = Workbook.getWorkbook(file);
            Sheet sh = book.getSheet(0);
            // 获取行
            int length = sh.getRows();
            WritableWorkbook wbook = Workbook.createWorkbook(file, book); // 根据book创建一个操作对象
            WritableSheet sheet = wbook.getSheet(0);// 得到一个工作对象

            Helper.i("写到第" + (length) + "行 ；");
            if (speedBean.success.equals("YES")) {
                content = new Label(0, length, speedBean.id, getContentFormat());
                content1 = new Label(1, length, "通过", getContentFormat());
                content2 = new Label(2, length, speedBean.time, getContentFormat());
                long time = TimeToMinSeconds(speedBean.time) + speedBean.use_time * 1000;
                String endTime = MinSecondsToTime(time);
                content3 = new Label(3, length, endTime, getContentFormat());
                content4 = new Label(4, length, speedBean.failTime, getContentFormat());
                content5 = new Label(5, length, speedBean.use_time + "", getContentFormat());
                content6 = new Label(6, length, speedBean.way, getContentFormat());
                content7 = new Label(7, length, speedBean.web, getContentFormat());
                content8 = new Label(8, length, speedBean.type, getContentFormat());
                content9 = new Label(9, length, speedBean.speed_average, getContentFormat());
                content10 = new Label(10, length, speedBean.operator, getContentFormat());
                content11 = new Label(11, length, speedBean.webType, getContentFormat());
                content12 = new Label(12, length, speedBean.signals, getContentFormat());
                content13 = new Label(13, length, speedBean.signalStrength, getContentFormat());
            } else {
                content = new Label(0, length, speedBean.id, getErrorFormat());
                content1 = new Label(1, length, "不通过", getErrorFormat());
                content2 = new Label(2, length, speedBean.time, getErrorFormat());
                content3 = new Label(3, length, "", getErrorFormat());
                content4 = new Label(4, length, speedBean.failTime, getErrorFormat());
                long use_time = TimeToMinSeconds(speedBean.failTime) - TimeToMinSeconds(speedBean.time);
                content5 = new Label(5, length, use_time / 1000 + "", getErrorFormat());
                content6 = new Label(6, length, speedBean.way, getErrorFormat());
                content7 = new Label(7, length, speedBean.web, getErrorFormat());
                content8 = new Label(8, length, speedBean.type, getErrorFormat());
                content9 = new Label(9, length, speedBean.speed_average, getErrorFormat());
                content10 = new Label(10, length, speedBean.operator, getErrorFormat());
                content11 = new Label(11, length, speedBean.webType, getErrorFormat());
                content12 = new Label(12, length, speedBean.signals, getErrorFormat());
                content13 = new Label(13, length, speedBean.signalStrength, getErrorFormat());
            }
            sheet.addCell(content);
            sheet.addCell(content1);
            sheet.addCell(content2);
            sheet.addCell(content3);
            sheet.addCell(content4);
            sheet.addCell(content5);
            sheet.addCell(content6);
            sheet.addCell(content7);
            sheet.addCell(content8);
            sheet.addCell(content9);
            sheet.addCell(content10);
            sheet.addCell(content11);
            sheet.addCell(content12);
            sheet.addCell(content13);

            wbook.write();
            wbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<File> getDirFileXls(String path) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isFile()) {
                    if (listFiles[i].getName().contains(".xls")) {
                        fileArrayList.add(listFiles[i].getAbsoluteFile());
                    }
                }
            }
        }

        return fileArrayList;
    }

    private static long TimeToMinSeconds(String time) {
        long minSeconds = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(time);
            minSeconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return minSeconds;
    }

    private static String MinSecondsToTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }

    @NonNull
    private static WritableCellFormat getContentFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
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

    @NonNull
    private static WritableCellFormat getErrorFormat() throws WriteException {
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat titleFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        titleFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return titleFormat;
    }
}
