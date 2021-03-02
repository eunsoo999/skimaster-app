package lecture.mobile.final_project.ma01_20160940;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class RegionSearchMain extends AppCompatActivity {
    EditText ettargetTitle;
    String targetTitle;
    ListView lvList; //메인 액티비티 리스트뷰
    String address; //api url
    ArrayList<SkiItem> resultList; //스키장정보arraylist
    MyCustomAdapter MyAdapter;
    Button btn_sc; //버튼 누르면 스키장상세정보페이지로
    List<Address> addList;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_region_main);

        ettargetTitle = (EditText)findViewById(R.id.et_sear);
        btn_sc = (Button)findViewById(R.id.btn_search);
        lvList = (ListView) findViewById(R.id.lvList);

        resultList = new ArrayList();
        MyAdapter = new MyCustomAdapter(this, R.layout.skicustom_view, resultList);
        lvList.setAdapter(MyAdapter);
        geocoder = new Geocoder(this);

        //리스트를 클릭하면 클릭한 스키장 근처에 있는 렌탈샵 출력하기위해서 생성.
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(RegionSearchMain.this, ListInfoMain.class);
                SkiItem selectList = resultList.get(pos);
                intent.putExtra("skiId", resultList.get(pos).getContentid());
                intent.putExtra("selectList", selectList);

                startActivity(intent);
            }
        });
        address = getResources().getString(R.string.search_keyword_url) + "A03021200&keyword=";
    }

    public void onClick(View v) { //btn_search누를시 스키장검색 ex)용평리조트,알펜시아 등등 검색
        switch (v.getId()) {
            case R.id.btn_search:
                targetTitle = ettargetTitle.getText().toString();
                if (targetTitle.equals(""))
                    targetTitle = ettargetTitle.getHint().toString();
                new NetworkAsyncTask().execute(address);
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
            progressDlg = ProgressDialog.show(RegionSearchMain.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            StringBuilder result = new StringBuilder();
            BufferedReader br = null;
            HttpURLConnection conn = null;

            try {
                String keyword = URLEncoder.encode(targetTitle, "UTF-8"); //국문은 인코딩이 필요하여 추가.
                URL url = new URL(address + keyword);
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

                if (resultList.get(0).getTel() == null) { //폰번호가 없는 곳이면
                    try {
                        addList = geocoder.getFromLocationName(resultList.get(0).getAddress(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultList.get(0).setTel("02-" + addList.get(0).getPostalCode());
                }

            MyAdapter.setList(resultList);
            MyAdapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(RegionSearchMain.this, "Error!!!", Toast.LENGTH_SHORT).show();
            progressDlg.dismiss();
        }
    }
}