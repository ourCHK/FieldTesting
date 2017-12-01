package com.gionee.autotest.field.data.db;


import android.content.ContentValues;
import android.content.Context;

import com.gionee.autotest.field.ui.network_switch.model.NetworkSwitchResult;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NetworkSwitchDBManager {

    private static NetworkSwitchDBHelper helper;

    static void init(Context context) {
        if (helper == null) {
            helper = new NetworkSwitchDBHelper(context);
        }
    }

    public static void writeResult(String fileName, NetworkSwitchResult result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("resultFileName", fileName);
        contentValues.put("result", new Gson().toJson(result));
        helper.writeResult(contentValues);
    }

    public static void deleteTable(){
        helper.deleteTable("networkSwitch");
    }


    public static ArrayList<String> getResultFileNameList() {
        return helper.getResultFileNameList();
    }

    public static ArrayList<NetworkSwitchResult> getNetworkSwitchResultList(String fileName) {
        return helper.getNetworkSwitchResultList(fileName);
    }

}
