package com.example.liuxiangfeng.myapplication;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity {
    public final static int TAB_INDEX_TAB_1 = 0;
    public final static int TAB_INDEX_TAB_2 = 1;
    public final static int TAB_INDEX_TAB_3 = 2;
    public final static int TAB_INDEX_TAB_4 = 3;
    public final static int TAB_COUNT = 4;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mainActivity","onCreate.......");
        setContentView(R.layout.activity_main);

        // 创建Tab
        Log.d("mainActivity", "getSupportActionBar()="+getSupportActionBar()+",getSupportActionBar()="+getSupportActionBar());
        setupTest1();
        setupTest2();
        setupTest3();
        setupTest4();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // 创建 view pager
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(new TestViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new TestPagerListener());
        mViewPager.setCurrentItem(TAB_INDEX_TAB_1);
        Log.d("mainActivity","onCreate.......end.");

//        Resources res = getResources(); // Resource object to get Drawables
//
//        TabHost tabHost = getTabHost();  // The activity TabHost
//
//        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
//
//        Intent intent;  // Reusable Intent for each tab
//
//
//
//        // Create an Intent to launch an Activity for the tab (to be reused)
//
//        intent = new Intent().setClass(this, favorite.class);
//
//
//
//        // Initialize a TabSpec for each tab and add it to the TabHost
//
//        spec = tabHost.newTabSpec("favorite").setIndicator("favorites",
//
//                res.getDrawable(R.drawable.ic_tab))
//
//                .setContent(intent);
//
//        tabHost.addTab(spec);
//
//
//
//        // Do the same for the other tabs
//
//        intent = new Intent().setClass(this, applications.class);
//
//        spec = tabHost.newTabSpec("application").setIndicator("applications",
//
//                res.getDrawable(R.drawable.ic_tab))
//
//                .setContent(intent);
//
//        tabHost.addTab(spec);
//
//
//
//        intent = new Intent().setClass(this, games.class);
//
//        spec = tabHost.newTabSpec("game").setIndicator("games",
//
//                res.getDrawable(R.drawable.ic_tab))
//
//                .setContent(intent);
//
//        tabHost.addTab(spec);
//
//
//
//        tabHost.setCurrentTab(2);
    }


    private void setupTest1(){
        ActionBar.Tab tab = this.getSupportActionBar().newTab();
        tab.setContentDescription("Tab 1");
        tab.setText("Tab 1");
        tab.setTabListener(mTabListener);
        getSupportActionBar().addTab(tab);
    }

    private void setupTest2(){
        ActionBar.Tab tab = this.getSupportActionBar().newTab();
        tab.setContentDescription("Tab 22");
        tab.setText("Tab 22");
        tab.setTabListener(mTabListener);
        tab.setIcon(R.drawable.ic_tab);
        getSupportActionBar().addTab(tab);
    }

    private void setupTest3(){
        ActionBar.Tab tab = this.getSupportActionBar().newTab();
        tab.setContentDescription("Tab 3");
        tab.setText("Tab 3");
        tab.setTabListener(mTabListener);
        getSupportActionBar().addTab(tab);
    }

    private void setupTest4(){
        ActionBar.Tab tab = this.getSupportActionBar().newTab();
        tab.setContentDescription("Tab 4");
        tab.setText("Tab 4");
        tab.setTabListener(mTabListener);
        getSupportActionBar().addTab(tab);
    }
    private final ActionBar.TabListener mTabListener = new ActionBar.TabListener() {
        private final static String TAG = "TabListener";



        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onTabSelected()");
            if (mViewPager != null)
                mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            Log.d(TAG, "onTabUnselected()");
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onTabReselected");
        }
    };

    class TestPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            getSupportActionBar().selectTab(getSupportActionBar().getTabAt(arg0));
        }
    }
}
