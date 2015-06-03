package com.example.liuxiangfeng.myapplication2;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends ActionBarActivity {
    TabHost mTabHost;

    TabManager mTabManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();


        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        mTabHost.setCurrentTab(0);//设定一开始就跳到第一个分页

        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        spec = mTabHost.newTabSpec("favorite").setIndicator("",
                res.getDrawable(R.drawable.ic_tab));

        mTabManager.addTab(spec,
                frag_tab1.class, null);

        mTabManager.addTab(mTabHost.newTabSpec("tab2").setIndicator("tab2"),
                frag_tab2.class, null);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        System.out.print("test for system.out");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

}
