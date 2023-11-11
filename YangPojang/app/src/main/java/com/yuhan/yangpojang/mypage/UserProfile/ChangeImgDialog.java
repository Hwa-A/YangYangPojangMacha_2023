package com.yuhan.yangpojang.mypage.UserProfile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;

//https://wuny-dev.tistory.com/19
// 이미지 변경을 위한 팝업
//https://www.youtube.com/watch?v=nOtlFl1aUCw
public class ChangeImgDialog extends Dialog{

//    ActivityResultLauncher<Intent> resultLauncher;
// 화면 내 작동하는 버튼

    public static ImageButton changeImg; // 프ꃠ필 이미지를 변경합니다.
    Button applyBtn; // 취소 버튼
    Button cancleBtn; // 적용 버튼

    private Context context;
    private ActivityResultLauncher<String> mGetContent;
    private static Uri pickUri;
    private String user_info_uid;
    private static ImageView userImg;

    private ProgressDialog progressDialog;
    public ChangeImgDialog(@NonNull Context context, ActivityResultLauncher<String> mGetContent,
                           String user_info_uid) {
        super(context);
        this.context = context;
        this.mGetContent = mGetContent;
        this.user_info_uid = user_info_uid;
    }

    public static void getPicUri(Uri pickUri){
        ChangeImgDialog.pickUri = pickUri;
    }
    public void setuserImg(ImageView user){
        userImg = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_change_userimg_popup);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("이미지 업로드 중..."); // 로딩 메시지 설정
        progressDialog.setCancelable(false); // 취소 불가능 설정

        changeImg = findViewById(R.id.showChangeUserImg);
        applyBtn = findViewById(R.id.applyChangeImg);
        cancleBtn = findViewById(R.id.cancleChangeImg);

        // 이미지 변경 버튼에 대한 동작 설정
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("팝업", "onClick: changeImg");
                // 이미지 변경 동작 구현
                // 예를 들어 갤러리 열기 등
                mGetContent.launch("image/*"); // Activity Result 시작
            }
        });


        // 적용 버튼에 대한 동작 설정
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지 적용 동작 구현
                if(pickUri != null){
                    showLoading();
                    updateUsersPicInStorage();
                }
                dismiss(); // 팝업 창 닫기
            }
        });

        // 취소 버튼에 대한 동작 설정
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // 팝업 창 닫기
            }
        });
    }

    // 선택한 사진 스토리지에 업로드 후 프로필 사진 업데이트
    public void updateUsersPicInStorage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profileRef = storage.getReference().child("profile/" + user_info_uid);


        profileRef.putFile(pickUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("ChangeImgDialog", "업로드 성공");

                    String imageUrl = pickUri.toString();
                    Glide.with(context)
                            .load(imageUrl)
                            .error(R.drawable.error)
                            .circleCrop()
                            .into(userImg);
                    hideLoading();
                })
                .addOnFailureListener(e -> {
                    Log.e("ChangeImgDialog", "오류 메세지 " + e);
                });
    }

    private void showLoading() {
        progressDialog.show(); // 로딩 화면 표시
    }

    private void hideLoading() {
        progressDialog.dismiss(); // 로딩 화면 숨기기
    }

}
