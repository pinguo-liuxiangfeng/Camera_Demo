package com.example.liuxiangfeng.arcseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Scroller;

/**
 * Created by liuxiangfeng on 15-5-11.
 */

/**
 * Created by marui on 14-03-03.
 * 弧形SeekBar,支持透明的thumb,
 * 可以通过setScaleAdapter设置刻度;
 */
public class ArcSeekBar extends View {


    private static final int SCLAE_ACCESSORY_PADDING_TO_THUMB = 5;
    private DrawFilter mDrawFilter;
    //seek parameters
    private float mSeekRate;
    private OnSeekChangedListener mListener;

    // arc parameters
    private int mArcWidth;
    private int mArcLeft;
    private int mArcRight;
    private int mArcTop;
    private int mArcHeight;
    private float mArcSweepAngleRadian;
    private float mArcStartAngleRadian;
    private float mArcCenterX;
    private float mArcCenterY;
    private float mArcCircleRadius;
    private RectF mArcCircleRect;

    private float mD;//distance from two level.


    //thumb parameters
    private Paint mThumbPaint;
    private int mThumbOffset; //todo 这个数字不稳定，导致绘制出问题，正在检查
    private int mThumbRadius;
    private int mThumbStorkeWidth;
    private Drawable mThumbDrawable;

    //line parameters
    private Paint mLinePaint1;
    private Paint mLinePaint2;
    private int mLineWidth;

    //text parameters
    private Paint mTextPaint;
    private Paint mAlphaTextPaint;
    private int mTextOffsetToLine;

    //touch parameters
    private GestureDetector mGestureDetector;
    private boolean mIsScrolling = false;
    private OnScrollingListener mScrollingListener;

    //Scale parameters
    private int mCurrentScaleItem;
    private int mOldScaleItem;
    private ScaleAdapter mScaleAdapter;
    private OnScaleChangedListener mScaleListener;

    //animation parameters
    private Scroller mScroller;
    private Scroller mThumbScaleScroller;
    private float mThumbScaleRate;//拖动时thumb放大率
    private Paint mSectorPaint;

    private boolean mIsTouching = false;

//    //是否只显示两端的刻度值
//    private boolean mOnlyShowBothEndValue = false;

    public ArcSeekBar(Context context) {
        this(context, null);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcSeekBar, 0, 0);
//        mOnlyShowBothEndValue = a.getBoolean(R.styleable.ArcSeekBar_showBothEnd, false);
//        a.recycle();
        initSize();
        init();
    }

    /*add by me*/
    public void setInitSize(float arcCenterX, float arcCenterY, float arcCircleR , float d, int arcWidth){
        mArcCenterX = arcCenterX;
        mArcCenterY = arcCenterY;
        mArcCircleRadius = arcCircleR;
        mD = d;
        mArcWidth = arcWidth;
    }
    /*end*/
    private void initSize() {
        mThumbRadius = dpToPixel(6);
        mThumbStorkeWidth = dpToPixel(1f);
        mLineWidth = dpToPixel(1f);
        mTextOffsetToLine = dpToPixel(2.5f);
        mThumbScaleRate = 1.4f;
    }

    private void init() {
//        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mScroller = new Scroller(getContext());
        mThumbScaleScroller = new Scroller(getContext());

        mGestureDetector = new GestureDetector(getContext(), new SeekBarGestureListener());

        mThumbPaint = new Paint();
        mThumbPaint.setAntiAlias(true);
        mThumbPaint.setColor(Color.WHITE);
        mThumbPaint.setStrokeWidth(mThumbStorkeWidth);
        mThumbPaint.setStyle(Paint.Style.STROKE);

        mLinePaint1 = new Paint();
        mLinePaint1.setAntiAlias(true);
        mLinePaint1.setColor(Color.WHITE);
        mLinePaint1.setAlpha(200);
        mLinePaint1.setStyle(Paint.Style.STROKE);
        mLinePaint1.setStrokeWidth(mLineWidth);

        mLinePaint2 = new Paint();
        mLinePaint2.setAntiAlias(true);
        mLinePaint2.setColor(Color.WHITE);
        mLinePaint2.setAlpha(200);
        mLinePaint2.setStyle(Paint.Style.STROKE);
        mLinePaint2.setStrokeWidth(mLineWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(dpToPixel(10));

        mAlphaTextPaint = new Paint();
        mAlphaTextPaint.setAntiAlias(true);
        mAlphaTextPaint.setColor(Color.WHITE);
        mAlphaTextPaint.setTextSize(dpToPixel(10));

        mSectorPaint = new Paint();
        mSectorPaint.setColor(Color.RED);
        mSectorPaint.setAlpha(100);
        mSectorPaint.setAntiAlias(true);
        mSectorPaint.setStyle(Paint.Style.STROKE);
        mSectorPaint.setStrokeWidth(150);

    }

    private static final int NOT_INIT = -12398;
    private int mW = NOT_INIT;
    private int mH = NOT_INIT;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int hsize;
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        //解决拖动时，会闪屏的问题,其他UI发生变化时，会触发request导致问题出现
        if (mW == NOT_INIT && mH == NOT_INIT) {
            initArcParameter();
        } else {
            if (mW != getWidth() || mH != getHeight()) {
                initArcParameter();
            }
        }

        if (hmode == MeasureSpec.AT_MOST) {
            hsize = (mThumbRadius + mThumbStorkeWidth) * 2;//thumb高度
            if (mThumbDrawable != null) {
                hsize = hsize > mThumbDrawable.getIntrinsicHeight() ? hsize : mThumbDrawable.getIntrinsicHeight();
            }
            hsize += mArcHeight;//弯曲高度
            hsize += mTextPaint.getTextSize() + mTextOffsetToLine;//字体高度,字体间距
            hsize += getPaddingTop() + getPaddingBottom();//padding高度
            setMeasuredDimension(wsize, hsize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        mW = getWidth();
        mH = getHeight();

        Log.d("ADV", "mW:" + mW + ", mH:" + mH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.setDrawFilter(mDrawFilter);
        float drawThumbRadius = mThumbRadius;
        boolean drawCircle = false;
        if (mThumbScaleScroller.computeScrollOffset()) {
            drawThumbRadius = mThumbScaleScroller.getCurrY();
            drawCircle = true;
            invalidate();
        } else if (mIsScrolling) {
            drawThumbRadius = mThumbRadius * mThumbScaleRate;
            drawCircle = true;
        }

        float sweepAngleDegree = radianToDegree(mArcSweepAngleRadian);
        float startAngleDegree = radianToDegree(mArcStartAngleRadian);
        float endAngleDegree = startAngleDegree + sweepAngleDegree;

        //draw thumb
        float thumbSweepAngleRadian = computeAngleA(drawThumbRadius * 2, mArcCircleRadius, mArcCircleRadius);
        float thumbSweepAngleDegree = radianToDegree(thumbSweepAngleRadian);
        final int thumbX;
        final int thumbY;
        thumbX = mArcLeft + mThumbOffset;
        thumbY = Math.round(computeOffsetYOnCircle(thumbX, mArcCenterX, mArcCenterY, mArcCircleRadius));

        if (mThumbDrawable != null && !drawCircle) {
            int tdWidth = mThumbDrawable.getIntrinsicWidth();
            int tdHeight = mThumbDrawable.getIntrinsicHeight();
            final int thumbDrawableLeft = thumbX - tdWidth / 2;
            final int thumbDrawableTop = thumbY - tdHeight / 2;
            final int thumbDrawableRight = thumbX + tdWidth / 2;
            final int thumbDrawableBottom = thumbY + tdHeight / 2;
            mThumbDrawable.setBounds(thumbDrawableLeft, thumbDrawableTop, thumbDrawableRight, thumbDrawableBottom);
            mThumbDrawable.draw(canvas);
        } else {
            canvas.drawCircle(thumbX, thumbY, drawThumbRadius, mThumbPaint);
        }

        // 计算thumb当前偏移量
        float thumbCurrentRadian = computeCurrentAngleRadian(thumbX, thumbY);
        float thumbCurrentDegree = radianToDegree(thumbCurrentRadian);
        float thumbLeftCurrentDegree = thumbCurrentDegree - thumbSweepAngleDegree / 2;

        // draw arc
        if (thumbLeftCurrentDegree > 0) {
            canvas.drawArc(mArcCircleRect, startAngleDegree, thumbLeftCurrentDegree, false, mLinePaint1);
        }
        if (endAngleDegree - startAngleDegree
                - thumbLeftCurrentDegree - thumbSweepAngleDegree > 0) {
            canvas.drawArc(mArcCircleRect, startAngleDegree + thumbLeftCurrentDegree + thumbSweepAngleDegree, endAngleDegree - startAngleDegree
                    - thumbLeftCurrentDegree - thumbSweepAngleDegree, false, mLinePaint1);
        }

        if (mScaleAdapter != null) {
            //draw scale
            FadeScaleInfo fadeScaleInfo = findCurrentThumbOnWhichScaleIndex(thumbCurrentRadian, mArcSweepAngleRadian,
                    thumbSweepAngleRadian);
            final int textStartX = mArcLeft + mArcWidth / 2;
            float rotateDegree = (sweepAngleDegree / (mScaleAdapter.getScaleViewCount() - 1));
            float originalDegree = sweepAngleDegree / 2;
            for (int i = 0; i < mScaleAdapter.getScaleViewCount(); i++) {
                int realIndexOnScale;
                if (mScaleAdapter.getScaleViewCount() != 1) {
                    realIndexOnScale = (mScaleAdapter.getScaleCount() - 1) / (mScaleAdapter.getScaleViewCount() - 1) * i;
                } else {
                    realIndexOnScale = i;
                }
                ScaleAdapter.Scale scale = mScaleAdapter.getScale(realIndexOnScale);
                if (scale != null) {
                    canvas.save();
                    final int textStartY;
                    float drawWidthOffsetRadian;//刻度标识的偏移量
                    if (scale.getDrawable() != null) {
                        textStartY = getPaddingTop() + mTextOffsetToLine + scale.getDrawable().getIntrinsicHeight() / 2;
                        drawWidthOffsetRadian = computeAngleA(scale.getDrawable().getIntrinsicWidth(),
                                mArcCircleRadius + mTextOffsetToLine, mArcCircleRadius + mTextOffsetToLine);
                    } else {
                        textStartY = getPaddingTop() + mTextOffsetToLine + (int) Math.ceil(mTextPaint.getTextSize());
                        drawWidthOffsetRadian = computeAngleA(computeTextDrawWidth(scale.getText()),
                                mArcCircleRadius + mTextOffsetToLine, mArcCircleRadius + mTextOffsetToLine);
                    }
                    canvas.translate(textStartX, textStartY);
                    canvas.save();
                    canvas.rotate(rotateDegree * i - originalDegree - radianToDegree(drawWidthOffsetRadian / 2),
                            mArcCenterX - textStartX, mArcCenterY - textStartY);
//                    if (mOnlyShowBothEndValue) {
//                        if (i == 0 || i == mScaleAdapter.getScaleViewCount() - 1) {
//                            if (fadeScaleInfo != null && fadeScaleInfo.index == i) {
//                                int fullAlpha = 120;//当滑动离开刻度后，刻度显示的透明度，[0..255]
//                                mAlphaTextPaint.setAlpha((int) (fullAlpha * fadeScaleInfo.alpha));
//                                drawScale(scale, canvas, mAlphaTextPaint);
//                            } else {
//                                drawScale(scale, canvas, mTextPaint);
//                            }
//                        }
//                    } else
                    {
                        if (fadeScaleInfo != null && fadeScaleInfo.index == i) {
                            int fullAlpha = 120;//当滑动离开刻度后，刻度显示的透明度，[0..255]
                            mAlphaTextPaint.setAlpha((int) (fullAlpha * fadeScaleInfo.alpha));
                            drawScale(scale, canvas, mAlphaTextPaint);
                        } else {
                            drawScale(scale, canvas, mTextPaint);
                        }
                    }
                    canvas.restore();
                    canvas.restore();
                }
            }
            // draw scale text or pic on above thumb
            ScaleAdapter.Scale scale = mScaleAdapter.getScale(mCurrentScaleItem);
            if (scale != null) {
                canvas.save();
                float scaleOffsetOnThumbX;
                float scaleOffsetOnThumbY;
                if (scale.getDrawable() != null) {
                    scaleOffsetOnThumbX = thumbX - scale.getDrawable().getIntrinsicWidth() / 2;
                    scaleOffsetOnThumbY = thumbY - drawThumbRadius - mThumbStorkeWidth -
                            SCLAE_ACCESSORY_PADDING_TO_THUMB - scale.getDrawable().getIntrinsicHeight();
                } else {
                    scaleOffsetOnThumbX = thumbX - computeTextDrawWidth(scale.getText()) / 2;
                    scaleOffsetOnThumbY = thumbY - drawThumbRadius - mThumbStorkeWidth - SCLAE_ACCESSORY_PADDING_TO_THUMB;
                }
                canvas.translate(scaleOffsetOnThumbX, scaleOffsetOnThumbY);
//                drawScale(scale, canvas, mTextPaint);
                canvas.restore();
            }
        }
        if (mScroller.computeScrollOffset()) {
            mThumbOffset = mScroller.getCurrY();
            invalidate();
        }
        super.onDraw(canvas);
    }

    private void initArcParameter() {
        //这里进行只需要一次的计算
        //计算圆弧的形状
        final int width = getWidth();
        mArcWidth = width - getPaddingLeft() - getPaddingRight() - mThumbRadius * 2 - mThumbStorkeWidth * 2;
        mArcLeft = getPaddingLeft() + mThumbRadius + mThumbStorkeWidth;
        mArcRight = width - getPaddingRight() - mThumbRadius - mThumbStorkeWidth;
//        mArcHeight = (int) (mArcWidth * mArcBending);

        float angleA = (float) Math.asin(mArcWidth / 2f / mArcCircleRadius);
        mArcSweepAngleRadian = (float) (angleA * 2);
        mArcStartAngleRadian = (float) ((Math.PI + Math.PI / 2f) - angleA);

        mArcHeight = (int) (mArcCircleRadius - mArcCircleRadius*Math.sin(angleA));
        mArcTop = getPaddingTop() + mThumbRadius + mThumbStorkeWidth / 2 - mLineWidth +
                (int) Math.ceil(mTextPaint.getTextSize()) + mTextOffsetToLine;

//        mArcCircleRadius = (float) ((mArcWidth / 2f) / Math.sin(mArcSweepAngleRadian / 2f));//计算绘制圆的半径
//        mArcCenterX = mArcLeft + mArcWidth / 2f;
//        mArcCenterY = mArcTop + mArcCircleRadius;
        mArcCircleRect = new RectF(mArcCenterX - mArcCircleRadius, mArcCenterY - mArcCircleRadius,
                mArcCenterX + mArcCircleRadius, mArcCenterY + mArcCircleRadius);
//        if (mCurrentScaleItem != 0) {
//            setCurrentScaleItem(mCurrentScaleItem);
//        }

    }

    private void drawScale(ScaleAdapter.Scale scale, Canvas canvas, Paint mTextPaint) {
        if (scale != null) {
            if (scale.getDrawable() != null) {
                scale.getDrawable().setAlpha(mTextPaint.getAlpha());
                scale.getDrawable().draw(canvas);
            } else if (!TextUtils.isEmpty(scale.getText())) {
                canvas.drawText(scale.getText(), 0, 0, mTextPaint);
            }
        }
    }

    /**
     * 设置滑动监听
     *
     * @param listener
     */
    public void setOnSeekChangedListener(OnSeekChangedListener listener) {
        mListener = listener;
    }

    /**
     * 设置刻度监听
     *
     * @param listener
     */
    public void setOnScaleChangedListener(OnScaleChangedListener listener) {
        mScaleListener = listener;
    }

    public void setOnScrollingListener(OnScrollingListener listener) {
        mScrollingListener = listener;
    }

    public float getCurrentSeekValue() {
        return mSeekRate;
    }

    /**
     * 设置当前Seek数值
     *
     * @param currentValue from[0..1]
     */
    public void setCurrentSeekValue(float currentValue) {
        if (mSeekRate > 1) {
            mSeekRate = 1;
        } else if (mSeekRate < 0) {
            mSeekRate = 0;
        }
        mSeekRate = currentValue;
        mThumbOffset = (int) (mArcWidth * mSeekRate);
        invalidate();
    }

    public int getCurrentScaleItem() {
        return mCurrentScaleItem;
    }

    /**
     * 设置额外的thumbDrawable
     *
     * @param resId
     */
    public void setThumbDrawable(int resId) {
        mThumbDrawable = getResources().getDrawable(resId);
        mThumbRadius = mThumbDrawable.getIntrinsicWidth() / 2;
        mThumbStorkeWidth = 0;
    }

    /**
     * 设置thumb半径
     *
     * @param radiusPx
     */
    public void setThumbRadius(int radiusPx) {
        mThumbRadius = radiusPx;
    }

    /**
     * 设置线宽
     *
     * @param mLineWidth
     */
    public void setLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public Paint getLinePaint1() {
        return mLinePaint1;
    }

    public Paint getLinePaint2() {
        return mLinePaint2;
    }

    public Paint getThumbPaint() {
        return mThumbPaint;
    }

    public void setScaleAdapter(ScaleAdapter adapter) {
        mScaleAdapter = adapter;
    }

    /**
     * 设置当前刻度的位置
     *
     * @param index
     */
    public void setCurrentScaleItem(int index) {
        if (index >= 0 && index < mScaleAdapter.getScaleCount()) {
            mCurrentScaleItem = index;
            int offset = getStandardOffset(index);
            mThumbOffset = offset;
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsTouching = true;
                mOldScaleItem = mCurrentScaleItem;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                mIsTouching = false;
                break;
        }

        boolean consume = mGestureDetector.onTouchEvent(event);
        if (!consume && (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL)) {
            if (mIsScrolling) {
                mIsScrolling = false;
                animateThumbScale((int) (mThumbRadius * mThumbScaleRate), mThumbRadius, 100);
                if (mScrollingListener != null) {
                    mScrollingListener.onStopScrolling(this);
                }
            }
            if (mScaleAdapter != null) {
                // 粘合至刻度位置
//            int offsetLength = mThumbOffset;
                StandardScaleInfo info = getStandardScale(mThumbOffset);
                animateToOffset(info.currentX);
                // 计算并更新刻度值
                int scaleItem = info.currentItem;
                if (mCurrentScaleItem != scaleItem) {
                    mCurrentScaleItem = scaleItem;
                    if (mScaleListener != null) {
                        mScaleListener.onScaleChanged(mOldScaleItem, mCurrentScaleItem);
                    }
                }
                // 计算并更新rate值
                if (mListener != null && mArcWidth != 0) {
                    mSeekRate = (float) info.currentX / mArcWidth;
                    mListener.onSeekValueChanged(mSeekRate);
                }
            }
            invalidate();
            consume = true;
        } else if (!consume && event.getAction() == MotionEvent.ACTION_MOVE) {
            float ex = event.getX();
            int currentThumbOffset = getThumbOffset(ex - mArcLeft);
            if (!mIsScrolling) {
                if (Math.abs(currentThumbOffset - mThumbOffset) > mThumbRadius * 2) {
                    //禁止从thumb 远处开始滑动
                    return false;
                }
                animateThumbScale(mThumbRadius, (int) (mThumbRadius * mThumbScaleRate), 400);
                mIsScrolling = true;
                if (mScrollingListener != null) {
                    mScrollingListener.onStartScrolling(this);
                }
            }
            mThumbOffset = currentThumbOffset;
//            Log.i("AAAA", "drawCircle, mThumbOffset : " + mThumbOffset);
            if (mScaleAdapter != null) {
                StandardScaleInfo info = getStandardScale(mThumbOffset);
                int scaleItem = info.currentItem;
                if (mCurrentScaleItem != scaleItem) {
                    mCurrentScaleItem = scaleItem;
                    if (mScaleListener != null) {
                        mScaleListener.onScaleChanged(mOldScaleItem, mCurrentScaleItem);
                    }
                }
            }
            if (mListener != null && mArcWidth != 0) {
                mSeekRate = (float) mThumbOffset / mArcWidth;
                mListener.onSeekValueChanged(mSeekRate);
            }
            invalidate();
            consume = true;
        }
        return consume;
    }

    /**
     * 根据坐标获得当前thumb的标准值
     *
     * @param pos
     * @return
     */
    private int getThumbOffset(float pos) {
        int thumb = Math.round(pos);
        if (thumb < 0) {
            thumb = 0;
        } else if (thumb > mArcWidth) {
            thumb = mArcWidth;
        }
        return thumb;
    }

    /**
     * 将弧度转换为角度
     *
     * @param radian
     * @return
     */
    private float radianToDegree(float radian) {
        return (float) (radian * 180 / Math.PI);
    }

    /**
     * 动画移动至指定位置
     *
     * @param offsetLength
     */
    private void animateToOffset(int offsetLength) {
        mScroller.startScroll(0, mThumbOffset, 0, offsetLength - mThumbOffset, 400);
    }

    private void animateThumbScale(int oriRadius, int scaleRadius, int duration) {
        mThumbScaleScroller.startScroll(0, oriRadius, 0, scaleRadius - oriRadius, duration);
    }

    private void stopAnimateThumb() {
        mThumbScaleScroller.abortAnimation();
    }

    /**
     * 将当前的thumb偏移量与刻度粘合,
     * 得到最靠近offsetLength的标准刻度，
     *
     * @param offsetX
     * @return
     */
    private StandardScaleInfo getStandardScale(int offsetX) {
        offsetX += mArcLeft;
        int offsetY = (int) computeOffsetYOnCircle(offsetX, mArcCenterX, mArcCenterY, mArcCircleRadius);
        float currentRadian = computeCurrentAngleRadian(offsetX, offsetY);
        float currentAngleRate = currentRadian / mArcSweepAngleRadian;
        float ratePerScale = 1f / (mScaleAdapter.getScaleCount() - 1);
        int currentIndex = Math.round(currentAngleRate / ratePerScale);
        float standardRadian = currentIndex * (mArcSweepAngleRadian / (mScaleAdapter.getScaleCount() - 1));
        int fittedOffsetX = Math.round(computeXOnCircle(mArcCenterX, mArcCircleRadius, standardRadian + mArcStartAngleRadian)) - mArcLeft;
        fittedOffsetX = fittedOffsetX < 0 ? 0 : fittedOffsetX > mArcWidth ? mArcWidth : fittedOffsetX;
        StandardScaleInfo info = new StandardScaleInfo(currentIndex, fittedOffsetX);
        return info;
    }

    private int getStandardOffset(int scaleIndex) {
        float currentAngleRadian = mArcStartAngleRadian + ((float) scaleIndex / (mScaleAdapter.getScaleCount() - 1)) * mArcSweepAngleRadian;
        int offset = (int) (mArcCenterX + mArcCircleRadius * Math.cos(currentAngleRadian));
        return offset - mArcLeft;
    }

    /**
     * 计算当前坐标相对于arc起始点滑过的角度
     *
     * @param offsetX
     * @param offsetY
     * @return
     */
    private float computeCurrentAngleRadian(int offsetX, int offsetY) {
        float distance = computeDistance(mArcLeft, mArcTop + mArcHeight, offsetX, offsetY);
        return computeAngleA(distance, mArcCircleRadius, mArcCircleRadius);
    }

    /**
     * 余弦定理,根据三角形三边计算角度,
     * 计算由bc边构成的角A的度数
     *
     * @return Angle A in radian
     */
    private float computeAngleA(float a, float b, float c) {
        return (float) Math.acos((b * b + c * c - a * a) / (2 * b * c));
    }

    /**
     * 计算两点间的距离
     *
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return
     */
    private float computeDistance(float ax, float ay, float bx, float by) {
        return (float) Math.sqrt((by - ay) * (by - ay) +
                (bx - ax) * (bx - ax));
    }

    /**
     * 根据x坐标计算偏移的y坐标，圆的极坐标方程,
     *
     * @param offsetX
     * @return offsetY, 正方向y坐标
     */
    private float computeOffsetYOnCircle(float offsetX, float centerX, float centerY, float radius) {
        float t = (float) Math.acos((offsetX - mArcCenterX) / mArcCircleRadius);
        if (t > 0) {
            t = -t;
        }
        return (float) (mArcCenterY + mArcCircleRadius * Math.sin(t));
    }

    private float computeXOnCircle(float centerX, float radius, float angleT) {
        return (float) (centerX + radius * Math.cos(angleT));
    }

    private float computeTextDrawWidth(String text) {
        return text.getBytes().length * mTextPaint.getTextSize() / 2;
    }

    /**
     * 查找当前thumb位于哪个scale上，
     *
     * @param currentThumbRadian
     * @param arcSweepRadian
     * @param thumbSweepRadian
     * @return scale的index, 未找到返回-1
     */
    private FadeScaleInfo findCurrentThumbOnWhichScaleIndex(float currentThumbRadian, float arcSweepRadian, float thumbSweepRadian) {
        float radianPerScale = arcSweepRadian / (mScaleAdapter.getScaleViewCount() - 1);
        float currentIndexFloat = currentThumbRadian / radianPerScale;
        int currentIndex = Math.round(currentIndexFloat);
        float deviateRadian = Math.abs(currentThumbRadian - radianPerScale * currentIndex);
//        Log.e("test", "currentThumb:" + currentThumbRadian + ", radianPerScale:" + radianPerScale + ", arcSweep:"
//                + arcSweepRadian + ", thumbSweepRadian:" + thumbSweepRadian);
        if (deviateRadian < radianPerScale) {
            FadeScaleInfo info = new FadeScaleInfo(currentIndex, deviateRadian / radianPerScale);
            return info;
        } else {
            return null;
        }
    }

    private int dpToPixel(float dp) {
        return Math.round(getResources().getDisplayMetrics().density * dp);
    }

    public float getArcCenterX() {
        return mArcCenterX;
    }

    public float getArcCenterY() {
        return mArcCenterY;
    }

    public float getArcRaidus() {
        return mArcCircleRadius;
    }

    public float getArcStartAngleRadian() {
        return mArcStartAngleRadian;
    }

    public float getArcSweepAngleRadian() {
        return mArcSweepAngleRadian;
    }

    public void fadeOut() {
        fadeOut(null);
    }

    public void fadeIn() {
        fadeIn(null);
    }

    public void fadeOut(Animation.AnimationListener listener) {
        AlphaAnimation anim = new AlphaAnimation(1, 0);
        anim.setDuration(ParameterAdvanceSettingView.FAD_OUT_DURATION);
        anim.setAnimationListener(listener);
        startAnimation(anim);
        setVisibility(View.INVISIBLE);
    }

    public void fadeIn(Animation.AnimationListener listener) {
//        startAnimation(ParameterAdvanceSettingView.obtainFadeInAnimation(listener));
//        setVisibility(View.VISIBLE);
    }

    public void hide() {
        hide(300);
    }

    public void hide(long time) {
        RotateAnimation anim = new RotateAnimation(0, radianToDegree(mArcSweepAngleRadian) + 5, mArcCenterX, mArcCenterY);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(time);
        startAnimation(anim);
        setVisibility(View.INVISIBLE);
    }

    public void show() {
        RotateAnimation anim = new RotateAnimation(radianToDegree(mArcSweepAngleRadian) + 5, 0, mArcCenterX, mArcCenterY);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(300);
        startAnimation(anim);
        setVisibility(View.VISIBLE);
    }

    public interface OnSeekChangedListener {
        /**
         * @param rate from[0..1]
         */
        void onSeekValueChanged(float rate);
    }

    public interface OnScaleChangedListener {
        /**
         * @param scaleIndex from 0 to (mScaleAdapter.getScaleCount() - 1)
         */
        void onScaleChanged(int prescaleIndex, int scaleIndex);
    }

    public interface OnScrollingListener {
        void onStartScrolling(ArcSeekBar bar);

        void onStopScrolling(ArcSeekBar bar);
    }

    @SuppressWarnings("checkstyle:membername")
    private static class FadeScaleInfo {
        int index;
        float alpha;

        public FadeScaleInfo(int index, float alpha) {
            this.index = index;
            this.alpha = alpha;
        }
    }

    @SuppressWarnings("checkstyle:membername")
    private static class StandardScaleInfo {
        int currentItem;
        int currentX;

        public StandardScaleInfo(int index, int currentX) {
            this.currentItem = index;
            this.currentX = currentX;
        }
    }

    private class SeekBarGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (!mIsScrolling) {
                animateThumbScale(mThumbRadius, (int) (mThumbRadius * mThumbScaleRate), 400);
                mIsScrolling = true;
                invalidate();
                if (mScrollingListener != null) {
                    mScrollingListener.onStartScrolling(ArcSeekBar.this);
                }
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            if (!mOnlyShowBothEndValue)
            {
                int offsetLength = getThumbOffset(e.getX() - mArcLeft);
                if (mScaleAdapter != null) {
                    StandardScaleInfo info = getStandardScale(offsetLength);
                    offsetLength = info.currentX;
                    animateToOffset(offsetLength);
                    mCurrentScaleItem = info.currentItem;
                    if (mScaleListener != null) {
                        mScaleListener.onScaleChanged(mOldScaleItem, mCurrentScaleItem);
                    }
                }
                if (mListener != null && mArcWidth != 0) {
                    animateToOffset(offsetLength);
                    mSeekRate = (float) offsetLength / mArcWidth;
                    mListener.onSeekValueChanged(mSeekRate);
                }
                if (mIsScrolling) {
                    mIsScrolling = false;
                    if (mScrollingListener != null) {
                        mScrollingListener.onStopScrolling(ArcSeekBar.this);
                    }
                }
                invalidate();
            }
            return true;
        }
    }

}

