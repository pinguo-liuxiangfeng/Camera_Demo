package com.example.liuxiangfeng.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
//    private Camera mCamera;
//    public CameraPreview(Context context, Camera camera) {
//        super(context);
//        mCamera = camera;
//
//        // Install a SurfaceHolder.Callback so we get notified when the
//        // underlying surfjava.lang.Stringace is created and destroyed.
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//    }
    public CameraPreview(Context context) {
        super(context);
//        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surfjava.lang.Stringace is created and destroyed.
//        mHolder = getHolder();
//        mHolder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
//        CameraOperation.getInstance().doStartPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

//        if (mHolder.getSurface() == null){
//            // preview surface does not exist
//            return;
//        }
//
//        // stop preview before making changes
//        CameraOperation.getInstance().doStopPreview();
//
//        // set preview size and make any resize, rotate or
//        // reformatting changes here
//
//        // start preview with new settings
//        CameraOperation.getInstance().doStartPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
