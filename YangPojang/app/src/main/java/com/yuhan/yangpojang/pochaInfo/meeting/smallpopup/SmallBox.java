package com.yuhan.yangpojang.pochaInfo.meeting.smallpopup;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;

import java.util.ArrayList;
import java.util.Map;

public class SmallBox {
    MeetingData meetInfo;
    UserInfoModel user;
    Context context;
    String primaryKey;
    ArrayList<MeetingData> meetList;

    public SmallBox( ArrayList<MeetingData> meetList, MeetingData meetInfo, UserInfoModel user, String primaryKey, Context context) {
        this.meetInfo = meetInfo;
        this.user = user;
        this.context = context;
        this.primaryKey =primaryKey;
        this.meetList = meetList;
    }

    public void ShowAttenders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.meeting_popup, null);

        TextView attender = popupView.findViewById(R.id.attender);
        ImageButton closeBtn = popupView.findViewById(R.id.popupClose);
        Button attendBtn = popupView.findViewById(R.id.attend);
        Button cancelBtn = popupView.findViewById(R.id.cancel);

        //참석자들 출력
        Map<String, String> attends = meetInfo.getAttends();
        StringBuilder attendsStr = new StringBuilder();
        for (Map.Entry<String, String> entry : attends.entrySet()) {
            attendsStr.append(entry.getValue()).append("\n");
        }
        attender.setText(attendsStr.toString());


        // 사용자의 UID가 참석자 테이블에 없는 경우 참석 버튼 띄움
        if (!attends.containsKey(user.getUID())) {
            attendBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
            Log.d("번개smallbox", "// 사용자의 UID가 참석자 테이블에 없는 경우 참석 버튼 띄움 : ");
            WorkAttendBtn workAttendBtn = new WorkAttendBtn(meetInfo, user, context, meetList, primaryKey);

            if (meetInfo.getMaxMember() <= meetInfo.getNowMember()) {   // 정원이 다 찬 경우

                workAttendBtn.fullAttenders(attendBtn, cancelBtn);

            } else if (meetInfo.getMaxMember() >= meetInfo.getNowMember()) {   // 정원이 다 차지 않은 경우

                if (user.getAge() <= meetInfo.getMaxAge() && user.getAge() >= meetInfo.getMinAge()) { // 나이가 포함되는 경우
                    // 참석 버튼 클릭 시, 새로운 인원 추가
                    attendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            workAttendBtn.addAttender(attender,attendBtn);
                        }
                    });

                } else {    // 나이가 포함되지 않을 경우
                    attendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "아쉽지만 우린 함께 할 수 없어요...", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

        } else { // 사용자의 UID가 참석자 테이블에 있는 경우 참석 취소 버튼 띄움
            attendBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
            WorkCancleBtn workCancleBtn = new WorkCancleBtn( meetInfo,  user,  context, meetList);

            Log.d("번개smallbox", "// 사용자의 UID가 참석자 테이블에 있는 경우 참석 취소 버튼 띄움 --- : ");

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("번개smallbox", "// 사용자의 UID가 참석자 테이블에 있는 경우 참석 취소 버튼 띄움 onClick  ");
                    workCancleBtn.nonappearance(attender, cancelBtn);
                }
            });

        }

        AlertDialog alertDialog = builder.setView(popupView).create(); // Move this line up

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창 닫기
                alertDialog.dismiss();
            }
        });

        // 팝업창이 나타날 때 뒷 배경이 흐리게 바뀌도록 설정
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            // 팝업창 크기를 설정
            // 예: 500 픽셀 너비, 800 픽셀 높이로 설정
            params.width = 250;
            params.height = 800;
            window.setAttributes(params);
        }


//        // 팝업창이 나타날 때 뒷 배경이 흐리게 바뀌도록 설정
//        Window window = alertDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//            // 팝업창 크기 설정
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = WindowManager.LayoutParams.WRAP_CONTENT; // 너비
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 높이
//            window.setAttributes(params);
//        }

        alertDialog.show();
    }


}


