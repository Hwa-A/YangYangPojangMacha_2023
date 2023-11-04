package com.yuhan.yangpojang.pochaInfo.review;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
   pch: pojangmacha
   tv: TextView
   rev: review
   edt: EditText
   rtb: RatingBar
   imgbtn: ImageButton
*/
public class ReviewwriteActivity extends AppCompatActivity {
    TextView pchNameTv;          // 포차 이름
    Button revRegisterBtn;       // 리뷰 등록 버튼
    Button revCancelBtn;         // 리뷰 취소 버튼
    ReviewDTO reviewDTO;         // 리뷰 정보
    EditText revContentEdt;      // 리뷰 내용
    RatingBar revRtb;            // 리뷰 별점
    ImageButton revImgbtn;       // 리뷰 사진 버튼
    private DatabaseReference revDatabase;      // DB 참조

    ///// 사진 관련
    private final int GALLERY_CODE = 10;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewwrite);
//
//        //// 사진 관련
//        storage = FirebaseStorage.getInstance();
//
//        // 전달 받은 데이터로 변수 초기화
//        Intent intent = getIntent();
//        String pchName = intent.getStringExtra("pchName");      // 포차 이름
//        // String uid = intent.getStringExtra("uid");              // 회원 ID
//        String uid = "나나";
//        // 객체 생성 및 초기화
//        pchNameTv = (TextView) findViewById(R.id.tv_reviewwrite_pochaName);
//        revRegisterBtn = (Button) findViewById(R.id.btn_reviewwrite_register);
//        revCancelBtn = (Button) findViewById(R.id.btn_reviewwrite_cancel);
//        revContentEdt = (EditText)findViewById(R.id.edt_reviewwrite_textContent);
//        reviewDTO = new ReviewDTO(uid, pchName);
//        revRtb = (RatingBar)findViewById(R.id.rtb_reviewwrite_rating);
//        revDatabase = FirebaseDatabase.getInstance().getReference();
//
//        // 사진 버튼
//        revImgbtn = (ImageButton)findViewById(R.id.imgbtn_reviewwrite_picture);
//
//        // 상단의 포차 이름 설정
//        pchNameTv.setText(pchName);
//
//        // 리스너 연결
//        // 버튼 클릭 시, 리뷰 등록
//        revRegisterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = revContentEdt.getText().toString();    // 리뷰 내용
//                reviewDTO.setContent(content);
//                // 현재 시간 구하기
//                long now = System.currentTimeMillis();
//                Date date = new Date(now);
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                reviewDTO.setDate(dateFormat.format(date));
//                // DB에 저장
//                revDatabase.child("reviews").push().setValue(reviewDTO);
//                onBackPressed();
//            }
//        });
//        // 버튼 클릭 시, 리뷰 작성 취소
//        revCancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();       // 현재 Activity 종료
//            }
//        });
//        // 이미지 버튼 클릭 시, 사진 선택
//        revImgbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ContextCompat.checkSelfPermission(ReviewwriteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
//                    // 갤러리 접근 권한이 허용된 경우
//                    // Toast.makeText(getApplicationContext(), "접근 권한 허용 중", Toast.LENGTH_SHORT).show();
//                    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT );
//                    //intent.setType("image/*");
//
//                    if(v.getId() == R.id.imgbtn_reviewwrite_picture)
//                        loadAlbum();
//                }
//                else if(ActivityCompat.shouldShowRequestPermissionRationale(ReviewwriteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == true){
//                    // 갤러리 접근 권한을 명시적으로 거부한 경우,
//                    Toast.makeText(getApplicationContext(), "접근 권한 요청 거부", Toast.LENGTH_SHORT).show();
//                    ActivityCompat.requestPermissions(ReviewwriteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
//
//                }
//                else{
//                    // 권한 요청 메시지 - 처음 권한 요청 시, 나오는 메시지
//                    ActivityCompat.requestPermissions(ReviewwriteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
//                }
//            }
//        });
//
//        // RatingBar 변화 시, 현재 별점 점수 구함
//        revRtb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                reviewDTO.setRating(rating);
//            }
//        });
//
//    }
//    // 권한 요청 메시지 선택에 따른 동작
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode){
//            case 1000:
//                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
//                 //   Toast.makeText(this,"갤러리 접근 권한 실패",Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(this,"갤러리 접근 권한 성공",Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//                Toast.makeText(this,"권한 요청 오류",Toast.LENGTH_LONG).show();
//                break;
//        }
//    }
//
//
//    ///// 사진 관련
//    //갤러리 호출
//    private void loadAlbum(){
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        activityResultLauncher.launch(intent);
//    }
//    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if(result.getResultCode() == RESULT_OK){
//                        Intent intent = result.getData();
//                        Uri uri = intent.getData();
//                        long now = System.currentTimeMillis();
//                        String picName = "review/"+now+".png";         // 사진 이름
//                        StorageReference storageRef = storage.getReference();
//                        StorageReference riversRef = storageRef.child(picName);
//                        UploadTask uploadTask = riversRef.putFile(uri);
//
//                        try{
//                            InputStream in = getContentResolver().openInputStream(intent.getData());
//                            Bitmap img = BitmapFactory.decodeStream(in);
//                            in.close();
//                            revImgbtn.setImageBitmap(img);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(ReviewwriteActivity.this,"사진 업로드 오류",Toast.LENGTH_LONG).show();
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                              //  Toast.makeText(ReviewwriteActivity.this,"사진 정상 업로드",Toast.LENGTH_LONG).show();
//                                reviewDTO.picUrl = taskSnapshot.getStorage().getPath();
//                            }
//                        });
//                    }
//                }
//            });
    }
}