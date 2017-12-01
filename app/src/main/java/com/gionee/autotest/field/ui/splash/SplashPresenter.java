package com.gionee.autotest.field.ui.splash;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gionee.autotest.common.Preference;
import com.gionee.autotest.field.data.db.AppsDBManager;
import com.gionee.autotest.field.data.db.DBManager;
import com.gionee.autotest.field.data.db.model.App;
import com.gionee.autotest.field.data.db.model.AppList;
import com.gionee.autotest.field.ui.base.BasePresenter;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.Util;

import java.util.List;

/**
 * Created by viking on 11/6/17.
 *
 * Presenter implementation for Splash screen.
 */

class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter{

    private Context context ;

    SplashPresenter(Context context) {
        this.context = context ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null ;
    }

    @Override
    public void initialize(Bundle extras) {
        Log.i(Constant.TAG, "checkLoadAllDataToDB...") ;
        if (Preference.getBoolean(context, Constant.PREF_KEY_FIRST_LAUNCH, true)){
            Log.i(Constant.TAG, "first time launch") ;
            AppList appList = Util.parseAppJsonFile(context) ;
            //TODO change throw exception to Dialog display
            if (appList == null) throw new IllegalArgumentException("Can not fetch all app's information.") ;
            String version = appList.getVersion() ;
            List<App> apps = appList.getApps() ;
            Log.i(Constant.TAG, "version : " + version) ;
            Preference.putString(context, Constant.PREF_KEY_APP_VERSION, version) ;
            AppsDBManager.insertApps(apps) ;
            //always set first launch to false when all done
            Preference.putBoolean(context, Constant.PREF_KEY_FIRST_LAUNCH, false);
        }else{
            //maybe there is a new version
            AppList appList = Util.parseAppJsonFile(context) ;
            //TODO change throw exception to Dialog display
            if (appList == null) throw new IllegalArgumentException("Can not fetch all app's information.") ;
            String version = appList.getVersion() ;
            Log.i(Constant.TAG, "new version : " + version) ;
            String oldVersion = Preference.getString(context, Constant.PREF_KEY_APP_VERSION) ;
            Log.i(Constant.TAG, "old version : " + oldVersion) ;
            if (!version.equals(oldVersion)){
                //first delete all apps in database
                DBManager.deleteAllDB(context);
                //set version to new one
                Preference.putString(context, Constant.PREF_KEY_APP_VERSION, version) ;
                //update all new apps
                AppsDBManager.insertApps(appList.getApps()) ;
            }
        }
        if (isViewAttached()){
            getView().navigateToMainScreen();
        }
    }

}
