package com.yuhan.yangpojang.pochaInfo.meeting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.Dialog.UploadFailDialogFragment;
import com.yuhan.yangpojang.pochaInfo.Dialog.UploadSuccessDialogFragment;
import com.yuhan.yangpojang.pochaInfo.model.MeetingDTO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// meet: meeting
// pch: pojangmacha
// txtLay: textInputLayout
public class MeetingwriteActivity extends AppCompatActivity {
    //    private ConnectivityManager.NetworkCallback networkCallback;    // 인터넷 연결 여부 확인 콜백 메서드
    private String user_info_uid = null;    // 화원 uid
    private MeetingDTO meeting;              // 번개 객체
    private String pchKey;         // 포차 고유키
    TextInputEditText titleEdt;  // 번개 소개글 EditText
    TextInputLayout titleTxtLay;    // 번개 소개글 컨테이너
    private TimePickerDialog.OnTimeSetListener timeCallBack;    // 번개 시간 선택할 timePickerDialog 콜백 메서드
    private AutoCompleteTextView maxMemberTv;  // 번개 정원 autoCompleteTextView
    private  String[] maxMembers;       // 번개 정원 item 값들을 담는 배열
    private ArrayAdapter<String> arrayAdapter;  // autoCompleteTextView에서 maxMembers 관리할 Adapter
    TextInputLayout minAgeTxtLay;  // 번개 최소 연령 컨테이너
    TextInputLayout maxAgeTxtLay;  // 번개 최대 연령 컨테이너

    TextInputEditText minAgeEdt;     // 번개 최소 연령
    TextInputEditText maxAgeEdt;     // 번개 최대 연령
    private DatabaseReference ref;      // DB 참조 객체
    private ProgressDialog progressDialog;      // 등록 로딩 다이얼로그


    @Override     // onResume(): Activity가 재활성 될 때마다 호출 => 데이터 업데이트 + 초기화에 사용
    protected void onResume() {
        super.onResume();
        maxMemberTv.setAdapter(arrayAdapter);   // 번개 정원 선택을 위한 adapter 연결
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingwrite);

        /*
        *  // 네트워크 연결 상태 확인
        if (NetworkUtils.isNetworkAvailable(this)) {
            // 인터넷 연결 가능
            // 이곳에서 필요한 작업을 수행
        } else {
            // 인터넷 연결 끊김
            // 이곳에서 필요한 작업을 수행
        }
        * */

        // 객체 생성 및 초기화
        meeting = new MeetingDTO();         // 번개 객체
        TextView pchNameTv = findViewById(R.id.tv_meetingwrite_pochaName);  // 포차 이름 TextView
        Button registerBtn = findViewById(R.id.btn_meetingwrite_register);  // 번개 등록 Button
        Button cancelBtn = findViewById(R.id.btn_meetingwrite_cancel);  // 번개 취소 Button
        titleEdt = findViewById(R.id.edt_meetingwrite_title);  // 번개 소개글 EditText
        titleTxtLay = findViewById(R.id.txtLay_meetingwrite_titleContainer);    // 번개 소개글 컨테이너
        TextView dateTv = findViewById(R.id.tv_meetingwrite_date);      // 번개 날짜(당일) TextView
        TextView timeTv = findViewById(R.id.tv_meetingwrite_time);      // 번개 시간 TextView
        ImageView verifiedImg = findViewById(R.id.img_meetingwrite_verified);   // 포차 인증 여부
        maxMemberTv = findViewById(R.id.autoTxt_meetingwrite_member);  // 번개 정원 autoCompleteTextView
        maxMembers = getResources().getStringArray(R.array.maxMembers);     // 정원 item
        // new ArrayAdapter<>(눈에 나타낼 xml, drop down되는 xml, 표시할 배열)
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropitem_pchmeeting_member, maxMembers);
        minAgeTxtLay = findViewById(R.id.txtLay_meetingwrite_minAgeContainer);  // 번개 최소 연령 컨테이너
        minAgeEdt = findViewById(R.id.edt_meetingwrite_minAge);    // 번개 최소 연령
        maxAgeTxtLay = findViewById(R.id.txtLay_meetingwrite_maxAgeContainer);  // 번개 최대 연령 컨테이너
        maxAgeEdt = findViewById(R.id.edt_meetingwrite_maxAge);    // 번개 최대 연령
        progressDialog = new ProgressDialog(this);      // 등록 로딩 Dialog
        ref = FirebaseDatabase.getInstance().getReference();    // DB 참조 객체

        // ▼ PochameetingFragment에서 전달 받은 데이터 받아 처리
        Intent intent = getIntent();
        if (intent != null){
            pchKey = intent.getStringExtra("pchKey");         // 포차 고유키
            String pchName = intent.getStringExtra("pchName");      // 포차 이름
            Boolean verified = intent.getBooleanExtra("verified", false);   // 포차 인증 여부

            if(verified){
                // 인증된 포차인 경우, 인증 이미지가 보이도록 설정
                verifiedImg.setVisibility(View.VISIBLE);
            }else {
                // 인증되지 않은 포차인 경우, 인증 이미지가 안 보이도록 설정
                verifiedImg.setVisibility(View.GONE);
            }

            // 포차 이름 변경
            pchNameTv.setText(pchName);
        }

        // firebase에서 회원 id 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
            // 회원 id를 번개 객체(MeetingDTO)에 저장
            meeting.setHostUid(user_info_uid);
        }else {
            // 로그인 회원 id를 못 가져온 경우
            Toast.makeText(getApplicationContext(),"사용자 로그인 정보를 찾을 수 없습니다\n다시 로그인 후 사용해주시기 바랍니다", Toast.LENGTH_LONG).show();
            finish();   // 현재 액티비티 종료
        }

        // timePickerDialog에서 번개 시간이 선택되면 호출되는 메서드 구현
        timeCallBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                meeting.setTime(hourOfDay+"시 "+minute+"분");     // 번개 객체에 저장(번개 시간)
                timeTv.setText(hourOfDay+"시 "+minute+"분");      // 화면의 번개 시간 변경
            }
        };
        // 처음 화면의 번개 날짜에 보여줄 현재 날짜 구하기
        String yearDate = (getCurrentDateAndTime().get("yearDate"));
        if(yearDate != null){
            // yearDate 값이 존재하는 경우
            dateTv.setText(yearDate);       // 화면의 번개 날짜를 당일로 변경
            meeting.setYearDate(yearDate);      // 번개 객체에 저장
        }

        // 등록 로딩 다이얼로그 설정
        progressDialog.setMessage("번개 등록 중...");    // 로딩 메시지 설정
        progressDialog.setCancelable(false);    // 취소 불가능
        progressDialog.setCanceledOnTouchOutside(false);     // 외부 터치 불가능


        // 번개 소개글 입력에 따른 에러 메시지 출력
        titleErrorMessage(titleEdt, titleTxtLay);
        // 번개 최소 연령대 입력에 따른 에러 메시지 출력
        minAgeErrorMessage(minAgeEdt, minAgeTxtLay);
        // 번개 최대 연령대 입력에 따른 에러 메시지 출력
        maxAgeErrorMessage(maxAgeEdt, maxAgeTxtLay);

        // 번개 시간을 선택할 리스너 연결
        timeTv.setOnClickListener(selectTime);
        // 번개 등록 리스너 연결
        registerBtn.setOnClickListener(registerMeeting);

        // ▼ 번개 취소 버튼 클릭한 경우, 현재 Activity 종료 코드
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       // 현재 Activity 종료
            }
        });
    }

    // ▼ 현재 날짜 구하는 코드
    private Map<String, String> getCurrentDateAndTime(){
        // ▼ 현재 시간 구하는 코드
        long currentTimeMillis = System.currentTimeMillis();    // 현재 시간(밀리초)로 가져오기
        Date now = new Date(currentTimeMillis);      // 현재 시간(밀리초)를 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh시 mm분");     // 변환할 형식 지정
        String currentDate = dateFormat.format(now);       // 날짜 형식 변환
        Map<String, String> dateMap = new HashMap<>();    // 필요한 유형의 날짜들을 HashMap 형태로 저장할 Map 객체

        String yearDate = currentDate.substring(0, currentDate.indexOf(" "));  // yyyy/MM/dd 문자열 분리
        String date = yearDate.substring(yearDate.indexOf("/")+1);    // MM/dd 문자열 분리
        String registerTime = currentDate.substring(currentDate.indexOf(" ")+1);   // hh:mm 문자열 분리

        // Map에 값 저장
        dateMap.put("yearDate", yearDate);      // 날짜(년도O)
        dateMap.put("date", date);      // 날짜(년도X)
        dateMap.put("registerTime", registerTime);      // 시간(시,분)

        return dateMap;
    }

    // ▼ 버튼 클릭한 경우, 번개 등록
    View.OnClickListener registerMeeting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 번개 객체에 데이터 저장
            meeting.setTitle(titleEdt.getText().toString().trim());    // 문자열 양 끝단의 공백을 제거 후, 번개 소개글 저장
            String maxMember = maxMemberTv.getText().toString();
            String maxMem = maxMember.substring(0, maxMember.indexOf("명"));  // 번개 정원에서 숫자만 분리("명" 제외)
            meeting.setMaxMember(Integer.parseInt(maxMem));     // 번개 정원 저장
            if (!TextUtils.isEmpty(minAgeEdt.getText().toString())) {
                meeting.setMinAge(Integer.parseInt(minAgeEdt.getText().toString()));     // 번개 최소 연령 저장
            }
            if (!TextUtils.isEmpty(maxAgeEdt.getText().toString())) {
                meeting.setMaxAge(Integer.parseInt(maxAgeEdt.getText().toString()));     // 번개 최대 연령 저장
            }
            meeting.setDate(getCurrentDateAndTime().get("date"));   // 날짜(년도X)
            meeting.setRegisterTime(getCurrentDateAndTime().get("registerTime"));   // 날짜

            // 번개 객체의 모든 필드에 값이 존재하는 경우
            if (isValid()) {
                // 번개 연령 범위가 적절한 경우
                if ((minAgeTxtLay.getError() == null) && (maxAgeTxtLay.getError() == null)) {
                    progressDialog.show();      // 로딩 화면 표시

                    // 번개 id 생성 후 획득
                    String meetingKey = ref.child("meeting").push().getKey();
                    // shop의 hasMeeting이 false인 경우, true로 변경
                    ref.child("shops/"+pchKey+"/hasMeeting").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean hasMeeting = snapshot.getValue(Boolean.class);  // 현재 hasMeeting의 값 얻기

                            Log.e("test1", hasMeeting.toString());

                            ref.child("user-info/"+meeting.getHostUid()+"/user_Nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userNickName = snapshot.getValue(String.class);      // 개최자 닉네임 가져오기

                                    // firebase에 저장
                                    Map<String, Object> meetingInsert = new HashMap<>();
                                    // meeting 테이블에 저장
                                    // meeting > 포차 id > 번개 id > 번개 정보
                                    meetingInsert.put("/meeting/"+pchKey+"/"+meetingKey, meeting);
                                    // myMeeting 테이블에 저장
                                    // myMeeting > uid > 번개 id : 포차 id
                                    meetingInsert.put("/myMeeting/"+meeting.getHostUid()+"/"+meetingKey, pchKey);
                                    // meetingAttenders
                                    // meetingAttenders > 번개 id > uid : 닉네임
                                    meetingInsert.put("meetingAttenders/"+meetingKey+"/"+meeting.getHostUid(), userNickName);

                                    Log.e("test1", "판단하기 전");

                                    if(!hasMeeting){
                                        // shops 테이블에 저장
                                        // shops > 포차 id > hasMeeting
                                        meetingInsert.put("shops/"+pchKey+"/hasMeeting", true);
                                        Log.e("test1", "판단");

                                    }
                                    Log.e("test1", "판단하기 후");

                                    // firebase에 업로드
                                    ref.updateChildren(meetingInsert, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null){
                                                // 업로드 성공한 경우,
                                                progressDialog.dismiss();       // 로딩 화면 숨기기
                                                showUploadSuccessDialog();      // 성공 다이얼로그 출력
                                            }else {
                                                //  업로드 실패 경우
                                                progressDialog.dismiss();       // 로딩 화면 숨기기
                                                showUploadFailDialog();     // 실패 다이얼로그 출력
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else {
                    Toast.makeText(getApplicationContext(), "입력된 연령대의 범위가 옳지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "모든 항목의 입력은 필수입니다", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // ▼ 번개 업로드 성공한 경우, 성공 다이얼로그 출력
    private void showUploadSuccessDialog(){
        FragmentManager frgManager = getSupportFragmentManager();
        UploadSuccessDialogFragment successDialog = new UploadSuccessDialogFragment(); // 업로드 확인 다이어로그 생성 및 초기화
        successDialog.setDialogCallPlace("번개", "등록");   // 번개에서 다이얼로그를 호출함을 전달
        successDialog.show(frgManager, "upload_success_meeting");
    }

    // ▼ 번개 업로드 실패한 경우, 실패 다이어로그 출력
    private void showUploadFailDialog(){
        FragmentManager frgManager = getSupportFragmentManager();
        UploadFailDialogFragment failDialog = new UploadFailDialogFragment(); // 업로드 확인 다이어로그 생성 및 초기화
        failDialog.setDialogCallPlace("번개", "등록");   // 번개에서 다이얼로그를 호출함을 전달
        failDialog.show(frgManager, "upload_fail_meeting");
    }

    // ▼ 버튼 클릭한 경우, TimePickerDialog로 번개 시간 선택
    View.OnClickListener selectTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TimePickerDialog를 화면 출력할 때마다 해당 시점의 시간을 보여줄 수 있도록 onClick 안에 캘린더 객체 생성
            Calendar calendar = Calendar.getInstance();     // 캘린더(TimePickerDialog 구현에 사용할)
            // TimePickerDialog 생성 및 설정
            // is24Hour가 true: AM/PM의 12시간 모드, false:24시간 모드
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeCallBack
                    , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.setTitle("번개 시간 선택");      // timePicker 제목 변경
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);    // timePicker 배경 투명하게 변경
            timePickerDialog.show();    // 화면에 출력

        }
    };

    // ▼ 번개 소개글에 공백 입력한 경우, 에러 메시지 출력 코드
    public void titleErrorMessage(TextInputEditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // text 변경 전에 호출되는 메서드
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // text 변경 될 때 호출되는 메서드
            }
            @Override
            public void afterTextChanged(Editable s) {
                // text 변경 후에 호출되는 메서드
                // TextUtils.isEmpty(): 라이브러리에서 제공하는 'null or 공백' 체크 함수
                // 주의: "   "의 경우, isEmpty()의 결과가 false / ""의 경우, true
                // isEmpty() 사용 시, 문자열의 양 끝단의 공백을 제거 후 사용

                if(s.toString().indexOf(" ") == 0){
                    // 문자열의 맨 앞에 공백이 오는 경우, 공백 제거
                    edt.setText(s.toString().replaceFirst(" ", ""));
                    if(TextUtils.isEmpty(s.toString().trim())){
                        txtLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                        txtLayout.setError("한 글자 이상 입력 필수");
                    }
                }
                if(TextUtils.isEmpty(s.toString().trim()) || (s.toString() == null)){
                    txtLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                    txtLayout.setError("한 글자 이상 입력 필수");
                }else {
                    txtLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);     // clear text 아이콘 생성
                    // 문자열이 공백이 아닌 경우
                    txtLayout.setError(null);
                }
            }
        });
    }
    // ▼ 번개 최소 연령에 공백 입력 or 값이 최대 연령보다 높은 경우, 에러 메시지 출력
    public void minAgeErrorMessage(TextInputEditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우, 에러 메시지 출력
                    txtLayout.setError("나이 입력 필수");
                    if(!TextUtils.isEmpty(maxAgeEdt.getText().toString())){
                        maxAgeTxtLay.setError(null);
//                        maxAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                        maxAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                    }
                }else {
//                    txtLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);     // clear text 아이콘 생성
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                    if(s.toString().length() == 2){
                        if(s.toString().indexOf("0") == 0){
                            // 십의 자릿 수가 0인 두 자릿 수인 경우, 십의 자리의 0은 생략
                            edt.setText(s.toString().substring(1));
                            edt.setSelection(1);      // 커서를 맨 뒤로 이동
                        }
                    }
                    if(!TextUtils.isEmpty(maxAgeEdt.getText().toString())){
                        int min = Integer.parseInt(s.toString());   // 최소 연령
                        int max = Integer.parseInt(maxAgeEdt.getText().toString());  // 최대 연령
                        if(min > max){
                            // 범위가 틀린 경우
                            txtLayout.setError("잘못된 범위");
                        }else{
                            // 범위가 맞는 경우
                            maxAgeTxtLay.setError(null);
//                            maxAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                            maxAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                        }
                    }
                }
            }

        });
    }
    // ▼ 번개 최대 연령에 공백 입력 or 값이 최소 연령보다 낮은 경우, 에러 메시지 출력
    public void maxAgeErrorMessage(TextInputEditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우, 에러 메시지 출력
                    txtLayout.setError("나이 입력 필수");
                    if(!TextUtils.isEmpty(minAgeEdt.getText().toString())){
                        minAgeTxtLay.setError(null);
//                        minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                        minAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                    }
                }else {
//                    txtLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);     // clear text 아이콘 생성
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                    if((s.toString().length() == 2) && (s.toString().indexOf("0") == 0)){
                        // 십의 자릿 수가 0인 두 자릿 수인 경우, 십의 자리의 0은 생략
                        edt.setText(s.toString().substring(1));
                        edt.setSelection(1);      // 커서를 맨 뒤로 이동
                    }
                    if(!TextUtils.isEmpty(minAgeEdt.getText().toString())){
                        int min = Integer.parseInt(minAgeEdt.getText().toString());   // 최소 연령
                        int max = Integer.parseInt(s.toString());  // 최대 연령
                        if(max < min){
                            txtLayout.setError("잘못된 범위");
                        }else{
                            // 범위가 맞는 경우
                            minAgeTxtLay.setError(null);
//                            minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                            minAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                        }
                    }
                }
            }
        });
    }

    // ▼ 번개 객체의 모든 필드에 값이 존재하는지 확인
    private boolean isValid(){
        return !TextUtils.isEmpty(meeting.getHostUid()) && !TextUtils.isEmpty(meeting.getTitle())
                && !TextUtils.isEmpty(meeting.getYearDate()) && !TextUtils.isEmpty(meeting.getDate())
                && !TextUtils.isEmpty(meeting.getTime()) && (meeting.getMinAge() > 0)
                && (meeting.getMaxAge() > 0) && (meeting.getMaxMember() > 0)
                && !TextUtils.isEmpty(meeting.getRegisterTime());
    }

    // ▼ 화면 터치 시, 키보드를 숨기는 코드
    @Override //focusView의 화면에서 보이는 영역의 위치와 크기 정보
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();     // 현재 포커스를 가진 뷰를 가져옴
        if(focusView != null){
            Rect rect = new Rect();     // 포커스 뷰의 전역적인 가시영역을 가져옴
            focusView.getGlobalVisibleRect(rect);   // 포커스 뷰의 화면에서 보이는 역영의 위치와 크기 등 정보를 rect에 포함
            int x = (int)ev.getX();     // 현재 위치(x)
            int y = (int)ev.getY();     // 현재 위치(y)
            if(!rect.contains(x,y)){    // rect 객체의 위치가 현재 클릭 위치와 다른 경우
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null)
                    // 키보드 숨김
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                // 포커스 제거
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}