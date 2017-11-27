package com.gionee.autotest.field.ui.throughput.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gionee.autotest.field.ui.throughput.Util.Helper;
import com.gionee.autotest.field.ui.throughput.bean.SpeedBean;
import com.google.gson.Gson;

import java.util.ArrayList;


public class DatabaseUtil {

    private static Context mContext;
    private SQLiteDatabase db;
    public static final String DOWNLOAD_TYPE = "type";
    public static final String CHOOSE_WAY = "way";
    public static final String AVERAGE_SPEED = "averageSpeed";
    public static final String USE_TIME = "useTime";
    public static final String TIMES = "times";
    public static final String TIME = "time";
    public static final String SPEED = "speed";
    public static final String WEB = "web";
    public static final String START = "start";
    public static final String SERIAL = "serial";

    public DatabaseUtil(Context context) {
        this.mContext = context;
        open();
    }

    public void open() {
        DatabaseHelper mDBHelper = new DatabaseHelper(mContext);
//        db = mDBHelper.getReadableDatabase();
        db = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    public void insert(String table_name, ContentValues values) {
        db.insert(table_name, null, values);
    }

    public void insert(ContentValues values) {
        insert(DatabaseHelper.TABLE_NAME, values);
    }

    public void update(String table, ContentValues values, String pkgName, String softVersion) {
        db.update(table, values, "packagename=? And softVersion=?", new String[]{pkgName, softVersion});
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        db.update(table, values, whereClause, whereArgs);
    }

    public void update(String pkgName, String softVersion, ContentValues values) {
        update(DatabaseHelper.TABLE_NAME, values, pkgName, softVersion);
    }


    public Cursor rawQuery(String query, String[] selection) {
        return db.rawQuery(query, selection);
    }


    public void deleteTable(String table_name) {
        try {
            db.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void del(String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }


    public void clear() {
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
    }


    public int getTimes(String s) {
//        int maxID = getMaxID();
        Cursor cursor = db.rawQuery("select " + TIMES + " from " + DatabaseHelper.TABLE_NAME + " where type=?", new String[]{s});
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToLast()) {
//            Helper.i("获取到的次数为" + cursor.getInt(cursor.getColumnIndex(TIMES)));
            return cursor.getInt(cursor.getColumnIndex(TIMES));
        }
        return 0;
    }

    public ArrayList<SpeedBean> getContent() {
        ArrayList<SpeedBean> list = new ArrayList<>();
        Cursor data = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);
        Helper.i("------------------");
        Gson gson = new Gson();
        if (data != null && data.getCount() > 0) {
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String time = data.getString(data.getColumnIndex(DatabaseUtil.TIME));
                String way = data.getString(data.getColumnIndex(DatabaseUtil.CHOOSE_WAY));
//                String way ="上传";
                String type = data.getString(data.getColumnIndex(DatabaseUtil.DOWNLOAD_TYPE));
                String web = "内网";
                try {
                    web = data.getString(data.getColumnIndex(DatabaseUtil.WEB));
                } catch (Exception e) {
                    Helper.i(e.toString());
                }
                String average_speed = data.getString(data.getColumnIndex(DatabaseUtil.AVERAGE_SPEED));
                String use_time = data.getString(data.getColumnIndex(DatabaseUtil.USE_TIME));
                String times = data.getString(data.getColumnIndex(DatabaseUtil.TIMES));
                String speed = data.getString(data.getColumnIndex(DatabaseUtil.SPEED));
//                String id = data.getString(data.getColumnIndex("_id"));
                String id = data.getString(data.getColumnIndex(DatabaseUtil.SERIAL));
                SpeedBean.RateBean rateBean = gson.fromJson(speed, SpeedBean.RateBean.class);
                list.add(new SpeedBean().setID(id).setWeb(web).setTime(time).setWay(way).setSpeed(rateBean).setSpeed_average(average_speed).setTimes(Integer.parseInt(times)).setType(type).setUse_time(Integer.parseInt(use_time)));
            }
        }
        return list;
    }
    public ArrayList<String> getstartContent() {
        ArrayList<String> list = new ArrayList<>();
        Cursor data = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);
        Helper.i("------------------");
        Gson gson = new Gson();
        if (data != null && data.getCount() > 0) {
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String start = data.getString(data.getColumnIndex(DatabaseUtil.START));
                list.add(start);
            }
        }
        return list;
    }

    public ArrayList<SpeedBean> getTimeContent(String start) {
        ArrayList<SpeedBean> list = new ArrayList<>();
        Cursor data = db.rawQuery("select * from " + DatabaseHelper.TABLE_NAME+" where start=\""+start+"\"", null);
        Helper.i("------------------");
        Gson gson = new Gson();
        if (data != null && data.getCount() > 0) {
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String time = data.getString(data.getColumnIndex(DatabaseUtil.TIME));
                String way = data.getString(data.getColumnIndex(DatabaseUtil.CHOOSE_WAY));
//                String way ="上传";
                String type = data.getString(data.getColumnIndex(DatabaseUtil.DOWNLOAD_TYPE));
                String web = "内网";
                try {
                    web = data.getString(data.getColumnIndex(DatabaseUtil.WEB));
                } catch (Exception e) {
                    Helper.i(e.toString());
                }
                String average_speed = data.getString(data.getColumnIndex(DatabaseUtil.AVERAGE_SPEED));
                String use_time = data.getString(data.getColumnIndex(DatabaseUtil.USE_TIME));
                String times = data.getString(data.getColumnIndex(DatabaseUtil.TIMES));
                String speed = data.getString(data.getColumnIndex(DatabaseUtil.SPEED));
//                String id = data.getString(data.getColumnIndex("_id"));
                String id = data.getString(data.getColumnIndex(DatabaseUtil.SERIAL));
                SpeedBean.RateBean rateBean = gson.fromJson(speed, SpeedBean.RateBean.class);
                list.add(new SpeedBean().setID(id).setWeb(web).setTime(time).setWay(way).setSpeed(rateBean).setSpeed_average(average_speed).setTimes(Integer.parseInt(times)).setType(type).setUse_time(Integer.parseInt(use_time)));
            }
        }
        return list;
    }

    public void insertData(SpeedBean speedBean) {
        Gson gson = new Gson();
        String json = gson.toJson(speedBean.speed);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseUtil.DOWNLOAD_TYPE, speedBean.type);
        cv.put(DatabaseUtil.WEB, speedBean.web);
        cv.put(DatabaseUtil.SPEED, json);
        cv.put(DatabaseUtil.AVERAGE_SPEED, speedBean.speed_average);
        cv.put(DatabaseUtil.USE_TIME, speedBean.use_time);
        cv.put(DatabaseUtil.TIMES, speedBean.times + 1);
        cv.put(DatabaseUtil.CHOOSE_WAY, speedBean.way);
        cv.put(DatabaseUtil.START, speedBean.start);
        cv.put(DatabaseUtil.TIME, speedBean.time);
        cv.put(DatabaseUtil.SERIAL, speedBean.serial);
        insert(cv);
    }
}
