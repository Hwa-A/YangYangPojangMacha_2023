package com.yuhan.yangpojang.fragment;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.naver.maps.map.widget.CompassView;
import com.naver.maps.map.widget.LocationButtonView;
import com.yuhan.yangpojang.home.CategoryListAdapter;
import com.yuhan.yangpojang.home.pochaListRecyclerView;
import com.yuhan.yangpojang.model.LikeShopData;
import com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.home.HttpResponse;
import com.yuhan.yangpojang.home.PochaListAdapter;
import com.yuhan.yangpojang.home.SearchActivity;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.model.StoreData;
import com.yuhan.yangpojang.home.onPochaListItemClickListener;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback, onPochaListItemClickListener{

    //OnMapReadyCallback : 지도가 준비되었을 때 호출되는 콜백을 처리, onMapReady()메서드 구현해야 함

    private BottomNavigationView bottomNavigationView;   // 하단 네비게이션 뷰
    private FusedLocationSource locationSource; //안드로이드의 위치 서비스를 이용하여 현재 위치를 가져올 수 있는 클래스
    private NaverMap mNaverMap; //네이버 지도를 표시하고 조작하기 위한 클래스
    private static final int PERMISSION_REQUEST_CODE = 100; //위치 권한 요청을 처리하기 위한 요청 코드
    private static final String[] PERMISSIONS = { //위치 권한을 요청할 때 필요한 권한 선언
            android.Manifest.permission.ACCESS_FINE_LOCATION, //고정밀 위치 정보 권한
            Manifest.permission.ACCESS_COARSE_LOCATION // 대략적인 위치 정보 권한
    };
    TextView searchAdd;
    View homeview;

    com.naver.maps.map.MapFragment mapFragment;

    ImageButton authoff, meetingoff, authon, meetingon; //인증, 번개 버튼
    boolean auth, meeting; //auth, meeting 상태를 확인하는 변수
    UiSettings uiSettings;
    CategoryListAdapter categoryListAdapter;

    /* onMapReady()메서드 - 지도가 준비되었을 때 호출되며, NaverMap 객체에 위치 소스를 설정하고 권한을 확인하는 작업 수행
                             - 지도 초기화 및 사용자 정의 작업 수행, 지도가 초기화되고 사용 가능한 상태일 때 호출되는 콜백 */
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d("나만 볼거야", "onMapReady() 실행");
        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;

        // 위치권한 여부에 따른 현위치 버튼 표시, 위치 권한 거부 시 서울 시청 기준으로 가게 띄우기
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermission();
            mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        }
        // 권한 부여가 되지 않은 경우 권한 부여 메세지 생성
        else
        {
            locationPermission();
            // 위치권한 거부 시 서울시청 중심으로 가게 데이터 요청
            // 위치권한 거부 시 현재 카메라 위치 기준으로 가게 데이터 요청
            CameraPosition cameraposition = mNaverMap.getCameraPosition();
            StoreData.addLocation(cameraposition.target.latitude, cameraposition.target.longitude, calculateRadius());
            loadStoreData();
        }


        //UiSettings 클래스 - 지도 컨트롤 객체 관리
        uiSettings = mNaverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);// 현위치 버튼
        uiSettings.setScaleBarEnabled(false); // 축적바
        uiSettings.setCompassEnabled(false); //나침반
        uiSettings.setZoomControlEnabled(false); // 줌 버튼


        //지도 클릭 리스너 설정
        mNaverMap.setOnMapClickListener(mapL);

        //지도 "이동" 리스너 설정
        mNaverMap.addOnCameraChangeListener(cameraChangeListener);


        // 인증 및 번개 버튼 리스너 설정
        authoff = getActivity().findViewById(R.id.authoff);
        meetingoff = homeview.findViewById(R.id.meetingoff);
        authon = getActivity().findViewById(R.id.authon);
        meetingon = homeview.findViewById(R.id.meetingon);
        authoff.setOnClickListener(authmeetingL);
        authon.setOnClickListener(authmeetingL);
        meetingoff.setOnClickListener(authmeetingL);
        meetingon.setOnClickListener(authmeetingL);

        // 목록보기 버튼 구현
        ImageButton show_list = getActivity().findViewById(R.id.showlist);
        show_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listStores != null && !listStores.isEmpty()) {
                    ArrayList<Shop> pochas = new ArrayList<>(listStores);
                    PochaListView(pochas);
                } else {
                    Toast.makeText(getActivity(), "조건에 맞는 업체가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 현 위치 좌표를 얻기 위한 리스너
        mNaverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                // 맵 시작 시 현재 위치를 기준으로 데이터 로드
                StoreData.addLocation(location.getLatitude(), location.getLongitude(), calculateRadius());
                loadStoreData();

                // 현 위치 기준으로 키워드 검색 시 같은 행정구역의 데이터를 가져오기 위함
                HttpResponse.setCurrentLocation(getActivity(), location.getLatitude(), location.getLongitude());

                Log.d("Home", "현위치 : " + location);
                mNaverMap.removeOnLocationChangeListener(this); // 리스너 해제
            }
        });

        // 카테고리 구현
        RecyclerView categoryList = getActivity().findViewById(R.id.category_list);
        categoryListAdapter = new CategoryListAdapter(getActivity());
        categoryList.setAdapter(categoryListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryList.setLayoutManager(layoutManager);

    }

    // 위치 권한 거부 시 리스너 등록(팝업창)
    View.OnClickListener locationPermissionPopup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // 사용자 정의 레이아웃을 설정
            LayoutInflater inflater = getLayoutInflater();
            View customView = inflater.inflate(R.layout.location_permission_popup, null);
            builder.setView(customView);
            final AlertDialog dialog = builder.create();

            Button cancel = customView.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button move = customView.findViewById(R.id.move);
            move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 설정 화면으로 이동하는 인텐트 생성
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getActivity().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    };


    //지도 "이동" 리스너 설정
    NaverMap.OnCameraChangeListener cameraChangeListener = new NaverMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(int reason, boolean animated) {
            if(reason == -1){ // -1 : 지도가 사용자 제스처에 의해 이동했을 경우
                AppCompatButton re_searchbtn = homeview.findViewById(R.id.re_searchbtn);
                re_searchbtn.setVisibility(VISIBLE);
                re_searchbtn.setOnClickListener(re_searchbtnL);

                calculateRadius();
            }

        }
    };

    // 현재 지도 화면 경계와 중심 좌표를 기준으로 반경을 계산하는 메서드
    private float calculateRadius() {
        LatLngBounds visibleBounds = mNaverMap.getContentBounds(); // 현재 지도 화면의 경계
        LatLng center = visibleBounds.getCenter(); // 현재 지도 화면의 중심 좌표

        float[] results = new float[1];
        Location.distanceBetween(
                center.latitude, center.longitude,
                visibleBounds.getSouthLatitude(), center.longitude,
                results
        );

        return results[0];
    }

    //현재 지도에서 가게 재검색 버튼 리스너 설정
    View.OnClickListener re_searchbtnL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppCompatButton re_searchbtn = homeview.findViewById(R.id.re_searchbtn);
            re_searchbtn.setVisibility(INVISIBLE);

            CameraPosition currentPosition = mNaverMap.getCameraPosition(); //현재 화면에 표시되고 있는 지도의 카메라 위치 정보를 가져옴
            LatLng currentLatLng = currentPosition.target.toLatLng(); // currentPosition 위도, 경도값 추출, target : 현재 카메라의 중심 위치 -> LatLng객체로 변환

            //StoreData에 위치값 보내기
            StoreData mainStoreData = new StoreData();
            StoreData.addLocation(currentLatLng.latitude, currentLatLng.longitude, calculateRadius());
            loadStoreData(); //주소 검색 후 검색한 주소 기준으로 데이터 로드
        }
    };

    pochaListRecyclerView pochalist_view;
    AppCompatButton close_pochalist;
    ArrayList<Shop> pochaListPochas = new ArrayList<>();
    //포차리스트 구현
    public void PochaListView(ArrayList<Shop> pochas){
        pochaListPochas = pochas;
        pochalist_view = getActivity().findViewById(R.id.pocha_list); //리사이클러 뷰
        close_pochalist = getActivity().findViewById(R.id.close_pochalist);
        pochalist_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 이곳에서 애니메이션을 시작
                showPochaListAni(pochalist_view, close_pochalist);

                // 애니메이션을 시작한 후에는 리스너를 제거
                pochalist_view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        close_pochalist.setOnClickListener(close_pochalistL);

        PochaListAdapter pochaListAdapter = new PochaListAdapter(pochaListPochas, this, HomeFragment.this); // 어댑터
        pochalist_view.setAdapter(pochaListAdapter); //리사이클러뷰에 어댑터 장착

        pochalist_view.setLayoutManager(new LinearLayoutManager(getActivity())); //레이아웃 매니저 지정

    }

    // 포차리스트 아이템 리스너
    @Override
    public void onPochaListItemClick(View v, int position) {
        try{
            String className = "com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity";
            Class<?> activityClass = Class.forName(className);

            Intent intent = new Intent(v.getContext(), activityClass);
            intent.putExtra("shopInfo", stores.get(position));
            v.getContext().startActivity(intent);

        }catch (ClassNotFoundException e){
            Toast.makeText(v.getContext(), "클래스 찾을 수 없음: PochainfoActivity", Toast.LENGTH_SHORT).show();
        }
    }

    // 포차리스트 출력 시 애니메이션
    public void showPochaListAni(RecyclerView pochalist, AppCompatButton close){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // 시작 x 좌표
                0,                 // 종료 x 좌표
                pochalist.getHeight(),  // 시작 y 좌표
                0);                // 종료 y 좌표
        animate.setDuration(500);
        pochalist.startAnimation(animate);
        close.startAnimation(animate);
        pochalist.setVisibility(View.VISIBLE);
        close.setVisibility(View.VISIBLE);
    }

    // 닫기 버튼 클릭 시 애니메이션
    public void hidePochaListAni(RecyclerView pochalist, AppCompatButton close){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // 시작 x 좌표
                0,                 // 종료 x 좌표
                0,                 // 시작 y 좌표
                pochalist.getHeight()); // 종료 y 좌표
        animate.setDuration(500);
        pochalist.startAnimation(animate);
        pochalist.setVisibility(View.INVISIBLE);
        close.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener close_pochalistL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(pochalist_view != null && close_pochalist != null){
                hidePochaListAni(pochalist_view, close_pochalist);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("나만 볼거야", "onResume() 실행");
    }

    @Override
    public void onPause() {
        super.onPause(); Log.d("나만 볼거야", "onPause() 실행");
    }

    private ActivityResultLauncher<Intent> getSearchActivityResult;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //제보 완료시 하단바 - 홈으로 이동시키기 위해 추가함(홍서빈)
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView); // BottomNavigationView의 ID로 변경 필요
        if (bottomNavigationView != null) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_map); // 예시로 'navigation_home'을 HomeFragment에 해당하는 ID로 변경해야 합니다.
        }



        Log.d("나만 볼거야", "onCreate() 실행");

        introductionPopup();



        //위치를 반환하는 구현체인 FusedLocationSource 생성, locationSource를 초기화 하는 시점에 권한 허용여부를 확인한다(PermissionActivity의 onRequestPermissionResult())
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        getSearchActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // SearchActivity로부터 돌아올 때 어떤 결과 값을 받아올 수 있는 통로
                    if (result.getResultCode() == RESULT_OK){
                        // 서브 액티비티의 입력 값을 메인에서 받아서 텍스트뷰에 표시 ...!
                        basemap(); // 기본 지도 형태(이전에 생성되었던 지도 상의 이벤트 원상복구)

                        Location recentLocation = result.getData().getParcelableExtra("select_recent_location"); //최근검색 - 위치값
                        String recentAddress = result.getData().getStringExtra("select_recent_address"); //최근검색 - 주소값

                        Location autocompleteLocation = result.getData().getParcelableExtra("select_autocomplete_location"); //자동완성 - 위치값
                        String autocompleteAddress = result.getData().getStringExtra("select_autocomplete_address"); //자동완성 - 주소값

                        if(recentLocation != null){
                            Log.d("MainActivity", "받은 주소(최근검색어): " + recentLocation + recentAddress);
                            searchAdd.setText(recentAddress);
                            searchplace_recent(recentAddress, recentLocation);
                            recentLocation = null;
                        } else if(autocompleteLocation != null){
                            Log.d("MainActivity", "받은 주소(자동완성): " + autocompleteLocation + "(" + autocompleteAddress + ")");
                            searchAdd.setText(autocompleteAddress);
                            searchplaceAutocomplete(autocompleteLocation);
                            autocompleteLocation = null;
                        } else{
                            Toast.makeText(getActivity(), "주소값 받아오기 실패", Toast.LENGTH_SHORT).show();
                        }

                        //주소 검색창의 x버튼 리스너 구현
                        ImageButton search_close_btn = homeview.findViewById(R.id.search_close_btn);
                        search_close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(searchPlaceMarker != null){
                                    searchPlaceMarker.setMap(null);
                                    searchAdd.setText("");
                                    for(Marker marker : pochas){
                                        marker.setMap(null);
                                    }
                                }
                            }
                        });
                    }
                });

    }


    @Nullable  // null 체크유도, 경고를 통해 누락된 체크를 알려줄수 있음
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeview = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("나만 볼거야", "onCreateView() 실행");
        //지도 객체 생성하기 (xml에 있는 지도와 연결 후, 지도 출력)
        // MapFragment를 다른 프래그먼트 내에 배치할 경우 supportFragmentManager
        // 대신 childFragmentManager()를 사용해 MapFragment를 자식 프래그먼트로 두어야 함.
        // getSupportFragmentManager() -> getChildFragmentManager()로 변경
        FragmentManager fragmentManager = getChildFragmentManager();
        mapFragment = (com.naver.maps.map.MapFragment) fragmentManager.findFragmentById(R.id.map); //fragmentmanager를 통해 추가된 map이라는 프래그먼트를 찾아서 MapFragment변수에 할당
        if (mapFragment == null) {
            Log.d("onCreateView","mapFragment if 진입");
            com.naver.maps.map.MapFragment mapFragment = com.naver.maps.map.MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();

        }else{
            Log.d("onCreateView","mapFragment else 진입");
            //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
            mapFragment.getMapAsync(this);

            //나침반 버튼, 현재 위치 버튼 재설정
            mapFragment.getMapAsync(naverMap -> {
                CompassView compassViewView = homeview.findViewById(R.id.compass);
                compassViewView.setMap(naverMap);
                //LocationButtonView locationButtonView = homeview.findViewById(R.id.location_btn);
                //locationButtonView.setMap(naverMap);

            });
        }

        return homeview;
    } // onCreateView 끝

    // 앱 실행 시 팝업
    public void introductionPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.app_introduction_popup, null);
        builder.setView(customView);
        final AlertDialog dialog = builder.create();

        final TextView description = customView.findViewById(R.id.description);
        final TextView authmeeting = customView.findViewById(R.id.authmeeting);
        final ImageView auth = customView.findViewById(R.id.auth);
        final ImageView meet = customView.findViewById(R.id.meet);
        final Button end = customView.findViewById(R.id.end);

        final String[] descriptions = {getActivity().getResources().getString(R.string.description1), getActivity().getResources().getString(R.string.description2), getActivity().getResources().getString(R.string.description3)};
        final String[] authmeetings = {getActivity().getResources().getString(R.string.title1), getActivity().getResources().getString(R.string.title2), getActivity().getResources().getString(R.string.title3)};
        final int[] currentIndex = {0};

        Button cancel = customView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.dismiss(); }
        });

        Button next = customView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 배열에서 다음 텍스트를 가져와 설정
                if (currentIndex[0] < descriptions.length) {
                    currentIndex[0]++;
                    if (currentIndex[0] == 1) {
                        auth.setVisibility(VISIBLE);
                        authmeeting.setVisibility(VISIBLE);
                    } else if (currentIndex[0] == 2) {
                        auth.setVisibility(INVISIBLE);
                        meet.setVisibility(VISIBLE);
                        cancel.setVisibility(INVISIBLE);
                        next.setVisibility(INVISIBLE);
                        end.setVisibility(VISIBLE);
                    } else {
                        auth.setVisibility(View.GONE);
                        meet.setVisibility(View.GONE);
                        authmeeting.setVisibility(View.GONE);
                    }

                    description.setText(descriptions[currentIndex[0]]);
                    authmeeting.setText(authmeetings[currentIndex[0]]);
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.dismiss(); }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // request code와 권한 획득 여부 확인
        if(requestCode == PERMISSION_REQUEST_CODE){ // onMapReady()메서드에 요청한 권한 요청과 일치하는지 확인
            locationPermission();
            mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        }// onRequestPermissionsResult()메서드는 권한 요청 결과를 처리하고, 권한이 획득되었을 경우에만 NaverMap객체의 위치 추적 모드를 설정하는 역할을 함

    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d("나만 볼거야", "onStart() 실행");
        //주소 창 클릭 시 SearchActivity로 이동 후 검색 값 받아오기
        searchAdd = homeview.findViewById(R.id.searchAdd);
        searchAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeview.getContext(), SearchActivity.class);
                getSearchActivityResult.launch(intent); // startActivityForResult랑 동일한 기능
            }
        });

        // 현위치 버튼 -> 다른 액티비티 다녀오면 현위치 재검색 버튼 invisible
        AppCompatButton re_searchbtn = homeview.findViewById(R.id.re_searchbtn);
        re_searchbtn.setVisibility(INVISIBLE);

        // 포차리스트 invisible
        if(pochalist_view != null && close_pochalist != null){
            pochalist_view.setVisibility(INVISIBLE);
            close_pochalist.setVisibility(INVISIBLE);
        }

        locationPermission();
        loadStoreData();
    }


    // 위치 권한이 변경될 때 호출할 메소드
    private void locationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(mNaverMap != null ){
                mNaverMap.setLocationSource(locationSource);

                LocationButtonView locationButtonView = homeview.findViewById(R.id.location_btn);
                locationButtonView.setMap(mNaverMap);

                ImageButton location_btn_custom = homeview.findViewById(R.id.location_btn_custom);
                location_btn_custom.setVisibility(INVISIBLE);

            }
        } else if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if (mNaverMap != null) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None); // 위치 추적 모드 중지
            }

            LocationButtonView locationButtonView = homeview.findViewById(R.id.location_btn);
            locationButtonView.setMap(null);

            ImageButton location_btn_custom = homeview.findViewById(R.id.location_btn_custom);
            location_btn_custom.setVisibility(VISIBLE);
            location_btn_custom.setOnClickListener(locationPermissionPopup);
        }
    }


    ArrayList<Shop> stores;
    ArrayList<Marker> pochas;
    Marker currentClickedMarker; //현재 클릭한 마커를 추적하기 위한 변수
    ConstraintLayout pocha_info; //가게 정보 탭
    String itemName = null; //선택된 카테고리를 받아올 변수

    ArrayList<Shop> listStores = new ArrayList<>(); //현재 활성화된 가게만 리스트(->포차리스트로 보냄)
    // 초기화 시점 : 첫 화면, 카테고리 클릭 시, 인증&번개 버튼 클릭 시

    public void loadStoreData(){
        StoreData.initializeStores(new StoreData.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<Shop> getStore) {
                listStores = new ArrayList<>();
                if(getStore != null){
                    stores = getStore;
                    Log.d("MainActivity","받아온 가게 수 : " + stores.size());

                    // 기존 마커 초기화
                    if (pochas != null) {
                        for (Marker marker : pochas) {
                            marker.setMap(null);
                        }
                    }
                    pochas = new ArrayList<>(stores.size());

                    for(int i = 0; i < stores.size(); i++){
                        Marker pochas_marker = new Marker();
                        visibilityMarker(pochas_marker, stores.get(i));
                        pochas_marker.setPosition(new LatLng(stores.get(i).getLatitude(), stores.get(i).getLongitude()));

                        // 인증, 번개 버튼 상태에 따른 마커 표시
                        updateMarker(pochas_marker, stores.get(i));

                        // 초기 카테고리 상태에 따른 마커 표시
                        setMarkerByCategory(itemName, stores.get(i), pochas_marker);

                        pochas.add(pochas_marker);
                        int index = i;
                        // 마커 클릭 이벤트 처리
                        pochas.get(i).setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {

                                // 마커 클릭 시 크기 조절
                                if(currentClickedMarker != null){  //이전에 클릭한 마커가 존재하는지 확인
                                    currentClickedMarker.setWidth(80);
                                    currentClickedMarker.setHeight(80); //이전에 클릭한 마커가 존재하는 경우, 해당 마커의 크기를 원래대로 복구
                                }
                                pochas.get(index).setWidth(120);
                                pochas.get(index).setHeight(120);
                                pochas.get(index).setZIndex(1); // 클릭한 마커 우선순위 1
                                currentClickedMarker = pochas.get(index); //현재 클릭한 마커 저장

                                //클릭한 마커 기준으로 카메라 이동
                                CameraUpdate cameralocationUpdate = CameraUpdate.scrollTo(new LatLng(stores.get(index).getLatitude(), stores.get(index).getLongitude()));
                                mNaverMap.moveCamera(cameralocationUpdate);

                                settingPochainfo(index); // 하단에 가게 상세정보 탭 표시

                                return true;
                            }
                        });

                        if(pochas_marker.getMap() != null && pochas_marker.getIcon() != null){
                            listStores.add(stores.get(i));
                        }
                    }

                    // 카테고리 클릭 리스너
                    categoryListAdapter.setOnItemClickListener(new CategoryListAdapter.onItemClickListener() {
                        @Override
                        public void onItemClick(int position, String item) {
                            listStores = new ArrayList<>();
                            itemName = item;
                            for(int i = 0; i < pochas.size(); i++){
                                setMarkerByCategory(itemName, stores.get(i), pochas.get(i));

                                if(pochas.get(i).getMap() != null && pochas.get(i).getIcon() != null){
                                    listStores.add(stores.get(i));
                                }
                            }

                        }
                    });
                }
            }
        });
    }   // loadStoreData() 끝

    // 카테고리 클릭 상태에 따른 마커 출력
    public void setMarkerByCategory(String itemName, Shop store, Marker pochaMarker){
        // 마커 출력 기준
        // 1. 카테고리 클릭하지 않았을 경우 -> 전체 마커 표시
        // 2. "전체"카테고리를 클릭했을 경우 -> 전체 마커 표시
        // 3. "←"카테고리 클랙했을 경우 -> 전체 마커 표시
        if(itemName == null || Objects.equals(itemName, "전체") || Objects.equals(itemName, "←")){
            pochaMarker.setMap(mNaverMap);
        }
        else if((Objects.equals(itemName, "술") || Objects.equals(itemName, "술&전체")) && store.getCategory().startsWith("술")){
            // 4. "술"이나 "술&전체"클릭 할 경우 술에 대한 카테고리만 표시
            pochaMarker.setMap(mNaverMap);
        }
        else if(Objects.equals(itemName, store.getCategory())) {
            // 5. 클릭한 카테고리와 같은 카테고리를 갖고 있는 마커 -> 해당 마커 표시
            pochaMarker.setMap(mNaverMap);
        }
        else{
            pochaMarker.setMap(null);
        }
    }


    // 마커 클릭 시 하단에 가게 상세정보 탭 설정
    public void settingPochainfo(int index){
        // 클릭 시 하단에 가게정보 탭 생성
        // 프래그먼트에서 findViewByID 사용 https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=sweetsmell9&logNo=221344510749
        TextView pocha_name = homeview.findViewById(R.id.pocha_name);
        TextView pocha_category = homeview.findViewById(R.id.pocha_category);
        TextView pocha_add = homeview.findViewById(R.id.pocha_add);
        RatingBar pocha_rating = homeview.findViewById(R.id.pocha_rating);
        ImageView pocha_image = homeview.findViewById(R.id.pocha_image);
        pocha_name.setText(stores.get(index).getShopName());
        pocha_category.setText(stores.get(index).getCategory());
        pocha_add.setText(stores.get(index).getAddressName());
        pocha_rating.setRating(stores.get(index).getRating());
        downloadFireStorage(getActivity(), stores.get(index).getExteriorImagePath(),pocha_image);

        //heart이미지 설정
        ImageButton empty_heart = homeview.findViewById(R.id.pochainfo_emptyheart);
        ImageButton full_heart = homeview.findViewById(R.id.pochainfo_fullheart);
        isLikeShop(stores.get(index).getShopKey(), empty_heart, full_heart);
        //heart리스너 설정
        View.OnClickListener heartL = setHeartListener(getActivity(), stores.get(index).getShopKey(), empty_heart, full_heart);
        empty_heart.setOnClickListener(heartL);
        full_heart.setOnClickListener(heartL);

        //하단 탭 설정
        pocha_info = getView().findViewById(R.id.pocha_info);
        pocha_info.setVisibility(VISIBLE);
        // 하단 탭 클릭 시 가게 상세정보 페이지로 이동(기본키 함께 보냄)
        pocha_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PochainfoActivity.class);
                intent.putExtra("shopInfo", stores.get(index));
                v.getContext().startActivity(intent);
            }
        });

        //마커 클릭 시 하단 상세정보 탭이 생성되면서 인증,번개 버튼의 위치가 가려지지 않도록 위치 변경
        ButtonPosition(uiSettings);
    }
    // 좋아요 여부 확인 후 하트 모양 설정(초기 가시성)
    public void isLikeShop(String shopId, ImageButton emptyHeart, ImageButton fullHeart){
        LikeShopData.likeShopDataLoad(new LikeShopData.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<String> likeShopsId) {
                if(likeShopsId != null){
                    boolean isLiked = likeShopsId.contains(shopId);
                    if(isLiked){
                        emptyHeart.setVisibility(View.INVISIBLE);
                        fullHeart.setVisibility(View.VISIBLE);
                    }else{
                        emptyHeart.setVisibility(View.VISIBLE);
                        fullHeart.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    // 하트 모양 리스너
    public View.OnClickListener setHeartListener(Context context, String shopId, ImageButton empty_heart, ImageButton full_heart) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                LikeShopData likeShopData = new LikeShopData();

                if (viewId == R.id.pochainfo_emptyheart || viewId == R.id.imgbtn_pochainfo_notgoodButton) {
                    // 좋아요 목록에 추가
                    likeShopData.addLikedShop(shopId);
                    isLikeShop(shopId, empty_heart, full_heart);
                    Toast.makeText(context, "좋아요 목록에 추가되었습니다", Toast.LENGTH_SHORT).show();


                } else if(viewId == R.id.pochainfo_fullheart || viewId == R.id.imgbtn_pochainfo_goodButton){
                    // 좋아요 목록에서 삭제
                    likeShopData.removeLikedShop(shopId);
                    isLikeShop(shopId, empty_heart, full_heart);
                }
            }
        };
    }

    // 파이어베이스 스토리지에 저장된 이미지 다운로드
    public void downloadFireStorage(Context context, String ExteriorImagePath, ImageView pocha_image){

        if (ExteriorImagePath == null || ExteriorImagePath.isEmpty()) {
            // 경로가 유효하지 않을 때 예외 처리
            pocha_image.setImageResource(R.drawable.pocha); //기본 이미지
            Log.e("HomeFragment", "Invalid path");
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance(); //FirebaseStorage 인스턴스 얻기
        StorageReference storageRef = storage.getReference().child(ExteriorImagePath); // 이미지 경로
        pocha_image.setImageResource(R.drawable.loading); //imageview 초기화
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) { //uri : 위 이미지 파일에 대한 다운로드 uri
                String imageUrl = uri.toString(); //다운로드 url을 문자열로 변환
                Glide.with(context)
                        .load(imageUrl) // 이미지 다운로드 url
                        .error(R.drawable.error) // 이미지 로딩 오류 시 표시할 이미지
                        .into(pocha_image);
            }
        });

    }


    Marker searchPlaceMarker = new Marker(); // Geocoding - 검색 위치 마커

    public void searchplace_recent(String recentAddress, Location recentLocation) {
        if(recentLocation != null){
            if(Objects.equals(recentAddress, "No address")){
                Toast.makeText(getActivity(), "주소를 바르게 입력하세요", Toast.LENGTH_LONG).show();
                searchAdd.setText("");
                if(pochas != null){
                    for(Marker marker : pochas){
                        marker.setMap(null);
                    }
                }
            } else{
                // 받아온 location에서 위도, 경도 추출
                double latitude = recentLocation.getLatitude();
                double longitude = recentLocation.getLongitude();

                searchSuccess(latitude, longitude); // 검색 성공 - 카메라 이동, 마커 설정

                //StoreData에 위치값 보내기
                StoreData.addLocation(latitude, longitude, calculateRadius());
                loadStoreData(); //주소 검색 후 검색한 주소 기준으로 데이터 로드


            }
        }else{
            Toast.makeText(getActivity(), "주소검색 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            searchAdd.setText("");
        }

    }

    public void searchplaceAutocomplete(Location autocompleteLocation){
        if(autocompleteLocation != null){
            // 받아온 location에서 위도, 경도 추출
            double latitude = autocompleteLocation.getLatitude();
            double longitude = autocompleteLocation.getLongitude();

            searchSuccess(latitude, longitude); // 검색 성공 - 카메라 이동, 마커 설정

            //StoreData에 위치값 보내기
            StoreData.addLocation(latitude, longitude, calculateRadius());
            loadStoreData(); //주소 검색 후 검색한 주소 기준으로 데이터 로드

        }else{
            Toast.makeText(getActivity(), "주소검색 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            searchAdd.setText("");
        }
    }

    // 검색 성공 시 카메라 이동, 마커 설정
    public void searchSuccess(double latitude, double longitude){

        // 해당 위치로 카메라 이동, 줌 레벨 증가
        CameraUpdate cameralocationUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude));
        CameraUpdate camerazoomUpdate = CameraUpdate.zoomTo(14L);
        mNaverMap.moveCamera(cameralocationUpdate);
        mNaverMap.moveCamera(camerazoomUpdate);

        // 검색 마커 설정
        searchPlaceMarker.setMap(null); // 마커 초기화
        searchPlaceMarker.setPosition(new LatLng(latitude, longitude));
        //searchPlaceMarker.setZIndex(1); // 마커 겹칠 시 우선순위
        searchPlaceMarker.setWidth(70);
        searchPlaceMarker.setHeight(90);
        searchPlaceMarker.setIcon(MarkerIcons.GRAY);
        searchPlaceMarker.setMap(mNaverMap); // 해당 위치에 마커 설정
    } // searchSuccess() 끝


    // 지도 클릭 리스너 구현
    NaverMap.OnMapClickListener mapL = new NaverMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
            basemap();
        }
    };


    // 기본적인 지도 형태(마커 크기, 탭 원상복구용)
    public void basemap(){
        if(stores != null){
            for(int i = 0; i < stores.size(); i++){
                pochas.get(i).setWidth(80); //마커 사이즈 복구
                pochas.get(i).setHeight(80);
            }
        }
        if (pocha_info != null) {
            pocha_info.setVisibility(INVISIBLE); //가게 상세정보 탭 닫기
            ButtonPosition(uiSettings);
        }

    }

    // 인증, 번개 버튼 리스너
    View.OnClickListener authmeetingL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listStores = new ArrayList<>();

            int id = v.getId();
            if(id == R.id.authoff){
                auth = true;
            }
            else if(id == R.id.authon){
                auth = false;
            }
            else if(id == R.id.meetingoff){
                meeting = true;
            }
            else{
                meeting = false;
            }
            updateButtonVisibility();

            if(pochas != null){
                for(int i = 0; i < pochas.size(); i++){
                    updateMarker(pochas.get(i), stores.get(i));

                    if(pochas.get(i).getMap() != null && pochas.get(i).getIcon() != null){
                        listStores.add(stores.get(i));
                    }
                }
            }
        }
    };

    // 버튼 상태에 따라 버튼의 이미지를 설정하는 메서드
    public void updateButtonVisibility(){
        authoff = homeview.findViewById(R.id.authoff);
        meetingoff = getActivity().findViewById(R.id.meetingoff);
        authon = homeview.findViewById(R.id.authon);
        meetingon = getActivity().findViewById(R.id.meetingon);

        authoff.setVisibility(auth ? INVISIBLE : View.VISIBLE);
        authon.setVisibility(auth ? View.VISIBLE : INVISIBLE);
        meetingoff.setVisibility(meeting ? INVISIBLE : View.VISIBLE);
        meetingon.setVisibility(meeting ? View.VISIBLE : INVISIBLE);
    }

    //버튼 상태에 따라 표시되는 마커를 재설정하는 메서드
    public void updateMarker(Marker pochas_marker, Shop mainStores){
        if(auth && !meeting){
            if(mainStores.getVerified() == auth && mainStores.getHasMeeting() == meeting){
                pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.authon_meetingoff));
            }
            else if(mainStores.getVerified() == auth && mainStores.getHasMeeting()){
                pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.all_on));
            }
            else{
                pochas_marker.setIcon(null);
            }
        }
        else if(auth == true && meeting == true){
            if(mainStores.getVerified() == auth && mainStores.getHasMeeting() == meeting){
                pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.all_on));
            }
            else{
                pochas_marker.setIcon(null);
            }
        }
        else if(auth == false && meeting == true){
            if(mainStores.getVerified() == auth && mainStores.getHasMeeting() == meeting){
                pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.authoff_meetingon));
            }
            else if(mainStores.getVerified() == true && mainStores.getHasMeeting() == meeting){
                pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.all_on));
            }
            else{
                pochas_marker.setIcon(null);
            }
        }
        else{
            visibilityMarker(pochas_marker, mainStores);
        }
    }

    // 인증 off, 번개 off 상태일 때(기본 상태) 마커 설정 메서드
    public void visibilityMarker(Marker pochas_marker, Shop mainStores){
        if(mainStores.getVerified() == true && mainStores.getHasMeeting() == false){
            pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.authon_meetingoff));
        }
        else if(mainStores.getVerified() == true && mainStores.getHasMeeting() == true){
            pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.all_on));
        }
        else if(mainStores.getVerified() == false && mainStores.getHasMeeting() == true){
            pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.authoff_meetingon));
        }
        else if(mainStores.getVerified() == false && mainStores.getHasMeeting() == false) {
            pochas_marker.setIcon(OverlayImage.fromResource(R.drawable.all_off));
        }

    }

    public void ButtonPosition(UiSettings uiSettings) {
        LocationButtonView locationButtonView = getActivity().findViewById(R.id.location_btn);
        ImageButton location_btn_custom = getActivity().findViewById(R.id.location_btn_custom);

        if(pocha_info.getVisibility() == View.VISIBLE){
            animateBottomMargin(meetingoff, 420);
            animateBottomMargin(meetingon, 420);
            animateBottomMargin(locationButtonView, 480);
            animateBottomMargin(location_btn_custom, 480);
            uiSettings.setLogoMargin(30, 0, 0, 430);

        }else{
            // 각 뷰의 시작 값과 끝 값을 지정하고 애니메이션을 시작
            animateBottomMargin(meetingoff, 190);
            animateBottomMargin(meetingon, 190);
            animateBottomMargin(locationButtonView, 80);
            animateBottomMargin(location_btn_custom, 80);
            uiSettings.setLogoMargin(30, 0, 0, 30);
        }
    }

    //하단 탭 생성 시 현위치버튼, 인증&번개 버튼 위치 애니메이션
    private void animateBottomMargin(View view, int endValue) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        int startValue = layoutParams.bottomMargin;

        ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(150);  // 애니메이션 지속 시간을 500ms로 설정
        animator.setInterpolator(new LinearInterpolator());  // 애니메이션 속도를 일정하게 설정
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.bottomMargin = value;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.start();  // 애니메이션 시작
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("나만 볼거야", "onDestroy() 실행");
        if (mNaverMap != null) {
            mNaverMap.removeOnCameraChangeListener(cameraChangeListener); //리스너 해제(메모리 누수 방지)
        }
    }
}   // 끝