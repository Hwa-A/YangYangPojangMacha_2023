package com.yuhan.yangpojang.fragment;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.naver.maps.map.widget.CompassView;
import com.naver.maps.map.widget.LocationButtonView;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.home.PochaListAdapter;
import com.yuhan.yangpojang.home.SearchActivity;
import com.yuhan.yangpojang.model.Store;
import com.yuhan.yangpojang.model.StoreData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//https://navermaps.github.io/android-map-sdk/guide-ko/4-1.html
//https://asong-study-record.tistory.com/69

public class HomeFragment extends Fragment implements OnMapReadyCallback, Overlay.OnClickListener {

    //OnMapReadyCallback : 지도가 준비되었을 때 호출되는 콜백을 처리, onMapReady()메서드 구현해야 함

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

    /* onMapReady()메서드 - 지도가 준비되었을 때 호출되며, NaverMap 객체에 위치 소스를 설정하고 권한을 확인하는 작업 수행
                         - 지도 초기화 및 사용자 정의 작업 수행, 지도가 초기화되고 사용 가능한 상태일 때 호출되는 콜백 */
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
        mNaverMap = naverMap;
        mNaverMap.setLocationSource(locationSource); // NaverMap객체에 위치 소스를 지정 - 현재 위치 사용
        // 권한 확인, onRequestPermissionResult 콜백 메서드 호출 - 앱에서 위치 권한을 얻기 위해 권한 요청 대화상자를 표시하는 역할
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        }
        // 권한 부여가 되지 않은 경우 권한 부여 메세지 생성
        else
        {
            Toast.makeText(getActivity(), "설정> 애플리케이션 > YangPojag > 권한에서 지도 권한을 부여하세요", Toast.LENGTH_SHORT).show();
        }

        //UiSettings 클래스 - 지도 컨트롤 객체 관리
        uiSettings = mNaverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);// 현위치 버튼
        uiSettings.setScaleBarEnabled(false); // 축적바
        uiSettings.setCompassEnabled(false); //나침반
        uiSettings.setZoomControlEnabled(false); // 줌 버튼


        //앱 시작 시 줌레벨 설정
        /*double mapLatitude = 0; //초기 위치 변수 초기화
        double mapLongitude = 0;
        // 마지막으로 알려진 위치 정보 가져오기
        Location currentLocation = locationSource.getLastLocation(); //Location 클래스 - 지도와 관련된 위치 정보를 나타내기 위한 클래스
        if(currentLocation != null){
            mapLatitude = currentLocation.getLatitude(); //초기 위치 설정
            mapLongitude = currentLocation.getLongitude();
        }*/
        // 카메라 줌레벨 설정
        //mNaverMap.setCameraPosition(new CameraPosition(new LatLng(0, 0), 14)); //기본값=14;


        // 맵 시작 시 현재 위치를 기준으로 데이터 로드
        loadStoreData();



        //지도 클릭 리스너 설정
        mNaverMap.setOnMapClickListener(mapL);

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
                if (stores != null && !stores.isEmpty()) {
                    ArrayList<Store> pochas = new ArrayList<>(stores);

                    PochaListView(pochas);
                } else {
                    Toast.makeText(getActivity(), "조건에 맞는 업체가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    RecyclerView pochalist_view;
    //포차리스트 구현
    public void PochaListView(ArrayList<Store> pochas){
        pochalist_view = getActivity().findViewById(R.id.pocha_list); //리사이클러 뷰
        pochalist_view.setVisibility(View.VISIBLE);
        PochaListAdapter pochaListAdapter = new PochaListAdapter(pochas); // 어댑터
        pochalist_view.setAdapter(pochaListAdapter); //리사이클러뷰에 어댑터 장착

        pochalist_view.setLayoutManager(new LinearLayoutManager(getActivity())); //레이아웃 매니저 지정

    }

    private ActivityResultLauncher<Intent> getSearchActivityResult;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //위치를 반환하는 구현체인 FusedLocationSource 생성
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        getSearchActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // SearchActivity로부터 돌아올 때 어떤 결과 값을 받아올 수 있는 통로
                    if (result.getResultCode() == RESULT_OK){
                        // 서브 액티비티의 입력 값을 메인에서 받아서 텍스트뷰에 표시 ...!
                        basemap(); // 기본 지도 형태(이전에 생성되었던 지도 상의 이벤트 원상복구)

                        String select_recent_address;
                        String select_recent_name;
                        Location select_autocomplete;

                        select_recent_name = result.getData().getStringExtra("select_recent_name");
                        select_recent_address = result.getData().getStringExtra("select_recent_address");

                        select_autocomplete = result.getData().getParcelableExtra("select_autocomplete_location");
                        String select_autocomplete_address = result.getData().getStringExtra("select_autocomplete_address");

                        if(select_recent_name != null){
                            Log.d("MainActivity", "받은 주소(최근검색어): " + select_recent_name + select_recent_address);
                            searchAdd.setText(searchplace_recent(select_recent_address, select_recent_name));
                            select_recent_name = null;
                        }
                        else if(select_autocomplete != null){
                            Log.d("MainActivity", "받은 주소(자동완성): " + select_autocomplete + "(" + select_autocomplete_address + ")");
                            searchAdd.setText(select_autocomplete_address);
                            searchplace_autocomplete(select_autocomplete);
                            select_autocomplete = null;
                        }
                        else{
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

//            //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
//            mapFragment.getMapAsync(this);
//
//            //나침반 버튼, 현재 위치 버튼 재설정
//            mapFragment.getMapAsync(naverMap -> {
//                CompassView compassViewView = homeview.findViewById(R.id.compass);
//                compassViewView.setMap(naverMap);
//                LocationButtonView locationButtonView = homeview.findViewById(R.id.location_btn);
//                locationButtonView.setMap(naverMap);
//            });

        }else{
            Log.d("onCreateView","mapFragment else 진입");
            //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
            mapFragment.getMapAsync(this);

            //나침반 버튼, 현재 위치 버튼 재설정
            mapFragment.getMapAsync(naverMap -> {
                CompassView compassViewView = homeview.findViewById(R.id.compass);
                compassViewView.setMap(naverMap);
                LocationButtonView locationButtonView = homeview.findViewById(R.id.location_btn);
                locationButtonView.setMap(naverMap);
            });
        }
        return homeview;
    } // onCreateView 끝

    @Override
    public void onStart() {
        super.onStart();

        //위치를 반환하는 구현체인 FusedLocationSource 생성
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        //주소 창 클릭 시 SearchActivity로 이동 후 검색 값 받아오기
        searchAdd = homeview.findViewById(R.id.searchAdd);
        searchAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeview.getContext(), SearchActivity.class);
                getSearchActivityResult.launch(intent); // startActivityForResult랑 동일한 기능
            }
        });


    }

    Marker searchPlaceMarker = new Marker(); // Geocoding - 검색 위치 마커

    ArrayList<Store> stores;
    Marker[] pochas;
    Marker currentClickedMarker; //현재 클릭한 마커를 추적하기 위한 변수
    ConstraintLayout pocha_info; //가게 정보 탭

    public void loadStoreData(){
        StoreData.initializeStores(new StoreData.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<Store> getStore) {
                if(getStore != null){
                    stores = getStore;
                    Log.d("MainActivity","받아온 가게 수 : " + stores.size());

                    // 기존 마커 초기화
                    if (pochas != null) {
                        for (Marker marker : pochas) {
                            marker.setMap(null);
                        }
                    }
                    pochas = new Marker[stores.size()];

                    for(int i = 0; i < stores.size(); i++){
                        pochas[i] = new Marker();
                        pochas[i].setIcon(OverlayImage.fromResource(R.drawable.all_off));
                        pochas[i].setPosition(new LatLng(stores.get(i).getLatitude(), stores.get(i).getLongitude()));
                        pochas[i].setMap(mNaverMap);
                        int index = i;
                        // 마커 클릭 이벤트 처리
                        pochas[i].setOnClickListener(new Overlay.OnClickListener() {
                            @Override
                            public boolean onClick(@NonNull Overlay overlay) {
                                // 마커 클릭 시 크기 조절
                                if(currentClickedMarker != null){  //이전에 클릭한 마커가 존재하는지 확인
                                    currentClickedMarker.setWidth(80);
                                    currentClickedMarker.setHeight(80); //이전에 클릭한 마커가 존재하는 경우, 해당 마커의 크기를 원래대로 복구
                                }
                                pochas[index].setWidth(120);
                                pochas[index].setHeight(120);
                                pochas[index].setZIndex(1); // 클릭한 마커 우선순위 1
                                currentClickedMarker = pochas[index]; //현재 클릭한 마커 저장

                                //클릭한 마커 기준으로 카메라 이동
                                CameraUpdate cameralocationUpdate = CameraUpdate.scrollTo(new LatLng(stores.get(index).getLatitude(), stores.get(index).getLongitude()));
                                mNaverMap.moveCamera(cameralocationUpdate);

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

                                pocha_info = getView().findViewById(R.id.pocha_info);
                                pocha_info.setVisibility(VISIBLE);

                                //마커 클릭 시 하단 상세정보 탭이 생성되면서 인증,번개 버튼의 위치가 가려지지 않도록 위치 변경
                                ButtonPosition(uiSettings);

                                return true;
                            }
                        });
                    }
                }
            }
        });
    }   // loadStoreData() 끝

    // 파이어베이스 스토리지에 저장된 이미지 다운로드
    public void downloadFireStorage(Context context, String ExteriorImagePath, ImageView pocha_image){

        if (ExteriorImagePath == null || ExteriorImagePath.isEmpty()) {
            // 경로가 유효하지 않을 때 예외 처리
            pocha_image.setImageResource(R.drawable.pocha);
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


    // Geocoder : 텍스트로 입력된 주소의 경도, 위도 추출
    public String searchplace_recent(String select_recent_address, String select_recent_name) {

        double latitude = 0, longitude = 0;
        List<Address> address = null; // 주소 정보를 담을 리스트
        searchPlaceMarker.setMap(null); // 마커 초기화
        Geocoder geocoder = new Geocoder(getActivity(), new Locale("ko", "KR"));
        String fullAddress = null;
        String fullAddress1 = null;

        if(Objects.equals(select_recent_address, "No address")){ //주소가 No address일 경우
            Toast.makeText(getActivity(), "주소를 바르게 입력하세요", Toast.LENGTH_LONG).show();
            searchAdd.setText("");
            if(pochas != null){
                for(Marker marker : pochas){
                    marker.setMap(null);
                }
            }

        }else {

            try {
                if (select_recent_name != null && !select_recent_name.isEmpty()) {
                    address = geocoder.getFromLocationName(select_recent_name, 1);
                }

                if (address != null && address.size() > 0) {
                    // name으로 검색 성공한 경우
                    latitude = address.get(0).getLatitude();
                    longitude = address.get(0).getLongitude();
                    fullAddress = address.get(0).getAddressLine(0);
                    fullAddress1 = fullAddress.replace("대한민국", "");
                } else {

                    if (select_recent_address != null && !select_recent_address.isEmpty()) {
                        address = geocoder.getFromLocationName(select_recent_address, 1);
                    }

                    if (address != null && address.size() > 0) {
                        latitude = address.get(0).getLatitude();
                        longitude = address.get(0).getLongitude();
                        fullAddress = address.get(0).getAddressLine(0);
                        fullAddress1 = fullAddress.replace("대한민국", "");
                    } else {
                        // 두 번의 검색 모두 실패한 경우
                        Toast.makeText(getActivity(), "주소를 바르게 입력하세요", Toast.LENGTH_LONG).show();
                        searchAdd.setText("");
                    }
                }

                // 주소 검색 성공할 경우
                if (latitude != 0 && longitude != 0) {
                    //StoreData에 위치값 보내기
                    StoreData mainStoreData = new StoreData();
                    mainStoreData.addLocation(latitude, longitude, 1500);
                    loadStoreData(); //주소 검색 후 검색한 주소 기준으로 데이터 로드

                    searchSuccess(latitude, longitude);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return fullAddress1;
    } // searchplace_recent() 끝

    public void searchplace_autocomplete(Location select_autocomplete){
        if(select_autocomplete != null){
            // 받아온 location에서 위도, 경도 추출
            double latitude = select_autocomplete.getLatitude();
            double longitude = select_autocomplete.getLongitude();

            //StoreData에 위치값 보내기
            StoreData mainStoreData = new StoreData();
            mainStoreData.addLocation(latitude, longitude, 1500);
            loadStoreData(); //주소 검색 후 검색한 주소 기준으로 데이터 로드

            searchSuccess(latitude, longitude); // 검색 성공 - 카메라 이동, 마커 설정

        }else{
            Toast.makeText(getActivity(), "주소검색 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            searchAdd.setText("");
        }
    } // searchplace_autocomplete() 끝

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
                pochas[i].setWidth(80); //마커 사이즈 복구
                pochas[i].setHeight(80);
            }
        }
        if (pocha_info != null) {
            pocha_info.setVisibility(INVISIBLE); //가게 상세정보 탭 닫기
            ButtonPosition(uiSettings);
        }
        if(pochalist_view != null)
            pochalist_view.setVisibility(INVISIBLE); //포차리스트 닫기
    }

    //Overlay.OnClickListener : 네이버 지도 API에서 제동하는 인터페이스로, 오버레이(지도 위에 그려지는 그래픽 요소) 객체를 클릭했을 때 발생하는 이벤트 처리하는 메소드 정의
    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        if(overlay instanceof LocationOverlay){
            Toast.makeText(getActivity(), "설정> 애플리케이션 > YangPojag > 권한에서 지도 권한을 부여하세요", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // request code와 권한 획득 여부 확인
        if(requestCode == PERMISSION_REQUEST_CODE){ // onMapReady()메서드에 요청한 권한 요청과 일치하는지 확인
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow); //지도에서 현재 위치를 추적하여 따라가는 모드를 의미
            }
        }// onRequestPermissionsResult()메서드는 권한 요청 결과를 처리하고, 권한이 획득되었을 경우에만 NaverMap객체의 위치 추적 모드를 설정하는 역할을 함

    }

    // 인증, 번개 버튼 리스너
    View.OnClickListener authmeetingL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
            updateMarker();

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
    public void updateMarker(){
        if(stores != null){
            if(auth == true && meeting == false){
                for(int i = 0; i < stores.size(); i++){
                    if(stores.get(i).getVerified() == auth && stores.get(i).getHasMeeting() == meeting){
                        pochas[i].setIcon(OverlayImage.fromResource(R.drawable.authon_meetingoff));
                    }
                    else{
                        pochas[i].setIcon(null);
                    }
                }
            }
            else if(auth == true && meeting == true){
                for(int i = 0; i < stores.size(); i++){
                    if(stores.get(i).getVerified() == auth && stores.get(i).getHasMeeting() == meeting){
                        pochas[i].setIcon(OverlayImage.fromResource(R.drawable.all_on));
                    }
                    else{
                        pochas[i].setIcon(null);
                    }
                }
            }
            else if(auth == false && meeting == true){
                for(int i = 0; i < stores.size(); i++){
                    if(stores.get(i).getVerified() == auth && stores.get(i).getHasMeeting() == meeting){
                        pochas[i].setIcon(OverlayImage.fromResource(R.drawable.authoff_meetingon));
                    }
                    else{
                        pochas[i].setIcon(null);
                    }
                }
            }
            else{
                for(int i = 0; i < stores.size(); i++){
                    pochas[i].setIcon(OverlayImage.fromResource(R.drawable.all_off));

                }
            }
        }

    }



    public void ButtonPosition(UiSettings uiSettings) {
        LocationButtonView locationButtonView = getActivity().findViewById(R.id.location_btn);
        if(pocha_info.getVisibility() == View.VISIBLE){
            ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) meetingoff.getLayoutParams();
            layoutParams1.bottomMargin = 350;
            meetingoff.setLayoutParams(layoutParams1);

            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) meetingon.getLayoutParams();
            layoutParams2.bottomMargin = 350;
            meetingon.setLayoutParams(layoutParams2);


            ConstraintLayout.LayoutParams layoutParams3 = (ConstraintLayout.LayoutParams) locationButtonView.getLayoutParams();
            layoutParams3.bottomMargin = 480;
            locationButtonView.setLayoutParams(layoutParams3);

            uiSettings.setLogoMargin(30, 0, 0, 430);

        }else{
            ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) meetingoff.getLayoutParams();
            layoutParams1.bottomMargin = 70;
            meetingoff.setLayoutParams(layoutParams1);

            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) meetingon.getLayoutParams();
            layoutParams2.bottomMargin = 70;
            meetingon.setLayoutParams(layoutParams2);

            ConstraintLayout.LayoutParams layoutParams3 = (ConstraintLayout.LayoutParams) locationButtonView.getLayoutParams();
            layoutParams3.bottomMargin = 80;
            locationButtonView.setLayoutParams(layoutParams3);

            uiSettings.setLogoMargin(30, 0, 0, 30);
        }
    }



}   // 끝