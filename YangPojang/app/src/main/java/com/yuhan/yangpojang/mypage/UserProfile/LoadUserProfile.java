package com.yuhan.yangpojang.mypage.UserProfile;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoadUserProfile {
    private String UID;
    private String userNick;
    private String userImg;

    // 닉네임을 위한 데이터베이스 연결
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;



    public LoadUserProfile(String UID, final dataLoadedCallback callback){
        this.UID = UID;

        databaseReference = firebaseDatabase.getReference("user-info/"+UID);
        Log.d("테스트user_info", "user-info: " + UID );

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNick = snapshot.child("user_Nickname").getValue(String.class);
                userImg = snapshot.child("user_img").getValue(String.class);
                Log.d("테스트user_info", "userNick" + userNick );
                Log.d("테스트user_info", "userImg" + userImg );
                callback.onDataLoaded(userNick,userImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface dataLoadedCallback{
        void onDataLoaded(String userNick, String userImg);

    }
}
