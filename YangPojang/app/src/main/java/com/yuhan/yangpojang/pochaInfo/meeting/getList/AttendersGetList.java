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
import java.util.HashMap;
import java.util.Map;

public class AttendersGetList {

    private ArrayList<MeetingData> attendersData = new ArrayList<MeetingData>();

    int i = 0;
    int cnt = 0;

    String pochaKey;
    public void getAttendersList(String pochaKey, final AttendersGetList.DataLoadedCallback dataLoadedCallback) {

        new MeetingGetList(pochaKey, new MeetingGetList.MeetingListCallback() {
            @Override
            public void onMeetingDataLoaded(ArrayList<MeetingData> meetingDatas, ArrayList<String> selectMeeting) {
                Log.d("번개AttendersGetList", "onDataChange: selectShop 경로 :" + selectMeeting);

                DatabaseReference attenderRef = FirebaseDatabase.getInstance().getReference("meetingAttenders/");
                Log.d("번개AttendersGetList", "selectMeeting.size() : " + selectMeeting.size());

                attenderRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attendersData.clear();
                        // 한 번개 연결
                        for (i = 0; i < selectMeeting.size(); i++) {
                            Log.d("번개AttendersGetList", "onDataChange: " + i);
                            String meetingLink = selectMeeting.get(i);

                            Map<String, String> attendList = new HashMap<>();
                            MeetingData model = new MeetingData();
                            Log.d("번개AttendersGetList", "데이터 추가 : ");

                            cnt = 0;
                            // 참석자 리스트 테이블에서 참석자 가져오기
                            for (DataSnapshot childSnapshot : snapshot.child(meetingLink).getChildren()) {
                                String uid = childSnapshot.getKey();
                                String nick = childSnapshot.getValue(String.class);
                                Log.d("번개AttendersGetList", "String key = childSnapshot.getKey();: " + uid);
                                Log.d("번개AttendersGetList", "String key = childSnapshot.value();: " + nick);

                                //map에 값 저장
                                attendList.put(uid,nick);
                                Log.d("번개AttendersGetList", "attendList.put(uid,nick) : " + attendList.get(uid));

                                cnt++;
                            }

                            for (Map.Entry<String, String> entry : attendList.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                Log.d("번개AttendersGetList", "Map.Entry<String, String> entry : attendList.entrySet() : " + "Key: " + key + ", Value: " + value);
                            }

                            model.setAttends(attendList);
                            model.setNowMember(attendList.size());
                            model.setDate(meetingDatas.get(i).getDate());
                            model.setHostUid(meetingDatas.get(i).getHostUid());
                            model.setMaxAge(meetingDatas.get(i).getMaxAge());
                            model.setMaxMember(meetingDatas.get(i).getMaxMember());
                            model.setMinAge(meetingDatas.get(i).getMinAge());
                            model.setRegisterTime(meetingDatas.get(i).getRegisterTime());
                            model.setTime(meetingDatas.get(i).getTime());
                            model.setTitle(meetingDatas.get(i).getTitle());
                            model.setYearDate(meetingDatas.get(i).getYearDate());
                            model.setMeetingKey(meetingDatas.get(i).getMeetingKey());
                            model.setPochaKey(meetingDatas.get(i).getPochaKey());

                            for (Map.Entry<String, String> entry : model.getAttends().entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                Log.d("번개AttendersGetList", "model.setAttends(attendList) : " + "Key: " + key + ", Value: " + value);
                            }

//                            attendersData.add(model);
                            Log.d("번개AttendersGetList", "meetingDatas.add(model) " + model.getAttends());
                            Log.d("번개AttendersGetList", "meetingDatas.add(model) " + meetingDatas.get(i).getTitle());
                            attendersData.add(model);
                        }
                        dataLoadedCallback.onDataLoaded(attendersData);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public interface DataLoadedCallback{
        void onDataLoaded(ArrayList<MeetingData> meetingData);
    }

}
