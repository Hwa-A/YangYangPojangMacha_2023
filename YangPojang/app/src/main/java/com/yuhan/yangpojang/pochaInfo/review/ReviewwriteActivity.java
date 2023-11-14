package com.yuhan.yangpojang.pochaInfo.review;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlendMode;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.Dialog.UploadFailDialogFragment;
import com.yuhan.yangpojang.pochaInfo.Dialog.UploadSuccessDialogFragment;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/*
   pch: pojangmacha
   tv: TextView
   rev: review
   edt: EditText
   rtb: RatingBar
   imgbtn: ImageButton
   txtLay: TextInputLayout
*/
public class ReviewwriteActivity extends AppCompatActivity {
    private String user_info_uid = null;    // 화원 uid
    private ReviewDTO review;       // 리뷰 객체
    private String pchKey;         // 포차 고유키

    private ActivityResultLauncher<Intent> imagePickerLauncher; // 다른 액티비티를 시작 + 반환된 결과 처리 인터페이스
    private List<Uri> selectedImageUris = new ArrayList<>();    // 선택한 리뷰 이미지 URL 저장할 리스트
    private List<ImageButton> imageBtns = new ArrayList<>();    // 리뷰 이미지를 나타낼 버튼 리스트
    private List<ImageButton> imageClearBtns = new ArrayList<>();   // 리뷰 이미지 삭제 버튼 리스트
    private List<Bitmap> imageBitmaps = new ArrayList<>();      // 선택된 사진의 비트맵 리스트
    private List<FrameLayout> imageContainers = new ArrayList<>();      // 리뷰 이미지 컨테이너 리스트
    private int selectedImageCount = 0;     // 선택된 이미지 개수
    private static final int MAX_IMAGE_COUNT = 3;   // 최대 선택 가능한 이미지 수
    private ProgressDialog progressDialog;      // 등록 로딩 다이얼로그
    private Boolean uploadImageCheck = true;    // storeage에 이미지 업로드 성공 여부

    TextView pchNameTv;          // 포차 이름
    TextInputLayout summaryTxtLay;      // 리뷰 내용 컨테이너
    TextInputEditText summaryEdt;      // 리뷰 내용
    RatingBar starRtb;            // 리뷰 별점

    // 액티비티 종료 시, 메모리 해제
    @Override
    protected void onDestroy() {
        if(imageBitmaps != null){
            for(int i=0; i < imageBitmaps.size(); i++){
                Bitmap bitmap = imageBitmaps.get(i);
                bitmap.recycle();
                bitmap = null;
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewwrite);

        // 객체 생성 및 초기화
        review = new ReviewDTO();   // 리뷰 객체
        pchNameTv = findViewById(R.id.tv_reviewwrite_pochaName);    // 포차 이름
        starRtb = findViewById(R.id.rtb_reviewwrite_rating);        // 리뷰 별점 RatingBar
        summaryTxtLay = findViewById(R.id.txtLay_reviewwrite_summaryContainer);     // 리뷰 내용 컨테이너
        summaryEdt = findViewById(R.id.edt_reviewwrite_summary);    // 리뷰 내용 EditText
        progressDialog = new ProgressDialog(ReviewwriteActivity.this);      // 등록 로딩 Dialog

        Button registerBtn = findViewById(R.id.btn_reviewwrite_register);  // 리뷰 등록 Button
        Button cancelBtn = findViewById(R.id.btn_reviewwrite_cancel);      // 리뷰 취소 Button
        // 이미지 관련
        ImageButton imageBtn1 = findViewById(R.id.imgbtn_reviewwrite_picture1);     // 리뷰 이미지 Button1
        ImageButton imageBtn2 = findViewById(R.id.imgbtn_reviewwrite_picture2);     // 리뷰 이미지 Button2
        ImageButton imageBtn3 = findViewById(R.id.imgbtn_reviewwrite_picture3);     // 리뷰 이미지 Button3
        ImageButton imageClearBtn1 = findViewById(R.id.imgbtn_reviewwrite_pictureCancel1);      // 리뷰 이미지 삭제 Button1
        ImageButton imageClearBtn2 = findViewById(R.id.imgbtn_reviewwrite_pictureCancel2);      // 리뷰 이미지 삭제 Button2
        ImageButton imageClearBtn3 = findViewById(R.id.imgbtn_reviewwrite_pictureCancel3);      // 리뷰 이미지 삭제 Button3
        FrameLayout imageContainer1 = findViewById(R.id.framLay_reviewwrite_pictureContainer1);     // 리뷰 이미지 컨테이너1
        FrameLayout imageContainer2 = findViewById(R.id.framLay_reviewwrite_pictureContainer2);     // 리뷰 이미지 컨테이너2
        FrameLayout imageContainer3 = findViewById(R.id.framLay_reviewwrite_pictureContainer3);     // 리뷰 이미지 컨테이너3
        // 이미지를 보여줄 이미지 버튼들을 imageBtns 리스트에 삽입
        imageBtns.add(imageBtn1);
        imageBtns.add(imageBtn2);
        imageBtns.add(imageBtn3);
        // 이미지 삭제 버튼들을 imageClearBtns 리스트에 삽입
        imageClearBtns.add(imageClearBtn1);
        imageClearBtns.add(imageClearBtn2);
        imageClearBtns.add(imageClearBtn3);
        // 이미지 그룹(이미지 버튼 + 이미지 삭제 버튼)들을 imageContainers 리스트에 삽입
        imageContainers.add(imageContainer1);
        imageContainers.add(imageContainer2);
        imageContainers.add(imageContainer3);
        // 이미지 선택을 위한 ActivityResultLauncher 초기화
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> { // 결과 처리 콜백 함수(람다식)
                    if (result.getResultCode() == RESULT_OK) {
                        //  성공적으로 액티비티가 실행된 경우
                        Intent data = result.getData();   // 결과 데이터 가져옴
                        if (data != null) {
                            // 결과 데이터가 있는 경우
                            imagesSelection(data);     // 결과 데이터 처리할 메서드 호출
                        }
                    }
                });

        // ▼ PochareviewFragment에서 전달 받은 데이터 받아 처리
        Intent intent = getIntent();
        if (intent != null) {
            pchKey = intent.getStringExtra("pchKey");         // 포차 고유키
            String pchName = intent.getStringExtra("pchName");      // 포차 이름
            // 포차 이름 변경
            pchNameTv.setText(pchName);
        }

        // firebase에서 회원 id 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
            // 회원 id를 리뷰 객체(ReviewDTO)에 저장
            review.setUid(user_info_uid);
        }else {
            // 로그인 회원 id를 못 가져온 경우
            Toast.makeText(getApplicationContext(),"사용자 로그인 정보를 찾을 수 없습니다\n다시 로그인 후 사용해주시기 바랍니다", Toast.LENGTH_LONG).show();
            finish();   // 현재 액티비티 종료
        }

        // 등록 로딩 다이얼로그 설정
        progressDialog.setMessage("리뷰 등록 중...");    // 로딩 메시지 설정
        progressDialog.setCancelable(false);    // 취소 불가능
        progressDialog.setCanceledOnTouchOutside(false);     // 외부 터치 불가능

        // 리뷰 등록 리스너 연결
        registerBtn.setOnClickListener(registerReview);
        // 리뷰 취소 리스너 연결
        cancelBtn.setOnClickListener(cancelReview);

        // 리뷰 내용의 에러 메시지 출력 시, 에러 아이콘은 없도록 설정
        summaryTxtLay.setErrorIconDrawable(null);
        // 리뷰 내용 입력에 따른 에러 메시지 출력
        summaryErrorMessage(summaryEdt, summaryTxtLay);

        // 버튼을 클릭한 경우, 갤러리 열기
        imageBtn1.setOnClickListener(clickOpenGallery);
        imageBtn2.setOnClickListener(clickOpenGallery);
        imageBtn3.setOnClickListener(clickOpenGallery);

        // 버튼 클릭한 경우, 선택된 이미지 삭제
        deleteSelectedImage(imageClearBtn1, 0);
        deleteSelectedImage(imageClearBtn2, 1);
        deleteSelectedImage(imageClearBtn3, 2);
    }

    // ▼ 클릭한 경우, 리뷰 등록
    View.OnClickListener registerReview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 리뷰 객체에 데이터 저장
            setReviewData();

            if(isValid()){
                // 로딩 화면 표시
                progressDialog.show();

                // 회원 id, 리뷰 별점, 리뷰 내용, 리뷰 작성날짜 모두 입력된 경우
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();  // firebase 참조 객체 생성 및 초기화
                String reviewKey = ref.child("reviews").push().getKey();    // 리뷰 id
                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("review/"+pchKey+"/"+reviewKey); // storage 참조 객체 생성 및 초기화

                if(selectedImageUris.size() > 0){
                    // 선택된 이미지가 있는 경우, storage에 업로드
                    uploadImageAndTransaction(storageRef, reviewKey, ref);
                }else {
                    // 선택된 이미지가 없는 경우, 트랜잭션만 실행
                    Log.e("test1", "선택된 이미지 없음");
                    runReviewTransaction(ref, reviewKey);
                }
            }else {
                // 회원 id, 리뷰 별점, 리뷰 내용, 리뷰 작성날짜 중 하나라도 입력이 안된 경우
                Toast.makeText(getApplicationContext(), "리뷰 내용과 별점 입력은 필수입니다", Toast.LENGTH_LONG).show();
            }
        }
    };

    // ▼ 클릭한 경우, 리뷰 취소
    View.OnClickListener cancelReview = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();       // 현재 액티비티 종료
        }
    };

    // ▼ 이미지 업로드
    private void uploadImageAndTransaction(StorageReference storageRef, String reviewKey, DatabaseReference ref){
        List<String> uploadImagePaths = new ArrayList<>();      // storage에 업로드한 이미지 경로 리스트

        try {
            for (int index=0; index < selectedImageUris.size(); index++){
                Uri imageUri = selectedImageUris.get(index);    // 해당 인덱스의 이미지 uri
                String imageName = "pic" + (index+1);    // 이미지 이름

                // storage에 이미지 업로드
                // review > 포차 id > 리뷰 id > pic + 숫자(차례로 1,2,3) 형식으로 저장
                storageRef.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 업로드 성공한 경우
                        // 업로드 된 이미지의 storage 경로 가져오기
                        Log.e("test1", "업로드 성공");
                        String uploadPath = taskSnapshot.getStorage().getPath();
                        uploadImagePaths.add(uploadPath);

                        if (selectedImageUris.size() == uploadImagePaths.size()){
                            // 선택된 이미지 개수와 업로드 성공한 이미지 개수가 동일한 경우
                            // 즉, 선택된 이미지가 모두 성공적으로 업로드 된 경우
                            // review 객체에 저장된 이미지 경로 저장
                            setReviewImageUrl(uploadImagePaths, ref, reviewKey);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 업로드 실패한 경우
                        Log.e("test1", "에러 메시지: "+e.getMessage());
                        // 트랜잭션이 충돌하거나 실패한 경우
                        progressDialog.dismiss();       // 로딩 화면 숨기기
                        showUploadFailDialog();     // 실패 다이얼로그 출력
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // ▼ 이미지 경로를 review 객체에 저장
    private void setReviewImageUrl(List<String> uploadImagePaths,DatabaseReference ref, String reviewKey){
        for(int i=0; i < uploadImagePaths.size(); i++){
            String imagePath = uploadImagePaths.get(i);    // 해당 인덱스의 경로 가져오기
            char finalCharPath = imagePath.charAt(imagePath.length() - 1);     // 경로의 마지막 문자 가져오기

            if(finalCharPath == '1'){
                review.setPicUrl1(imagePath);
                Log.e("test1", "이미지1: "+review.getPicUrl1());
            }else if(finalCharPath == '2'){
                review.setPicUrl2(imagePath);
                Log.e("test1", "이미지2: "+review.getPicUrl2());
            }else {
                review.setPicUrl3(uploadImagePaths.get(i));
                Log.e("test1", "이미지3: "+review.getPicUrl3());
            }
        }
        Log.e("test1", "객체에 이미지 경로 저장 성공");
        runReviewTransaction(ref, reviewKey);   // 트랜잭션 실행
    }

    // ▼ realtime database에 데이터 저장하는 트랜잭션
    private void runReviewTransaction(DatabaseReference ref, String reviewKey){
        ref.child("shops/" + pchKey + "/rating").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                if (currentData.getValue() == null) {
                    // 데이터가 없는 경우 초기값 설정하는 구간
                    // shops 테이블의 rating은 처음부터 0으로 초기화 되어 있음
                } else {
                    //  AtomicReference : 특정 객체에 대한 참조를 원자적으로 처리하도록 지원
                    //                  : 주로 여러 스레드가 공유하는 변수에 안전하게 값을 업데이트하거나 읽을 때 사용
                    AtomicReference<Float> avgRatingRef = new AtomicReference<>();    // 별점 평균

                    // 현재 포차의 리뷰들의 별점 읽어와 평균 구하기
                    ref.child("reviews/"+pchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                // 데이터가 존재하는 경우
                                float ratingTotal = 0;      // 별점 총합
                                long reviewCnt = snapshot.getChildrenCount();   // 리뷰 총 갯수

                                for(DataSnapshot reviewSnapshot : snapshot.getChildren()){
                                    ReviewDTO rev = reviewSnapshot.getValue(ReviewDTO.class);

                                    if(rev != null){
                                        // 별점을 읽어 총합 구하기
                                        float addRating = rev.getRating();  // 별점 읽어오기
                                        ratingTotal += addRating;   // 총합에 현재 별점 더하기
                                    }
                                }
                                // 평균 구하기(현재 작성 중인 리뷰도 포함)
                                ratingTotal = ratingTotal + review.getRating();  // 총합
                                reviewCnt = reviewCnt + 1;      // 리뷰 갯수
                                float avgRating = getAvgRating(ratingTotal, reviewCnt);   // 평균 구하기
                                avgRatingRef.set(avgRating);
                                Log.e("test1", "평균: "+avgRating);
                            }else {
                                // 현재 firebase에 등록된 리뷰가 존재하지 않는 경우
                                float avgRating = review.getRating();    // 현재 별점으로 별점 평균 변경
                                avgRatingRef.set(avgRating);
                            }
                            Float finalAvgRating = avgRatingRef.get();  // avgRatingRef에 저장된 값 가져오기

                            // firebase에 업로드할 경로와 데이터를 저장할 Map
                            Map<String, Object> uploadReviewMap = new HashMap<>();

                            // review 테이블에 저장
                            // reviews > 포차 id > 리뷰 id > 리뷰 정보
                            uploadReviewMap.put("/reviews/" + pchKey + "/" + reviewKey, review);

                            // myReview 테이블에 저장
                            // myReview > 회원 id > 리뷰 id : 포차 id
                            uploadReviewMap.put("/myReview/" + review.getUid() + "/" + reviewKey, pchKey);

                            // shops 테이블의 해당 포차의 별점 수정
                            // shops > 포차 id > rating
                            uploadReviewMap.put("shops/" + pchKey + "/rating", finalAvgRating);
                            Log.e("test1", "별점: "+finalAvgRating);

                            // firebase에 업로드
                            ref.updateChildren(uploadReviewMap);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 읽기 취소될 때
                            Log.e("test1", error.getMessage());
                        }
                    });
                }
                return Transaction.success(currentData);    // 트랜잭션 성공 알림(사전에 별도로 데이터 저장 필수)
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    // 트랜잭션이 성공적으로 완료한 경우
                    progressDialog.dismiss();       // 로딩 화면 숨기기
                    showUploadSuccessDialog();      // 성공 다이얼로그 출력
                } else {
                    // 트랜잭션이 충돌하거나 실패한 경우
                    progressDialog.dismiss();       // 로딩 화면 숨기기
                    showUploadFailDialog();     // 실패 다이얼로그 출력
                }
            }
        });
    }

    // ▼ 리뷰 업로드 성공한 경우, 성공 다이얼로그 출력
    private void showUploadSuccessDialog() {
        FragmentManager frgManager = getSupportFragmentManager();
        UploadSuccessDialogFragment successDialog = new UploadSuccessDialogFragment(); // 업로드 확인 다이어로그 생성 및 초기화
        successDialog.setDialogCallPlace("리뷰", "등록");   // 리뷰에서 다이얼로그를 호출함을 전달
        successDialog.show(frgManager, "upload_success_review");
    }

    // ▼ 리뷰 업로드 실패한 경우, 실패 다이어로그 출력
    private void showUploadFailDialog() {
        FragmentManager frgManager = getSupportFragmentManager();
        UploadFailDialogFragment failDialog = new UploadFailDialogFragment(); // 업로드 확인 다이어로그 생성 및 초기화
        failDialog.setDialogCallPlace("리뷰", "등록");   // 리뷰에서 다이얼로그를 호출함을 전달
        failDialog.show(frgManager, "upload_fail_review");
    }



    // ▼ 별점 평균 구하기
    private Float getAvgRating(Float totalNum, long reviewCnt){
        Float result = totalNum / reviewCnt;    // 평균 계산(총합 / 리뷰 갯수)
        DecimalFormat decimalFormat = new DecimalFormat("#.#");     // 소수점 한 자릿수까지만 나오도록 형식 지정
        String strResult = decimalFormat.format(result);   // 소수점 첫번째 자리 까지 변환(String형)
        result = Float.parseFloat(strResult);     // float 형으로 변환

        return result;
    }

    // ▼ 리뷰 객체에 필수 데이터 저장
    private void setReviewData(){
        review.setRating(starRtb.getRating());      // 리뷰 별점
        review.setSummary(summaryEdt.getText().toString().trim());    // 리뷰 내용 (문자열 양 끝단의 공백 제거된)
        review.setYearDate(getCurrentDate().get("yearDate"));   // 리뷰 등록 날짜(년도O)
        review.setRegisterTime(getCurrentDate().get("registerTime"));   // 리뷰 등록 시간
    }

    // ▼ 현재 날짜 구하는 코드
    private Map<String, String> getCurrentDate(){
        // ▼ 현재 시간 구하는 코드
        long currentTimeMillis = System.currentTimeMillis();    // 현재 시간(밀리초)로 가져오기
        Date now = new Date(currentTimeMillis);      // 현재 시간(밀리초)를 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh시 mm분");     // 변환할 형식 지정
        String currentDate = dateFormat.format(now);       // 날짜 형식 변환
        Map<String, String> dateMap = new HashMap<>();    // 필요한 유형의 날짜들을 HashMap 형태로 저장할 Map 객체

        String yearDate = currentDate.substring(0, currentDate.indexOf(" "));  // yyyy/MM/dd 문자열 분리
        String registerTime = currentDate.substring(currentDate.indexOf(" ")+1);   // hh:mm 문자열 분리

        // Map에 값 저장
        dateMap.put("yearDate", yearDate);      // 날짜(년도O)
        dateMap.put("registerTime", registerTime);      // 시간(시,분)

        return dateMap;
    }

    // ▼ 클릭한 경우, 갤러리 실행
    View.OnClickListener clickOpenGallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery();  // 갤러리를 실행하는 메서드
        }
    };

    // ▼ 갤러리 실행
    public void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");       // 이미지 형식 지정(이미지에 해당되는 모든 확장자)
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);      // 다중 이미지 선택 허용
        imagePickerLauncher.launch(galleryIntent);  // 갤러리 호출
    }

    // ▼ 갤러리에서 선택된 이미지들 처리
    private void imagesSelection(Intent data){
        // getClipData(): 다중 이미지 선택인 경우에만 사용, URL를 반환
        // 만약 다중 이미지 선택을 지원하지 않는 기기인 경우, getData()로 접근 필요
        if(data.getClipData() != null){
            int itemCount = data.getClipData().getItemCount();  // 선택된 이미지의 개수 획득
            Boolean addImageCheck = true;       // 이미지를 리스트에 추가 가능 여부(기존의 선택된 이미지와 동일한 이미지인 경우, false)
            Boolean equalMessageCheck = true;       // 동일한 이미지가 여럿일 경우, 한번만 메시지가 나오도록 하기 위한 확인
            for (int i = 0; i < itemCount; i++) {       // 선택된 이미지 갯수 만큼 반복문
                if(selectedImageCount < MAX_IMAGE_COUNT){
                    // 선택된 이미지 수가 최대 선택 가능한 이미지 수 이하인 경우
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    for(int j=0; j < selectedImageUris.size(); j++){
                        if(imageUri.equals(selectedImageUris.get(j))) {
                            // 기존의 선택된 이미지와 동일한 이미지가 선택된 경우
                            if(equalMessageCheck) {
                                Toast.makeText(getApplicationContext(), "이미 동일한 사진이 선택되어었습니다", Toast.LENGTH_SHORT).show();
                                equalMessageCheck = false;  // 메시지가 한 번만 나오도록
                            }
                            addImageCheck = false;
                            break;
                        }else {
                            addImageCheck = true;
                        }
                    }
                    if(addImageCheck) {
                        selectedImageUris.add(imageUri);    // 리스트에 추가
                        selectedImageCount++;   // 이미지 선택 수 증가
                        Bitmap bitmap = loadBitmapFromUri(imageUri);    // 얻어온 Uri로 비트맵 로드
                        imageBitmaps.add(bitmap);       // 이미지 비트맵 리스트에 추가
                    }
                }else{
                    // 선택된 이미지 수가 최대 선택 가능한 이미지 수 초과한 경우
                    Toast.makeText(getApplicationContext(), "사진은 최대 3개 까지만 선택 가능합니다", Toast.LENGTH_LONG).show();
                }
            }
        }else if(data.getData() != null){
            if(selectedImageCount < MAX_IMAGE_COUNT){
                // 단일 이미지 선택인 경우
                Uri imageUri = data.getData();  // 선택된 이미지의 uri 획득
                Boolean addImageCheck = true;       // 이미지를 리스트에 추가 가능 여부(기존의 선택된 이미지와 동일한 이미지인 경우, false)
                for(int i=0; i < selectedImageUris.size(); i++){
                    if(imageUri.equals(selectedImageUris.get(i))) {
                        // 기존의 선택된 이미지와 동일한 이미지가 선택된 경우
                        Toast.makeText(getApplicationContext(), "이미 동일한 사진이 선택되어었습니다", Toast.LENGTH_SHORT).show();
                        addImageCheck = false;
                        break;
                    }else {
                        addImageCheck = true;
                    }
                }
                if(addImageCheck){
                    selectedImageUris.add(imageUri);    // 리스트에 추가
                    // 선택된 이미지 수가 최대 선택 가능한 이미지 수 이하인 경우
                    selectedImageCount++;   // 이미지 선택 수 증가
                    Bitmap bitmap = loadBitmapFromUri(imageUri);    // 얻어온 Uri로 비트맵 로드
                    imageBitmaps.add(bitmap);       // 이미지 비트맵 리스트에 추가
                }
            }else{
                // 선택된 이미지 수가 최대 선택 가능한 이미지 수 초과한 경우
                Toast.makeText(getApplicationContext(), "사진은 최대 3개 까지만 선택 가능합니다", Toast.LENGTH_LONG).show();
            }
        }
        // 선택된 이미지를 화면에 출력
        displaySelectedImages();
    }

    // ▼ 비트맵을 로드
    private Bitmap loadBitmapFromUri(Uri imageUri){
        try {
            // 받아온 Uri로부터 비트맵 생성
            Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), imageUri));
            return bitmap;  // 비트맵 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ▼ 선택된 이미지를 화면에 출력
    private void displaySelectedImages(){
        // 선택된 이미지가 존재하지 않는 경우
        // 기존 이미지 버튼 하나 화면에 출력
        if(selectedImageUris.size() == 0){
            imageContainers.get(0).setVisibility(View.VISIBLE);
            imageBtns.get(0).setVisibility(View.VISIBLE);
        }

        // 선택된 이미지를 이미지 버튼에 출력
        for (int i = 0; i < selectedImageUris.size(); i++) {
            ImageButton imageBtn = imageBtns.get(i);
            Bitmap bitmap = imageBitmaps.get(i);
            ImageButton imageClearBtn = imageClearBtns.get(i);
            FrameLayout imageContainer = imageContainers.get(i);

            if (bitmap != null) {
                // 비트맵을 정상적으로 얻은 경우
                if (imageContainer.getVisibility() == View.GONE) {
                    // 해당 이미지 컨테이너가 화면에 없는 상태인 경우
                    imageContainer.setVisibility(View.VISIBLE);     // 화면에 보이도록 변경
                }
                imageBtn.setImageBitmap(bitmap);    // 현재 이미지 변경
                imageBtn.setClickable(false);       // 현재 이미지 버튼 클릭 막기
                if (imageClearBtn.getVisibility() == View.GONE) {
                    // 해당 이미지 삭제 버튼이 화면에 없는 경우
                    imageClearBtn.setVisibility(View.VISIBLE);     // 화면에 보이도록 변경
                }
                if(i < 2){
                    ImageButton affterImageBtn = imageBtns.get(i+1);
                    FrameLayout affterImageContainer = imageContainers.get(i+1);
                    affterImageContainer.setVisibility(View.VISIBLE);     // 화면에 보이도록 변경
                    affterImageBtn.setVisibility(View.VISIBLE);     // 다음 이미지 버튼이 보이도록 변경
                }
            } else {
                if(i < selectedImageUris.size()){
                    selectedImageCount--;       // 선택된 이미지 감소
                    selectedImageUris.remove(i);    // 해당 인덱스의 uri 제거
                    imageBitmaps.remove(i);     // 해당 인덱스의 비트맵 삭제 후, 삭제된 비트맵 반환
                }
                // 비트맵 못 얻은 경우
                Toast.makeText(getApplication(), "이미지를 제대로 로드하지 못하였습니다", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ▼ 이미지 삭제 및 관련 메모리 해제
    private void deleteSelectedImage(ImageButton imageButton, int index){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < selectedImageUris.size()){
                    // 선택한 인덱스가 선택된 이미지의 Url 리스트의 크기(개수)보다 적을 때 실행
                    selectedImageCount--;       // 선택된 이미지 감소
                    selectedImageUris.remove(index);    // 해당 인덱스의 uri 제거
                    Bitmap bitmap = imageBitmaps.remove(index);     // 해당 인덱스의 비트맵 삭제 후, 삭제된 비트맵 반환
                    // 비트맵 메모리 해제
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    // 맨 끝의 이미지 버튼 비활성
                    int uriSize = selectedImageUris.size();
                    ImageButton maxImageBtn = imageBtns.get(uriSize);
                    ImageButton maxImageClearBtn = imageClearBtns.get(uriSize);
                    FrameLayout maxImageContainer = imageContainers.get(uriSize);

                    // 삭제한 이미지 버튼의 이미지를 기존 이미지로 변경
                    maxImageBtn.setImageResource(R.drawable.pochareview_camera);

                    for(int i=uriSize; i<3; i++){
                        ImageButton goneImageBtn = imageBtns.get(i);
                        ImageButton goneImageClearBtn = imageClearBtns.get(i);
                        FrameLayout goneImageContainer = imageContainers.get(i);
                        // 삭제된 이미지의 버튼들 화면에서 삭제
                        goneImageBtn.setClickable(true);
                        goneImageClearBtn.setVisibility(View.GONE);
                        goneImageContainer.setVisibility(View.GONE);
                    }
                }

                // 다시 이미지를 화면에 출력
                displaySelectedImages();
            }
        });
    }

    // ▼ 리뷰 내용에 공백 입력한 경우, 에러 메시지 출력
    public void summaryErrorMessage(TextInputEditText edt, TextInputLayout txtLayout){
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
                        txtLayout.setError("한 글자 이상 입력 필수");
                    }
                }
                if(TextUtils.isEmpty(s.toString().trim()) || (s.toString() == null)){
                    txtLayout.setError("한 글자 이상 입력 필수");
                }else {
                    // 문자열이 공백이 아닌 경우
                    txtLayout.setError(null);
                }
            }
        });
    }

    // ▼ 리뷰 객체의 모든 필드에 값이 존재하는지 확인
    private boolean isValid(){
        return !TextUtils.isEmpty(review.getUid()) && (review.getRating() > 0)
                && !TextUtils.isEmpty(review.getSummary()) && !TextUtils.isEmpty(review.getYearDate())
                && !TextUtils.isEmpty(review.getRegisterTime());
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