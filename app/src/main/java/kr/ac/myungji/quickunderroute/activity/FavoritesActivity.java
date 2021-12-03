package kr.ac.myungji.quickunderroute.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import kr.ac.myungji.quickunderroute.R;

public class FavoritesActivity extends Activity {
    private ListView listView;

    Button btnInit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        final ArrayList<String> items = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(FavoritesActivity.this,
                android.R.layout.simple_list_item_1,items);

        btnInit = (Button)findViewById(R.id.btnInit);
        listView = (ListView) findViewById(R.id.listview1);
        listView.setAdapter(adapter);

        String[] numSTname = new String[100];

        //즐겨찾기 삭제가 있을 경우
        if(pref.getBoolean("CheckDelete",false) == true){
            int num2 = pref.getInt("num",0);
            int time = pref.getInt("timeDelete",0);//몇번 삭제 되었는지
            //삭제된 만큼 재정렬
            for(int i =0 ; i< time; i++){
                int numDelete = pref.getInt("numDelete"+i,0);
                int k =0;
                for(int j = numDelete+1 ; j <num2;j++){
                    numSTname[k] = pref.getString("numST"+j,"");
                    k++;
                }
                num2--;
                k=0;
                for(int j = numDelete ; j <num2; j++){
                    editor.putString("numST"+j,numSTname[k]);
                    editor.commit();
                    k++;
                }
                editor.putInt("num",num2);
                editor.commit();
            }
            editor.putBoolean("CheckDelete",false);
            editor.putInt("timeDelete",0);
            editor.commit();
        }

        //즐겨찾기 역 정렬
        int num = pref.getInt("num",0);
        for (int i = 0; i < num; i++){
            items.add(pref.getString("numST"+i, ""));//추가
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();

        //초기화 버튼//개발자를 위한 버튼//곧 삭제 예정
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("num", 0);
                editor.commit();
                Toast.makeText(FavoritesActivity.this, "초기화 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //즐겨찾기 항목을 누르면 해당 역 정보로 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),StationActivity.class);
                //intent.putExtra("no",pref.getString("numST"+i, ""));
                intent.putExtra("no",adapterView.getAdapter().getItem(i).toString());
                startActivity(intent);
            }
        });

        //역을 길게 누르면 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putBoolean("CheckDelete",true);//삭제가 있는지
                int time = pref.getInt("timeDelete",0);//삭제 몇번 했는지
                editor.putInt("numDelete"+time,i);//삭제한 번호
                time++;
                editor.putInt("timeDelete",time);
                editor.commit();
                items.remove(i);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

    }
}
