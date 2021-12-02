package kr.ac.myungji.quickunderroute.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import kr.ac.myungji.quickunderroute.List.ListViewAdapter;
import kr.ac.myungji.quickunderroute.R;

public class FavoritesActivity extends Activity {
    private ListView listView;
    private ListViewAdapter adapter;
    //즐겨찾기 초기화
    Button btnInit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int num = pref.getInt("num",0);
        //String text1 = pref.getString("numST"+num,"");

        btnInit = (Button)findViewById(R.id.btnInit);

        adapter = new ListViewAdapter();

        listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("num",0);
                editor.commit();
            }
        });

        //adapter.addItem(text1);
        for (int i = 0; i < num; i++){
            adapter.addItem(pref.getString("numST"+i, ""));
        }

        adapter.notifyDataSetChanged();


    }
}
