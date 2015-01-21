package com.example.liuxiangfeng.camerademo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

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
    private Button mCaptureButton;
    private Drawable mFocusedDrawable, mNormalDrawable;
    private boolean isInZoomStatus = false;

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

    private void initUI() {
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(mActivity);
        FrameLayout preview = (FrameLayout) mActivity.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mPreview.getHolder().addCallback(mSurfaceCallBack);

        // Add a listener to the Capture button
        mCaptureButton = (Button) mActivity.findViewById(R.id.button_capture);
        mCaptureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"mCaptureButton, onClick");
                        // get an image from the camera
//                        mActivity.capturePicture();
                        mActivity.doTakePicture();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
        mCaptureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"mCaptureButton, onLongClick");
                updateFocusUI(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mActivity.autoFocus();
                return false;
            }
        });
        mCaptureButton.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"mCaptureButton, onTouch, event.getAction()="+event.getAction());
                if((event.getAction()==MotionEvent.ACTION_UP)
                        ||(event.getAction()==MotionEvent.ACTION_CANCEL)){
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        mSeekBar = (SeekBar) mActivity.findViewById(R.id.zoom_seekbar);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mActivity.zoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isInZoomStatus = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isInZoomStatus = false;
                updateZoomSeekbar(View.VISIBLE);
            }
        });
        //UI
        updateZoomSeekbar(View.VISIBLE);

        mProgressBar = (ProgressBar) mActivity.findViewById(R.id.focus);
        mSettings = (ImageButton) mActivity.findViewById(R.id.setting);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSettingsBtnClicked();
            }
        });

        //init focus ui objects.
        mFocusedDrawable = getResources().getDrawable(R.drawable.progress_circle_focused);
        mNormalDrawable = getResources().getDrawable(R.drawable.progress_circle);
    }

    public void updateFocusUI(boolean focused){
        Log.d(TAG,"updateFocusUI, focused="+focused);
        calculateFocusArea();
        if(focused) {
            mProgressBar.setIndeterminateDrawable(mFocusedDrawable);
        }else{
            mProgressBar.setIndeterminateDrawable(mNormalDrawable);
        }
    }

    public void calculateFocusArea(){
        WindowManager wm = (WindowManager) mActivity
                .getSystemService(Context.WINDOW_SERVICE);
        int CIRCLE_WIDTH = 15;
        int width = wm.getDefaultDisplay().getWidth()/2;
        int height = wm.getDefaultDisplay().getHeight()*2/5;

        mFocusedDrawable.setBounds(width-CIRCLE_WIDTH, height-CIRCLE_WIDTH, width+CIRCLE_WIDTH, height+CIRCLE_WIDTH);
        mNormalDrawable.setBounds(width-CIRCLE_WIDTH, height-CIRCLE_WIDTH, width+CIRCLE_WIDTH, height+CIRCLE_WIDTH);

    }
    private Timer mTimer;
    private TimerTask mTimerTask;
    public void updateZoomSeekbar(int visibility){
        Log.d(TAG,"updateZoomSeekbar, visibility="+visibility);
        if(View.VISIBLE == visibility) {
            mSeekBar.setVisibility(visibility);
            if (null != mTimer) {
                mTimer.cancel();
            }
            mTimer = new Timer(true);
            mTimerTask = new TimerTask() {
                public void run() {
                    mSeekBar.post(new Runnable() {
                        @Override
                        public void run() {
//                            mSeekBar.setVisibility(View.INVISIBLE);
                            updateZoomSeekbar(View.INVISIBLE);
                        }
                    });
                }

            };
            mTimer.schedule(mTimerTask, 2000);
        }else{
            if(!isInZoomStatus) {
                mSeekBar.setVisibility(visibility);
            }
            if (null != mTimer) {
                mTimer.cancel();
                mTimer = null;
            }
        }

    }
}
