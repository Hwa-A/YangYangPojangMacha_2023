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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private ReviewDTO review;       // 리뷰 객체
    private String pchKey;         // 포차 고유키

    private ActivityResultLauncher<Intent> imagePickerLauncher; // 다른 액티비티를 시작 + 반환된 결과 처리 인터페이스
    private List<Uri> selectedImageUris = new ArrayList<>();    // 선택한 리뷰 이미지 URL 저장할 리스트
    private List<ImageButton> imageBtns = new ArrayList<>();    // 리뷰 이미지를 나타낼 버튼 리스트
    private List<ImageButton> imageClearBtns = new ArrayList<>();   // 리뷰 이미지 삭제 버튼 리스트
    private int selectedImageCount = 1;     // 선택된 이미지 개수
    private static final int MAX_IMAGE_COUNT = 3;   // 최대 선택 가능한 이미지 수

    TextView pchNameTv;          // 포차 이름
    Button revRegisterBtn;       // 리뷰 등록 버튼
    Button revCancelBtn;         // 리뷰 취소 버튼
    ReviewDTO reviewDTO;         // 리뷰 정보
    TextInputLayout summaryTxtLay;      // 리뷰 내용 컨테이너
    TextInputEditText summaryEdt;      // 리뷰 내용
    RatingBar starRtb;            // 리뷰 별점
    private DatabaseReference revDatabase;      // DB 참조

    ///// 사진 관련
    private final int GALLERY_CODE = 10;
    private FirebaseStorage storage;


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
            String uid = intent.getStringExtra("uid");          // 회원 id
            // 포차 이름 변경
            pchNameTv.setText(pchName);
            // 회원 id를 리뷰 객체(reviewDTO)에 저장
            review.setUid(uid);    // 회원 id
        }

        // 리뷰 내용의 에러 메시지 출력 시, 에러 아이콘은 없도록 설정
        summaryTxtLay.setErrorIconDrawable(null);
        // 리뷰 내용 입력에 따른 에러 메시지 출력
        summaryErrorMessage(summaryEdt, summaryTxtLay);

        // 버튼을 클릭한 경우, 갤러리 열기
        imageBtn1.setOnClickListener(clickOpenGallery);
        imageBtn2.setOnClickListener(clickOpenGallery);
        imageBtn3.setOnClickListener(clickOpenGallery);

        // 버튼 클릭한 경우, 선택된 이미지 삭제
        imageClearBtn1.setOnClickListener(clickClearImage);
    }

    // ▼ 클릭한 경우, 선택되어 보여지고 있는 이미지 삭제
    View.OnClickListener clickClearImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearImageView();   // ImageButton에서 보여지는 선택된 이미지 삭제
        }
    };

    // ▼ 클릭한 경우, ImageButton에서 보여지는 선택된 이미지 삭제
    private void clearImageView(){
        if(selectedImageUris != null && !selectedImageUris.isEmpty()){
            // 이미지가 선택되어 있는 경우
            // 해당 이미지의 URI를 통해 본 어플의 외부 저장소(db)에서 이미지 삭제
            Uri clearUri = selectedImageUris.get(0);
            Log.e("test1", "전");
            try {
                int rowsDeleted = getContentResolver().delete(clearUri, null, null);
                if (rowsDeleted > 0) {
                    Log.e("test1", "삭제됨");
                } else {
                    Log.e("test1", "삭제 실패: 이미지를 찾을 수 없음");
                }
            } catch (Exception e){
                Log.e("test1", "삭제 중 오류 발생: " + e.getMessage());
            }
            Log.e("test1", "삭제됨");
            imageBtns.get(0).setImageResource(R.drawable.pochareview_camera);   // ImageView의 이미지 원상복구
            selectedImageUris.set(0, null);    // ImageView에 이미지가 없음을 표현
        }
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
            for (int i = 0; i < itemCount; i++) {       // 선택된 이미지 갯수 만큼 반복문
                if(selectedImageCount <= MAX_IMAGE_COUNT){
                    // 선택된 이미지 수가 최대 선택 가능한 이미지 수 이하인 경우
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);    // 리스트에 추가
                    selectedImageCount++;   // 이미지 선택 수 증가
                }else{
                    // 선택된 이미지 수가 최대 선택 가능한 이미지 수 초과한 경우
                    Toast.makeText(getApplicationContext(), "사진은 최대 3개 까지만 선택 가능합니다", Toast.LENGTH_LONG).show();
                }
            }
        }else if(data.getData() != null){
            if(selectedImageCount <= MAX_IMAGE_COUNT){
                // 단일 이미지 선택인 경우
                Uri imageUri = data.getData();  // 선택된 이미지의 uri 획득
                selectedImageUris.add(imageUri);    // 리스트에 추가
                // 선택된 이미지 수가 최대 선택 가능한 이미지 수 이하인 경우
                selectedImageCount++;   // 이미지 선택 수 증가
            }else{
                // 선택된 이미지 수가 최대 선택 가능한 이미지 수 초과한 경우
                Toast.makeText(getApplicationContext(), "사진은 최대 3개 까지만 선택 가능합니다", Toast.LENGTH_LONG).show();
            }
        }
        // 선택된 이미지를 화면에 출력
        displaySelectedImages();
    }

    // ▼ 선택된 이미지를 화면에 출력
    private void displaySelectedImages(){
        // 선택된 이미지를 이미지 버튼에
        for (int i = 0; i < selectedImageUris.size(); i++) {
            Uri imageUri = selectedImageUris.get(i);
            ImageButton imageBtn = imageBtns.get(i);
            imageBtn.setVisibility(imageBtn.VISIBLE);
            try {
                Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), imageUri));
                imageBtn.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                if(TextUtils.isEmpty(s.toString().trim())){
                    txtLayout.setError("한 글자 이상 입력 필수");
                    if(s.toString().indexOf(" ") == 0){
                        // 문자열의 맨 앞에 공백이 오는 경우, 공백 제거
                        edt.setText(s.toString().replaceFirst(" ", ""));
                    }
                }else {
                    // 문자열이 공백이 아닌 경우
                    txtLayout.setError(null);
                }
            }
        });
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