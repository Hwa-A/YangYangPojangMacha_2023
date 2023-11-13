package com.yuhan.yangpojang.pochaInfo.meeting.getList;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;


import java.util.ArrayList;

public class MeetingGetList {
    private String pochaKey;

    private ArrayList<MeetingData> meetingDatas = new ArrayList<>(); // 정보 전송용

    private ArrayList<String> selectMeeting = new ArrayList<>(); // 미팀 목록 받기, 참석자 목록 조회

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    int i = 0;

    public MeetingGetList() {
        Log.d("번개getList진입", "번개 getList진입");
    }


    public MeetingGetList(String pochaKey, final MeetingListCallback firstCallback) {
        this.pochaKey = pochaKey;
        databaseReference = firebaseDatabase.getReference("meeting/" + pochaKey);
        Log.d("번개getList", "데이터 베이스 레퍼런스 : " + "meeting/" + pochaKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectMeeting.clear();
                meetingDatas.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    // 해당 가게의 번개 목록 받기
                    // 해당 가게 번개의 참석자 목록 받기
                    selectMeeting.add(snap.getKey()); // 경로 : 번개 id 들만
                    Log.d("번개getList", "selectMeeting.add(snap.getKey()) " + selectMeeting);
                }

                for (i = 0; i < selectMeeting.size(); i++) {
                    String meetingLink = selectMeeting.get(i);
                    Log.d("번개getList", "meetingLink : " + meetingLink);

                    // 번개 정보 불러오기
                    String date = snapshot.child(meetingLink).child("date").getValue(String.class);
                    String hostUid = snapshot.child(meetingLink).child("hostUid").getValue(String.class);
                    int maxAge = snapshot.child(meetingLink).child("maxAge").getValue(int.class);
                    int maxMember = snapshot.child(meetingLink).child("maxMember").getValue(int.class);
                    int minAge = snapshot.child(meetingLink).child("minAge").getValue(int.class);
                    String registerTime = snapshot.child(meetingLink).child("registerTime").getValue(String.class);
                    String time = snapshot.child(meetingLink).child("time").getValue(String.class);
                    String title = snapshot.child(meetingLink).child("title").getValue(String.class);
                    String yearDate = snapshot.child(meetingLink).child("yearDate").getValue(String.class);
                    String meetingKey = meetingLink.toString();


                    Log.d("번개getList", "snapshot.child(date) : " + date);
                    Log.d("번개getList", "snapshot.child(hostUid) : " + hostUid);
                    Log.d("번개getList", "snapshot.child(meetingKey) : " + meetingKey);

                    MeetingData model = new MeetingData(date, hostUid, maxAge, maxMember, minAge,
                            registerTime, time, title, yearDate, meetingKey, pochaKey);

                    meetingDatas.add(model);



                }
                firstCallback.onMeetingDataLoaded(meetingDatas, selectMeeting);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public interface MeetingListCallback {
        void onMeetingDataLoaded(ArrayList<MeetingData> meetingDatas, ArrayList<String> selectMeeting);
    }
}

