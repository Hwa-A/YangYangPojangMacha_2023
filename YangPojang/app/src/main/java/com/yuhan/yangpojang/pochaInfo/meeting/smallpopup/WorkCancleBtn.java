package com.yuhan.yangpojang.pochaInfo.meeting.smallpopup;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;


import java.util.ArrayList;

public class WorkCancleBtn {
    MeetingData meetInfo;
    UserInfoModel user;
    Context context;
    ArrayList<MeetingData> meetingDatas;

    public WorkCancleBtn(MeetingData meetInfo, UserInfoModel user, Context context, ArrayList<MeetingData> meetingDatas){
        this.meetInfo = meetInfo;
        this.user = user;
        this.context = context;
        this.meetingDatas = meetingDatas;
    }

    public void nonappearance(TextView attender, Button cancelBtn){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("meetingAttenders/" + meetInfo.getMeetingKey());

        dbRef.child(user.getUID()).removeValue();
        Toast.makeText(context, "떠난다니 아쉽군요...", Toast.LENGTH_SHORT).show();

        cancelBtn.setVisibility(View.GONE);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder attendsStr = new StringBuilder();
                int attendCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    attendsStr.append(snapshot.getValue(String.class)).append("\n");
                            attendCount++;
                }
                attender.setText(attendsStr.toString());

                // 참석자가 0명이면 번개 삭제
                if(attendCount == 0){
                    // meeting 테이블에서 삭제
                    DatabaseReference meetingRef = FirebaseDatabase.getInstance().getReference("meeting/" + meetInfo.getPochaKey() + "/" +meetInfo.getMeetingKey());
                    // meetingAttenders에서 삭제
                    DatabaseReference meetingAttendRef = FirebaseDatabase.getInstance().getReference("meetingAttenders/" + meetInfo.getMeetingKey());
                    // 마이페이지에서 삭제
                    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("myMeeting/" + user.getUID());
                    meetingRef.removeValue();
                    meetingAttendRef.removeValue();
                    profileRef.child(meetInfo.getMeetingKey()).removeValue();

                    // 번개를 삭제한 후에 hasMeeting 값을 업데이트
                    meetingDatas.remove(meetInfo);  // 삭제한 번개를 리스트에서 제거
                    new CntTodayMeet(meetingDatas, meetInfo.getPochaKey());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }
}
