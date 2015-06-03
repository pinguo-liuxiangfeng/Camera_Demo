package us.pinguo.android.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DemoMainActivity extends ListActivity {
    private static final String CATEGORY = "us.pinguo.android.demo.DEMO_TEST";
    private static final String KEY_PATH = "com.example.android.apis.Path";
    private static final String KEY_INTENT = "intent";
    private static final String KEY_TITLE = "title";
    private static final int MSG_QUIT = 1;

    private boolean mIsQuiting = false;
    private boolean mIsRoot = false;
    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_QUIT) {
                mIsQuiting = false;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String path = intent.getStringExtra(KEY_PATH);
        if (path == null) {
            path = "";
            mIsRoot = true;
        } else {
            setTitle(path);
        }

        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[]{KEY_TITLE},
                new int[]{android.R.id.text1}));
        getListView().setTextFilterEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(MSG_QUIT);
    }

    protected List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list) {
            return myData;
        }

        String[] prefixPath;

        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }

        int len = list.size();

        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;
            if (prefix.length() == 0 || label.startsWith(prefix)) {
                String[] labelPath = label.split("/");
                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];
                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData, nextLabel, activityIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);
        return myData;
    }

    private final static Comparator<Map> sDisplayNameComparator = new Comparator<Map>() {
        private final Collator collator = Collator.getInstance();

        public int compare(Map map1, Map map2) {
            return collator.compare(map1.get(KEY_TITLE), map2.get(KEY_TITLE));
        }
    };

    /**
     * @param pkg
     * @param componentName
     * @return
     */
    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    /**
     * @param path
     * @return
     */
    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, DemoMainActivity.class);
        result.putExtra(KEY_PATH, path);
        return result;
    }

    protected void addItem(List<Map> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put(KEY_TITLE, name);
        temp.put(KEY_INTENT, intent);
        data.add(temp);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map map = (Map) l.getItemAtPosition(position);
        Intent intent = (Intent) map.get(KEY_INTENT);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (mIsRoot) {
            boolean consumed = false;
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    consumed = true;
                    if (mIsQuiting) {
                        finish();
                    } else {
                        Toast.makeText(this, R.string.press_again_to_quit, Toast.LENGTH_SHORT).show();
                        mIsQuiting = true;
                        mHandler.sendEmptyMessageDelayed(MSG_QUIT, 3000);
                    }
                    break;
            }
            return consumed;
        }
        return super.onKeyDown(keyCode, event);
    }
}
