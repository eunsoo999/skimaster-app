package lecture.mobile.final_project.ma01_20160940;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewAddActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 200;
    private String mCurrentPhotoPath;

    ImageView ivPhoto;
    EditText etMemo;
    EditText etDate;
    EditText etTitle;
    ReviewDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_write);

        helper = new ReviewDBHelper(this);

        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        etMemo = (EditText)findViewById(R.id.etMemo);
        etDate = (EditText)findViewById(R.id.etDate);
        etTitle = (EditText)findViewById(R.id.etTitle);

        ivPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
                    dispatchTakePictureIntent();
                    return true;
                }
                return false;
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "lecture.mobile.final_project.ma01_20160940.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //취소할시 갤러리에서 사진 삭제
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSave:
//                DB에 촬영한 사진의 파일 경로 및 메모 저장
                SQLiteDatabase db = helper.getWritableDatabase();

                String memo = etMemo.getText().toString();
                String photo = mCurrentPhotoPath;
                String date = etDate.getText().toString();
                String title = etTitle.getText().toString();

                ContentValues row = new ContentValues();
                row.put(ReviewDBHelper.PATH, photo);
                row.put(ReviewDBHelper.MEMO, memo);
                row.put(ReviewDBHelper.DATE, date);
                row.put(ReviewDBHelper.TITLE, title);

                db.insert(ReviewDBHelper.TABLE_NAME, null, row);
                helper.close();
                Toast.makeText(this, "리뷰를 등록하였어요!", Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.btnCancel:
                File myf = new File(mCurrentPhotoPath);
                myf.delete();
                finish();
                break;
        }
    }

    private void setPic() {
        int targetW = ivPhoto.getWidth();
        int targetH = ivPhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }

}
