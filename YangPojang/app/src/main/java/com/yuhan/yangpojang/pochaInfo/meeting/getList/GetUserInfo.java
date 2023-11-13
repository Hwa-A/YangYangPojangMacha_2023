package com.yuhan.yangpojang.pochaInfo.meeting.getList;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;

import java.time.LocalDate;
import java.util.Date;

public class GetUserInfo {

    UserInfoModel userInfo = new UserInfoModel();

    public GetUserInfo() {}

    public GetUserInfo(String uid, final UserDataCallback userDataCallback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user-info/" + uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String UID, String nick, String brith, int age)
                userInfo.setUID(uid);
                userInfo.setBrith(snapshot.child("user_birthday").getValue(String.class));
                userInfo.setNick(snapshot.child("user_Nickname").getValue(String.class));

                String[] splitBirth = userInfo.getBrith().split("/");
                userInfo.setAge((int) LocalDate.now().getYear() - Integer.parseInt(splitBirth[0]));
                Log.d("번개GetUser", "onDataChange: " + userInfo.getAge());

                userDataCallback.userDataLoaded(userInfo);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public interface UserDataCallback {
        void userDataLoaded(UserInfoModel userInfo);
    }
}
