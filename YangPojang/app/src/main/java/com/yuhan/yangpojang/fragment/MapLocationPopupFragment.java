package com.yuhan.yangpojang.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.yuhan.yangpojang.model.Shop;

import java.io.IOException;
import java.util.ArrayList;
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


    // xml(ui)관련 요소 변수
    private EditText locationSearchEditText; // 주소 검색지를 입력하는 칸
    private Button searchButton;  // 주소 검색버튼
    private Button selectLocationButton; // 위치 선택 버튼
    private Button cancelLocationButton ; // 위치 선택 취소 버튼
    private TextView showXY;  // xy 좌표가 잘잡히는지 보기위해 임시로(?) 넣어둔 xy 좌표 출력용 텍스트뷰
    private Marker selectedMarker; // 선택된 좌표에 표시될 핀 마커
    private TextView addressTextView; // 보류 - 지금 위치를 팝업 지도에 제대로 찍히는지 확인 하기 위해 임의로 적은 값
    private BottomNavigationView bottomNavigationView;
    //    private ListView locationListView;  // 주소 검색 결과를 표시할 ListView
    private ArrayAdapter<String> locationAdapter; // 검색결과를 ListView에 연결할 어댑터

    private List<Marker> shopMarkers = new ArrayList<>();

    private DatabaseReference shopDatabaseReference;
    private ValueEventListener shopValueEventListener;

    // 중요 메서드 설명
    // 1. requestGeocode: 주소를 받아 위치 좌표 찾음:  지도이동, 좌표에 핀찍는 용도로 사용
    // 2. requestReverseGedocode: 좌표값으로 주소(서울 10-1) 찾음

    // 지도 팝업 framgment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.popup_map_location, container, false);
        locationSearchEditText = view.findViewById(R.id.locationSearchEditText); // 주소검색하는 글씨창 부분
        searchButton = view.findViewById(R.id.searchButton); // 검색버튼
        selectLocationButton = view.findViewById(R.id.selectLocationBtn);  // (위치 선택후) 선택 버튼
        cancelLocationButton = view.findViewById(R.id.cancelLocationButton); // 취소 버튼
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        // enter 키보드를 눌러도 searchButton을 누른것과 같은 효과를 주기위함
        locationSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            // enter 키인지 판별하는 boolean  두가지 변수
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnterKeyPressed = (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN);
                boolean isSearchAction = (actionId == EditorInfo.IME_ACTION_SEARCH);

                if(isEnterKeyPressed || isSearchAction)
                {
                    // ENTER 를 눌렀을때 실행되는 메서드 호출
                    performSearch();
                    return  true;
                }
                return false;
            }
        });
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
                    return; // 위치 선택이 되지 않았으므로 함수를 빠져나감
                }
                else
                {
                    // 50미터 근방에 샵이 있는지 확인
                    LatLng selectedLatLng = selectedMarker.getPosition();
                    Log.d("fdsfsadfsadfsd", String.valueOf(selectedLatLng));
                    boolean shopsWithinRadius = false;
                    for (Marker shopMarker : shopMarkers)
                    {
                        LatLng shopLatLng = shopMarker.getPosition();
                        double distance = calculateDistance(selectedLatLng.latitude, selectedLatLng.longitude, shopLatLng.latitude, shopLatLng.longitude);
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
                                        // Yes를 클릭하면 선택한 위치 주소 정보를 가져오고 팝업을 닫음
                                        Bundle resultBundle = new Bundle();
                                        resultBundle.putDouble("latitude", selectedLatLng.latitude);
                                        resultBundle.putDouble("longitude", selectedLatLng.longitude);
                                        Log.d("fdsfsdaf", String.valueOf(selectedLatLng.latitude));
                                        Log.d("fdsfsdaf", String.valueOf(selectedLatLng.longitude));

                                        getParentFragmentManager().setFragmentResult("locationResult", resultBundle);
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
                        // 주변에 가게가 없으면 선택한 위치 주소 정보를 가져오고 팝업을 닫음
                        Bundle resultBundle = new Bundle();
                        resultBundle.putDouble("latitude", selectedLatLng.latitude);
                        resultBundle.putDouble("longitude", selectedLatLng.longitude);
                        getParentFragmentManager().setFragmentResult("locationResult", resultBundle);
                        getParentFragmentManager().popBackStack();
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // 위치 검색 버튼을 클릭했을때 작동
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideKeyboard();
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // 검색한 주소를  addr 변수에 가져옴
                        final String addr = locationSearchEditText.getText().toString();

                        //searchedLatLng: 위도 경도 저장
                        // getCoordinatesFromeAddress로 검색한 주소를 좌표로 변환 -> 검색한 부분으로 지도 이동, 핀 찍기 위함
                        LatLng searchedLatLng = getCoordinatesFromAddress(addr);
                        // 주소검색한 내용이 null이 아니면 ( 검색한 내용이 있으면 ) =>
                        if (searchedLatLng != null)
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    // => 검색한 위치로  지도 이동
//                              지우셈      popNaverMap.moveCamera(CameraUpdate.scrollTo(searchedLatLng));
                                    // 이전에 표시된 마커는 지우고 초기화
                                    updateMapWithLatLng(searchedLatLng);
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        // MapFragment를 초기화하고 뷰에 추가
        // getChildFragMent  _ 부모 fragment 위에 자식 fragment를 다루기 위해 getChldFragmentManager 사용( 팝업처럼 뜨는 창이여서 이렇게 작동시킴)
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.PopMapFragment);
        if (mapFragment == null)
        {
            // mapFragment가 없을 경우 새 MapFragment 인스턴스 생성
            mapFragment = MapFragment.newInstance();
            // 자식 프래그먼트 매니저에 MapFragment 추가
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mapFragment)
                    .commit();
        }
        // getMapAsync: 지도가 준비되면 onMapReady 콜백 호출
        mapFragment.getMapAsync(this);

        return view;
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
        popLocationSource = new FusedLocationSource(requireActivity(), PERMISSION_REQUEST_CODE);
        // 지도에 위치 소스를 설정합니다
        popNaverMap.setLocationSource(popLocationSource);
        // 위치 권환 요청
        ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE);

        // 권한이 얻어져있는 경우 현재 위치를 표시 가능하므로 시작할때 내 위치로 뜨게 설정
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            popNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
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
        // 보이는 부분의 경계
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

    // 검색 기능 수행 - enter로도 검색버튼 누른것과 같은 효과를 위해 작성한 파트
    private void performSearch()
    {
        hideKeyboard();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final String addr = locationSearchEditText.getText().toString();
                LatLng searchedLatLng = getCoordinatesFromAddress(addr);
                if(searchedLatLng!=null)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            updateMapWithLatLng(searchedLatLng);
                            Log.d("fdfj","dfkdf");
                        }
                    });
                }
            }
        }).start();
    }



    private void updateMapWithLatLng(LatLng latLng)
    {
        popNaverMap.moveCamera(CameraUpdate.scrollTo(latLng));
        if (selectedMarker != null)
        {
            selectedMarker.setMap(null);
            selectedMarker = null;
        }

        selectedMarker = new Marker();
        selectedMarker.setPosition(latLng);
        selectedMarker.setIcon(OverlayImage.fromResource(R.drawable.pin_black));
        selectedMarker.setMap(popNaverMap);
    }



    // 마커 표시된 곳이 항상 가운데를 유지하기 위해 호출되는 메서드
    private void moveMapToLocation(LatLng latLng)
    {
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng)
                .animate(CameraAnimation.Fly,1000);
        popNaverMap.moveCamera(cameraUpdate);
    }

//    p
    // 주소로부터 좌표를 가져올 새로운 메서드를 추가

    public LatLng getCoordinatesFromAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), new Locale("ko", "KR"));
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address foundAddress = addresses.get(0);
                double latitude = foundAddress.getLatitude();
                double longitude = foundAddress.getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 키보드를 숨기는 메서드
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationSearchEditText.getWindowToken(), 0);
    }


}