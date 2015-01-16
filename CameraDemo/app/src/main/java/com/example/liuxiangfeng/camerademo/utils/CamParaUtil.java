package com.example.liuxiangfeng.camerademo.utils;

import android.hardware.Camera;
import android.preference.ListPreference;
import android.util.Log;
import android.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class CamParaUtil {
    private static final String TAG = "CamParaUtil";
    public static void filterUnsupportedOptions(ListPreference pref,
                                                      List<String> supported){
        Log.d(TAG,"filterUnsupportedOptions");
        CharSequence[] prefEntries = pref.getEntries();
        CharSequence[] prefEntryValues = pref.getEntryValues();
        Log.d(TAG,"supported="+supported);
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> entryValues = new ArrayList<CharSequence>();
        for (int i = 0, len = prefEntryValues.length; i < len; i++) {
            if (supported.indexOf(prefEntryValues[i].toString()) >= 0) {
                entries.add(prefEntries[i]);
                entryValues.add(prefEntryValues[i]);
            }
        }
        int size = entries.size();
        prefEntries = entries.toArray(new CharSequence[size]);
        prefEntryValues = entryValues.toArray(new CharSequence[size]);
        pref.setEntries(prefEntries);
        pref.setEntryValues(prefEntryValues);
        //add for set default value.
        pref.setDefaultValue(entries.get(0));
    }

    public static Camera.Size getPropPreviewSize(Camera.Parameters params, int width, int height){
        Log.i(TAG, "getPropPreviewSize:width = " + width + " height = " + height);
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        int wd=0,hd=0,tempwd=0, temphd=0;
        Camera.Size size = previewSizes.get(0);
        for(int i=0; i< previewSizes.size(); i++){
            Camera.Size s = previewSizes.get(i);
            tempwd = Math.abs(s.width-width);
            temphd = Math.abs(s.height-height);
            if(0==i){
                wd = Math.abs(s.width-width);
                hd = Math.abs(s.height-height);
            }
            if((tempwd <= wd) || (temphd <= hd) ){
                size = s;
                wd = tempwd;
                hd = temphd;
            }
        }
        //just for special case error.
        Camera.Size temp = previewSizes.get(0);
        if(size.width>temp.width){
            size = temp;
        }
        return size;
    }
    public static Camera.Size getPropPictureSize(Camera.Parameters params, int width, int height){
        Log.i(TAG, "pictureSizes:width = " + width + " height = " + height);
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        int wd=0,hd=0,tempwd=0, temphd=0;
        Camera.Size size = pictureSizes.get(0);
        for(int i=0; i< pictureSizes.size(); i++){
            Camera.Size s = pictureSizes.get(i);
            tempwd = Math.abs(s.width-width);
            temphd = Math.abs(s.height-height);
            if(0==i){
                wd = Math.abs(s.width-width);
                hd = Math.abs(s.height-height);
            }
            if((tempwd <= wd) || (temphd <= hd) ){
                size = s;
                wd = tempwd;
                hd = temphd;
            }
        }
        return size;
    }

    /**打印支持的previewSizes
     * @param params
     */
    public static void printSupportPreviewSize(Camera.Parameters params){
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = " + size.width + " height = " + size.height);
        }

    }

    /**打印支持的pictureSizes
     * @param params
     */
    public static void printSupportPictureSize(Camera.Parameters params){
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public static void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
