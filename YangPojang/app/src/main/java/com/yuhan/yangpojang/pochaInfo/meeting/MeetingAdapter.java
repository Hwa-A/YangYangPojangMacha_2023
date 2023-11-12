package com.yuhan.yangpojang.pochaInfo.meeting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;


import java.util.ArrayList;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingHolder> {

    private ArrayList<MeetingData> meetList = new ArrayList<>();
    private Context context;


    public MeetingAdapter(ArrayList<MeetingData> meetList, Context context) {
        this.meetList.clear();
        this.meetList = meetList;
        this.context = context;
    }



    public static class MeetingHolder extends RecyclerView.ViewHolder{
        TextView meeting_Introduction;
        TextView meeting_date;
        TextView meeting_Time;
        TextView meeting_minAge;
        TextView meeting_maxAge;
        TextView meeting_participation;
        TextView meeting_MaxNumber;
        Button meeting_Attendbuton;
        Button meeting_cancelbutton;


        MeetingHolder(View itemview){
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
    public void onBindViewHolder(@NonNull MeetingAdapter.MeetingHolder holder, int position){
        holder.meeting_Introduction.setText(meetList.get(position).getTitle());
        holder.meeting_date.setText(meetList.get(position).getDate());
        holder.meeting_Time.setText(meetList.get(position).getTime());
        holder.meeting_maxAge.setText(String.valueOf(meetList.get(position).getMaxAge()));
        holder.meeting_minAge.setText(String.valueOf(meetList.get(position).getMinAge()));
        holder.meeting_MaxNumber.setText(String.valueOf(meetList.get(position).getMaxMember()));
        holder.meeting_participation.setText(String.valueOf(meetList.get(position).getNowMember()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("번개Adapter", "아이템 뷰 클릭 : " + position);
//                Intent intent = new Intent(context, MeetingData.class);
//                intent.putExtra("title",meetList.get(position).getTitle());
//                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return (meetList != null ? meetList.size() : 0);
    }

}

