package com.yuhan.yangpojang.pochaInfo.meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.MeetingDTO;

// meet: meeting
// pch: pojangmacha
public class MeetingwriteActivity extends AppCompatActivity {
    MeetingDTO meeting;              // 번개 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingwrite);

        // 객체 생성 및 초기화
        meeting = new MeetingDTO();         // 번개 객체
        TextView pchNameTv = findViewById(R.id.tv_meetingwrite_pochaName);  // 포차 이름 TextView

        // ▼ PochameetingFragment에서 전달 받은 데이터 받아 처리
        Intent intent = getIntent();
        if (intent != null){
            String pchKey = intent.getStringExtra("pchKey");         // 포차 고유키
            String pchName = intent.getStringExtra("pchName");      // 포차 이름
            String uid = intent.getStringExtra("uid");              // 회원 id
            // 포차 이름 변경
            pchNameTv.setText(pchName);
            // 포차 고유키, 회원 id를 번개 객체(MeetingDTO)에 저장
            meeting.setPchKey(pchKey);      // 포차 고유키
            meeting.setHostUid(uid);        // 회원 id
        }

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