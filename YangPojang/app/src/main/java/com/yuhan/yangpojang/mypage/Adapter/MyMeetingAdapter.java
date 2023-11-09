package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.home.CategoryListAdapter;
import com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection.GetAllMyMeetingItems;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.AllMeetingItemModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingAttendersModel;

import java.util.ArrayList;

public class MyMeetingAdapter extends RecyclerView.Adapter<MyMeetingAdapter.ViewHolder>{

    private Context context;

    ArrayList<AllMeetingItemModel> allMeetingItemModels = new ArrayList<>();
    public MyMeetingAdapter(Context context, ArrayList<AllMeetingItemModel> allMeetingItemModel){
        this.context = context;
        allMeetingItemModels = allMeetingItemModel;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView MyReviewShopName; //번개제목
        TextView MyReviewShopAdd; //가게 주소
        NestedScrollView meeingMemberScroll; //참석자 목록
        LinearLayout meeingMemberlinear;
        TextView meetingTime; //번개 시간
        TextView meetingAgeMin; //번개 나이 제한
        TextView meetingAgeMax;
        TextView meetingMin; //번개 참석 인원
        TextView meetingMax; //번개 최대 인원


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            MyReviewShopName = itemView.findViewById(R.id.MyReviewShopName);
            MyReviewShopAdd = itemView.findViewById(R.id.MyReviewShopAdd);
            meeingMemberScroll = itemView.findViewById(R.id.meeingMemberScroll);
            meeingMemberlinear = itemView.findViewById(R.id.meeingMemberlinear);
            meetingTime = itemView.findViewById(R.id.meetingTime);
            meetingAgeMin = itemView.findViewById(R.id.meetingAgeMin);
            meetingAgeMax = itemView.findViewById(R.id.meetingAgeMax);
            meetingMin = itemView.findViewById(R.id.meetingMin);
            meetingMax = itemView.findViewById(R.id.meetingMax);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.profile_meeting_item_view, parent, false);
        MyMeetingAdapter.ViewHolder viewHolder = new MyMeetingAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    AllMeetingItemModel allMeetingItemModel = allMeetingItemModels.get(position);

    holder.MyReviewShopName.setText(allMeetingItemModel.getTitle());
    holder.MyReviewShopAdd.setText(allMeetingItemModel.getAddressName());
    holder.meetingTime.setText(allMeetingItemModel.getTime());
    holder.meetingAgeMin.setText(String.valueOf(allMeetingItemModel.getMinAge()));
    holder.meetingAgeMax.setText(String.valueOf(allMeetingItemModel.getMaxAge()));
    holder.meetingMin.setText(String.valueOf(allMeetingItemModel.getCountAttenders()));
    holder.meetingMax.setText(String.valueOf(allMeetingItemModel.getMaxMember()));

    for(MeetingAttendersModel item : allMeetingItemModel.getAttenders()){
        TextView textView = new TextView(context);
        textView.setText(item.getNickName());
        holder.meeingMemberlinear.addView(textView);
    }


    }

    @Override
    public int getItemCount() {
        return allMeetingItemModels.size();
    }
}
