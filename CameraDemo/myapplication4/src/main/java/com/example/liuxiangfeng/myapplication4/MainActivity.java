package com.example.liuxiangfeng.myapplication4;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources(); // Resource object to get Drawables

        TabHost tabHost = getTabHost();  // The activity TabHost

        TabHost.TabSpec spec;  // Resusable TabSpec for each tab

        Intent intent;  // Reusable Intent for each tab



        // Create an Intent to launch an Activity for the tab (to be reused)

        intent = new Intent().setClass(this, tab1_activity.class);



        // Initialize a TabSpec for each tab and add it to the TabHost

        spec = tabHost.newTabSpec("artists").setIndicator("Artists",

                res.getDrawable(R.drawable.ic_tab))

                .setContent(intent);

        tabHost.addTab(spec);



        // Do the same for the other tabs

        intent = new Intent().setClass(this, tab2_activity.class);

        spec = tabHost.newTabSpec("albums").setIndicator("",

                res.getDrawable(R.drawable.ic_tab))

                .setContent(intent);

        tabHost.addTab(spec);



        tabHost.setCurrentTab(1);
    }



}
