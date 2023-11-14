package com.yuhan.yangpojang.mypage.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection.GetAllMyMeetingItems;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.AllMeetingItemModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingAttendersModel;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;

import java.util.ArrayList;

public class MyMeetingAdapter extends RecyclerView.Adapter<MyMeetingAdapter.ViewHolder> {

    private Context context;
    private UserInfoModel userInfo = new UserInfoModel();


    ArrayList<AllMeetingItemModel> allMeetingItemModels = new ArrayList<>();
    public MyMeetingAdapter(Context context, ArrayList<AllMeetingItemModel> allMeetingItemModel){
        this.context = context;
        allMeetingItemModels = allMeetingItemModel;
    }

    // 현재 사용자의 UID 받아오기
    public static String getUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String user_info_uid = user.getUid();
            return user_info_uid;
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView MyReviewShopName; //번개제목
        TextView MyReviewShopAdd; //가게 주소
        LinearLayout meetingMemberlinear; //참석자 목록
        TextView meetingTime; //번개 시간
        TextView meetingAgeMin; //번개 나이 제한
        TextView meetingAgeMax;
        TextView meetingMin; //번개 참석 인원
        TextView meetingMax; //번개 최대 인원
        Button cancelJoin; //참석취소 버튼



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            MyReviewShopName = itemView.findViewById(R.id.MyReviewShopName);
            MyReviewShopAdd = itemView.findViewById(R.id.MyReviewShopAdd);
            meetingMemberlinear = itemView.findViewById(R.id.meeingMemberlinear);
            meetingTime = itemView.findViewById(R.id.meetingTime);
            meetingAgeMin = itemView.findViewById(R.id.meetingAgeMin);
            meetingAgeMax = itemView.findViewById(R.id.meetingAgeMax);
            meetingMin = itemView.findViewById(R.id.meetingMin);
            meetingMax = itemView.findViewById(R.id.meetingMax);
            cancelJoin = itemView.findViewById(R.id.cancelJoin);

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

        holder.meetingMemberlinear.removeAllViews();
        for(MeetingAttendersModel item : allMeetingItemModel.getAttenders()){
            TextView textView = new TextView(context);
            textView.setText(item.getNickName());
            holder.meetingMemberlinear.addView(textView);
        }

        //아이템 리스너
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String className = "com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity";
                    Class<?> activityClass = Class.forName(className);

                    Intent intent = new Intent(v.getContext(), activityClass);
                    intent.putExtra("shopInfo", allMeetingItemModel.getShop());
                    v.getContext().startActivity(intent);

                }catch (ClassNotFoundException e){
                    Toast.makeText(v.getContext(), "클래스 찾을 수 없음: PochainfoActivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 참석취소 버튼 클릭 시
        holder.cancelJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    popup(allMeetingItemModel.getTitle(), new AnswerCallback() {
                        @Override
                        public void onAnswer(boolean answer) {
                            if (answer) {
                                String uid = getUserUID();
                                DatabaseReference myMeetingDatabaseRef = FirebaseDatabase.getInstance().getReference("myMeeting/" + uid);
                                DatabaseReference meetingAttendersDatabaseRef = FirebaseDatabase.getInstance().getReference("meetingAttenders/" + allMeetingItemModel.getMeetingId());
                                if (uid != null) {
                                    // myMeeting테이블에서 내가 참여하는 번개 지우기
                                    myMeetingDatabaseRef.child(allMeetingItemModel.getMeetingId()).removeValue();
                                    // meetingAttenders테이블에서 해당 번개 참석자에서 나를 지우기
                                    meetingAttendersDatabaseRef.child(uid).removeValue();

                                    allMeetingItemModels.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, allMeetingItemModels.size());

                                    GetAllMyMeetingItems getAllMyMeetingItems = new GetAllMyMeetingItems();
                                    getAllMyMeetingItems.getMeetingInfo(new GetAllMyMeetingItems.allMeetingItemLoadCallback() {
                                        @Override
                                        public void onAllLoaded(ArrayList<AllMeetingItemModel> allMeetingItemMode) {
                                            allMeetingItemModels = allMeetingItemMode;
                                        }
                                    });

                                    // 참석자가 0명일 경우 해당 번개 삭제
                                    if(allMeetingItemModel.getCountAttenders() == 1){
                                        DatabaseReference meetingDatabaseRef = FirebaseDatabase.getInstance().getReference("meeting/" + allMeetingItemModel.getShop().getPrimaryKey());
                                        meetingDatabaseRef.child(allMeetingItemModel.getMeetingId()).removeValue();

                                        // 삭제 시 해당 가게의 번개 갯수가 0이면 가게의 hasMeeting값을 true -> false로 설정
                                        final long[] cnt = new long[1];
                                        meetingDatabaseRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                cnt[0] = snapshot.getChildrenCount();
                                                if(cnt[0] == 0){
                                                    DatabaseReference shopDatabaseRef = FirebaseDatabase.getInstance().getReference("shops/" + allMeetingItemModel.getShop().getPrimaryKey());
                                                    shopDatabaseRef.child("hasMeeting").setValue(false);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }

                                } else {
                                    Log.e("MyMeetingAdapter", "User UID is null");
                                }
                            } else {
                                Log.e("MyMeetingAdapter", "User cancellation response");
                            }
                        }
                    });
                }
            }
        });

    }

    public void popup(String meetingTitle, AnswerCallback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.check_availability_popup, null);
        builder.setView(customView);
        final AlertDialog dialog = builder.create();

        TextView title = customView.findViewById(R.id.meetingTitle);
        title.setText(meetingTitle);

        Button accept = customView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onAnswer(true);
            }
        });

        Button cancel = customView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onAnswer(false);
            }
        });

        dialog.show();

    }

    public interface AnswerCallback {
        void onAnswer(boolean answer);
    }

    @Override
    public int getItemCount() {
        return allMeetingItemModels.size();
    }
}
