package kr.ac.myungji.quickunderroute.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import kr.ac.myungji.quickunderroute.R;


public class NoticeActivity extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notice);

        final ArrayList<String> items = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NoticeActivity.this,
                android.R.layout.simple_list_item_1,items);

        listView = (ListView) findViewById(R.id.listview2);
        listView.setAdapter(adapter);

        items.add("공지사항1");
        items.add("공지사항2");
        items.add("공지사항3");

        adapter.notifyDataSetChanged();
    }
}
