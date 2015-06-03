package com.example.liuxiangfeng.myapplication3;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiangfeng on 15-5-5.
 */
public class TabBar extends LinearLayout{
    // This value will be set to 0 as soon as the first tab is added to TabHost.
    private int mCurrentTab = -1;
    private int mTabCount = 0;
    private List<TabSpec> mTabSpecs = new ArrayList<TabSpec>(2);
    private OnTabSelectionChanged mSelectionChangedListener;

    public TabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTabCount = 0;
    }

    public TabBar(Context context) {
        super(context);
        mTabCount = 0;
    }

    public TabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTabCount = 0;
    }


    public void setTabSelectionChangedListener(OnTabSelectionChanged listener){
        mSelectionChangedListener = listener;
    }

    public void addTab(TabSpec tabSpec){
        tabSpec.setIndicator(mTabCount);
        mTabSpecs.add(tabSpec);
        mTabCount++;
        View v = tabSpec.createIndicatorView();
        addView(v);
    }
    public int getTabCount(){
        return mTabCount;
    }
    public void setCurrentTab(int index){
        Log.d("TAB", "setCurrentTab, index="+index);
        if (index < 0 || index >= mTabSpecs.size()) {
            return;
        }
        if (index == mCurrentTab) {
            return;
        }

        mCurrentTab = index;
        mSelectionChangedListener.onTabSelectionChanged(mCurrentTab, false);
    }
    public void setCurrentTabByTag(String tag){
        int i;
        for (i = 0; i < mTabSpecs.size(); i++) {
            if (mTabSpecs.get(i).getTag().equals(tag)) {
                setCurrentTab(i);
                break;
            }
        }
    }
    public String getTabTagByIndex(int index){
        if (index < 0 || index >= mTabSpecs.size()) {
            return null;
        }
        return mTabSpecs.get(index).getTag();
    }
    @Override
    public void addView(View child) {
        if (child.getLayoutParams() == null) {
            final LinearLayout.LayoutParams lp = new LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            lp.setMargins(0, 0, 0, 0);
            child.setLayoutParams(lp);
        }

        // Ensure you can navigate to the tab with the keyboard, and you can touch it
        child.setFocusable(true);
        child.setClickable(true);

        super.addView(child);

        // TODO: detect this via geometry with a tabwidget listener rather
        // than potentially interfere with the view's listener
        TabClickListener listener = new TabClickListener(getTabCount() - 1);
        child.setOnClickListener(listener);
        child.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAB", "child, onTouch, event="+event.getAction());
                return false;
            }
        });
        Log.d("TAB", "addView, listener="+listener);
    }
    // registered with each tab indicator so we can notify tab host
    private class TabClickListener implements OnClickListener {

        private final int mTabIndex;

        private TabClickListener(int tabIndex) {
            mTabIndex = tabIndex;
        }

        public void onClick(View v) {
            mCurrentTab = mTabIndex;
            mSelectionChangedListener.onTabSelectionChanged(mTabIndex, true);
            Log.d("TAB", "onClick, mCurrentTab=" + mCurrentTab);
        }
    }
    static interface OnTabSelectionChanged {
        /**
         * Informs the TabHost which tab was selected. It also indicates
         * if the tab was clicked/pressed or just focused into.
         *
         * @param tabIndex index of the tab that was selected
         * @param clicked whether the selection changed due to a touch/click
         * or due to focus entering the tab through navigation. Pass true
         * if it was due to a press/click and false otherwise.
         */
        void onTabSelectionChanged(int tabIndex, boolean clicked);
    }
    public TabSpec newTabSpec(String tag) {
        return new TabSpec(tag);
    }
    public class TabSpec {

        private String mTag;
        private CharSequence mLabel;
        private Drawable mIcon;
        private View mView;
        private int mTabIndex;

        private TabSpec(String tag) {
            mTag = tag;
        }

        /**
         * Specify a label as the tab indicator.
         */
        public TabSpec setIndicator(int index) {
            mTabIndex = index;
            return this;
        }

        /**
         * Specify a label as the tab indicator.
         */
        public TabSpec setIndicator(CharSequence label) {
            mLabel = label;
            return this;
        }

        /**
         * Specify a label and icon as the tab indicator.
         */
        public TabSpec setIndicator(CharSequence label, Drawable icon) {
            mLabel = label;
            mIcon = icon;
            return this;
        }

        /**
         * Specify a view as the tab indicator.
         */
        public TabSpec setIndicator(View view) {
            mView = view;
            return this;
        }
        public String getTag() {
            return mTag;
        }
        public int getTabIndex() {
            return mTabIndex;
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public View createIndicatorView() {
            final Context context = getContext();
//            LayoutInflater inflater =
//                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View tabIndicator = inflater.inflate(R.layout.tab_indicator,
//                    null, // tab widget is the parent
//                    false); // no inflate params
//
//            final Button tv = (Button) tabIndicator.findViewById(R.id.title);
            Button btn = new Button(context);
            btn.setText(mLabel);

            btn.setBackground(mIcon);

            return btn;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAB","bar, onTouchEvent, event="+event.getAction());
        return super.onTouchEvent(event);
    }
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("TAB","bar, dispatchTouchEvent, ev="+ev.getAction());
        return super.dispatchTouchEvent(ev);
    }
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("TAB","bar, onInterceptTouchEvent, ev="+ev.getAction());
        boolean flag = super.onInterceptTouchEvent(ev);
        Log.d("TAB","flag="+flag);
        return flag;
    }
}
