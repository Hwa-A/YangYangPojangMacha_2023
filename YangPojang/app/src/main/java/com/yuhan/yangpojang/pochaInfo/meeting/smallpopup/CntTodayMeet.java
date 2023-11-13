package com.yuhan.yangpojang.pochaInfo.meeting.smallpopup;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class CntTodayMeet {
    int sameDataMeetCnt;
    private String primaryKey;

    public CntTodayMeet (ArrayList<MeetingData> meetingDatas, MeetingData meetInfo) {
        this.primaryKey = meetInfo.getPochaKey();

        // 같은 meetingKey 값을 가지고 있을 경우 삭제
        Iterator<MeetingData> iterator = meetingDatas.iterator();
        while (iterator.hasNext()) {
            MeetingData data = iterator.next();
            if (data.getMeetingKey().equals(meetInfo.getMeetingKey())) {
                iterator.remove();
            }
        }

        Log.d("번개CntTodayMeet", "CntTodayMeet: " + meetingDatas.size());



        // 오늘 날짜의 번개 수 계산
        sameDataMeetCnt = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        for (MeetingData meetData : meetingDatas) {
            if (currentDate.equals(meetData.getYearDate())) {
                sameDataMeetCnt++;
                Log.d("번개CntTodayMeet", "sameDataMeetCnt : " + sameDataMeetCnt);
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
