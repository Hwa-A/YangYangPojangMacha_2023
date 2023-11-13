package com.yuhan.yangpojang.pochaInfo.meeting.smallpopup;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CntTodayMeet {
    int sameDataMeetCnt;
    private String primaryKey;

    public CntTodayMeet (ArrayList<MeetingData> meetingDatas, String primaryKey) {
        this.primaryKey = primaryKey;
        // 오늘 날짜의 번개 수 계산
        sameDataMeetCnt = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        for (MeetingData meetData : meetingDatas) {
            if (currentDate.equals(meetData.getYearDate())) {
                sameDataMeetCnt++;
            }
        }


        // 오늘 날짜의 번개가 있으면 shop 테이블의 hasMeeting 속성을 true로 변경
        if (sameDataMeetCnt > 0) {
            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops/" + primaryKey);
            Log.d("번개CntTodayMeet", "CntTodayMeet: " + "shops/" + primaryKey);
            shopRef.child("hasMeeting").setValue(true);
        } else if (sameDataMeetCnt == 0) {
            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference("shops/" + primaryKey);
            shopRef.child("hasMeeting").setValue(false);
        }

    }
}
