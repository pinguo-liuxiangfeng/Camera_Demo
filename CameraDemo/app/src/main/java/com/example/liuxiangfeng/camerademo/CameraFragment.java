package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Created by liuxiangfeng on 15-1-7.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private CameraPreview mPreview;
    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private ImageButton mSettings;
    private onBtnClickedListener mCallback;
    private CameraActivity mActivity;
    private SurfaceHolder.Callback mSurfaceCallBack;

    public interface onBtnClickedListener{
        public void onSettingsBtnClicked();
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frag_camera, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity = (CameraActivity) activity;
        mCallback = (onBtnClickedListener)activity;
        mSurfaceCallBack = (SurfaceHolder.Callback) activity;
    }

    private void initUI(){
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(mActivity);
        FrameLayout preview = (FrameLayout) mActivity.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mPreview.getHolder().addCallback(mSurfaceCallBack);

        // Add a listener to the Capture button
        Button captureButton = (Button) mActivity.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
//                        mActivity.capturePicture();
                        mActivity.doTakePicture();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
        captureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mActivity.autoFocus();
//                updateFocusUI(false);
                mProgressBar.setVisibility(View.VISIBLE);

                return false;
            }
        });
        //
        mSeekBar = (SeekBar) mActivity.findViewById(R.id.zoom_seekbar);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mActivity.zoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.focus);
        mSettings = (ImageButton) mActivity.findViewById(R.id.setting);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSettingsBtnClicked();
            }
        });

    }
    public void updateFocusUI(boolean focused){
//        Drawable mfocused = getResources().getDrawable(R.drawable.progress_circle_focused);
//        Log.d(TAG, "updateFocusUI, mfocused=" + mfocused);
//        if(true) {
//            mProgressBar.setIndeterminateDrawable(mfocused);
//            mProgressBar.setProgressDrawable(mfocused);
//        }else{
//            mProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_circle));
//        }
    }
}
