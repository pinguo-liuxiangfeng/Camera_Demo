package com.example.liuxiangfeng.myapplication3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import java.util.HashMap;

/**
 * Created by liuxiangfeng on 15-5-5.
 */
public class TabManager implements TabBar.OnTabSelectionChanged{
    private final FragmentActivity mActivity;
    private final TabBar mTabBar;
    private final int mContainerId;
    private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
    TabInfo mLastTab;

    @Override
    public void onTabSelectionChanged(int tabIndex, boolean clicked) {
        Log.d("TAB", "onTabSelectionChanged, tabIndex=" + tabIndex+", mLastTab="+mLastTab);
        String tag = mTabBar.getTabTagByIndex(tabIndex);
        TabInfo newTab = mTabs.get(tag);
        Log.d("TAB", "onTabSelectionChanged, newTab=" + newTab);
        if (mLastTab != newTab) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                Log.d("TAB", "onTabSelectionChanged, newTab.fragment=" + newTab.fragment);
                Log.d("TAB", "onTabSelectionChanged, newTab.clss.getName()=" + newTab.clss.getName());
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(mActivity,
                            newTab.clss.getName(), newTab.args);
                    Log.d("TAB", "onTabSelectionChanged, newTab.fragment=" + newTab.fragment+", mContainerId="+mContainerId);
                    ft.add(mContainerId, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            mActivity.getSupportFragmentManager().executePendingTransactions();
        }
    }

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            tag = _tag;
            clss = _class;
            args = _args;
        }
    }
    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabManager(FragmentActivity activity, TabBar tabHost, int containerId) {
        mActivity = activity;
        mTabBar = tabHost;
        mContainerId = containerId;
        mTabBar.setTabSelectionChangedListener(this);
    }

    public void addTab(TabBar.TabSpec tabSpec, Class<?> clss, Bundle args) {
//        tabSpec.setContent(new DummyTabFactory(mActivity));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        if (info.fragment != null && !info.fragment.isDetached()) {
            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
            ft.detach(info.fragment);
            ft.commit();
        }

        mTabs.put(tag, info);
        mTabBar.addTab(tabSpec);
        Log.d("TAB", "addTab, tag="+tag+", info="+info);
    }

}

