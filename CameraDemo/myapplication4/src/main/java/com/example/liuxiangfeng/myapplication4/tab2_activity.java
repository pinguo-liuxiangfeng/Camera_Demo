package com.example.liuxiangfeng.myapplication4;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class tab2_activity extends Activity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        TextView textview = new TextView(this);

        textview.setText("This is the tab2 tab");

        setContentView(textview);

    }

}