package lecture.mobile.final_project.ma01_20160940;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RtSearchMain extends AppCompatActivity {
    private EditText etSear;
    String targetTitle;
    ListView lvList;
    String address; //api url
    ArrayList<RentalItem> resultList;
    MyRentalCustomAdapter MyAdapter;
    List<Address> addList;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rt_main);

        geocoder = new Geocoder(this);
        etSear = (EditText)findViewById(R.id.et_search);
        lvList = (ListView)findViewById(R.id.rtList);
        resultList = new ArrayList();
        MyAdapter = new MyRentalCustomAdapter(this, R.layout.skicustom_view, resultList);
        lvList.setAdapter(MyAdapter);

        //첫화면 : 모든 렌탈샵 목록 출력
        address = getResources().getString(R.string.all_serch_rt_url);
        new RtSearchMain.NetworkAsyncTask().execute(address);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(RtSearchMain.this, RantalInfoMain.class);
                intent.putExtra("selectItem", resultList.get(pos));
                intent.putExtra("selectContentid", resultList.get(pos).getContentid());
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_rt :
                //버튼 클릭시 검색 결과 추력
                targetTitle = etSear.getText().toString();
                address = getResources().getString(R.string.search_keyword_url) + "A03022600&keyword=";
                new RtSearchMain.NetworkAsyncTask().execute(address);
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
            progressDlg = ProgressDialog.show(RtSearchMain.this, "렌탈샵 검색중..", "잠시만 기다려주세요.");     // 진행상황 다이얼로그 출력
        }
        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            StringBuilder result = new StringBuilder();
            BufferedReader br = null;
            HttpURLConnection conn = null;

            try {
                if(address.equals(getResources().getString(R.string.all_serch_rt_url))) //전체출력일 시
                {
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                } else {
                    String keyword = URLEncoder.encode(targetTitle, "UTF-8"); //국문은 인코딩이 필요하여 추가.
                    URL url = new URL(address + keyword);
                    conn = (HttpURLConnection) url.openConnection();
                }

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

            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getTel() == null) { //폰번호가 없는 곳이면
                    try {
                        addList = geocoder.getFromLocationName(resultList.get(i).getAddress(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultList.get(i).setTel("02-" + addList.get(0).getPostalCode());
                }
            }

            MyAdapter.setList(resultList);
            MyAdapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(RtSearchMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }
}
