package com.gionee.autotest.field.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.data.db.model.AppList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.Comparator;

/**
 * Created by viking on 11/7/17.
 *
 * Utility method for application
 */

public class Util {

    public static AppList parseAppJsonFile(Context ctx){
        try{
            StringBuilder content = new StringBuilder() ;
            InputStream inputStream = ctx.getResources().getAssets().open("apps.json") ;
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line ;
            while((line = bufReader.readLine()) != null){
                content.append(line);
            }
            if (!content.toString().equals("")){
                return new Gson().fromJson(content.toString(), AppList.class);
            }
        }catch (Exception e){
            Log.i(Constant.TAG, "Parsing apps.json has an exception ： " + e.getMessage() );
            e.printStackTrace();
        }
        return null ;
    }

    public static final Comparator<App> APP_COMPARATOR = new Comparator<App>() {
        private final Collator mCollator = Collator.getInstance();
        @Override
        public int compare(App obj1, App obj2) {
            String key1 = obj1.getKey() + "";
            String key2 = obj2.getKey() + "";
            return mCollator.compare(key1, key2);
        }
    };

    public static ProgressDialog showLoadingDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
/*        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }*/
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        if (message != null)
            progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }
}
