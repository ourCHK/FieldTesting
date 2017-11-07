package com.gionee.autotest.field.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viking on 11/7/17.
 */

class AppsDBHelper extends DBHelper {

    public AppsDBHelper(Context context) {
        super(context);
    }

    private void insertApp(App app){
        ContentValues cv = new ContentValues() ;
        cv.put(Constant.APPDB.COLUMN_NAME_KEY , app.getKey());
        cv.put(Constant.APPDB.COLUMN_NAME_LABEL , app.getLabel());
        cv.put(Constant.APPDB.COLUMN_NAME_ICON , app.getIcon());
        cv.put(Constant.APPDB.COLUMN_NAME_ACTIVITY , app.getActivity());
        cv.put(Constant.APPDB.COLUMN_NAME_INSTALLED , app.isInstalled() ? "1": "0");
        mDb.insert(Constant.APPDB.TABLE_NAME, null, cv);
    }

    protected boolean insertApps(List<App> apps){
        mDb.beginTransaction();
        boolean isSuccess = false ;
        try{
            if (apps != null && apps.size() > 0){
                for (App app : apps){
                    insertApp(app) ;
                }
            }
            isSuccess = true ;
            mDb.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            mDb.endTransaction();
        }
        return isSuccess ;
    }

    protected List<App> fetchAllApps(boolean installed){
        String where = Constant.APPDB.COLUMN_NAME_INSTALLED + "= ?" ;
        String[] whereValue = new String[]{installed ? "1" : "0"};
        List<App> illegalPermissions = null ;
        Cursor query = mDb.query(Constant.APPDB.TABLE_NAME, null, where, whereValue, null, null, Constant.APPDB.ORDER_BY) ;
        try {
            if (query != null && query.getCount() > 0){
                illegalPermissions = new ArrayList<>();
                while(query.moveToNext()){
                    int key         = query.getInt(query.getColumnIndex(Constant.APPDB.COLUMN_NAME_KEY)) ;
                    String label    = query.getString(query.getColumnIndex(Constant.APPDB.COLUMN_NAME_LABEL)) ;
                    String icon     = query.getString(query.getColumnIndex(Constant.APPDB.COLUMN_NAME_ICON)) ;
                    String activity = query.getString(query.getColumnIndex(Constant.APPDB.COLUMN_NAME_ACTIVITY)) ;
                    illegalPermissions.add(new App(key, label, icon, activity, installed)) ;
                }
            }
        }finally {
            if (query != null) query.close();
        }
        return illegalPermissions ;
    }

    protected boolean updateApp(int key, boolean installed){
        ContentValues cv = new ContentValues() ;
        cv.put(Constant.APPDB.COLUMN_NAME_INSTALLED, installed ? "1" : "0");
        String where = Constant.APPDB.COLUMN_NAME_KEY + "= ?" ;
        String[] whereValue = new String[]{key+""};
        return mDb.update(Constant.APPDB.TABLE_NAME, cv, where, whereValue) > 0 ;
    }

    protected boolean deleteAllApp(){
        return mDb.delete(Constant.APPDB.TABLE_NAME, null, null) > 0;
    }
}
