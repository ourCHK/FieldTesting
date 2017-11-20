package com.gionee.autotest.field.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.gionee.autotest.field.R;
import com.gionee.autotest.field.services.SignalMonitorService;
import com.gionee.autotest.field.util.Constant;
import com.gionee.autotest.field.util.SignalHelper;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences sp ;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.app_settings);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
        onSharedPreferenceChanged(sp, getString(R.string.preference_key_signal_monitor));
        Log.i(Constant.TAG, "signal monitor value: " + sp.getBoolean(getString(R.string.preference_key_signal_monitor), false)) ;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key) ;
        if (key != null && key.equals(getString(R.string.preference_key_signal_monitor))){
            Log.i(Constant.TAG, "signal monitor preferences changed : " + key) ;
            SwitchPreferenceCompat switchPref = (SwitchPreferenceCompat) preference;
            if (switchPref.isChecked()){
                Log.i(Constant.TAG, "start signal monitor...") ;
                com.gionee.autotest.common.Preference.putBoolean(getActivity(),
                        Constant.PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL, true) ;
                getActivity().startService(new Intent(getActivity(), SignalMonitorService.class)) ;
            }else{
                Log.i(Constant.TAG, "stop signal monitor...") ;
                com.gionee.autotest.common.Preference.putBoolean(getActivity(),
                        Constant.PREF_KEY_MANUAL_STOP_MONITOR_SIGNAL, false) ;
                getActivity().stopService(new Intent(getActivity(), SignalMonitorService.class)) ;
                SignalHelper.getInstance(getActivity()).destroy();
            }
        }
    }
}