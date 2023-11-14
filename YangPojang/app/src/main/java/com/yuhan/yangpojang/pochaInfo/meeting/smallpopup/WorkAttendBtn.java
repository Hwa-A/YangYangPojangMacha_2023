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

public class WorkAttendBtn {
    MeetingData meetInfo;
    UserInfoModel user;
    Context context;
    ArrayList<MeetingData> meetingDatas;
    String primaryKey;

    public WorkAttendBtn(MeetingData meetInfo, UserInfoModel user, Context context, ArrayList<MeetingData> meetingDatas, String primaryKey) {
        this.meetInfo = meetInfo;
        this.user = user;
        this.context = context;
        this.meetingDatas = meetingDatas;
        this.primaryKey = primaryKey;
    }

    // 참석자가 다 찾을 경우 : 참석, 참석 취소 버튼 모두 비활성화
    public void fullAttenders(Button attendBtn, Button cancelBtn) {
        attendBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
    }

    public void addAttender(TextView attender, Button attendBtn) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("meetingAttenders/" + meetInfo.getMeetingKey());
        DatabaseReference myMeetRef = FirebaseDatabase.getInstance().getReference("myMeeting/" + user.getUID());


        dbRef.child(user.getUID()).setValue(user.getNick());    // 새로운 참석자 삽입
        myMeetRef.child(meetInfo.getMeetingKey()).setValue(meetInfo.getPochaKey()); // 마이미팅 테이블 추가
        Toast.makeText(context, "파티원이 되었습니다.", Toast.LENGTH_SHORT).show();

        attendBtn.setVisibility(View.GONE);
//        new CntTodayMeet(meetingDatas, primaryKey); // shop 테이블 반영

        dbRef.addValueEventListener(new ValueEventListener() {      // 새로운 참석자가 포함된 참석자 목록
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder attendsStr = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    attendsStr.append(snapshot.getValue(String.class)).append("\n");
                }
                attender.setText(attendsStr.toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }

}
