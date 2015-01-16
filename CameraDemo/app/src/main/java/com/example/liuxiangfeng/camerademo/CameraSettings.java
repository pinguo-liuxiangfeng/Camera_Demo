package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;

import com.example.liuxiangfeng.camerademo.utils.CamParaUtil;
import com.example.liuxiangfeng.camerademo.utils.Util;

import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-12.
 */
public class CameraSettings {
    //定义SharedPreferences对象
    private SharedPreferences sp;
    public static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
//    public static final String KEY_PREVIEW_SIZE = "pref_camera_previewsize_key";
    public static final String SETTING_FILE_NAME = "settings_info";
    private static final int NOT_FOUND = -1;
    private static final String TAG = "CameraSettings";
    private final Context mContext;
    private final Camera.Parameters mParameters;
    private Preference mPictureSizePref;

    public CameraSettings(Activity activity, Camera.Parameters parameters) {
        mContext = activity;
        mParameters = parameters;
        initCameraParams();
    }
    public Camera.Parameters getmParameters(){
        return mParameters;
    }
    public void initCameraParams() {

        mParameters.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
        mParameters.setRotation(90);
        String picSize = readSettingsValue(KEY_PICTURE_SIZE);
        Log.d(TAG,"initCameraParams, picSize="+picSize);
        if(null==picSize){
            Camera.Size size = mParameters.getPictureSize();
            String str = Util.sizeToString(size);
            writeSettingsValue(KEY_PICTURE_SIZE, str);
            //for test
            String value = readSettingsValue(KEY_PICTURE_SIZE);
            Log.d(TAG,"test, value="+value);
        }else {
            updatePicSizeParams(picSize);
        }
    }
    public void initPreference(ListPreference preference) {
//        ListPreference pictureSize = (ListPreference) group.findPreference(KEY_PICTURE_SIZE);

        // Since the screen could be loaded from different resources, we need
        // to check if the preference is available here
        Log.d(TAG, "initPreference, preference=" + preference);
        if (preference != null) {
            CamParaUtil.filterUnsupportedOptions(preference,
                    Util.sizeListToStringList(mParameters.getSupportedPictureSizes()));

            String picSize = readSettingsValue(KEY_PICTURE_SIZE);
            Log.d(TAG, "initPreference, picSize=" + picSize);
            if(null!=picSize) {
                preference.setValue(picSize);
            }else {
                picSize = preference.getValue();
                writeSettingsValue(KEY_PICTURE_SIZE, picSize);
            }
            mPictureSizePref = preference;
        }
    }
    /**
     * change picture size
     */
    public void updatePicSizeParams(String size){
        int index = size.indexOf('x');
        if (index == -1) return;
        int width = Integer.parseInt(size.substring(0, index));
        int height = Integer.parseInt(size.substring(index + 1));
        Log.d(TAG, "changePicSize, width=" + width + ", height=" + height);
        updatePicSizeParams(width, height);
        writeSettingsValue(KEY_PICTURE_SIZE, size);
        if(null!=mPictureSizePref){
            ((ListPreference)mPictureSizePref).setValue((String) size);
        }
    }
    public void updatePicSizeParams(int width, int height){
        Log.d(TAG,"changePicSize");
        if(mParameters != null) {
            //Todo:设置PreviewSize和PictureSize
            Camera.Size s = CamParaUtil.getPropPreviewSize(mParameters, width, height);
            Log.d(TAG, "getPropPreviewSize, width=" + s.width + ", height=" + s.height);
            mParameters.setPreviewSize(s.width, s.height);

            s = CamParaUtil.getPropPictureSize(mParameters, width, height);
            Log.d(TAG, "getPropPictureSize, width=" + s.width + ", height=" + s.height);
            mParameters.setPictureSize(s.width, s.height);
        }
    }
    /**
     * set focus mode
     **/
    public void updateFocusModeParams(){
        if(mParameters != null) {
            //Todo:设置FocusMode
//            CamParaUtil.printSupportFocusMode(mParameters);
            List<String> focusModes = mParameters.getSupportedFocusModes();
            if (focusModes.contains("auto")) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
    }
    /**
     * room
     */
    public void updateZoomParams(int progress){
        if(mParameters != null) {
            if(!mParameters.isZoomSupported()){
                Log.e(TAG,"Don't support zoom !");
                return;
            }
            int value = (mParameters.getMaxZoom()*progress)/100;
            Log.d(TAG,"zoom progress="+progress);
            Log.d(TAG,"zoom value="+value);
            Log.d(TAG,"zoom mParameters.getMaxZoom()="+mParameters.getMaxZoom());
            mParameters.setZoom(value);
        }
    }
    public static final int ZOOM_STEP = 2;
    public void updateZoomParams(boolean isUp){
        if(mParameters != null) {
            if(!mParameters.isZoomSupported()){
                Log.e(TAG,"Don't support zoom !");
                return;
            }
            int maxZoom = mParameters.getMaxZoom();
            int curValue = mParameters.getZoom();
            int newValue;
            if(isUp){
                newValue = curValue + ZOOM_STEP;
                if(newValue>maxZoom){
                    newValue = maxZoom;
                }
            }else{
                newValue = curValue - ZOOM_STEP;
                if(newValue<0){
                    newValue = 0;
                }
            }

            mParameters.setZoom(newValue);
        }
    }
    public void writeSettingsValue(String key, String value){
        sp = mContext.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String readSettingsValue(String key){
        sp = mContext.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE);
        String value = sp.getString(key, null);
        return value;
    }

}
