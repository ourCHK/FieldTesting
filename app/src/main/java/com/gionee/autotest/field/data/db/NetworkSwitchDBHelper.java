package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

public class NetworkSwitchDBHelper extends DBHelper {
    private Context mContext;

    NetworkSwitchDBHelper(Context context) {
        super(context);
        this.mContext = context;
    }

    public void insert(String table_name, ContentValues values) {
        mDb.insert(table_name, null, values);
    }

    public void writeResult(ContentValues values) {
        insert("networkSwitch", values);
    }

    public ArrayList<String> getResultFileNameList() {
        Cursor            cursor    = mDb.rawQuery("select resultFileName from networkSwitch", null);
        ArrayList<String> fileNames = new ArrayList<>();
        if (cursor == null) {
            return fileNames;
        }
        while (cursor.moveToNext()) {
            String resultFileName = cursor.getString(cursor.getColumnIndex("resultFileName"));
            if (!fileNames.contains(resultFileName)) {
                fileNames.add(resultFileName);
            }
        }
        return fileNames;
    }

    public ArrayList<NetworkSwitchResult> getNetworkSwitchResultList(String fileName) {
        Cursor                         cursor = mDb.rawQuery("select result from networkSwitch where resultFileName=" + fileName, null);
        ArrayList<NetworkSwitchResult> list   = new ArrayList<>();
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            try {
                String              resultFileName = cursor.getString(cursor.getColumnIndex("result"));
                NetworkSwitchResult bean           = new Gson().fromJson(resultFileName, NetworkSwitchResult.class);
                list.add(bean);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public void deleteTable(String table_name) {
        try {
            mDb.delete(table_name, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        try {
            mDb.delete("networkSwitch", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
