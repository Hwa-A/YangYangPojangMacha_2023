package com.yuhan.yangpojang.pochaInfo.info;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.FirebaseUtils;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.HomeFragment;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingFragment;
import com.yuhan.yangpojang.pochaInfo.review.PochareviewFragment;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.Manifest;
// pch: pojangmacha
// frg: fragment
// tv: TextView
public class PochainfoUpdate extends AppCompatActivity {

    private Shop shop ; // 가게 정보를 담을 Shop 객체
    // Firebase 데이터베이스를 초기화합니다
    private PochainfoActivity pochainfoActivity;   // pochainfoActivity 내에 메서드 사용을 위해 선언
    Spinner categorySpinner;    // 정보수정 - 카테고리 수정
    String selectedCategory ;  // 선택된 카테고리를 담은 변수

    CheckBox pwayCash, pwayCard, pwayMobile, pwayAccount;  // 정보수정 - 결제수단
    CheckBox mon, tue, wed, thu, fri, sat, sun;  // 정보수정 - 요일 수정
    ImageView storeExteriorPhoto;  // 정보수정 - 외관사진
    Button reportBtn; // 정보 수정 버튼 ( 제출버튼)
    private ProgressDialog progressDialog;      // 등록 로딩 다이얼로그

    private FirebaseDatabase database ;
    private DatabaseReference shopsRef ;
    private DatabaseReference detailShopRef;  // .child(shopkey) 까지 찾아놓은 db레퍼런스
    private Uri imageUri;
    private Uri storeExteriorImageUri; // 가게 외관 이미지의 URI
    private TextView storePhotoTextView; // 가게 사진 선택하기 글씨
    private String shopkey;
    private int requestCode;   // 어떤 사진을 선택중인지 판별위한 code
    private  String exteriorImageUrl;

//    // 기존 이미지의 Firebase Storage 경로
//    String storeExteriorPhotoPath = "stores/storeExteriorPhoto.jpg";
//    String menuBoardPhotoPath = "stores/menuBoardPhoto.jpg";

    ActivityResultLauncher<Intent> galleryLauncher; // 갤러리 오픈을 위한 intent launcher

    private BottomNavigationView bottomNavigationView;
    private static final int PICK_EXTERIOR_IMAGE_REQUEST = 1;    // 가게-외관이미지 선택

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_shop_detail);
//        String a= FirebaseUtils.getShop().getFbStoreImgurl();
//        String b=FirebaseUtils.getShop().getFbMenuImgurl();
//        Log.d("F","a"+a);
//
//        Log.d("F","a"+b);

        database = FirebaseDatabase.getInstance();
        shopsRef = database.getReference("shops");
        storeExteriorPhoto = findViewById(R.id.storeExteriorPhoto);
        storePhotoTextView= findViewById(R.id.storeBoardText); // 가게 사진 선택하기 글씨
        storeExteriorPhoto = findViewById(R.id.storeExteriorPhoto);
        progressDialog= new ProgressDialog(this);

        // 가게 제보 진행바 추가
        progressDialog.setMessage("가게 정보 수정 중 ...");
        progressDialog.setCancelable(false);    // 취소 불가능
        progressDialog.setCanceledOnTouchOutside(false);     // 외부 터치 불가능

        storeExteriorPhoto.setOnClickListener(new View.OnClickListener() // 가게 외관 사진 등록하기 누르면  갤러리 오픈
        {
            @Override
            public void onClick(View v)
            {
                requestCode=PICK_EXTERIOR_IMAGE_REQUEST;  // 요청코드: 외관 사진을 선택하는 것
                openGallery();
            }
        });




        /*순서
         *  1. openGallery 호출로 갤러리가 열리고 사용자가 이미지를 선택한다
         *  2. 이미지 선택을 결과에 대한 코드가 다음줄부터 진행된다*/
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>()
                {
                    @Override
                    public void onActivityResult(ActivityResult result)
                    {
                        if (result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent data = result.getData();
                            if (data != null)
                            {
                                imageUri = data.getData();
                                if (imageUri != null)
                                {
                                    // 선택한 이미지 URI를 저장
                                    if (requestCode == PICK_EXTERIOR_IMAGE_REQUEST) // 1
                                    {
                                        // storeExteriorImageUri : [content://media/external/images/media/~] 형태 - 안드로이드 내에서 선택된 이미지 경로
                                        storeExteriorImageUri = imageUri;  // db에 넣기위해선 이미지를 uri로
                                        storeExteriorPhoto.setImageURI(imageUri);  // 이미지뷰에 선택한 이미지 단순표시
                                        storeExteriorPhoto.setBackground(null);
                                        storePhotoTextView.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                });

        //UI요소들 찾기
        categorySpinner = findViewById(R.id.categorySpinner);
        pwayCash = findViewById(R.id.pwayCash);
        pwayCard = findViewById(R.id.pwayCard);
        pwayMobile = findViewById(R.id.pwayMobile);
        pwayAccount = findViewById(R.id.pwayAccount);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.Fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        storeExteriorPhoto = findViewById(R.id.storeExteriorPhoto);
        reportBtn = findViewById(R.id.reportBtn);

        // Spinner에 카테고리 목록 설정 -> res/values/strings에 목록있음
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() // 카테고리 스피너 선택 리스너 설정
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                selectedCategory = categorySpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { 
                selectedCategory = "...";     // 카테고리 - 아무것도 선택되지 않았을 때 "..." 으로 표시

            }
        });
        // Set an onClickListener for the report buttonre
        reportBtn.setOnClickListener(view -> {
            // Get the updated data from the UI components
            ArrayList<String> selectedDays = new ArrayList<>();
            String paymentMethods = "";

            if (!pwayCash.isChecked() && !pwayCard.isChecked() && !pwayMobile.isChecked() && !pwayAccount.isChecked())
            {
                Toast.makeText(PochainfoUpdate.this, "결제수단을 하나이상 선택해 주세요", Toast.LENGTH_SHORT).show();
                return; 
            }
            else
            {
                if (pwayCash.isChecked()) { paymentMethods += "현금, "; }
                if (pwayCard.isChecked()) {  paymentMethods += "카드, "; }
                if (pwayMobile.isChecked()){ paymentMethods += "모바일, "; }
                if (pwayAccount.isChecked()) { paymentMethods += "계좌이체, "; }
                // 마지막에 설정된 ,(컴마를 지운다)
                if (!paymentMethods.isEmpty()|| paymentMethods!=null)
                {
                    paymentMethods = paymentMethods.substring(0, paymentMethods.length() - 2);
                }
            }

            if (!mon.isChecked() && !tue.isChecked() && !wed.isChecked() && !thu.isChecked() && !fri.isChecked() && !sat.isChecked() && !sun.isChecked())
            {
                Toast.makeText(PochainfoUpdate.this, "요일을 하나이상 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                if (mon.isChecked()) { selectedDays.add("월"); }
                if (tue.isChecked()) { selectedDays.add("화"); }
                if (wed.isChecked()) { selectedDays.add("수"); }
                if (thu.isChecked()) { selectedDays.add("목"); }
                if (fri.isChecked()) { selectedDays.add("금"); }
                if (sat.isChecked()) { selectedDays.add("토"); }
                if (sun.isChecked()) { selectedDays.add("일"); }
            }


            progressDialog.show();
            // 파이어 베이스에 새 데이터 삽입
            updateInformation(selectedCategory, paymentMethods, selectedDays);
        });
    }

    private void openGallery() {    // 핸드폰 갤러리 여는 역할

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent); // onCreateView안에 작성된 galleryLauncher~~ 부분으로 이동
    }

    private void openGallery(ImageView imageView, String tag) {
        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        if (imageView.getTag() != null && imageView.getTag().equals(tag)) {
                            imageView.setImageURI(result);
                        }
                    }
                });
        imageView.setTag(tag);
        galleryLauncher.launch("image/*");
    }

    private void updateInformation(String selectedCategory, String paymentMethods, List<String> selectedDays) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

      Intent intent = getIntent();
        if(intent!=null)
        {
            Toast.makeText(PochainfoUpdate.this, " [[화면을 이동 하지 말아주세요]] - 정보 업데이트 중 ", Toast.LENGTH_SHORT).show();
            shopkey = intent.getStringExtra("shopKey"); // PochadetailFragment에서 정보수정 버튼으로 받아온 shopkey 를 변수에 넣음
            shop=(Shop)intent.getSerializableExtra("shopInfo");
            String aa = shop.getShopKey();
            String bb = shop.getPrimaryKey();

            if(shopkey!=null)
            {
                Log.d("FFFFFFPochainfoUpdate- shopKey값 확인",shopkey);
                detailShopRef= shopsRef.child(shopkey);
            }

        }
        if (storeExteriorImageUri != null) {
            StorageReference exteriorRef = storageRef.child("shops/" + shopkey + "/images/exterior.jpg");

            // Create a FutureTarget to get the Bitmap from Glide
            FutureTarget<Bitmap> futureTarget = Glide.with(PochainfoUpdate.this)
                    .asBitmap()
                    .load(storeExteriorImageUri)
                    .submit(500, 500); // max width & height

            // Use a separate thread to execute Glide request synchronously
            new Thread(() -> {
                try {
                    // Get the Bitmap from the FutureTarget
                    Bitmap bitmap = futureTarget.get();

                    // Compress the Bitmap and upload to Firebase Storage
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = exteriorRef.putBytes(data);

                    // Handle the upload success and failure
                    uploadTask.addOnSuccessListener(taskSnapshot -> {

                        Log.d("fffhappy",""+exteriorRef);
                        exteriorRef.getDownloadUrl().addOnSuccessListener(uri -> {   // uri is the download URL for the exterior image
                             exteriorImageUrl = uri.toString();
                             Log.d("ffff받아진값",exteriorImageUrl);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pochainfoActivity = new PochainfoActivity();
                                    if(pochainfoActivity!=null)
                                    {
                                        String a= "f";
                                        pochainfoActivity.loadAndDisplayImage(exteriorImageUrl);
                                    }
                                }
                            });

                            // Now you can use 'exteriorImageUrl' as needed
                        }).addOnFailureListener(e -> {
                            // Handle failures to get the download URL
                            Log.d("PochaInfoUpdate- Firebase", "외관 이미지 url 얻어오기에서 에러 발생", e);
                        });

                    }).addOnFailureListener(e -> {
                        Log.e("PochaInfoUpdate- Firebase", "외관 이미지 url 업로딩 과정에서 에러 발생", e);
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).start();
        }



        // 업데이트할 데이터를 Map으로 제작
        Map<String, Object> updates = new HashMap<>();
        updates.put("category", selectedCategory); // 수정한 카테고리

        // 결제방법 수정정보를  업데이트
        updates.put("pwayCash", pwayCash.isChecked());
        updates.put("pwayCard", pwayCard.isChecked());
        updates.put("pwayMobile", pwayMobile.isChecked());
        updates.put("pwayAccount", pwayAccount.isChecked());
        // 오픈일 수정정보를  업데이트
        updates.put("openMon", selectedDays.contains("월"));
        updates.put("openTue", selectedDays.contains("화"));
        updates.put("openWed", selectedDays.contains("수"));
        updates.put("openThu", selectedDays.contains("목"));
        updates.put("openFri", selectedDays.contains("금"));
        updates.put("openSat", selectedDays.contains("토"));
        updates.put("openSun", selectedDays.contains("일"));
        // > shops/shopKey (자식) 경로에 업데이트할 데이터를 전송
        if (detailShopRef != null) 
        {
            detailShopRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FFFFPochaInfoUpdate-Firebase", "데이터 업데이트 성공");

//                        Intent intent2 = new Intent(PochainfoUpdate.this, PochainfoActivity.class); // Replace NewActivity.class with the intended activity to start
//                        intent2.putExtra("shopInfo", shop);
//                        startActivity(intent2);
                        progressDialog.dismiss();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FFFFPochaInfoUpdate-Firebase", "데이터 업데이트 실패", e);
                    });

        } else {
            Toast.makeText(PochainfoUpdate.this, "결제수단을 하나이상 선택해 주세요", Toast.LENGTH_SHORT).show();

            Log.e("FFFFPochaInfoUpdate- firebase","detailShopRef를 찾을수 없습니다");
        }

    }
}

