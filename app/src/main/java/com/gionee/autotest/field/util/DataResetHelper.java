package com.gionee.autotest.field.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by xhk on 2017/11/20.
 */

public class DataResetHelper {

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
     * 获取时间
     *
     * @return
     */
    public static String getTimeDatas() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        String info[] = {"开始时间", "完成时间", "是否成功", "网络运营商", "网络类型", "信号格数", "信号强度"};
        int width[] = {30, 30, 20, 20, 20, 20, 20};
        //  makeDirects();
        try {
            book = Workbook.createWorkbook(new File(path));
            //生成名为eccif的工作表，参数0表示第一页
            WritableSheet sheet = book.createSheet("数据重激活", 0);

            // 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
            WritableFont bold = new WritableFont(WritableFont.createFont("微软雅黑"), 13, WritableFont.NO_BOLD);
            // 生成一个单元格样式控制对象
            WritableCellFormat titleFormate = new WritableCellFormat(bold);
            // 单元格中的内容水平方向居中
            titleFormate.setAlignment(jxl.format.Alignment.CENTRE);
            // 单元格的内容垂直方向居中
            titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            //表头导航
            for (int j = 0; j < info.length; j++) {
                Label label = new Label(j, 0, info[j], titleFormate);
                sheet.setColumnView(j, width[j]);
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
     * @param args
     * @throws IOException
     * @throws BiffException
     * @throws WriteException
     * @throws RowsExceededException
     */
    public static void addExcel(File file, String[] args) {
        Workbook book = null;
        int width[] = {30, 30, 20, 20, 20, 20, 20};
        try {
            book = Workbook.getWorkbook(file);

            Sheet sheet = book.getSheet(0);
            // 获取行
            int length = sheet.getRows();
            System.out.println(length);
            WritableWorkbook wbook = Workbook.createWorkbook(file, book); // 根据book创建一个操作对象
            WritableSheet sh = wbook.getSheet(0);// 得到一个工作对象

            // 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
            WritableFont bold = new WritableFont(WritableFont.createFont("微软雅黑"), 13, WritableFont.NO_BOLD);
            // 生成一个单元格样式控制对象
            WritableCellFormat titleFormate = new WritableCellFormat(bold);
            // 单元格中的内容水平方向居中
            titleFormate.setAlignment(jxl.format.Alignment.CENTRE);
            // 单元格的内容垂直方向居中
            titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

            // 从最后一行开始加
            for (int i = 0; i < args.length; i++) {
                Label label = new Label(i, length, args[i], titleFormate);
                sh.setColumnView(i, width[i]);
                sh.addCell(label);
            }
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

    public static boolean getTimeDifference(String starttime, String endtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse(starttime);
            date2= sdf.parse(endtime);

            long l = date2.getTime() - date1.getTime();

            if (l>20*1000){
                return true;
            }else{
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


}
