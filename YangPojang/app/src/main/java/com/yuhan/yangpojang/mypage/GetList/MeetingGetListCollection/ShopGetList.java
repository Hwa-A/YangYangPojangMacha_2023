package com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MyMeetingModel;

import java.util.ArrayList;

public class
ShopGetList {
    private static DatabaseReference databaseReference;
    static ArrayList<Shop> shops = new ArrayList<>();
    static ArrayList<String> shopIds = new ArrayList<>(); // 내가 속한 번개의 가게 id

    public static void getShopId(ArrayList<MyMeetingModel> myMeetings){
        shopIds.clear();
        for(int i = 0; i < myMeetings.size(); i++){
            String shopId = myMeetings.get(i).getShopId();
            shopIds.add(shopId);
        }
    }

    public static void meetingDataLoad(final shopDataLoadedCallback callback) {

        final int[] remainingTasks = {shopIds.size()};

        databaseReference = FirebaseDatabase.getInstance().getReference("shops/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shops.clear();
                for (int i = 0; i < shopIds.size(); i++) {
                    String link = shopIds.get(i);

                    Shop shop = snapshot.child(link).getValue(Shop.class);
                    shop.setPrimaryKey(shop.getShopKey());
                    shops.add(shop);

                    remainingTasks[0]--;
                    if (remainingTasks[0] == 0) {  // 모든 작업이 완료되었을 때
                        callback.onShopLoaded(shops);  // 콜백 호출
                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface shopDataLoadedCallback{
        void onShopLoaded(ArrayList<Shop> shops);
    }

}
