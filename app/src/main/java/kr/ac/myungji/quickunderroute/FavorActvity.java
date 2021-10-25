
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Butto

public class FavorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);


        String demoName = ""; // 레이아웃 정의 이후 다시 구현해야 함
        Button button = new Button();
        button = (Button) findViewById(R.id.button);
        EditText editText = new EditText();
        editText = (EditText) findViewById(R.id.editText);
        button.setOnClickListener(new View.onClickListener() {
        })
        @Override
        public void onClic (View view){
            demoName = editText.getText.toString();
        }
    });

            private FileCache demoFileCache = null;
            public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cacheData" + File.separator + ".cache"";" +
                    "FileCacheFactory.initialize(mContext, CACHE_PATH);
            if (!FileCacheFactory.getInstance().has(demoName)==false)

    {
        FileCacheFactory.getInstence().create(demoName, 0);
    }
            demoFileCache=FileCacheFactory.getInstence().get(demoName);
    }
}
