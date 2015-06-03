package com.example.liuxiangfeng.myapplication3;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends FragmentActivity {

    private TabBar mTabBar;
    private TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAB", "onCreate......");
        setContentView(R.layout.activity_main);
        mTabBar = (TabBar)findViewById(android.R.id.tabs);
        mTabManager = new TabManager(this, mTabBar, R.id.realtabcontent);
        mTabBar.setTabSelectionChangedListener(mTabManager);

        Resources res = getResources();
        TabBar.TabSpec spec;  // Resusable TabSpec for each tab
        spec = mTabBar.newTabSpec("favorite").setIndicator("favorite",
                res.getDrawable(R.drawable.ic_tab));
        mTabManager.addTab(spec,
                frag_tab1.class, null);
        mTabManager.addTab(mTabBar.newTabSpec("tab2").setIndicator("tab2"),
                frag_tab2.class, null);
        mTabBar.setCurrentTab(0);
        if (savedInstanceState != null) {
            mTabBar.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAB","activity, onTouchEvent, event="+event.getAction());
        return super.onTouchEvent(event);
    }
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("TAB","activity, dispatchTouchEvent, ev="+ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

}
