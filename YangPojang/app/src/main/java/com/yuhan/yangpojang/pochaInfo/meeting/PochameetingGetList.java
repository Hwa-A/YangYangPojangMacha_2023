package com.yuhan.yangpojang.pochaInfo.meeting;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.GetList.MyLikeShopGetList;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PochameetingGetList {
    private String pochaKey;

    private ArrayList<PochameetingData> meetDatas = new ArrayList<>();   // 번개 정보 전송용

    private ArrayList mtlist = new ArrayList<>(); // 번개 리스트

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public PochameetingGetList() {

    }

    public static String getCurrentDate() {
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("M/dd", Locale.getDefault());  // 2월 15일
        // "M월 dd일" 에는 원하는 형식을 넣어주면 됩니다.

        return format.format(dateNow);
    }


    public void getMeetingList(String pochaKey, final dataLoadedCallback callback)
    {
        this.pochaKey = pochaKey;     //UID 연결
        mtlist.clear();

        //DB테이블 연결
        databaseReference = firebaseDatabase.getReference("meeting/"+pochaKey);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    mtlist.add(snap.getKey());
                }

                for (Object shopKey : mtlist) {
                    DatabaseReference meetingRef = FirebaseDatabase.getInstance().getReference("meeting").child(pochaKey);
                    Query myTopPostsQuery = meetingRef.orderByChild("date").equalTo(getCurrentDate());
                    myTopPostsQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot meetSnapshot) {
                            if (meetSnapshot.exists()) {

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 에러 처리 부분
                        }
                    });


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리 부분
            }

        });


    }
    public interface dataLoadedCallback{

    }

}
