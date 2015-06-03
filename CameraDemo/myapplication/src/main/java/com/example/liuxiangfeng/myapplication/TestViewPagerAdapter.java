package com.example.liuxiangfeng.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * Created by liuxiangfeng on 15-5-4.
 */
public class TestViewPagerAdapter extends FragmentPagerAdapter {
    public TestViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int arg0) {
// TODO Auto-generated method stub
        switch (arg0) {
            case MainActivity.TAB_INDEX_TAB_1:
                return new applications_frag();

            case MainActivity.TAB_INDEX_TAB_2:
                return new favorite_frag();

            case MainActivity.TAB_INDEX_TAB_3:
                return new applications_frag();

            case MainActivity.TAB_INDEX_TAB_4:
                return new favorite_frag();
        }

        throw new IllegalStateException("No fragment at position " + arg0);      }

    @Override
    public int getCount() {
        return MainActivity.TAB_COUNT;
    }
}
