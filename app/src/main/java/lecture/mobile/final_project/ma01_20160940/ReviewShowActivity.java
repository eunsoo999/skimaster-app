package lecture.mobile.final_project.ma01_20160940;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ReviewShowActivity extends AppCompatActivity {

    final static String TAG = "ReviewShowActivity";

    ReviewDBHelper helper;
    ImageView ivPhoto;
    TextView tvMemo;
    TextView tvDate;
    TextView tvTitle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_show);

        helper = new ReviewDBHelper(this);
        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvMemo = (TextView)findViewById(R.id.tvMemo);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTitle = (TextView)findViewById(R.id.tvTitle);

        intent = getIntent();
        Long id = intent.getExtras().getLong("key");

//        MainActivity 에서 전달 받은 _id 값을 사용하여 DB 레코드를 가져온 후 ImageView 와 TextView 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        Cursor cursor = db.query (ReviewDBHelper.TABLE_NAME, null, whereClause, whereArgs,
                null, null, null, null);

        cursor.moveToNext();
        tvMemo.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.MEMO)));
        ivPhoto.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.PATH))));
        tvDate.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.DATE)));
        tvTitle.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.TITLE)));

        cursor.close();
        helper.close();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSns :
                shareTwitter();
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }

    public void shareTwitter() {
        String strLink = null;
        try {
            strLink = String.format("http://twitter.com/intent/tweet?text=%s",
                    URLEncoder.encode(tvMemo.getText().toString(), "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLink));
        startActivity(intent);
    }
}
