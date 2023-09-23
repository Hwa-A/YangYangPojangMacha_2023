package com.yuhan.yangpojang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

// met: meeting
public class MeetingwriteActivity extends AppCompatActivity {
    MeetingDTO meetingDTO;          // 번개 객체
    Button metRegisterBtn;          // 번개 등록 버튼
    Button metCancelBtn;            // 번개 등록 취소
    EditText metTitleEdt;           // 번개 소개글

    private DatabaseReference metDatabase;      // DB 참조


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingwrite);

        // 전달 받은 데이터로 변수 초기화
        Intent intent = getIntent();
        String pchName = intent.getStringExtra("pchName");      // 포차 이름
        String uid = intent.getStringExtra("uid");              // 회원 ID

        // 객체 생성 및 초기화
        meetingDTO = new MeetingDTO(uid, pchName);
        metDatabase = FirebaseDatabase.getInstance().getReference();
        TextView pchNameTv = findViewById(R.id.tv_meetingwrite_pochaName);  // 상단의 포차 이름
        metRegisterBtn = findViewById(R.id.btn_meetingwrite_register);
        metCancelBtn = findViewById(R.id.btn_meetingwrite_cancel);
        metTitleEdt = findViewById(R.id.edt_meetingwrite_title);

        // 상단의 포차 이름 설정
        pchNameTv.setText(pchName);

        // 리스너 연결
        // 버튼 클릭 시, 번개 등록
        metRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 번개 작성 시간 구하기
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                meetingDTO.setDate(dateFormat.format(date));
                // 번개 소개글
                String titleData = metTitleEdt.getText().toString();
               meetingDTO.setTitle(titleData);

            }
        });
        // 버튼 클릭 시, 번개 작성 취소
        metCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       // 현재 Activity 종료
            }
        });
    }
}