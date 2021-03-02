package lecture.mobile.final_project.ma01_20160940;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class LikeDBHelper extends SQLiteOpenHelper {

    private final static String TAG = "LikeListAdapter";

    private final static String DB_NAME ="like_db";
    public final static String TABLE_NAME = "like_table";
    public final static String ID = "_id";
    public final static String TITLE = "title";
    public final static String NUMBER = "number";
    public final static String ADDRESS = "address";


    public LikeDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + TITLE + " text, " + NUMBER + " text, " + ADDRESS + " text);";

        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}