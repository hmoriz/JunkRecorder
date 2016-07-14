package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.JunkDataStore;
import jp.ac.titech.itpro.hmoriz.junkrecorder.R;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junk.Junk;

/**
 * Created by hmoriz on 2016/07/11.
 */
public class InputMapFragment extends JunkRecorderFragment{

    private final static LatLng MY_LOCATION = new LatLng(35.604667, 139.682759);
    SupportMapFragment mapFragment;
    ViewGroup mContainer;
    FloatingActionButton currentPlaceButton;
    Location mLocation;
    Marker myPlace;
    Marker tempPlace;
    HashMap<String, Marker> markerHashMap = new HashMap<>();
    Marker selectedPlace;
    LocationManager mLocationManager;
    String provider;
    boolean mAddMode = false;

    private boolean mCalledPermission = false;
    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final int REQCODE_PERMISSIONS = 1111;
    private final static String MAPTAG = "inputmapfragment_map";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        currentPlaceButton = ButterKnife.findById(view, R.id.button1);
        mContainer = ButterKnife.findById(view, R.id.mapContainer);
        // フラグメントを作成する(すでに存在しているならそのコンテンツを破棄して再生成する)
        if (getFragmentManager().findFragmentByTag(MAPTAG) != null) {
            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentByTag(MAPTAG))
                    .commit();
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        options.compassEnabled(true);
        options.useViewLifecycleInFragment(true);
        mapFragment = SupportMapFragment.newInstance(options);
        getFragmentManager().beginTransaction()
                .add(R.id.mapContainer, mapFragment, MAPTAG)
                .commit();
        mapFragment.getMapAsync(new MapMarkerLoader());
        currentPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        moveToCurrentLocation(googleMap, true);
                    }
                });
            }
        });
        setUpLocationListenner();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void loadJunk(String filename) {
        super.loadJunk(filename);
    }

    // filenameのJunkが位置登録されていないならば、Googlemapの位置登録モードを始める
    public void startAddJunkMode(String filename){
        mJunk = JunkDataStore.getInstance().readJunkJson(mainActivity, filename);
        if(mJunk.getLocation()== null) {
            mAddMode = true;
            this.filename = filename;
        }else{
            // Map?->Input->Map
            // リストに既に存在してるはず
            tempPlace = markerHashMap.get(filename);
            if(tempPlace == null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        CameraPosition position = CameraPosition.builder()
                                .target(mJunk.getLocation())
                                .zoom(15)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                });
            } else {
                tempPlace.showInfoWindow();
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        CameraPosition position = CameraPosition.builder()
                                .target(tempPlace.getPosition())
                                .zoom(15)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                });
            }
        }

    }

    // Map -> Input -> Mapで位置登録完了
    public void registerJunk(final String filename){
        this.filename = filename;
        mJunk = JunkDataStore.getInstance().readJunkJson(mainActivity, filename);
        if(tempPlace != null){
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng latLng = tempPlace.getPosition();
                    tempPlace.remove();
                    tempPlace = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(InputMapFragment.this.filename+"のつぶやき")
                    .snippet(mJunk.text));
                }
            });
        }
        tempPlace = null;
    }

    // GoogleMapが作られたタイミングで呼び出すようにする
    // このクラス内の実装で、GoogleMapの中に自分の書いた位置つきぼやきを読み込めるようにする
    // また、Mapの長押し時等のイベントを設定する
    private class MapMarkerLoader implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            moveToCurrentLocation(googleMap, false);
            googleMap.setOnMapLongClickListener(new MyOnMapLongClickListener());
            googleMap.setOnMarkerClickListener(new MyOnMarkerClickListener());
            HashMap<String, Junk> junkList = JunkDataStore.getInstance().readAllJunks(mainActivity);
            if(junkList != null){
                for(String filename : junkList.keySet()){
                    Junk junk = junkList.get(filename);
                    if(junk.getLocation() != null){
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .title(filename+"のつぶやき")
                                .position(junk.getLocation())
                                .snippet(junk.text)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        markerHashMap.put(filename, marker);
                    }
                }
            }
        }
    }

    private class MyOnMapClickListener implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
        @Override
        public void onMapClick(LatLng latLng) {
            if(selectedPlace != null){
                mapFragment.getMapAsync(MyOnMapClickListener.this);
                selectedPlace = null;
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // 現状何もしない
        }
    }

    // 長押しされたときのイベントはここに、そのときのMapを読み込んで変更する処理もこの中に実装
    private class MyOnMapLongClickListener implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
        LatLng latLng;

        @Override
        public void onMapLongClick(LatLng latLng) {
            // 緯度経度を元にぼやきを加える
            this.latLng = latLng;
            mapFragment.getMapAsync(MyOnMapLongClickListener.this);
            if(mAddMode && filename != null){
                // DO NOTHING
            }else {
                String filename = JunkDataStore.makeFilename(mainActivity);
                mainActivity.moveToInputFragment(filename, this.latLng);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if(mAddMode && filename != null){
                Junk junk = JunkDataStore.getInstance().readJunkJson(mainActivity, filename);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(filename+"のつぶやき")
                        .snippet(junk.text)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mJunk.setLocation(latLng);
                JunkDataStore.getInstance().writeJunk(mainActivity, mJunk, filename);
                filename = null;
                mAddMode = false;
            }else {
                tempPlace = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("新しいつぶやき(編集中)")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
    }

    // マーカーが押された時のイベントはここに
    private class MyOnMarkerClickListener implements GoogleMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            // マーカーからぼやきをよびだす
            if(marker.equals(selectedPlace)) {
                String filename = marker.getTitle().replace("のつぶやき", "");
                mainActivity.moveToInputFragment(filename);
            }else{
                selectedPlace = marker;
            }
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQCODE_PERMISSIONS && Arrays.equals(permissions, PERMISSIONS)){
            mCalledPermission = true;
            setUpLocationListenner();
        }
    }

    private void moveToCurrentLocation(GoogleMap googleMap, boolean animation){
        if (myPlace != null) {
            myPlace.remove();
        }
        LatLng latLng;
        updateLocation();
        if (mLocation != null) {
            latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            myPlace = googleMap.addMarker(new MarkerOptions().position(latLng).title("Your Location").snippet(mLocation.getLatitude() + "," + mLocation.getLongitude()));
        } else {
            latLng = MY_LOCATION;
            myPlace = googleMap.addMarker(new MarkerOptions().position(latLng).title("Our Great Ookayama").snippet(latLng.toString()));
        }
        CameraPosition position = CameraPosition.builder().target(latLng).zoom(14).build();
        if(animation) googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        else googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        CameraUpdateFactory.newLatLng(latLng);
    }

    private void setUpLocationListenner() {
        if(mLocationManager != null)return;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("setUpLocationListener", "Permission Denied!");
            return;
        }
        // LocationManagerを取得し登録
        mLocationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();
        // Accuracyを指定(低精度)
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // PowerRequirementを指定(低消費電力)
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // ロケーションプロバイダの取得
        provider = mLocationManager.getBestProvider(criteria, true);

        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LocationListener","onLocationChanged");
                // ロケーションを登録
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("LocationListener","onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("LocationListener","onProviderDisabled");
            }
        };
        mLocationManager.requestLocationUpdates(provider, 10000, 1, mLocationListener);
        Log.i("setUpLocationListener", "Permission Passed!");
    }

    private void updateLocation(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // まだ拒否されてない場合、許可を求める
            if(!mCalledPermission) ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQCODE_PERMISSIONS);
            return;
        }
        setUpLocationListenner();
        mLocation = mLocationManager.getLastKnownLocation(provider);
    }

}
