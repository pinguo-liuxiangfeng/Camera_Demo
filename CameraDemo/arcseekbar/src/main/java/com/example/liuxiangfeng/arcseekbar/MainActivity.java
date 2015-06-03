package com.example.liuxiangfeng.arcseekbar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private ParameterAdvanceSettingView mParameterAdvanceSettingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_for_test);
        FrameLayout container = (FrameLayout)findViewById(R.id.test_content);
        LayoutInflater inflater=getLayoutInflater();
        mParameterAdvanceSettingView = (ParameterAdvanceSettingView) inflater.inflate(R.layout.camera_advance_params, null);
        container.addView(mParameterAdvanceSettingView);
//        final TextView tv1 = (TextView)findViewById(R.id.first);
//        tv1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("main", "touch first textview");
//                tv1.setText("first is touched!");
//                return false;
//            }
//        });
//        final TextView tv2 = (TextView)findViewById(R.id.second);
//        tv2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("main", "touch second textview");
//                tv2.setText("second is touched!");
//                return false;
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
