package com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingAttendersModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MyMeetingModel;

import java.util.ArrayList;

public class MeetingAttendersGetList {
    private static DatabaseReference databaseReference;
    static ArrayList<ArrayList<MeetingAttendersModel>> attenders = new ArrayList<>();
    static ArrayList<String> meetingIds = new ArrayList<>();

    public static void getMeetingId(ArrayList<MyMeetingModel> myMeetings){
        meetingIds.clear();
        for(int i = 0; i < myMeetings.size(); i++){
            String meetingId = myMeetings.get(i).getMeetingId();
            meetingIds.add(meetingId);
        }
    }

    public static void attendersDataLoad(final attenderDataLoadedCallback callback){

        final int[] remainingTasks = {meetingIds.size()};

            databaseReference = FirebaseDatabase.getInstance().getReference("meetingAttenders/");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    attenders.clear();
                    for(int i = 0; i < meetingIds.size(); i++){
                        String link = meetingIds.get(i);
                        ArrayList<MeetingAttendersModel> models = new ArrayList<>();
                    for(DataSnapshot snap : snapshot.child(link).getChildren()){
                        MeetingAttendersModel meetingAttendersModel = new MeetingAttendersModel();
                        meetingAttendersModel.setAttender(snap.getKey());
                        meetingAttendersModel.setNickName(snap.getValue(String.class));

                        models.add(meetingAttendersModel);
                    }
                    attenders.add(models);

                    remainingTasks[0]--;
                    if (remainingTasks[0] == 0) {  // 모든 작업이 완료되었을 때
                        callback.onAttenderLoaded(attenders);  // 콜백 호출
                    }
                }
            }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    public interface attenderDataLoadedCallback{
        void onAttenderLoaded(ArrayList<ArrayList<MeetingAttendersModel>> attenders);
    }
}
