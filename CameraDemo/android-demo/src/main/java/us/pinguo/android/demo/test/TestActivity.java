package us.pinguo.android.demo.test;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Toast;

import us.pinguo.android.demo.R;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_demo_test);
        Toast.makeText(this, R.string.hello_world, Toast.LENGTH_SHORT).show();
    }
}
