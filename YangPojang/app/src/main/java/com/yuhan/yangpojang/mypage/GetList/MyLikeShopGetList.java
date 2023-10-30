package com.yuhan.yangpojang.mypage.GetList;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;

import java.util.ArrayList;

public class MyLikeShopGetList {
    private String UID;

    private ArrayList<MyLikeShopModel> shopDatas = new ArrayList<>();   // 가게 정보 전송용
    private ArrayList bflist = new ArrayList<>(); // 가게 리스트
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public MyLikeShopGetList() {
        Log.d("테스트like", "MyLikeShopGetList: 진입");
    }

    public void GetMyLikeShopList(String UID, final dataLoadedCallback callback)
    {
        this.UID = UID;     //UID 연결

        //DB테이블 연결
        databaseReference = firebaseDatabase.getReference("likeShop/"+UID);
        Log.d(TAG, "GetMyLikeShopList: 테스트like 값 likeShop/" + UID );

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {

                    // 해당 UID의 like가게 리스트 저장
                    bflist.add(snap.getKey());
                    Log.d(TAG, "onDataChange: bflist");
                }

                // bflist에 있는 가게 정보를 가져오기 위한 루프
                for (Object shopKey : bflist) {
                    DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops").child(shopKey.toString());
                    shopRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot shopSnapshot) {
                            if (shopSnapshot.exists()) {
                                Log.d("테스트like", "onDataChange: 2");
                                // 가게 정보 불러오기
                                String category = shopSnapshot.child("category").getValue(String.class);
                                String addressName = shopSnapshot.child("addressName").getValue(String.class);
                                String shopName = shopSnapshot.child("shopName").getValue(String.class);

                                // MyLikeShopModel 객체 생성 및 값 설정
                                MyLikeShopModel shop = new MyLikeShopModel();
                                shop.setCategory(category);
                                shop.setAddressName(addressName);
                                shop.setShopName(shopName);

                                shopDatas.add(shop); // 가져온 가게 정보를 likeShops 리스트에 추가
                                Log.d("테스트like", "Category: " + category);
                                Log.d("테스트like", "Address Name: " + addressName);
                                Log.d("테스트like", "Shop Name: " + shopName);


                                if(bflist.size() == shopDatas.size()){
                                    callback.onDataLoaded(shopDatas);
                                }
                                Log.d("테스트like", "shop size-----: " + shopDatas.size());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 에러 처리 부분
                        }
                    });


                }


                Log.d("테스트like bf", String.valueOf(bflist));
                
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리 부분
            }


        });



        //return shopDatas;
    }
    public interface dataLoadedCallback{
        void onDataLoaded(ArrayList<MyLikeShopModel> shopDatas);

    }

}


