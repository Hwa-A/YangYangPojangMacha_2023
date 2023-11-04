package com.yuhan.yangpojang.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.fragment.HomeFragment;

import java.util.ArrayList;

public class LikeShopData {
    private static DatabaseReference databaseRef;
    static ArrayList<String> likeShops = new ArrayList<>();

    // 현재 사용자의 UID 받아오기
    public static String getUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String user_info_uid = user.getUid();
            return user_info_uid;
        }
        return null;
    }

    // 사용자가 좋아요를 누른 가게 ID 받아오기
    public static void likeShopDataLoad(final dataLoadedCallback callback){
        likeShops.clear();

       databaseRef = FirebaseDatabase.getInstance().getReference("likeShop/" + getUserUID());

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String key = snapshot1.getKey();
                    likeShops.add(key);
                }
                callback.onDataLoaded(likeShops);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read value.(LikeShopData)", error.toException());
            }
        });
    }

    // 사용자가 좋아요 누른 가게 ID 추가
    public void addLikedShop(String shopId){
        if(getUserUID() != null){
            databaseRef.child(shopId).setValue("");
        }else {
            // 사용자 UID가 없을 때의 예외 처리
            Log.e("LikeShopData", "User UID is null");
        }
    }

    // 사용자가 좋아요 누른 가게 ID 삭제
    public void removeLikedShop(String shopId){
        if(getUserUID() != null){
            databaseRef.child(shopId).removeValue();
        }else {
            // 사용자 UID가 없을 때의 예외 처리
            Log.e("LikeShopData", "User UID is null");
        }
    }

    public interface dataLoadedCallback{
        void onDataLoaded(ArrayList<String> likeShopsId);
    }
}
