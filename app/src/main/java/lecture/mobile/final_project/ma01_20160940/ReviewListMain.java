package lecture.mobile.final_project.ma01_20160940;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ReviewListMain extends AppCompatActivity {
    SimpleCursorAdapter reviewAdapter;
    Cursor cursor;
    ReviewDBHelper helper;
    ListView lvMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_list_main);

        lvMemo = (ListView)findViewById(R.id.reviewList);
        helper = new ReviewDBHelper(this);
//        어댑터에 SimpleCursorAdapter 연결
        reviewAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, new String[] {"title", "date"}, new int[] {android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//title
        lvMemo.setAdapter(reviewAdapter);

        lvMemo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
                intent = new Intent(ReviewListMain.this, ReviewShowActivity.class);
                intent.putExtra("key", _id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DB 에서 모든 레코드를 가져와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + ReviewDBHelper.TABLE_NAME, null);

        reviewAdapter.changeCursor(cursor);
        helper.close();
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_write:
//                AddMemoActivity 호출
                intent = new Intent(this, ReviewAddActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
