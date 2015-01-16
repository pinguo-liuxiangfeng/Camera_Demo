package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.hardware.Camera;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

import com.example.liuxiangfeng.camerademo.utils.Util;

import java.io.IOException;


public class CameraActivity extends Activity implements CameraFragment.onBtnClickedListener,
        Preference.OnPreferenceChangeListener, SettingFragment.onSettingsInterface,
        SurfaceHolder.Callback
{

    private static final String TAG = "CameraActivity";
    private Camera mCamera;
    private int mCameraId;
    private Camera.Parameters mParams;
    private SurfaceHolder mSurfaceHolder;
    private CameraSettings mSettings;
    private boolean mOpenCameraFail = false;
    private boolean isPreviewing = false;
    // We use a thread in ImageSaver to do the work of saving images and
    // generating thumbnails. This reduces the shot-to-shot time.
    private ImageSaverThread mImageSaver;
    private CameraFragment mCameraFragment;
    private SettingFragment mSettingFragment;
    private Preference mPictureSizePref;
    private boolean isInitialedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        // Create an instance of Camera
//        mCameraDevice = CameraOperation.getInstance().getCameraInstance();

        // 开启线程来启动摄像头
        mCameraOpenThread.start();
        Log.d(TAG, "onCreate, mCameraDevice=" + mCamera);

        // 设置UI布局文件
        if(findViewById(R.id.frag_container)!=null){
            //However, if we are being restored from previous state, we do
            //nothing and should return or else we end up the overlapping fragment.
            if(null!=savedInstanceState){
                return;
            }
//            //Create an instance of cameraFragment
//            mCameraFragment = new CameraFragment();
//            mSettingFragment = new SettingFragment();
//            //In case this activity was started with special instructions from an intent
//            //pass the intent's extras to the fragment as arguments
////            cameraFragment.setArguments(getIntent().getExtras());
//            //Add the fragment to the 'fragment_container' framelayout
//            int commit = getFragmentManager().beginTransaction()
//                    .add(R.id.frag_container, mCameraFragment).commit();
//            Log.d(TAG,"commit result = "+commit);
            mCameraFragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.cameraFrag);
            mSettingFragment = (SettingFragment) getFragmentManager().findFragmentById(R.id.settingFrag);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(mSettingFragment);
            transaction.show(mCameraFragment);
            transaction.commit();
        }
        // Make sure camera device is opened.
        try {
            // 这个join语句就是为了保证openCamera的线程执行完后，当前的线程才开始运行。主要是为了确保camera设备被
            // 打开了
            mCameraOpenThread.join();
            // 线程执行完后置为空来让系统回收资源
            mCameraOpenThread = null;
            if (mOpenCameraFail) {
                // 打开camera失败，显示“无法连接到相机”
                Util.showErrorAndFinish(this, R.string.cannot_connect_camera);
                return;
            }
        } catch (InterruptedException ex) {
            // ignore
        }
        //initial settings.
        mParams = mCamera.getParameters();
        mSettings = new CameraSettings(this, mParams);
        // 初始化一个图片的保存线程
        mImageSaver = new ImageSaverThread();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        openCamera();
        if(null != mSurfaceHolder){
            doStartPreview(mSurfaceHolder);
        }
        if (null == mImageSaver) {
            mImageSaver = new ImageSaverThread();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if((!isInitialedFlag)&&(null!=mPictureSizePref)){
            mSettings.initPreference((ListPreference) mPictureSizePref);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        if (mImageSaver != null) {
            mImageSaver.finish();
            mImageSaver = null;
        }
        releaseCamera();// release the camera immediately on pause event
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();

    }

    @Override
    public void onSettingsBtnClicked() {
        openSettingsFlag();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG,"onPreferenceChange, newValue="+newValue);
        changePicSize((String) newValue);
        return false;
    }

    @Override
    public void initialPref(Preference pref) {
        Log.d(TAG,"initialPref.mSettings="+mSettings);
        if(null != mSettings) {
            mSettings.initPreference((android.preference.ListPreference) pref);
            isInitialedFlag = true;
        }else{
            mPictureSizePref = pref;
            isInitialedFlag = false;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"surfaceCreated.");
        // The Surface has been created, now tell the camera where to draw the preview.
        mSurfaceHolder = holder;
        doStartPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        doStopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        doStartPreview(holder);
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        doStopPreview();
        mSurfaceHolder = null;
    }


    /**
     * UI:open setting display window.
     */
    public void openSettingsFlag(){
//        SettingFragment settingFragment = new SettingFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.frag_container, mSettingFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
        Log.d(TAG,"openSettingsFlag.....");
        transaction.hide(mCameraFragment);
        transaction.show(mSettingFragment);
        transaction.commit();
        if((!isInitialedFlag)&&(mPictureSizePref!=null)){
            mSettings.initPreference((ListPreference) mPictureSizePref);
        }
    }

    /**start to preview
     * @param holder
     */
    public void doStartPreview(SurfaceHolder holder){
        Log.i(TAG, "doStartPreview...");
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera != null){
//            initCameraParams();
            try {
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
                mCamera.setParameters(mSettings.getmParameters());
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            isPreviewing = true;

            //Test log:
            mParams = mCamera.getParameters(); //重新get一次
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + ", Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + ", Height = " + mParams.getPictureSize().height);
//            CamParaUtil.printSupportPreviewSize(mParams);
//            CamParaUtil.printSupportPictureSize(mParams);
        }
    }
    /**
     * stop preview
     **/
    public void doStopPreview(){
        Log.d(TAG,"doStopPreview");
        if(null != mCamera) {
            mCamera.stopPreview();
        }
        isPreviewing = false;
    }
    /**
     * open thread.
     */
    Thread mCameraOpenThread = new Thread(new Runnable() {
        public void run() {
            mCameraId = CameraHolder.instance().getBackCameraId();
            mCamera = CameraHolder.instance().tryOpen(mCameraId);
            if(null==mCamera){
                mOpenCameraFail = true;
            }
        }
    });
    /**
     * open camera
     **/
    public void openCamera(){
        Log.d(TAG,"openCamera");
        mCamera = CameraHolder.instance().tryOpen(mCameraId);
    }
    /**
     * release camera
     **/
    public void releaseCamera(){
        Log.d(TAG,"releaseCamera");
        if(null == mCamera){
            return;
        }
        doStopPreview();
        mCamera = null;
        CameraHolder.instance().release();
    }
//    /**
//     * capture pictures
//     **/
//    public void capturePicture(){
//        Log.d(TAG, "capturePicture..");
//
//        new Thread(new Runnable() {
//            public void run() {
//                Log.d(TAG, "mTakePictureThread..run begin.");
//                doTakePicture();
//                Log.d(TAG, "mTakePictureThread..run end.");
//            }
//        }).start();
//        Log.d(TAG, "capturePicture..1");
//        doStartPreview(mSurfaceHolder);
//    }
    /**
     * take pictures
     **/
    public void doTakePicture(){
        Log.d(TAG, "doTakePicture..");
        mCamera.takePicture(mShutterCallback, mRowPicture, mPicture);
    }
    private Camera.ShutterCallback mShutterCallback = new ShutterCallback();
    private Camera.PictureCallback mRowPicture = new RawPictureCallback();
    private final class ShutterCallback
            implements android.hardware.Camera.ShutterCallback {
        // 拍照按下时触发这个事件
        public void onShutter() {
            Log.d(TAG, "mShutterCallbackTime = " + System.currentTimeMillis());
//            mFocusManager.onShutter();
        }
    }

    private final class RawPictureCallback implements Camera.PictureCallback {
        public void onPictureTaken(
                byte [] rawData, android.hardware.Camera camera) {
            Log.d(TAG, "mShutterToRawCallbackTime = "
                    + System.currentTimeMillis());
        }
    }
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "mShutterToJpegCallbackTime = "
                    + System.currentTimeMillis());

            if(null != data) {
                Log.d(TAG,"data.size="+data.length);
                mCamera.stopPreview();
                mImageSaver.addImage(data);
                //再次进入预览
                mCamera.startPreview();
            }
        }
    };
//    /**
//     * set focus mode
//     **/
//    public void initCameraParams(){
//        if(mCamera != null) {
//            mParams = mCamera.getParameters();
//            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
////            mParams.setPreviewSize(3840,2160);
////            mParams.setPictureSize(4000,3000);
//            mCamera.setDisplayOrientation(90);
//            mCamera.setParameters(mParams);
//        }
//    }
    /**
     * set focus mode
     **/
    public void setFocusMode(){
        mSettings.updateFocusModeParams();
        if(mCamera != null) {
            mCamera.setParameters(mSettings.getmParameters());
        }
    }
    /**
     * room
     */
    public void zoom(int progress){
        mSettings.updateZoomParams(progress);
        if(mCamera != null) {
            mCamera.setParameters(mSettings.getmParameters());
        }
    }
    public void zoom(boolean isUp){
        mSettings.updateZoomParams(isUp);
        if(mCamera != null) {
            mCamera.setParameters(mSettings.getmParameters());
        }
    }
    /**
     * change picture size
     */
    public void changePicSize(String size){
        mSettings.updatePicSizeParams(size);
        if(mCamera != null) {
            mCamera.setParameters(mSettings.getmParameters());
        }
    }
    public void onBackPressed() {
        boolean isHidden = mSettingFragment.isHidden();
        Log.d(TAG,"onBackPressed, isHidden="+isHidden);
        if(!isHidden){
            getFragmentManager().beginTransaction().hide(mSettingFragment).commit();
            getFragmentManager().beginTransaction().show(mCameraFragment).commit();
        }else{
            super.onBackPressed();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                zoom(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                zoom(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
