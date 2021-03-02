package lecture.mobile.final_project.ma01_20160940;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mylike :
                intent = new Intent(MainActivity.this, LikeListMain.class);
                startActivity(intent);
                break;
            case R.id.btn_search_ski :
                intent = new Intent(MainActivity.this, SkiSearchMain.class);
                startActivity(intent);
                break;
            case R.id.btn_search_rt :
                intent = new Intent(MainActivity.this, RtSearchMain.class);
                startActivity(intent);
                break;
            case R.id.btn_review :
                intent = new Intent(MainActivity.this, ReviewListMain.class);
                startActivity(intent);
                break;
        }
    }
}
