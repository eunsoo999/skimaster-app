package lecture.mobile.final_project.ma01_20160940;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class ReviewDBHelper extends SQLiteOpenHelper {

    private final static String TAG = "ReviewDBHelper";

    private final static String DB_NAME ="review_db";
    public final static String TABLE_NAME = "review_table";
    public final static String ID = "_id";
    public final static String PATH = "path"; //사진
    public final static String MEMO = "memo"; //메모
    public final static String DATE = "date";
    public final static String TITLE = "title";

    public ReviewDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + PATH + " text, " + MEMO + " text, " + DATE + " text, " + TITLE + " text);";

        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

