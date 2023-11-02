package com.yuhan.yangpojang.mypage.UserProfile;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.yuhan.yangpojang.R;

//https://wuny-dev.tistory.com/19
// 이미지 변경을 위한 팝업
//https://www.youtube.com/watch?v=nOtlFl1aUCw
public class ChangeImgDialog extends Dialog{

//    ActivityResultLauncher<Intent> resultLauncher;
// 화면 내 작동하는 버튼
    ImageButton changeImg = findViewById(R.id.showChangeUserImg);
    Button applyBtn = findViewById(R.id.applyChangeImg);
    Button cancleBtn = findViewById(R.id.cancleChangeImg);

    public ChangeImgDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_change_userimg_popup);

        ImageButton changeImg = findViewById(R.id.showChangeUserImg);
        Button applyBtn = findViewById(R.id.applyChangeImg);
        Button cancelBtn = findViewById(R.id.cancleChangeImg);

        // 이미지 변경 버튼에 대한 동작 설정
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("팝업", "onClick: changeImg");
                // 이미지 변경 동작 구현
                // 예를 들어 갤러리 열기 등
            }
        });

        // 적용 버튼에 대한 동작 설정
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지 적용 동작 구현
                dismiss(); // 팝업 창 닫기
            }
        });

        // 취소 버튼에 대한 동작 설정
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // 팝업 창 닫기
            }
        });
    }
}
