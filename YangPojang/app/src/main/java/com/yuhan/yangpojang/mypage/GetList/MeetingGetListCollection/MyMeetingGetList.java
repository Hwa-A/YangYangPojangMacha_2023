package com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.AllMeetingItemModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MyMeetingModel;

import java.util.ArrayList;

public class MyMeetingGetList {

    private static DatabaseReference databaseReference;
    static ArrayList<MyMeetingModel> myMeetings = new ArrayList<>();

    // 현재 사용자의 UID 받아오기
    public static String getUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String user_info_uid = user.getUid();
            return user_info_uid;
        }
        return null;
    }

    public static void myMeetingDataLoad(final myMeetingDataLoadedCallback callback){

        databaseReference = FirebaseDatabase.getInstance().getReference("myMeeting/" + getUserUID());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myMeetings.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    MyMeetingModel myMeetingModel = new MyMeetingModel();
                    myMeetingModel.setMeetingId(snap.getKey());
                    myMeetingModel.setShopId(snap.getValue(String.class));

                    myMeetings.add(myMeetingModel);
                }
                callback.onMyMeetingLoaded(myMeetings);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public interface myMeetingDataLoadedCallback{
        void onMyMeetingLoaded(ArrayList<MyMeetingModel> myMeetings);
    }


}
