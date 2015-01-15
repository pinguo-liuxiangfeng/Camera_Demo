package com.example.liuxiangfeng.camerademo;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * Created by liuxiangfeng on 15-1-14.
 */
public class CameraHolder {
    private static final String TAG = "CameraHolder";
    private Camera mCameraDevice;
    private int mNumberOfCameras;
    private int mCameraId = -1;  // current camera id
    private int mBackCameraId = -1, mFrontCameraId = -1;
    private Camera.CameraInfo[] mInfo;

    // We store the camera parameters when we actually open the device,
    // so we can restore them in the subsequent open() requests by the user.
    // This prevents the parameters set by the Camera activity used by
    // the VideoCamera activity inadvertently.
    private Camera.Parameters mParameters;

    // Use a singleton.
    private static CameraHolder sHolder;
    public static synchronized CameraHolder instance() {
        if (sHolder == null) {
            sHolder = new CameraHolder();
        }
        return sHolder;
    }


    private CameraHolder() {
        mNumberOfCameras = android.hardware.Camera.getNumberOfCameras();
        mInfo = new Camera.CameraInfo[mNumberOfCameras];
        for (int i = 0; i < mNumberOfCameras; i++) {
            mInfo[i] = new Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, mInfo[i]);
            if (mBackCameraId == -1 && mInfo[i].facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i;
            }
            if (mFrontCameraId == -1 && mInfo[i].facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = i;
            }
        }
    }

    public int getNumberOfCameras() {
        return mNumberOfCameras;
    }

    public Camera.CameraInfo[] getCameraInfo() {
        return mInfo;
    }

    private synchronized android.hardware.Camera open(int cameraId)
            throws CameraHardwareException {
        if (mCameraDevice != null && mCameraId != cameraId) {
            mCameraDevice.release();
            mCameraDevice = null;
            mCameraId = -1;
        }
        if (mCameraDevice == null) {
            try {
                Log.v(TAG, "open camera " + cameraId);
                mCameraDevice = android.hardware.Camera.open(cameraId);
                mCameraId = cameraId;
            } catch (RuntimeException e) {
                Log.e(TAG, "fail to connect Camera", e);
                throw new CameraHardwareException(e);
            }
            mParameters = mCameraDevice.getParameters();
        } else {
            try {
                mCameraDevice.reconnect();
            } catch (IOException e) {
                Log.e(TAG, "reconnect failed.");
                throw new CameraHardwareException(e);
            }
            mCameraDevice.setParameters(mParameters);
        }
        return mCameraDevice;
    }

    /**
     * Tries to open the hardware camera. If the camera is being used or
     * unavailable then return {@code null}.
     */
    public synchronized android.hardware.Camera tryOpen(int cameraId) {
        Log.d(TAG,"tryOpen");
        try {
            return open(cameraId);
        } catch (CameraHardwareException e) {
            return null;
        }
    }

    public synchronized void release() {
        if((null==mCameraDevice)){
            return;
        }
        mCameraDevice.stopPreview();
        releaseCamera();
    }

    private synchronized void releaseCamera() {
        Log.d(TAG,"releaseCamera");
        if((null==mCameraDevice)){
            return;
        }
        mCameraDevice.release();
        mCameraDevice = null;
        // We must set this to null because it has a reference to Camera.
        // Camera has references to the listeners.
        mParameters = null;
        mCameraId = -1;
    }

    public int getBackCameraId() {
        return mBackCameraId;
    }

    public int getFrontCameraId() {
        return mFrontCameraId;
    }
}
