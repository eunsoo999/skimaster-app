package lecture.mobile.final_project.ma01_20160940;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class SkiMapSearchMain extends AppCompatActivity {
    private final static int ZOOM_LEVEL = 10;                   // 지도 확대 배율
    private final static int PERMISSION_REQ_CODE = 100;         // permission 요청 코드
    private GoogleMap mGoogleMap;           // 구글맵 객체 저장 멤버 변수
    Intent intent;
    Geocoder geocoder;
    ArrayList<SkiItem> resultList = null; //결과 받아옴
    List<Address> addList = null;
    private LocationManager locmanager; //위치정보 매니저
    private Location myLocation; //나의 위치 받으옴
    private Marker centerMarker;
    private MarkerOptions options;
    private ArrayList<Marker> markerList;
    private MarkerOptions poiMarkerOptions;
    String searchString;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_map_main);

        editText = (EditText)findViewById(R.id.et_search);
        geocoder = new Geocoder(this);
        locmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        markerList = new ArrayList<Marker>();

        //인텐트로 전달된 ArrayList 가져오기
        intent = getIntent();
        resultList = (ArrayList<SkiItem>) intent.getSerializableExtra("resultList");

        //구글맵 준비
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        if (ActivityCompat.checkSelfPermission(SkiMapSearchMain.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SkiMapSearchMain.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SkiMapSearchMain.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQ_CODE);
            return;
        }
        myLocation = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_position : //누르면 내 위치로 이동
                Toast.makeText(SkiMapSearchMain.this, "사용자의 위치입니다.", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQ_CODE);
                    return;
                }
                locmanager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 5, locationListener);
                break;
            case R.id.btn_s :
                searchString = editText.getText().toString();

                for (int i = 0; i < resultList.size(); i++) { //바꿈
                    if ((resultList.get(i).getTitle()).contains(searchString))//검색어와 타이틀이 맞는 부분이 있으면
                    {
                        markerList.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        Toast.makeText(SkiMapSearchMain.this,  resultList.get(i).getTitle() + "의 위치입니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 10));
            try {
                addList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            options = new MarkerOptions();
            options.position(myLatLng);
            options.title("사용자 위치");
            options.snippet(addList.get(0).getAddressLine(0));
            centerMarker = mGoogleMap.addMarker(options);
            centerMarker.showInfoWindow();

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            로딩한 구글맵을 보관
            mGoogleMap = googleMap;
            LatLng lastLatLng;
            if (myLocation != null) {
            lastLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            } else {
                lastLatLng = new LatLng(37.606320, 127.041808);
            }
            //이동
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, ZOOM_LEVEL));

            poiMarkerOptions = new MarkerOptions();
            LatLng poiPosition;
            for(int i = 0; i < resultList.size(); i++) { //바꿈
                poiPosition = new LatLng(Double.parseDouble(resultList.get(i).getMapY()), Double.parseDouble(resultList.get(i).getMapX()));
                poiMarkerOptions.position(poiPosition);
                poiMarkerOptions.title(resultList.get(i).getTitle());
                poiMarkerOptions.snippet(resultList.get(i).getAddress());
                poiMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerList.add(mGoogleMap.addMarker(poiMarkerOptions));
            }

            for (int i = 0; i < markerList.size(); i++)
                markerList.get(i).showInfoWindow();

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String markerId = marker.getId();
                String[] values = markerId.split("m");
                Intent intent = new Intent(SkiMapSearchMain.this, ListInfoMain.class);

                SkiItem selectList = resultList.get(Integer.parseInt(values[1]));
                intent.putExtra("skiId", resultList.get(Integer.parseInt(values[1])).getContentid());
                intent.putExtra("selectList", selectList);
                startActivity(intent);
            }
        });
        }
    };
}
