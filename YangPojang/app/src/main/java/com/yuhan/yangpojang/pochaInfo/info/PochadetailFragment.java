package com.yuhan.yangpojang.pochaInfo.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        TextView detailAddressTv= view.findViewById(R.id.tv_pochadetail_address);
        TextView detailPayTv = view.findViewById(R.id.tv_pochadetail_pay);
        TextView detailOpendayTv = view.findViewById(R.id.tv_pochadetail_openday);
        MapView mapView=  view.findViewById(R.id.map_pochadetail_location);

        // ▼ PochainfoActivity.java에서 전달한 데이터를 받는 코드
        Bundle bundle = getArguments();
        if (bundle != null) {
            Shop shop = (Shop) bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            String uid = bundle.getString("uid");



            if (shop != null) {
                // Address 설정
                String address = shop.getAddressName();

                double a = shop.getLatitude();
                double b = shop.getLongitude();
                Log.d("fuck", String.valueOf(a));
                Log.d("fuck", String.valueOf(b));

                if (address != null && !address.isEmpty()) {
                    detailAddressTv.setText(address);
                } else {
                    detailAddressTv.setText("...");
                }
                MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map_pochadetail_location);
                if (mapFragment == null) {
                    mapFragment = MapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().add(R.id.map_pochadetail_location, mapFragment).commit();
                }
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull NaverMap naverMap) {
                        LatLng location = new LatLng(a, b);
                        naverMap.moveCamera(CameraUpdate.scrollTo(location));

                        // Marker를 사용하여 마커 추가
                        com.naver.maps.map.overlay.Marker marker = new com.naver.maps.map.overlay.Marker();
                        marker.setPosition(location);
                        marker.setMap(naverMap);

                        // 마커 아이콘의 크기 설정
                        marker.setWidth(48);  // 마커의 너비
                        marker.setHeight(65);  // 마커의 높이
                        // 지도 UI 설정에서 확대/축소 컨트롤 숨기기
                        naverMap.getUiSettings().setZoomControlEnabled(false);
                    }
                });

                // 결제 방식 설정
                StringBuilder paymentMethods = new StringBuilder();
                if (shop.isPwayMobile()) {
                    paymentMethods.append("모바일, ");
                }
                if (shop.isPwayCard()) {
                    paymentMethods.append("카드, ");
                }
                if (shop.isPwayAccount()) {
                    paymentMethods.append("계좌 이체, ");
                }
                if (shop.isPwayCash()) {
                    paymentMethods.append("현금");
                }
               // Remove the trailing comma if appended
                if (paymentMethods.length() > 0) {
                    if (paymentMethods.charAt(paymentMethods.length() - 1) == ' ') {
                        paymentMethods.delete(paymentMethods.length() - 2, paymentMethods.length());
                    }
                    detailPayTv.setText(paymentMethods.toString());
                }  else {
//                    detailPayTv.setText("결제 정보가 없습니다");
                    detailPayTv.setText("...");
                }

                // 오픈 요일 설정
                StringBuilder openDays = new StringBuilder();
                if (shop.isOpenMon()) {
                    openDays.append("월 ");
                }
                if (shop.isOpenTue()) {
                    openDays.append("화 ");
                }
                if (shop.isOpenWed()) {
                    openDays.append("수 ");
                }
                if (shop.isOpenThu()) {
                    openDays.append("목 ");
                }
                if (shop.isOpenFri()) {
                    openDays.append("금 ");
                }
                if (shop.isOpenSat()) {
                    openDays.append("토 ");
                }
                if (shop.isOpenSun()) {
                    openDays.append("일");
                }
// Remove the trailing comma if appended
                if (openDays.length() > 0) {
                    detailOpendayTv.setText(openDays.toString());
                }  else {
                    detailOpendayTv.setText("...");
//                    detailOpendayTv.setText("오픈 정보가 없습니다");
                }
            } else {
                // 가게 정보가 비어있는 경우 보여지는 메세지들
//                detailAddressTv.setText("위치 정보가 없습니다");
//                detailPayTv.setText("결제 정보가 없습니다");
//                detailOpendayTv.setText("오픈 정보가 없습니다");
                detailAddressTv.setText("...");
                detailPayTv.setText("...");
                detailOpendayTv.setText("...");
            }
        }

        else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchDatail");
            return view;
        }

        return view;
    }
}
