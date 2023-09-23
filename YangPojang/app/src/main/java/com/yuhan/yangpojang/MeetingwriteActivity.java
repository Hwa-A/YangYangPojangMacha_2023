package com.yuhan.yangpojang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// met: meeting
public class MeetingwriteActivity extends AppCompatActivity {
    MeetingDTO meetingDTO;          // 번개 객체
    Button metRegisterBtn;          // 번개 등록 버튼
    Button metCancelBtn;            // 번개 등록 취소
    EditText metTitleEdt;           // 번개 소개글
    EditText metMaxMember;          // 번개 정원
    ImageView metDateImgv;          // 번개 날짜 버튼 대용
    TextView metDateTv;             // 번개 날짜
    TextView metTimeTv;             // 번개 시간
    Calendar calendar;
    DatePickerDialog.OnDateSetListener callbackMethod;
    TimePickerDialog.OnTimeSetListener callbackTime;
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
        metMaxMember = findViewById(R.id.edt_meetingwrite_member);
        metDateImgv = findViewById(R.id.imgv_meetingwrite_calenderButton);
        metDateTv = findViewById(R.id.tv_meetingwrite_date);
        metTimeTv = findViewById(R.id.tv_meetingwrite_time);
        calendar = Calendar.getInstance();
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                meetingDTO.setMeetDate(year+"/"+(month+1)+"/"+dayOfMonth);
                metDateTv.setText(year+"/"+(month+1)+"/"+dayOfMonth);
            }
        };

        callbackTime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                meetingDTO.setTime(hourOfDay+"시 "+minute+"분");
                metTimeTv.setText(hourOfDay+"시 "+minute+"분");
            }
        };

        // 상단의 포차 이름 설정
        pchNameTv.setText(pchName);

        // 리스너 연결
        // 버튼 클릭 시, 번개 시간 선택(다이어로그로 구현)
        metTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // spinner 모드로 TimePickerDialog 구현
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, callbackTime,
                        // is24Hour가 true: AM/PM의 12시간 모드, false:24시간 모드
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });
        // 버튼 클릭 시, 번개 날짜 선택(다이어로그로 구현)
        metDateImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이어로그 생성 및 현재 날짜로 초기화
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), callbackMethod, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                // 화면에 띄움
                datePickerDialog.show();
            }
        });
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
                // 번개 정원 구하기
                double maxMemberData = Double.parseDouble(metMaxMember.getText().toString());
                meetingDTO.setMaxMember(maxMemberData);
                // DB에 저장
                metDatabase.child("meetings").push().setValue(meetingDTO);
                onBackPressed();
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