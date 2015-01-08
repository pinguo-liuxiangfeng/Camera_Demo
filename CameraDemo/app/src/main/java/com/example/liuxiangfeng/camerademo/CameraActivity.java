package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.File;


public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Context mContext;
    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // Create an instance of Camera
        mCamera = CameraOperation.getInstance().getCameraInstance();
        Log.d(TAG, "onCreate, mCamera="+mCamera);
        initUI();
    }
    private void initUI(){
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        CameraOperation.getInstance().doTakePicture();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
        captureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CameraOperation.getInstance().setFocusMode();
                mProgressBar.setVisibility(View.VISIBLE);
                return false;
            }
        });
        //
        mSeekBar = (SeekBar) findViewById(R.id.zoom_seekbar);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CameraOperation.getInstance().zoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.focus);
    }
    @Override
    protected void onPause() {
        super.onPause();
        CameraOperation.getInstance().releaseCamera();// release the camera immediately on pause event
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraOperation.getInstance().releaseCamera();

    }
}
