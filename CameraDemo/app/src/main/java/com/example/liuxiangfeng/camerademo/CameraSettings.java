package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-12.
 */
public class CameraSettings {

    public static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
    private static final int NOT_FOUND = -1;
    private static final String TAG = "CameraSettings";
    private final Context mContext;
    private final Camera.Parameters mParameters;



    public CameraSettings(Activity activity, Camera.Parameters parameters) {
        mContext = activity;
        mParameters = parameters;

    }
    public void initPreference(ListPreference preference) {
//        ListPreference pictureSize = (ListPreference) group.findPreference(KEY_PICTURE_SIZE);

        // Since the screen could be loaded from different resources, we need
        // to check if the preference is available here
        Log.d(TAG,"initPreference, preference="+preference);
        if (preference != null) {
//            mPreference = preference;
            CamParaUtil.filterUnsupportedOptions(preference,
                    sizeListToStringList(mParameters.getSupportedPictureSizes()));
        }
    }

    public void initialCameraPictureSize(
            Context context, Camera.Parameters parameters) {
        // When launching the camera app first time, we will set the picture
        // size to the first one in the list defined in "arrays.xml" and is also
        // supported by the driver.
        List<Camera.Size> supported = parameters.getSupportedPictureSizes();
        if (supported == null) return;
//        for (String candidate : context.getResources().getStringArray(
//                R.array.pref_camera_picturesize_entryvalues)) {
//            if (setCameraPictureSize(candidate, supported, parameters)) {
//
////                SharedPreferences settings = mContext.getSharedPreferences(FILE_PICTURE_SIZE, 0);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString(KEY_PICTURE_SIZE, candidate);
//                editor.apply();
//                return;
//            }
//        }
        Log.e(TAG, "No supported picture size found");
    }
    public static boolean setCameraPictureSize(
            String candidate, List<Camera.Size> supported, Camera.Parameters parameters) {
        int index = candidate.indexOf('x');
        if (index == NOT_FOUND) return false;
        int width = Integer.parseInt(candidate.substring(0, index));
        int height = Integer.parseInt(candidate.substring(index + 1));
        for (Camera.Size size : supported) {
            if (size.width == width && size.height == height) {
                parameters.setPictureSize(width, height);
                return true;
            }
        }
        return false;
    }
    private static List<String> sizeListToStringList(List<Camera.Size> sizes) {
        ArrayList<String> list = new ArrayList<String>();
        for (Camera.Size size : sizes) {
            list.add(String.format("%dx%d", size.width, size.height));
        }
        return list;
    }
}
