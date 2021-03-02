package lecture.mobile.final_project.ma01_20160940;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RantalInfoMain extends AppCompatActivity {
    String selectContentid;
    RentalItem selectItem;
    String address;
    ArrayList<RentalItem> resultList;
    TextView rtInfo;
    TextView op_add_tv;
    TextView op_tel_tv;
    List<Address> addList;
    Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rt_info);

        op_add_tv = (TextView)findViewById(R.id.op_add_tv);
        op_tel_tv = (TextView)findViewById(R.id.op_tel_tv);
        geocoder = new Geocoder(this);

        Intent intent = getIntent();
        selectContentid = intent.getStringExtra("selectContentid");
        selectItem = (RentalItem) intent.getSerializableExtra("selectItem");

        //렌탈샵 정보 액티비티 상위 TextView
        TextView rtName = (TextView)findViewById(R.id.rtName);
        rtName.setText(selectItem.getTitle());

        TextView rtInfo = (TextView)findViewById(R.id.rtInfo);

        //파서
        resultList = new ArrayList();
        address = getResources().getString(R.string.common_info);
        address += selectContentid;

        new RantalInfoMain.NetworkAsyncTask().execute(address);

    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rtReview :
                intent = new Intent(RantalInfoMain.this, ReviewAddActivity.class);
                if (intent != null) startActivity(intent);
                break;
            case R.id.btn_like : //즐겨찾기 누르면
                Toast.makeText(RantalInfoMain.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                LikeDBHelper helper = new LikeDBHelper(RantalInfoMain.this);
                SQLiteDatabase db =  helper.getWritableDatabase();

                ContentValues rows = new ContentValues();
                rows.put(LikeDBHelper.TITLE, resultList.get(0).getTitle());
                rows.put(LikeDBHelper.NUMBER, selectItem.getTel());
                rows.put(LikeDBHelper.ADDRESS, resultList.get(0).getAddress());
                db.insert(LikeDBHelper.TABLE_NAME, null, rows);
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        public final static String TAG = "NetworkAsyncTask";
        public final static int TIME_OUT = 10000;
        ProgressDialog progressDlg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(RantalInfoMain.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }
        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            StringBuilder result = new StringBuilder();
            BufferedReader br = null;
            HttpURLConnection conn = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection) url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        for (String line = br.readLine(); line != null; line = br.readLine()) {
                            result.append(line + '\n');
                        }
                    }
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                cancel(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                cancel(false);
            } finally {
                try {
                    if (br != null) br.close();
                    if (conn != null) conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
                MyRentalParser parser = new MyRentalParser();
                resultList = parser.parse(result);
                rtInfo = (TextView)findViewById(R.id.rtInfo);

                rtInfo.setText(resultList.get(0).getOverview());
                op_add_tv.setText(resultList.get(0).getAddress());
                op_tel_tv.setText( selectItem.getTel());
                rtInfo.setText(resultList.get(0).getOverview());
                progressDlg.dismiss();

        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(RantalInfoMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }

}
