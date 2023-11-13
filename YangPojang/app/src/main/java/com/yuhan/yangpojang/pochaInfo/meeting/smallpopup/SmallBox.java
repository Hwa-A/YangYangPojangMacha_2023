package com.yuhan.yangpojang.pochaInfo.meeting.smallpopup;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.meeting.model.MeetingData;
import com.yuhan.yangpojang.pochaInfo.meeting.model.UserInfoModel;

import java.util.Map;

public class SmallBox {
    MeetingData meetInfo;
    UserInfoModel user;
    Context context;

    public SmallBox(MeetingData meetInfo, UserInfoModel user, Context context){
        this.meetInfo = meetInfo;
        this.user = user;
        this.context = context;
    }

    public void ShowAttenders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.meeting_popup, null);

        TextView attender = popupView.findViewById(R.id.attender);
        ImageButton closeBtn = popupView.findViewById(R.id.popupClose);
        Button attendBtn = popupView.findViewById(R.id.attend);
        Button cancelBtn = popupView.findViewById(R.id.cancel);

        Map<String, String> attends = meetInfo.getAttends();
        StringBuilder attendsStr = new StringBuilder();
        for (Map.Entry<String, String> entry : attends.entrySet()) {
            attendsStr.append(entry.getValue()).append(" ");
        }

        attender.setText(attendsStr.toString());

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
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        alertDialog.show();
    }

}


