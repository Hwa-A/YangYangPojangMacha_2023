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
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PochameetingGetList {
    private String pochaKey;
    private String todaydate;

    private ArrayList<PochameetingData> mtlist = new ArrayList<>(); // 번개 리스트

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public PochameetingGetList() {

    }

    public static String getCurrentDate() {
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd", Locale.getDefault());

        return format.format(dateNow);
    }


    public void getMeetingList(String pochaKey, final dataLoadedCallback callback)
    {
        this.pochaKey = pochaKey;
        todaydate = getCurrentDate();

        //DB테이블 연결
        databaseReference = firebaseDatabase.getInstance().getReference("meeting").child(pochaKey);
        //date가 당일 날짜와 동일한 경우에만 출력
        Query equaldatemeet = databaseReference.orderByChild("date").equalTo(todaydate);

        equaldatemeet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mtlist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PochameetingData pochameetingData = dataSnapshot.getValue(PochameetingData.class);
                    mtlist.add(pochameetingData);
                    callback.onDataLoaded(mtlist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface dataLoadedCallback{
        void onDataLoaded(ArrayList<PochameetingData> mtlist);

    }
}