package com.yuhan.yangpojang.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.home.HttpResponse;
import com.yuhan.yangpojang.home.SearchAdapter_AutoComplete;
import com.yuhan.yangpojang.model.Shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class MapLocationPopupFragment extends Fragment implements OnMapReadyCallback {


    // FusedLocationSource: 현재위치 얻기 | 위치 권한 관리 | 위치 변경 감지 | 위치 업데이트 설정
    private FusedLocationSource popLocationSource;


    // NaverMap 객체: 지도 자체를 표시하는 역할 | ui설정 | 마커및 오버레이 관리 | 카메라 이동 | 지도 데이터(좌표) 등 요청
    private NaverMap popNaverMap;
    // 위치 권한 요청 및 api 통신을 위한 상수들 정의
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private double latitude;
    private double longitude;
    private String selectedLocationAdd;



    // xml(ui)관련 요소 변수
    private SearchView  locationSearchView; // 주소 검색지를 입력하는 칸
    private Button selectLocationButton; // 위치 선택 버튼
    private Button cancelLocationButton ; // 위치 선택 취소 버튼
    private TextView showXY;  // xy 좌표가 잘잡히는지 보기위해 임시로(?) 넣어둔 xy 좌표 출력용 텍스트뷰
    private Marker selectedMarker; // 선택된 좌표에 표시될 핀 마커
    private TextView addressTextView; // 보류 - 지금 위치를 팝업 지도에 제대로 찍히는지 확인 하기 위해 임의로 적은 값
    private BottomNavigationView bottomNavigationView;
    private ListView locationListView;  // 주소 검색 결과를 표시할 ListView

    private ArrayList<String> autoCompletes_name = new ArrayList<>();  // 검색한 위치 이름을 보유 [고양 스타필드]
    private ArrayList<String> autoCompletes_add = new ArrayList<>();  // 검색한 위치 주소를 보유 [고양시 덕양구 ~~]
    private ArrayList<Double> autoCompletes_latitude = new ArrayList<>(); // 검색한 위치의 경도를 보유
    private ArrayList<Double> autoCompletes_longitude = new ArrayList<>(); // 검색한 위치의 위도 보유

    private List<Marker> shopMarkers = new ArrayList<>();  // db에서 등록되있는 가게를 불러와 보유

    private DatabaseReference shopDatabaseReference;
    private ValueEventListener shopValueEventListener;

    private SearchAdapter_AutoComplete autoadapter; // 자동완성 목록 - 어댑터
    private InputMethodManager inputMethodManager;

    private LatLng selectedLatlng ; // 선택한 마커의 위치를 얻음





    // 중요 메서드 설명
    // 1. requestGeocode: 주소를 받아 위치 좌표 찾음:  지도이동, 좌표에 핀찍는 용도로 사용
    // 2. requestReverseGedocode: 좌표값으로 주소(서울 10-1) 찾음

    // 지도 팝업 framgment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.popup_map_location, container, false);

        locationListView = view.findViewById(R.id.search_listView) ;  // 검색 결과 리스트를 보여줌
        locationSearchView = view.findViewById(R.id.locationSearchsearchView); // 주소 검색하는 (글씨창) 부분
        selectLocationButton = view.findViewById(R.id.selectLocationBtn);  // (위치 선택후) 선택 버튼
        cancelLocationButton = view.findViewById(R.id.cancelLocationButton); // 취소 버튼
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 기본위치 받아와 설정
        Bundle locationBundle = getArguments();
        Log.d("abab", String.valueOf(locationBundle));
//        String currentLocation = locationBundle.getString("currentLocation");
//        double currentLatitude = locationBundle.getDouble("currentLatitude");
//        double currentLongitude = locationBundle.getDouble("currentLongitude");
//        Log.d("hihi",currentLocation);
        if(locationBundle!=null)
        {
            Log.d("decaffaine", String.valueOf(locationBundle));

//            Log.d("etttddd",currentLocation);
//            Log.d("fdsfsda", String.valueOf(currentLatitude));
//            Log.d("fdsfsda", String.valueOf(currentLongitude));

            // 이 부분에서 받은 위치 값을 이용해 지도를 설정
            // 여기서 popNaverMap은 지도 객체로 가정됨
//            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(currentLatitude, currentLongitude));
//            popNaverMap.moveCamera(cameraUpdate);


        }


        // 위치 선택 취소 버튼을 누르면 작동 -> 다시 제보화면으로 돌아감
        cancelLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업(자식 fragment인 MapLocationPopupFragment)을 닫는 코드
                getParentFragmentManager().popBackStack();
                // bottomNavigationView를 다시 표시
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        // 위치 선택 (완료) 버튼을 누르면 작동
        selectLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                // 만약 위치를 선택하지 않았다면 경고창 띄우기
                if (selectedMarker == null)
                {
                    Toast.makeText(getActivity(), "위치를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;// 위치 선택이 되지 않았으므로 함수를 빠져나감
                }
                else
                {
                    selectedLatlng = selectedMarker.getPosition(); // 선택한 마커의 위치를 얻음

                    // 50미터 근방에 샵이 있는지 확인
                    Log.d("fdsfsadfsadfsd", String.valueOf( selectedLatlng));

                    double selectedLatitude =  selectedLatlng.latitude;
                    double selectedLongitude =  selectedLatlng.longitude;
                    //역 지오코딩을 수행
                    Geocoder geocoder = new Geocoder(requireContext(), new Locale("ko", "KR"));
                    try {
                        List<Address> addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            String addressName = address.getAddressLine(0); // 추출된 주소
                            Log.d("rlejfkl",addressName);
                            // Bundle에 위도, 경도 및 주소 추가
                            Bundle resultBundle = new Bundle();
                            resultBundle.putDouble("selectedLatitude", selectedLatitude);
                            resultBundle.putDouble("selectedLongitude", selectedLongitude);
                            resultBundle.putString("selectedLocationAdd", addressName);

                            getParentFragmentManager().setFragmentResult("locationResult",resultBundle);
                            Log.d("ㄲ까", "Latitude: " + selectedLatitude + ", Longitude: " + selectedLongitude + ", Address: " + addressName);

                        } else {
                            Log.e("Address Fetch", "No address found for the location.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                    boolean shopsWithinRadius = false;
                    for (Marker shopMarker : shopMarkers)
                    {
                        LatLng shopLatLng = shopMarker.getPosition();
                        double distance = calculateDistance( selectedLatlng.latitude,  selectedLatlng.longitude, shopLatLng.latitude, shopLatLng.longitude);
                        if (distance <= 50.0) //50 미터가 너무 좁다고 판단되면 이 부분을 수정하면 됌
                        {
                            shopsWithinRadius = true;
                            break;
                        }
                    }
                    if (shopsWithinRadius)
                    {
                        // 주변에 가게가 존재하면 Yes/No AlertDialog 띄우기
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("주변에 가게가 있습니다. 그래도 선택하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id) {

                                        getParentFragmentManager().popBackStack();
                                        bottomNavigationView.setVisibility(View.VISIBLE);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        // 아니요를 클릭하면 아무 동작도 하지 않음
                                        dialog.dismiss();
                                    }
                                });
                        // AlertDialog 객체 생성 및 표시
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else
                    {
//                        // 주변에 가게가 없으면 선택한 위치 주소 정보를 가져오고 팝업을 닫음
//
                        getParentFragmentManager().popBackStack();
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                }
        });

        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              // 검색어가 제출되었을 때 처리할 작업을 여기에 작성
//              performSearch(query);
              Log.d("querysearch1","fjd");
              hideKeyboard();
              return true;
          }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("querysearch2","fjd");
                // 검색어가 변경될 때 처리할 작업을 여기에 작성
//                settingVisibility_change();
                if(!TextUtils.isEmpty(newText))
                {
                    Log.d("cign","cign");
                    HttpResponse.sendData(requireContext(), newText, new HttpResponse.DataCallback() {
                        @Override
                        public void onDataLoaded(LinkedHashMap<String, ArrayList<String>> address_hash) {

                            settingVisibility_change();
                            Log.d("test출력","1");
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    autoCompletes_name.clear();
                                    autoCompletes_add.clear();
                                    autoCompletes_latitude.clear();
                                    autoCompletes_longitude.clear();
                                    for(String key : address_hash.keySet()){
                                        autoCompletes_name.add(key);

                                        ArrayList<String> values = address_hash.get(key);
                                        if(values != null && !values.isEmpty()){
                                            autoCompletes_add.add(values.get(0));
                                            autoCompletes_latitude.add(Double.valueOf(values.get(1)));
                                            autoCompletes_longitude.add(Double.valueOf(values.get(2)));
                                        }
                                    }
                                 //Toolbar 설정
                                    androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
                                    ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
                                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                           locationListView.setVisibility(View.GONE);
                                            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                            locationSearchView.setQuery("", false); // Clear the search text
                                            hideKeyboard();
                                        }
                                    });
                                    autoadapter = new SearchAdapter_AutoComplete(requireContext(), autoCompletes_name, autoCompletes_add);
                                    locationListView.setAdapter(autoadapter);
                                    locationListView.setOnScrollListener(scrollL); // 스크롤 시 리스너 등록
                                    Log.d("apple", autoadapter.toString());
                                    autoadapter.notifyDataSetChanged();
                                    for(int i = 0; i < address_hash.size(); i++){
                                        Log.d("SearchActivity", "받은 주소 지명 : " + autoCompletes_name.get(i) + "   주소 : " + autoCompletes_add.get(i) + "   좌표 : " + autoCompletes_latitude.get(i) + ", " + autoCompletes_longitude.get(i));
                                    }
                                }
                            });
                        }

                    });
                }
                return false;
            }
        });

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLocationAdd= autoCompletes_add.get(position);
                Log.d("rototrl",selectedLocationAdd);
                double selectedLatitude = autoCompletes_latitude.get(position);
                double selectedLongitude = autoCompletes_longitude.get(position);

                LatLng selectedLatLng = new LatLng(selectedLatitude,selectedLongitude);
                updateMapWithLatLng(selectedLatLng);
                moveMapToLocation(selectedLatLng);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                locationSearchView.setQuery("", false); // Clear the search text
                locationListView.setVisibility(View.GONE);
                hideKeyboard();

                //BUNDLE 을 생성해 값전달
                ReportShopFragment reportShopFragment = new ReportShopFragment();
                Bundle resultBundle = new Bundle();

                resultBundle.putString("selectedLocationAdd", selectedLocationAdd);
                resultBundle.putDouble("selectedLatitude", selectedLatitude);
                resultBundle.putDouble("selectedLongitude", selectedLongitude);

                getParentFragmentManager().setFragmentResult("locationResult",resultBundle);
                Log.d("rototri", String.valueOf(resultBundle));


            }
        });

        // MapFragment를 초기화하고 뷰에 추가
        // getChildFragMent  _ 부모 fragment 위에 자식 fragment를 다루기 위해 getChldFragmentManager 사용( 팝업처럼 뜨는 창이여서 이렇게 작동시킴)
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.PopMapFragment);
        if (mapFragment == null)
        {
            Log.d("개똥", String.valueOf(mapFragment));
            // mapFragment가 없을 경우 새 MapFragment 인스턴스 생성
            mapFragment = MapFragment.newInstance();
            // 자식 프래그먼트 매니저에 MapFragment 추가
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mapFragment)
                    .commit();
        }
        Log.d("개똥2", String.valueOf(mapFragment));
        // getMapAsync: 지도가 준비되면 onMapReady 콜백 호출
        mapFragment.getMapAsync(this);
                return view;
    }

    // 스크롤 시 리스너 설정
    AbsListView.OnScrollListener scrollL = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 키보드 내리기
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                View currentFocus = getActivity().getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager manager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };



    public void settingVisibility_change() {
        Log.d("ro", "rori");
        locationListView.post(new Runnable() {
            @Override
            public void run() {
                locationListView.setVisibility(View.VISIBLE);
            }
        });
        cancelLocationButton.post(new Runnable() {
            @Override
            public void run() {
                cancelLocationButton.setVisibility(View.GONE);
            }
        });
        selectLocationButton.post(new Runnable() {
            @Override
            public void run() {
                selectLocationButton.setVisibility(View.GONE);
            }
        });
    }


    //50M 근방에 가게 있는지 계산하는 메서드
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        return distance;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        popNaverMap = naverMap;
        Bundle args = getArguments();
        if (args != null) {
            latitude = args.getDouble("latitude", 0.0);
            longitude = args.getDouble("longitude", 0.0);
            // Check if latitude and longitude values are provided
            if (latitude != 0.0 && longitude != 0.0) {
                // Use the provided coordinates to set the initial map location
                LatLng initialLatLng = new LatLng(latitude, longitude);
                Log.d("오", String.valueOf(initialLatLng));
                updateMapWithLatLng(initialLatLng);
                moveMapToLocation(initialLatLng);

            }
        }
        Log.d("소똥", popNaverMap.toString());
        popLocationSource = new FusedLocationSource(requireActivity(), PERMISSION_REQUEST_CODE);
        // 지도에 위치 소스를 설정합니다
        popNaverMap.setLocationSource(popLocationSource);
        // 위치 권환 요청
        ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE);

        // 권한이 얻어져있는 경우 현재 위치를 표시 가능하므로 시작할때 내 위치로 뜨게 설정
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(selectedMarker==null)
            {
                popNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

            }
        }
        // 권한 부여가 되지 않은 경우 권한 부여 메세지 생성
        else
        {
            Toast.makeText(getActivity(), "설정> 애플리케이션 > YangPojag > 권한에서 지도 권한을 부여하세요", Toast.LENGTH_SHORT).show();
            //어플 종료도 넣어야할지..
        }
        // 지도 UI 설정 구성
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlEnabled(true);

        // 샵들 정보를 firebase 에서 불러오는 코드
        fetchShopDataFromFirebase();

        popNaverMap.addOnCameraChangeListener((reason, animated) -> {
            if (reason == CameraUpdate.REASON_GESTURE) {
                // Load shop data based on the current visible region
                loadShopDataForVisibleRegion();
            }
        });

        // 지도 클릭을 위한 클릭 리스너 설정 => 지도를 클릭하면 그 위치의 주소와 핀을 찍기 위한 리스너
        popNaverMap.setOnMapClickListener(new NaverMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                // 이전 선택한 마커 제거
                updateMapWithLatLng(latLng);
                //선택한 위치로 지도 이동하고 중심 설정
                moveMapToLocation(latLng);
            }
        });
    }

    private void loadShopDataForVisibleRegion() {
        LatLngBounds visibleBounds = popNaverMap.getContentBounds();
        double north = visibleBounds.getNorthLatitude();
        double south = visibleBounds.getSouthLatitude();
        double east = visibleBounds.getEastLongitude();
        double west = visibleBounds.getWestLongitude();

        shopValueEventListener = shopDatabaseReference
                .orderByChild("latitude")
                .startAt(south)
                .endAt(north)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear existing shop markers
                        clearShopMarkers();

                        // Load and display shop data for the visible region
                        for (DataSnapshot shopSnapshot : dataSnapshot.getChildren()) {
                            Shop shop = shopSnapshot.getValue(Shop.class);
                            addShopMarker(shop);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle data loading errors
                        Log.e("Firebase", "Failed to fetch shop data: " + databaseError.getMessage());
                    }
                });
    }


    private void clearShopMarkers() {
        for (Marker marker : shopMarkers) {
            marker.setMap(null);
        }
        shopMarkers.clear();
    }




    @Override
    public void onDestroyView() {
        // Remove the ValueEventListener when the fragment is destroyed
        if (shopValueEventListener != null) {
            shopDatabaseReference.removeEventListener(shopValueEventListener);
        }

        // ... (other cleanup code)

        super.onDestroyView();
    }
    // Firebase Realtime Database에서 가게 데이터 가져오기 (기존 가게들 지도에 찍기 위함)
    private void fetchShopDataFromFirebase() {
        shopDatabaseReference= FirebaseDatabase.getInstance().getReference("shops");
        shopDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot shopSnapshot : dataSnapshot.getChildren()) {
                    // 각 가게 데이터를 Shop 객체로 파싱
                    Shop shop = shopSnapshot.getValue(Shop.class);

                    // 가져온 가게 데이터에 대한 핀을 지도에 추가
                    addShopMarker(shop);
                }
                addAllShopMarkers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                // 데이터 가져오기 실패 시 처리
                Log.e("Firebase", "Failed to fetch shop data: " + databaseError.getMessage());
            }

        });
    }


    private void updateMapWithLatLng(LatLng latLng)
    {
        Log.d("일", String.valueOf(latLng));
        Log.d("이","ASDfjdskfkldsjfklsdjfklsd");
        if(popNaverMap!=null)
        {
            Log.d("칠칠","ASDfjdskfkldsjfklsdjfklsd");
            popNaverMap.moveCamera(CameraUpdate.scrollTo(latLng));
        }
        if (selectedMarker != null)
        {
            Log.d("삼","ASDfjdskfkldsjfklsdjfklsd");
            selectedMarker.setMap(null);
            selectedMarker = null;
        }

        selectedMarker = new Marker();
        selectedMarker.setPosition(latLng);
        selectedMarker.setIcon(OverlayImage.fromResource(R.drawable.pin_black));
        selectedMarker.setMap(popNaverMap);
        selectLocationButton.setVisibility(View.VISIBLE);
        cancelLocationButton.setVisibility(View.VISIBLE);
    }

    // 마커 표시된 곳이 항상 가운데를 유지하기 위해 호출되는 메서드
    private void moveMapToLocation(LatLng latLng)
    {
        if(popNaverMap!=null)
        {
            Log.d("사.","ㄹㄹ");
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng)
                    .animate(CameraAnimation.Fly,1000);
            popNaverMap.moveCamera(cameraUpdate);
        }

    }

    public void addShopMarker(Shop shop)
    {
        if (shop != null) {
            LatLng shopLatLng = new LatLng(shop.getLatitude(), shop.getLongitude());
            Marker shopMarker = new Marker();
            shopMarker.setPosition(shopLatLng);
            shopMarker.setIcon(OverlayImage.fromResource(R.drawable.pin_store));
            shopMarker.setMap(popNaverMap);
            shopMarkers.add(shopMarker);
        }
    }
    // 모든 가게 마커를 지도에 추가
    private void addAllShopMarkers()
    {
        for (Marker marker : shopMarkers)
        {
            marker.setMap(popNaverMap);
        }
    }

    // 배경을 클릭하거나 enter을 쳤을때도 키보드가 감춰짐
    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //   지워도 되는가
    // 주소로부터 좌표를 가져올 새로운 메서드를 추가
//    public LatLng getCoordinatesFromAddress(String address) {
//        Geocoder geocoder = new Geocoder(requireContext(), new Locale("ko", "KR"));
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(address, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address foundAddress = addresses.get(0);
//                double latitude = foundAddress.getLatitude();
//                double longitude = foundAddress.getLongitude();
//                return new LatLng(latitude, longitude);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



}