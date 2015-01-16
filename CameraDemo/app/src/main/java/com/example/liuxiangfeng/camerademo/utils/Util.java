package com.example.liuxiangfeng.camerademo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import com.example.liuxiangfeng.camerademo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-13.
 */
public class Util {
    private static final String TAG = "Util";

    public static void showErrorAndFinish(final Activity activity, int msgId) {
        DialogInterface.OnClickListener buttonListener =
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                };
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.camera_error_title)
                .setMessage(msgId)
                .setNeutralButton(R.string.dialog_ok, buttonListener)
                .show();
    }
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    public static List<String> sizeListToStringList(List<Camera.Size> sizes) {
        ArrayList<String> list = new ArrayList<String>();
        for (Camera.Size size : sizes) {
            list.add(String.format("%dx%d", size.width, size.height));
        }
        return list;
    }
    public static String sizeToString(Camera.Size size){
        int width = size.width;
        int height = size.height;
        String str = new String(Integer.toString(width)+'x'+Integer.toString(height));
        Log.d(TAG, "str=" + str);
        return str;
    }
}
