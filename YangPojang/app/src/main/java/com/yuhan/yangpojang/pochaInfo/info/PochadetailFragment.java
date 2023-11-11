package com.yuhan.yangpojang.pochaInfo.info;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;

import org.slf4j.Marker;

public class PochadetailFragment extends Fragment {
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스
    private MutableLiveData<Shop> liveDataShop = new MutableLiveData<>();
    private PochaViewModel viewModel;

    private  TextView detailAddressTv;
    private TextView detailPayTv ;
    private TextView detailOpendayTv;


    // ▼ 인터페이스 객체 초기화 코드
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentReloadListener){
            onFrgReloadListener = (OnFragmentReloadListener) context;   // 초기화
        }else {
            // 에러 처리
            throw new RuntimeException(context.toString() + "must implement OnFragmentReloadListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochadetail, container, false);
        detailAddressTv= view.findViewById(R.id.tv_pochadetail_address);
        detailPayTv = view.findViewById(R.id.tv_pochadetail_pay);
        detailOpendayTv = view.findViewById(R.id.tv_pochadetail_openday);
        Button updatebtn = view.findViewById(R.id.btn_pochadetail_pochaUpdate);
        DatabaseReference shopReference; // Firebase Database reference

        viewModel = new ViewModelProvider(requireActivity()).get(PochaViewModel.class);

        viewModel.getShopLiveData().observe(getViewLifecycleOwner(), shop -> {
            // UI 업데이트를 위해 변경된 shop 데이터 사용
            if (shop != null) {
                updateUI(shop);
            }
        });
        // ▼ PochainfoActivity.java에서 전달한 데이터를 받는 코드
        Bundle bundle = getArguments();
        if (bundle != null) {
            Shop shop = (Shop) bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            String uid = bundle.getString("uid");
            String shopkey=shop.getPrimaryKey();  // getShopkey로는 안얻어짐 주의

            shopReference = FirebaseDatabase.getInstance().getReference().child("shops");
            // Fetch the shop key
            Log.d("abc", String.valueOf(shopReference));


            if (shop != null) {
                // Address 설정
                String address = shop.getAddressName();
                double latitude = shop.getLatitude();
                double longitude = shop.getLongitude();

                if (address != null && !address.isEmpty()) { //주소가 비어있지 않으면
                    detailAddressTv.setText(address); //주소 설정
                } else {
                    detailAddressTv.setText("...");
                }
                MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_pochadetail_location); // 가게 위치 지도로 띄우기
                if (mapFragment == null) {
                    mapFragment = MapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().add(R.id.map_pochadetail_location, mapFragment).commit();
                }
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull NaverMap naverMap) {
                        LatLng location = new LatLng(latitude, longitude);
                        naverMap.moveCamera(CameraUpdate.scrollTo(location));

                        // Marker를 사용하여 마커 추가
                        com.naver.maps.map.overlay.Marker marker = new com.naver.maps.map.overlay.Marker();
                        marker.setPosition(location);
                        marker.setMap(naverMap);

                        // 마커 아이콘의 크기 설정
                        marker.setWidth(48);  // 마커의 너비
                        marker.setHeight(65);  // 마커의 높이
                        naverMap.getUiSettings().setZoomControlEnabled(false); // 지도 UI 설정에서 확대/축소 컨트롤 숨기기
                    }
                });


            } else {
                // 가게 정보가 비어있는 경우 보여지는 메세지들
                detailAddressTv.setText("...");
                detailPayTv.setText("...");
                detailOpendayTv.setText("...");
            }

            updatebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Code to navigate to the update form activity
                    Intent intent = new Intent(getActivity(), PochainfoUpdate.class); // Replace UpdateFormActivity with your actual activity class
                    intent.putExtra("shopInfo", shop);
                    intent.putExtra("shopKey", shopkey);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            });
        }

        else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchDatail");
            return view;
        }

        return view;
    }
    private void  updateUI(Shop shop) {

        // 결제 방식
        // 설정
        StringBuilder paymentMethods = new StringBuilder();
        if (shop.isPwayMobile()) { paymentMethods.append("모바일, "); }
        if (shop.isPwayCard()) { paymentMethods.append("카드, "); }
        if (shop.isPwayAccount()) { paymentMethods.append("계좌 이체, "); }
        if (shop.isPwayCash()) { paymentMethods.append("현금"); }
        // 어색한 , 없애기
        if (paymentMethods.length() > 0) {
            if (paymentMethods.charAt(paymentMethods.length() - 1) == ' ') {
                paymentMethods.delete(paymentMethods.length() - 2, paymentMethods.length());
            }
            detailPayTv.setText(paymentMethods.toString());
        }  else {
            detailPayTv.setText("...");
        }

        // 오픈 요일 설정
        StringBuilder openDays = new StringBuilder();
        if (shop.isOpenMon()) { openDays.append("월 "); }
        if (shop.isOpenTue()) { openDays.append("화 "); }
        if (shop.isOpenWed()) { openDays.append("수 "); }
        if (shop.isOpenThu()) { openDays.append("목 "); }
        if (shop.isOpenFri()) { openDays.append("금 "); }
        if (shop.isOpenSat()) { openDays.append("토 "); }
        if (shop.isOpenSun()) { openDays.append("일");  }
        if (openDays.length() > 0) {   // 요일이 0개가 아니면 선택된 녀석으로 setText

            detailOpendayTv.setText(openDays.toString());
        }  else {  // 요일 선택된게 0이면 ...으로 setText
            detailOpendayTv.setText("...");
        }

    }
}

