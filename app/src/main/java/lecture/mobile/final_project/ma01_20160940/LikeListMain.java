package lecture.mobile.final_project.ma01_20160940;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import java.util.ArrayList;

public class LikeListMain extends AppCompatActivity {
    LikeListAdapter adapter;
    Cursor cursor;
    LikeDBHelper helper;
    ListView lvlist;
    ArrayList<LikeListDto> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_main);

        resultList = new ArrayList<LikeListDto>();
        lvlist = (ListView) findViewById(R.id.liked_list);
        helper = new LikeDBHelper(this);

        adapter = new LikeListAdapter(this, R.layout.skicustom_view, resultList);
        lvlist.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        DB 에서 모든 레코드를 가져와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + LikeDBHelper.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(LikeDBHelper.TITLE));
            String address = cursor.getString(cursor.getColumnIndex(LikeDBHelper.ADDRESS));
            String tel = cursor.getString(cursor.getColumnIndex(LikeDBHelper.NUMBER));

            resultList.add(new LikeListDto(title, address, tel));
        }
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
