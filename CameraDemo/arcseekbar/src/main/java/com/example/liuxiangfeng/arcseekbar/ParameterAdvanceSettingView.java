package com.example.liuxiangfeng.arcseekbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by liuxiangfeng on 15-5-11.
 */
public class ParameterAdvanceSettingView extends RelativeLayout implements ArcSeekBar.OnScrollingListener{
    private Activity mContext;

    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private int mMinR;
    private int mD;

    public static final int FAD_OUT_DURATION = 200;
    private static final int MSG_FADE_OUT_TIPS = 11;
    private ArcSeekBar mSbExposure;
    private ArcSeekBar mSbISO;
    private ArcSeekBar mSbSharpness;
    private ArcSeekBar mSbWhiteBalance;
    private ArcSeekBar mSbFocusMode;
    private ViewGroup mArcSeekbarContainer;

    private int mSupportedCount;

    public ParameterAdvanceSettingView(Context context) {
        super(context);
        mContext = (Activity) context;
    }

    public ParameterAdvanceSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
    }

    public ParameterAdvanceSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = (Activity) context;
    }

    @Override
    protected void onFinishInflate() {

//        sector = (SectorDrawable) findViewById(R.id.sector);
        mSbFocusMode = (ArcSeekBar) findViewById(R.id.camera_adv_focus_mode);
        mSbFocusMode.setThumbDrawable(R.drawable.ic_camera_thumb_focus);

        mSbExposure = (ArcSeekBar) findViewById(R.id.camera_adv_exposure);
        mSbExposure.setThumbDrawable(R.drawable.ic_camera_thumb_exposure);

        mSbISO = (ArcSeekBar) findViewById(R.id.camera_adv_iso);
        mSbISO.setThumbDrawable(R.drawable.ic_camera_thumb_iso);

        mSbSharpness = (ArcSeekBar) findViewById(R.id.camera_adv_sharpness);
        mSbSharpness.setThumbDrawable(R.drawable.ic_camera_thumb_sharpness);

        mSbWhiteBalance = (ArcSeekBar) findViewById(R.id.camera_adv_white_balance);
        mSbWhiteBalance.setThumbDrawable(R.drawable.ic_camera_thumb_wb);

//        sector.setArc(mSbFocusMode);

//        mTipValue = (TextView) findViewById(R.id.camera_adv_tips_1);
//        mTipSummer = (TextView) findViewById(R.id.camera_adv_tips_2);
//        mTipLayout = findViewById(R.id.camera_adv_tip_layout);


        mArcSeekbarContainer = (ViewGroup) findViewById(R.id.arc_container);

//        mTlpLayoutMargin = getResources().getDimensionPixelSize(R.dimen.adv_parameter_tip_layout_margin);

        super.onFinishInflate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("ADV", "wsize:"+wsize+", hsize:"+hsize);
//        mWidth = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        mHeight = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        this.measure(mWidth, mHeight);
//        mHeight = this.getMeasuredHeight();
//        mWidth = this.getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = wsize;
        mHeight = hsize;
        initSeekBars(null);

    }
    public void initSeekBars(PreferenceGroup pref) {

        mSupportedCount = 0;

        /****
         *

        ArcSeekBar visiableView = null;
        CameraSettingModel settingModel = CameraSettingModel.instance();

        //每个AcrSeekBar的的初始化代码的顺序需要与xml里的顺序保持一致
        //前置相机目前不支持触屏聚焦

        if (pref.isFocusModeSupported()) {
            mSupportedCount++;
            final IconListPreference focusModePref = (IconListPreference) pref.findPreference(CameraPrefKeys.KEY_CAMERA_FOCUS_MODE);

            mSbFocusMode.setScaleAdapter(new ScaleAdapter() {
                //                final CharSequence[] wbValues = wbPref.getEntryValues();
                final int[] focusModeIcons = focusModePref.getIconIds();

                @Override
                public int getScaleCount() {
                    return focusModeIcons.length;
                }

                @Override
                public int getScaleViewCount() {
                    return focusModeIcons.length;//因为聚焦类型最多为3个，所以这里不用进行size限制
                }

                @Override
                public Scale getScale(int position) {
                    return new MyScale("", getResources().getDrawable(focusModeIcons[position]));
                }
            });

            mSbFocusMode.setOnScaleChangedListener(new ArcSeekBar.OnScaleChangedListener() {
                @Override
                public void onScaleChanged(int preScaleIndex, int scaleIndex) {
                    updateParameters(scaleIndex, focusModePref);
                    resetTipLayoutPosition();
                }
            });

            mSbFocusMode.setCurrentScaleItem(focusModePref.getCurrValueIndex());
            mSbFocusMode.setOnScrollingListener(this);
            mSbFocusMode.setTag(mSupportedCount - 1);
            mSbFocusMode.setTag(R.id.camera_adv_tips_2, focusModePref);
            visiableView = mSbFocusMode;
        } else {
            mSbFocusMode.setVisibility(View.GONE);
        }

        if (settingModel.isWhiteBalanceSupported()) {
            mSupportedCount++;
            final IconListPreference wbPref = (IconListPreference) pref.findPreference(CameraPrefKeys.KEY_CAMERA_WHITE_BALANCE);

            mSbWhiteBalance.setScaleAdapter(new ScaleAdapter() {
                //                final CharSequence[] wbValues = wbPref.getEntryValues();
                final int[] wbIcons = wbPref.getIconIds();

                @Override
                public int getScaleCount() {
                    return wbIcons.length;
                }

                @Override
                public int getScaleViewCount() {
                    return wbIcons.length;//因为白平衡最多为8个，所以这里不用进行size限制
                }

                @Override
                public Scale getScale(int position) {
                    return new MyScale("", getResources().getDrawable(wbIcons[position]));
                }
            });

            mSbWhiteBalance.setOnScaleChangedListener(new ArcSeekBar.OnScaleChangedListener() {
                @Override
                public void onScaleChanged(int preScaleIndex, int scaleIndex) {
                    updateParameters(scaleIndex, wbPref);
                    resetTipLayoutPosition();
                }
            });

            mSbWhiteBalance.setCurrentScaleItem(wbPref.getCurrValueIndex());
            mSbWhiteBalance.setOnScrollingListener(this);
            mSbWhiteBalance.setTag(mSupportedCount - 1);
            mSbWhiteBalance.setTag(R.id.camera_adv_tips_2, wbPref);
            visiableView = mSbWhiteBalance;
        } else {
            mSbWhiteBalance.setVisibility(View.GONE);
        }

        if (settingModel.isSupportISO()) {
            mSupportedCount++;
            final ListPreference isoPref = pref.findPreference(CameraPrefKeys.KEY_CAMERA_ISO);
            mSbISO.setScaleAdapter(new ScaleAdapter() {
                final Object[] isoEntries = isoPref.getEntries();

                @Override
                public int getScaleCount() {
                    return isoEntries.length;
                }

                @Override
                public int getScaleViewCount() {
                    return caculateViewCount(isoEntries.length);
                }

                @Override
                public Scale getScale(int position) {
                    return new MyScale(String.valueOf(isoEntries[position]), null);
                }
            });
            mSbISO.setOnScaleChangedListener(new ArcSeekBar.OnScaleChangedListener() {
                @Override
                public void onScaleChanged(int preScaleIndex, int scaleIndex) {
                    updateParameters(scaleIndex, isoPref);
                    resetTipLayoutPosition();
                }
            });

            mSbISO.setCurrentScaleItem(isoPref.getCurrValueIndex());
            mSbISO.setOnScrollingListener(this);
            mSbISO.setTag(mSupportedCount - 1);
            mSbISO.setTag(R.id.camera_adv_tips_2, isoPref);
            visiableView = mSbISO;
        } else {
            mSbISO.setVisibility(View.GONE);
        }

        if (isExposureSupportedEx(settingModel, PreferenceGroup.get())) {
            final ListPreference exPref = pref.findPreference(CameraPrefKeys.KEY_CAMERA_EXPOSURE);
            mSupportedCount++;

            mSbExposure.setScaleAdapter(new ScaleAdapter() {
                final Object[] exValues = exPref.getEntryValues();
                final Object[] exEntries = exPref.getEntries();

                @Override
                public int getScaleCount() {
                    return exValues.length;
                }

                @Override
                public int getScaleViewCount() {
                    return caculateViewCount(exValues.length);
                }

                @Override
                public Scale getScale(int position) {
                    return new MyScale(String.valueOf(exEntries[position]), null);
                }
            });

            mSbExposure.setOnScaleChangedListener(new ArcSeekBar.OnScaleChangedListener() {
                @Override
                public void onScaleChanged(int preScaleIndex, int scaleIndex) {
                    updateParameters(scaleIndex, exPref);
                    resetTipLayoutPosition();
                }
            });

            mSbExposure.setCurrentScaleItem(exPref.getCurrValueIndex());

            mSbExposure.setOnScrollingListener(this);
            mSbExposure.setTag(mSupportedCount - 1);
            mSbExposure.setTag(R.id.camera_adv_tips_2, exPref);
            visiableView = mSbExposure;
        } else {
            mSbExposure.setVisibility(View.GONE);
        }

        if (settingModel.isSharpnessSupported()) {
            mSupportedCount++;
            final ListPreference sharpPref = pref.findPreference(CameraPrefKeys.KEY_CAMERA_SHARPNESS);
            final Object[] sharpEntries = sharpPref.getEntries();
            final int delta = sharpPref.getEntries().length / 8 + 1;// 可能小于8，那么应该默认从 1 开始
            final int scaleCount = sharpPref.getEntries().length / delta;
            mSbSharpness.setScaleAdapter(new ScaleAdapter() {

                @Override
                public int getScaleCount() {
                    return scaleCount;
                }

                @Override
                public int getScaleViewCount() {
                    return scaleCount;
                }

                @Override
                public Scale getScale(int position) {
                    //做数量限制，避免item太多，导致屏幕上太拥挤
                    position = position * delta;
                    if (position > sharpEntries.length) {
                        position = sharpEntries.length;
                    }
                    return new MyScale(String.valueOf(sharpEntries[position]), null);
                }
            });

            mSbSharpness.setOnScaleChangedListener(new ArcSeekBar.OnScaleChangedListener() {
                @Override
                public void onScaleChanged(int preScaleIndex, int scaleIndex) {
                    scaleIndex = scaleIndex * delta;
                    if (scaleIndex > sharpEntries.length) {
                        scaleIndex = sharpEntries.length;
                    }
                    updateParameters(scaleIndex, sharpPref);
                    resetTipLayoutPosition();
                }
            });

            mSbSharpness.setCurrentScaleItem(sharpPref.getCurrValueIndex());
            mSbSharpness.setOnScrollingListener(this);
            mSbSharpness.setTag(mSupportedCount - 1);
            mSbSharpness.setTag(R.id.camera_adv_tips_2, sharpPref);
            visiableView = mSbSharpness;
        } else {
            mSbSharpness.setVisibility(View.GONE);
        }

//        if (mSupportedCount != 0) {
//            sector.setArc(visiableView);
//            sector.setArcCount(mSupportedCount);
//            sector.requestLayout();
//        }

         ****/


        /*add by me*/
        mSbExposure.setTag(0);
        mSbFocusMode.setTag(1);
        mSbISO.setTag(2);
        mSbSharpness.setTag(3);
        mSbWhiteBalance.setTag(4);
        mSupportedCount = 5;
        //test


//        DisplayMetrics dm = new DisplayMetrics();
//        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;    //得到宽度
//        int height = dm.heightPixels;  //得到高度
//        mCenterX = width/2;
//        mCenterY = height;

        mCenterX = mWidth/2;
        mCenterY = mHeight;
        mMinR = mWidth/2;
        mD = (mHeight-mMinR)/mSupportedCount;
        mMinR = mMinR - mD;
        mD = (mHeight-mMinR)/mSupportedCount;
        int d = mD/2;

        Log.d("ADV", "mCenterX:" + mCenterX + ", mCenterY:" + mCenterY + ", mMinR:" + mMinR + ", mD:" + mD + ", mWidth:" + mWidth + ", mHeight:" + mHeight);
        int i = (int) mSbExposure.getTag();
        int r = mMinR + i*mD + d;
        mSbExposure.setInitSize(mCenterX,mCenterY,r,mD,mWidth);
        i = (int) mSbISO.getTag();
        r = mMinR + i*mD + d;
        mSbISO.setInitSize(mCenterX,mCenterY,r,mD,mWidth);
        i = (int) mSbSharpness.getTag();
        r = mMinR + i*mD + d;
        mSbSharpness.setInitSize(mCenterX,mCenterY,r,mD,mWidth);
        i = (int) mSbWhiteBalance.getTag();
        r = mMinR + i*mD + d;
        mSbWhiteBalance.setInitSize(mCenterX,mCenterY,r,mD,mWidth);
        i = (int) mSbFocusMode.getTag();
        r = mMinR + i*mD + d;
        mSbFocusMode.setInitSize(mCenterX,mCenterY,r,mD,mWidth);

        /*end*/
    }


//    private void updateParameters(int scaleIndex, ListPreference pref) {
//        AdvanceParameterClickEvent event = new AdvanceParameterClickEvent();
//        event.pref = pref;
//        event.pref.setValueByIndex(scaleIndex);
//        mTipValue.setText(String.valueOf(pref.getEntry()));
//
//        if (!mIsScrolling) {
//            mHandler.removeMessages(MSG_FADE_OUT_TIPS);
//            setInfoFadeInTips(pref);
//            mHandler.sendEmptyMessageDelayed(MSG_FADE_OUT_TIPS, 500);
//        }
//
//        PGEventBus.getInstance().post(event);
//    }
    private void resetTipLayoutPosition() {
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTipLayout.getLayoutParams();
//
//        View first = null;
//        for (int i = 0; i < mArcSeekbarContainer.getChildCount(); i++) {
//            if (mArcSeekbarContainer.getChildAt(i).getVisibility() == View.VISIBLE) {
//                first = mArcSeekbarContainer.getChildAt(i);
//                break;
//            }
//
//        }
//        if (first != null) {
//            int[] location = {0, 0};
//            first.getLocationOnScreen(location);
//            lp.setMargins(0, location[1] - mTipLayout.getHeight() - mTlpLayoutMargin, 0, 0);
//            mTipLayout.setLayoutParams(lp);
//        }

    }
    private static class MyScale implements ScaleAdapter.Scale {
        String mText = "def text";
        private Drawable mDrawable;

        public MyScale(String text, Drawable drawable) {
            mText = text;
            mDrawable = drawable;
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }
    }

    @Override
    public void onStartScrolling(ArcSeekBar bar) {

    }

    @Override
    public void onStopScrolling(ArcSeekBar bar) {

    }
}
