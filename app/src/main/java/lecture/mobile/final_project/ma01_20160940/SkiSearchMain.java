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
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SkiSearchMain extends AppCompatActivity  {
    Intent intent;
    ListView lvList;
    String address; //api url
    ArrayList<SkiItem> resultList; //스키장정보arraylist
    MyCustomAdapter MyAdapter;
    Geocoder geocoder;
    List<Address> addList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ski_main);
        lvList = (ListView)findViewById(R.id.list_ski);
        resultList = new ArrayList();
        MyAdapter = new MyCustomAdapter(this, R.layout.skicustom_view, resultList);
        lvList.setAdapter(MyAdapter);
        geocoder = new Geocoder(this);
        addList = null;

        address = getResources().getString(R.string.all_serch_ski_url);
        new NetworkAsyncTask().execute(address);

        //리스트를 클릭하면 클릭한 스키장 근처에 있는 렌탈샵 출력하기위해서 생성.
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(SkiSearchMain.this, ListInfoMain.class);
                String skiId = resultList.get(pos).getContentid();

                intent.putExtra("skiId", skiId);
                intent.putExtra("selectList", resultList.get(pos));

                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_region :
                intent = new Intent(SkiSearchMain.this, RegionSearchMain.class);
                startActivity(intent);
                break;
            case R.id.btn_map_sc :
                intent = new Intent(SkiSearchMain.this, SkiMapSearchMain.class);
                intent.putExtra("resultList", resultList);
                startActivity(intent);
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
            progressDlg = ProgressDialog.show(SkiSearchMain.this, "스키장 검색 중...", "잠시만 기다려주세요.");     // 진행상황 다이얼로그 출력
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
            Toast.makeText(SkiSearchMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }
}