package com.yuhan.yangpojang.pochaInfo.meeting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;
import com.yuhan.yangpojang.pochaInfo.meeting.smallpopup.SmallBox;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingHolder> {

    private ArrayList<MeetingData> meetList = new ArrayList<>();
    private Context context;
    private String UID;
    private String primaryKey; // 가게 id
    private UserInfoModel user = new UserInfoModel();

    public void catchUserInfo(UserInfoModel user) {
        this.user = user;
        Log.d("번개Adapter", "UserInfo: age " + user.getAge());
    }


    public MeetingAdapter(String UID, UserInfoModel userInfo, ArrayList<MeetingData> meetList, String primaryKey, Context context) {
        this.UID = UID;
        Log.d("번개Adapter", " this.UID = UID; : " + UID);
        this.user = userInfo;
        this.meetList.clear();
        this.meetList = meetList;
        this.primaryKey = primaryKey;
        this.context = context;
    }


    public static class MeetingHolder extends RecyclerView.ViewHolder {
        TextView meeting_Introduction;
        TextView meeting_date;
        TextView meeting_Time;
        TextView meeting_minAge;
        TextView meeting_maxAge;
        TextView meeting_participation;
        TextView meeting_MaxNumber;
        Button meeting_Attendbuton;
        Button meeting_cancelbutton;


        MeetingHolder(View itemview) {
            super(itemview);

            meeting_Introduction = itemview.findViewById(R.id.meeting_Introduction);
            meeting_date = itemview.findViewById(R.id.meeting_date);
            meeting_Time = itemview.findViewById(R.id.meeting_Time);
            meeting_minAge = itemview.findViewById(R.id.meeting_minAge);
            meeting_maxAge = itemview.findViewById(R.id.meeting_maxAge);
            meeting_participation = itemview.findViewById(R.id.meeting_participation);
            meeting_MaxNumber = itemview.findViewById(R.id.meeting_MaxNumber);
            meeting_Attendbuton = itemview.findViewById(R.id.meeting_Attendbuton);
            meeting_cancelbutton = itemview.findViewById(R.id.meeting_cancelbutton);
        }

    }


    // onCreateViewHolder = 실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    @NonNull
    @Override
    public MeetingAdapter.MeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pochameeting, parent, false);
        MeetingAdapter.MeetingHolder meetingHolder = new MeetingAdapter.MeetingHolder(view);

        return meetingHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MeetingAdapter.MeetingHolder holder, int position) {
        holder.meeting_Introduction.setText(meetList.get(position).getTitle());
        holder.meeting_date.setText(meetList.get(position).getDate());
        holder.meeting_Time.setText(meetList.get(position).getTime());
        holder.meeting_maxAge.setText(String.valueOf(meetList.get(position).getMaxAge()));
        holder.meeting_minAge.setText(String.valueOf(meetList.get(position).getMinAge()));
        holder.meeting_MaxNumber.setText(String.valueOf(meetList.get(position).getMaxMember()));
        holder.meeting_participation.setText(String.valueOf(meetList.get(position).getNowMember()));

        // 오늘 날짜와 비교
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());

        // 지날 날짜의 경우 클릭 안됨
        if (!currentDate.equals(meetList.get(position).getYearDate())) {
            holder.itemView.setEnabled(false);
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 오늘 날짜에만 클릭 가능
                    if (view.isEnabled()) {
                        Log.d("번개Adapter", "아이템 뷰 클릭 : " + position);
                        SmallBox smallBox = new SmallBox(meetList, meetList.get(position), user, primaryKey, context);
                        smallBox.ShowAttenders();
//                    new CntTodayMeet(meetList,primaryKey);
                    }
                }
            });

        }
    }



    @Override
    public int getItemCount() {
        return (meetList != null ? meetList.size() : 0);
    }

}
