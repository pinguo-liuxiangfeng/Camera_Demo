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

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        if(null == mCamera) {
            openCamera();
        }
        return mCamera; // returns null if camera is unavailable
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
    /**
    * take pictures
    **/
    public void doTakePicture(){
        Log.d(TAG, "doTakePicture..");
        mCamera.takePicture(null, null, mPicture);
    }

    /**start to preview
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder){
        Log.i(TAG, "doStartPreview...");
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera != null){
            initCameraParams();
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            isPreviewing = true;
//            mPreviwRate = previewRate;

            //Test log:
            mParams = mCamera.getParameters(); //重新get一次
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }
    /**
     * stop preview
     **/
    public void doStopPreview(){
        if(null != mCamera) {
            mCamera.stopPreview();
        }
        isPreviewing = false;
    }
    /**
     * release camera
     **/
    public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if(null != data) {
                Log.d(TAG,"data.size="+data.length);
                mCamera.stopPreview();
                FileUtil.saveBitmap(data);
            }
            //再次进入预览
            mCamera.startPreview();
        }
    };
    /**
     * set focus mode
     **/
    public void initCameraParams(){
        if(mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(mParams);
        }
    }
    /**
     * set focus mode
     **/
    public void setFocusMode(){
        if(mCamera != null) {
            mParams = mCamera.getParameters();
            //Todo:设置FocusMode
            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("auto")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(mParams);
        }
    }
    /**
     * set preview size
     **/
    public void setPreviewSize(){
        if(mCamera != null) {
            mParams = mCamera.getParameters();
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            //Todo:设置PreviewSize和PictureSize
            mCamera.setParameters(mParams);
        }
    }
    /**
     * set focus mode
     **/
    public void setPictureSize(){
        if(mCamera != null) {
            mParams = mCamera.getParameters();
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            //Todo:设置PreviewSize和PictureSize
            mCamera.setParameters(mParams);
        }
    }
    /**
     * room
     */
    public void zoom(int progress){
        if(mCamera != null) {
            mParams = mCamera.getParameters();
            if(!mParams.isZoomSupported()){
                Log.e(TAG,"Don't support zoom !");
                return;
            }
            int value = (mParams.getMaxZoom()*progress)/100;
            Log.d(TAG,"zoom value="+value);
            mParams.setZoom(value);
            //Todo:设置PreviewSize和PictureSize
            mCamera.setParameters(mParams);

        }
    }
}
