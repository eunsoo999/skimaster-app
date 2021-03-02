package lecture.mobile.final_project.ma01_20160940;
//고쳐야함

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//렌탈샵을 출력하는 화면액티비티
public class ListInfoMain extends AppCompatActivity {
    ListView rentalListView;
    String address;
    String skiAddress;
    ArrayList<RentalItem> resultList;
    ArrayList<SkiItem> skiResultList;
    MyRentalCustomAdapter MyAdapter;
    String mapx; //메인에서 받아온 스키장의 경도
    String mapy; //메인에서 받아온 스키장의 위도
    String skiContentId;
    SkiItem select;
    TextView skiName;
    TextView op_top;
    TextView op_add_tv;
    TextView op_tel_tv;
    TextView op_info_tv;
    TextView op_open_tv;
    AlarmManager am;
    PendingIntent sender;
    String alarmMonth;
    String alarmDay;
    TextView isRtTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ski_info);

        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        skiName = (TextView)findViewById(R.id.skiName);
        op_top = (TextView)findViewById(R.id.op_top);
        op_add_tv = (TextView)findViewById(R.id.op_add_tv);
        op_tel_tv = (TextView)findViewById(R.id.op_tel_tv);
        op_info_tv = (TextView)findViewById(R.id.op_info_tv);
        op_open_tv = (TextView)findViewById(R.id.op_open_tv);

        isRtTv = (TextView)findViewById(R.id.isRtTv);

        Intent intent = getIntent();
        skiContentId = intent.getStringExtra("skiId");
        select = (SkiItem) intent.getSerializableExtra("selectList"); //용평이면 용평의 title,
        skiName.setText(select.getTitle());

        mapx = select.getMapX();
        mapy = select.getMapY();
        skiResultList = new ArrayList();

        skiAddress = getResources().getString(R.string.common_info);
        skiAddress += skiContentId;
        new skiNetworkAsyncTask().execute(skiAddress);

        resultList = new ArrayList();
        address = getResources().getString(R.string.server_rental_url);
        address += mapx + "&mapY=" + mapy;
        rentalListView = (ListView) findViewById(R.id.rentalListView);
        MyAdapter = new MyRentalCustomAdapter(ListInfoMain.this, R.layout.skicustom_view, resultList);
        rentalListView.setAdapter(MyAdapter);

        new NetworkAsyncTask().execute(address);

        rentalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(ListInfoMain.this, RantalInfoMain.class);
                intent.putExtra("selectItem", resultList.get(pos));
                intent.putExtra("selectContentid", resultList.get(pos).getContentid());
                startActivity(intent);
            }
        });
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        public final static String TAG = "NetworkAsyncTask";
        public final static int TIME_OUT = 10000;
        ProgressDialog progressDlg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(ListInfoMain.this, "스키장 찾는 중...", "Downloading...");     // 진행상황 다이얼로그 출력
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

                if(resultList.size() == 0)
                    isRtTv.setText("근처 렌탈샵을 찾을 수 없습니다.");
                else {
                    MyAdapter.setList(resultList);
                    MyAdapter.notifyDataSetChanged();
                }
                progressDlg.dismiss();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(ListInfoMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }

    class skiNetworkAsyncTask extends AsyncTask<String, Integer, String> {
        public final static String TAG = "NetworkAsyncTask";
        public final static int TIME_OUT = 10000;
        ProgressDialog progressDlg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(ListInfoMain.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
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
            MyXmlParser parser = new MyXmlParser();
            skiResultList = parser.parse(result);
            op_top.setText(skiResultList.get(0).getTitle() + "에 놀러오세요~!\n");
            op_add_tv.setText(skiResultList.get(0).getAddress());
            op_tel_tv.setText(select.getTel());
            op_info_tv.setText(skiResultList.get(0).getOverview());
            alarmMonth = skiResultList.get(0).getOverview().substring(0, 2);
            alarmDay = skiResultList.get(0).getOverview().substring(3,5);
            if(alarmDay.contains("일")) {
                alarmDay = skiResultList.get(0).getOverview().substring(3,4);
            }


            if (skiResultList.get(0).getOverview().substring(0, 6).contains("월"))
                op_open_tv.setText(skiResultList.get(0).getOverview().substring(0, 6));
            else
                op_open_tv.setText("홈페이지 참고");
            progressDlg.dismiss();
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(ListInfoMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_call :
                AlertDialog.Builder alert = new AlertDialog.Builder(ListInfoMain.this);
                alert.setTitle(skiResultList.get(0).getTitle())
                        .setMessage("전화를 거시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/" + select.getTel()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("아니요", null)
                        .show();
                break;
            case R.id.btnLiked : //즐겨찾기 누르면
                Toast.makeText(ListInfoMain.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                LikeDBHelper helper = new LikeDBHelper(ListInfoMain.this);
                SQLiteDatabase db =  helper.getWritableDatabase();

                ContentValues rows = new ContentValues();
                rows.put(LikeDBHelper.TITLE, skiResultList.get(0).getTitle());
                rows.put(LikeDBHelper.NUMBER, select.getTel());
                rows.put(LikeDBHelper.ADDRESS, skiResultList.get(0).getAddress());
                db.insert(LikeDBHelper.TABLE_NAME, null, rows);
                break;
            case R.id.btnAlarm :
                if(op_open_tv.getText().equals("홈페이지 참고")){ //알람일을 알 수 없으면
                    AlertDialog.Builder alert2 = new AlertDialog.Builder(ListInfoMain.this);
                    alert2.setTitle("알람 불가")
                            .setMessage("알람이 불가능합니다. (개장일 홈페이지 참고)")
                            .setPositiveButton("네", null)
                            .show();
                }
                else {
                    Toast.makeText(ListInfoMain.this, alarmMonth+" 월" + alarmDay+"일" +"알람이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(ListInfoMain.this, AlarmReceiver.class);
                    intent.putExtra("skiName", skiResultList.get(0).getTitle());
                    sender = PendingIntent.getBroadcast(ListInfoMain.this, 0, intent, 0);

                    Calendar calendar = Calendar.getInstance();
                    //calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 3);
                    //calendar.set(2018, 11, 30, 10, 59);
                    //calendar.set(2018, Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDay));

                    // 알람 등록
                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
                }
                break;
            case R.id.btnReview :
                intent = new Intent(ListInfoMain.this, ReviewAddActivity.class);
                if (intent != null) startActivity(intent);
                break;
        }
    }
}
