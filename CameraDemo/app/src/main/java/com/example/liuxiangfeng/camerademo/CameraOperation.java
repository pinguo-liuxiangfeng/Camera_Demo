package com.example.liuxiangfeng.camerademo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class CameraOperation {
    private static final String TAG = "CameraOperation";
    private static CameraOperation mCameraOperation = null;
    private Camera mCamera = null;
    private boolean isPreviewing = false;
    private Camera.Parameters mParams;

    private CameraOperation(){
    }
    public static CameraOperation getInstance(){
        if(null == mCameraOperation){
            synchronized (CameraOperation.class) {
                if (null == mCameraOperation) {
                    mCameraOperation = new CameraOperation();
                }
            }
        }
        return mCameraOperation;
    }


    /**
     * open camera
     **/
    private void openCamera(){
        synchronized (CameraOperation.class) {
            if (null == mCamera) {
                try {
                    mCamera = Camera.open(); // attempt to get a Camera instance
                    Log.d(TAG,"open Camera success!");
                } catch (Exception e) {
                    // Camera is not available (in use or does not exist)
                    Log.d(TAG,"open Camera Fail!");
                }
            }
        }
    }

}
