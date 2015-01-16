package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.os.Bundle;


import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxiangfeng on 15-1-8.
 */
public class SettingFragment extends PreferenceFragment {
    private static final String TAG = "SettingFragment";
//    private String[] titles = new String[] { "1300万像素", "1200万像素", "1000万像素(16:9)",
//            "800万像素(16:9)", "800万像素","600万像素(16:9)","300万像素","200万像素(16:9)",
//            "200万像素","100万像素(5:3)" };
    private Preference.OnPreferenceChangeListener mListener;
    private ListPreference mPictureSizePref;
    private onSettingsInterface mInterface;

    public interface onSettingsInterface{
        public void initialPref(Preference pref);
    };

    public SettingFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate.");
        getPreferenceManager().setSharedPreferencesName("settings_preference");
        addPreferencesFromResource(R.xml.camera_settings);
        //Todo:filter no use items.
        mPictureSizePref = (ListPreference) getPreferenceManager().findPreference(CameraSettings.KEY_PICTURE_SIZE);
        if(null != mPictureSizePref) {
            mPictureSizePref.setOnPreferenceChangeListener(mListener);
            mInterface.initialPref(mPictureSizePref);
            String value = mPictureSizePref.getValue();
            Log.d(TAG,"default value is "+value);
            if(null == value){
                mPictureSizePref.setValueIndex(0);
                Log.d(TAG,"default value is "+value);
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView.");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.d(TAG, "onAttach.");
        mListener = (Preference.OnPreferenceChangeListener) activity;
        mInterface = (onSettingsInterface) activity;

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
        Log.d(TAG, "preference clicked:" + preference.getKey());
        return false;
    }
}
