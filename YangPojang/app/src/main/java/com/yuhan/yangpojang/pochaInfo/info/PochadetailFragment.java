package com.yuhan.yangpojang.pochaInfo.info;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.HomeFragment;
import com.yuhan.yangpojang.login.User;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.model.BtnPressedDTO;

import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;

public class PochadetailFragment extends Fragment {
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스
    private MutableLiveData<Shop> liveDataShop = new MutableLiveData<>();
    private PochaViewModel viewModel;

    private  TextView detailAddressTv;
    private TextView detailPayTv ;
    private TextView detailOpendayTv;


    private boolean hasPressedVerifyButton=false;
    private boolean hasPressedSingoButton=false;


    Button verifyBtn;
    ImageButton singoBtn;
    private  String useShopkey;

    private boolean userPressedVeriBtn;
    private Integer countVerified;


    private Integer useCountSingo;

    private TextView countSingoTv;
    private RatingBar ratingBar;



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
        detailAddressTv = view.findViewById(R.id.tv_pochadetail_address);
        detailPayTv = view.findViewById(R.id.tv_pochadetail_pay);
        detailOpendayTv = view.findViewById(R.id.tv_pochadetail_openday);
        Button updatebtn = view.findViewById(R.id.btn_pochadetail_pochaUpdate);
        verifyBtn = view.findViewById(R.id.btn_pochainfo_verify);
        singoBtn = view.findViewById(R.id.btn_pocha_singo);

        countSingoTv = view.findViewById(R.id.tv_count_singo);

        ratingBar= view.findViewById(R.id.rtb_pochadetail_rating);
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
            shop = (Shop) bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            uid = bundle.getString("uid");
            Log.d("fffjdsffffffudiyid", uid);
            String shopkey = shop.getPrimaryKey();  // getShopkey로는 안얻어짐 주의


            if (shop != null) {
                // Address 설정
                String address = shop.getAddressName();
                double latitude = shop.getLatitude();
                double longitude = shop.getLongitude();
                useShopkey = shop.getPrimaryKey();

                //Log.d("ffff;rfdae", useShopkey);
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

            updatebtn.setOnClickListener(new View.OnClickListener() { // 정보 수정 버튼을 누르면 실행
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

        } else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchDatail");
            return view;
        }

        // 인증 버튼이 눌리면
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bbbbcanigiveshop1111", String.valueOf(shop));

                checkVerifiedBool(shop);
                DatabaseReference verifyPressedRef = FirebaseDatabase.getInstance().getReference()
                        .child("userPressBtn")
                        .child(uid)
                        .child("verifiedPressed")
                        .child(useShopkey);

                verifyPressedRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("bbbbbbruno1", String.valueOf(verifyPressedRef));

                        Boolean hasPressed = snapshot.getValue(Boolean.class);
                        Log.d("bbbbbbruno2", String.valueOf(hasPressed));
                        if (hasPressed == null || !hasPressed)
                        {
                            Log.d("bbbbbbruno33", String.valueOf(hasPressed));

                            verifyPressedRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(requireContext(), "가게가 인증완료 되었습니다", Toast.LENGTH_SHORT).show();

                                        // Increment the countVerified field in the shop table
                                        DatabaseReference shopReference = FirebaseDatabase.getInstance().getReference()
                                                .child("shops")
                                                .child(useShopkey)
                                                .child("countVerified");
                                        shopReference.runTransaction(new Transaction.Handler()
                                        {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData currentData)
                                            {
                                                countVerified = currentData.getValue(Integer.class);
                                                if (countVerified == null)
                                                {
                                                    countVerified = 0;
                                                }
                                                countVerified++;
                                                currentData.setValue(countVerified);
                                                return Transaction.success(currentData);

                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                if (error != null) {
                                                    Log.e("Firebase", "Transaction failed: " + error.toException());
                                                }
                                                else
                                                {
                                                    countVerified = currentData.getValue(Integer.class);
                                                    Log.d("bbbbbbbbcountverified", String.valueOf(countVerified));
                                                    Log.d("bbbbcanigiveshop", String.valueOf(shop));
                                                    checkVerifiedBool(shop);
                                                }
                                            }
                                        });
                                    }

                                    else
                                    {
                                        Toast.makeText(requireContext(), "가게 인증이 실패하였습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        //haspressed 가 널이 아닐때 (이미 눌린 상태일때)
                        else
                        {
                            // The user has already pressed the verification button for this shop
                            Toast.makeText(requireContext(), "이미 인증한 가게입니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Database error: " + error.getMessage());
                    }
                });
            }
        });

        singoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference singoPressedRef = FirebaseDatabase.getInstance().getReference()
                        .child("userPressBtn")
                        .child(uid)
                        .child("singoPressed")
                        .child(useShopkey);

                singoPressedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean hasPressed = snapshot.getValue(Boolean.class);
                        if (hasPressed == null || !hasPressed) {
                            // The user has not pressed the report button for this shop yet
                            // Proceed with the report process
                            singoPressedRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Increment the countSingo field in the shop table
                                        DatabaseReference shopReference = FirebaseDatabase.getInstance().getReference()
                                                .child("shops")
                                                .child(useShopkey)
                                                .child("countSingo");
                                        shopReference.runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                Integer countSingo = currentData.getValue(Integer.class);
                                                if (countSingo == null) {
                                                    countSingo = 0;
                                                }
                                                countSingo++;
                                                currentData.setValue(countSingo);
                                                return Transaction.success(currentData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                if (error != null) {
                                                    Log.e("Firebase", "Transaction failed: " + error.toException());
                                                }
                                            }
                                        });
                                        Toast.makeText(requireContext(), "가게가 신고 되었습니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "가게 신고가 실패하였습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // The user has already pressed the report button for this shop
                            Toast.makeText(requireContext(), "이미 신고한 가게입니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Database error: " + error.getMessage());
                    }
                });
            }
        });


        return view;
    }

    private void checkVerifiedBool(Shop shop) {
        DatabaseReference countVerifiedRef = FirebaseDatabase.getInstance().getReference()
                .child("shops")
                .child(useShopkey)
                .child("countVerified");

        countVerifiedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer newCountVerified = snapshot.getValue(Integer.class);
                updateVerifiedStatus(shop, newCountVerified);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }

    private void updateVerifiedStatus(Shop shop, Integer countVerified) {
        Log.d("bbbrudno?f", "A" + String.valueOf(countVerified));

        if (countVerified != null && countVerified >= 3) {
            // If countVerified is not null and greater than or equal to 3, set verified to true
            shop.setVerified(true);
            updateVerifiedStatusInDatabase(true);
        } else {
            // If countVerified is less than 3, set verified to false
            shop.setVerified(false);
            updateVerifiedStatusInDatabase(false);
        }

        updateVerifiedStatusInDatabase(shop.getVerified());
    }
    private void updateVerifiedStatusInDatabase(boolean newStatus) {
        DatabaseReference shopReference = FirebaseDatabase.getInstance().getReference()
                .child("shops")
                .child(useShopkey)
                .child("verified");
        shopReference.setValue(newStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Shop verification status updated successfully");
                        } else {
                            Log.e("Firebase", "Failed to update shop verification status");
                        }
                    }
                });
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
        int countSingoUi= shop.getCountSingo();
        Log.d("bbbbbrudno", String.valueOf(countSingoUi));
        if (countSingoUi<3)
        {
            countSingoTv.setText(String.valueOf(countSingoUi));
            countSingoTv.setTextColor(Color.parseColor("#000000"));

        }
        else
        {
            countSingoTv.setText(String.valueOf(countSingoUi));
            countSingoTv.setTextColor(Color.parseColor("#EC1818"));
        }

        float shopRating = shop.getRating(); // Replace with the actual field name
        ratingBar.setRating(shopRating);



    }
}

