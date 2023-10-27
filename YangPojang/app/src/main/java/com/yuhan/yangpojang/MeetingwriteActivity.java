package com.yuhan.yangpojang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// meet: meeting
// pch: pojangmacha
public class MeetingwriteActivity extends AppCompatActivity {
    MeetingDTO meetDTO;              // 번개 객체
    TextView meetDateTv;             // 번개 날짜
    TextView meetTimeTv;             // 번개 시간
    Button meetRegisterBtn;          // 번개 등록 버튼
    Button meetCancelBtn;            // 번개 취소 버튼
    // TimePickerDialog.OnTimeSetListener callbackTime;
    private DatabaseReference meetDatabase;      // DB
    private AutoCompleteTextView autoCompleteTextView;  // 번개 정원 drop menu에 사용할 뷰
    private  String[] maxMembers;       // drop menu item 값인 번개 정원을 담는 배열
    private ArrayAdapter<String> arrayAdapter;  // autoCompleteTextView에서 maxMembers 관리할 Adapter
    // onResume(): Activity가 재활성화될 때마다 호출 => 데이터 업데이트 + 초기화에 사용
    @Override
    protected void onResume() {
        super.onResume();
        autoCompleteTextView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingwrite);

        // 전달 받은 데이터로 변수 초기화
        Intent intent = getIntent();
        String pchName = intent.getStringExtra("pchName");      // 포차 이름
        // String uid = intent.getStringExtra("uid");              // 회원 ID

        // 상단의 포차 이름 설정
        TextView pchNameTv = findViewById(R.id.tv_meetingwrite_pochaName);
        pchNameTv.setText(pchName);

        // 객체 생성 및 초기화 코드
        // 번개 객체 필드 관련
        // meetDTO = new MeetingDTO(uid, pchName);
        meetDateTv = findViewById(R.id.tv_meetingwrite_date);
        meetTimeTv = findViewById(R.id.tv_meetingwrite_time);
        TextInputLayout titleInputLayout = findViewById(R.id.txtLay_meetingwrite_titleContainer);   // 번개 소개글 컨테이너
        EditText meetTitleEdt = findViewById(R.id.edt_meetingwrite_title);
        // 번개 등록/취소 버튼 관련
        meetRegisterBtn = findViewById(R.id.btn_meetingwrite_register);
        meetCancelBtn = findViewById(R.id.btn_meetingwrite_cancel);
        // 번개 정원 drop menu 관련
        autoCompleteTextView = findViewById(R.id.autoTxt_meetingwrite_member);
        maxMembers = getResources().getStringArray(R.array.maxMembers);
        // new ArrayAdapter<>(눈에 나타낼 xml, drop down되는 xml, 표시할 배열)
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropitem_pchmeeting_member, maxMembers);

        // firebase 관련
        // meetDatabase = FirebaseDatabase.getInstance().getReference();
        // editText에 공백 입력 여부 확인 - 공백 시, error message 출력
        emptyError(meetTitleEdt, titleInputLayout);


//
//        calendar = Calendar.getInstance();
//        callbackMethod = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                meetingDTO.setMeetDate(year+"/"+(month+1)+"/"+dayOfMonth);
//                metDateTv.setText(year+"/"+(month+1)+"/"+dayOfMonth);
//            }
//        };
//
//        callbackTime = new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                meetingDTO.setTime(hourOfDay+"시 "+minute+"분");
//                metTimeTv.setText(hourOfDay+"시 "+minute+"분");
//            }
//        };
//
//
//
//        // 리스너 연결
//        // 버튼 클릭 시, 번개 시간 선택(다이어로그로 구현)
//        metTimeTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // spinner 모드로 TimePickerDialog 구현
//                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, callbackTime,
//                        // is24Hour가 true: AM/PM의 12시간 모드, false:24시간 모드
//                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
//                timePickerDialog.show();
//            }
//        });
//        // 버튼 클릭 시, 번개 날짜 선택(다이어로그로 구현)
//        metDateImgv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 다이어로그 생성 및 현재 날짜로 초기화
//                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), callbackMethod, calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//                // 화면에 띄움
//                datePickerDialog.show();
//            }
//        });
//        // 버튼 클릭 시, 번개 등록
//        metRegisterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 번개 작성 시간 구하기
//                long now = System.currentTimeMillis();
//                Date date = new Date(now);
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                meetingDTO.setDate(dateFormat.format(date));
//                // 번개 소개글
//                String titleData = metTitleEdt.getText().toString();
//                meetingDTO.setTitle(titleData);
//                // 번개 정원 구하기
//                double maxMemberData = Double.parseDouble(metMaxMember.getText().toString());
//                meetingDTO.setMaxMember(maxMemberData);
//                // DB에 저장
//                metDatabase.child("meetings").push().setValue(meetingDTO);
//                onBackPressed();
//            }
//        });
        // 버튼 클릭 시, 번개 작성 취소
        meetCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       // 현재 Activity 종료
            }
        });
    }
    // editText에 공백 입력 시, error message 출력 코드
    private void emptyError(EditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // text 입력 전 실행되는 메서드
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // text 입력과 동시에 실행되는 메서드
            }

            @Override
            public void afterTextChanged(Editable s) {
                // text 입력 후 실행되는 메서드: text가 변경된 것을 통보할 때 사용
                // TextUtils.isEmpty(): 라이브러리에서 제공하는 'null or 공백' 체크 함수
                if(TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우
                    txtLayout.setError("문자를 입력해주세요");
                }else{
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                }
            }
        });
    }

}