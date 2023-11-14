package com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MyMeetingModel;

import java.util.ArrayList;

public class MeetingGetList {
    private static DatabaseReference databaseReference;
    static ArrayList<MeetingModel> meetings = new ArrayList<>();
    static ArrayList<String> routes = new ArrayList<>();
    public static void getMeetingId_ShopId(ArrayList<MyMeetingModel> myMeetings){
        routes.clear();
        for(int i = 0; i < myMeetings.size(); i++){
            String route = myMeetings.get(i).getShopId() + "/" + myMeetings.get(i).getMeetingId();
            routes.add(route);
        }
    }

    public static void meetingDataLoad(final meetingDataLoadedCallback callback){

        final int[] remainingTasks = {routes.size()};

        databaseReference = FirebaseDatabase.getInstance().getReference("meeting/");//

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meetings.clear();
                for (int i = 0; i < routes.size(); i++) {
                    String link = routes.get(i);

                    MeetingModel meetingModel = snapshot.child(link).getValue(MeetingModel.class);
                    meetings.add(meetingModel);

                    remainingTasks[0]--;
                    if (remainingTasks[0] == 0) {  // 모든 작업이 완료되었을 때
                        callback.onMeetingLoaded(meetings);  // 콜백 호출
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public interface meetingDataLoadedCallback{
        void onMeetingLoaded(ArrayList<MeetingModel> meetings);
    }
}
