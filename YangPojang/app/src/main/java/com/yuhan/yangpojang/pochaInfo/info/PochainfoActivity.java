package com.yuhan.yangpojang.pochaInfo.info;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.StartupTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.HomeFragment;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingFragment;
import com.yuhan.yangpojang.pochaInfo.review.PochareviewFragment;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.io.Serializable;
import java.net.URL;

// pch: pojangmacha
// frg: fragment
// tv: TextView
public class PochainfoActivity extends AppCompatActivity implements OnFragmentReloadListener {
    PochadetailFragment pchDetailFrg;      // 포차 상세정보 Fragment
    PochareviewFragment pchReviewFrg;      // 포차 리뷰 Fragment
    PochameetingFragment pchMeetingFrg;        // 포차 번개 Fragment
    Button pchDetailBtn;                   // 포차 상세정보 Button
    Button pchReviewBtn;                   // 포차 리뷰 Button
    Button pchMeetingBtn;                   // 포차 번개 Button
    FragmentManager frgManager;             // Fragment 관리자
    FragmentTransaction frgTransaction;     // Fragment 트랜잭션 : Fragment 작업을 처리
    private  String pchImagePath;     // firebase storage에 외관 사진이 저장된 경루 : shops/shopkey/images/exterior.jpg
    private Shop shop;          // 포차 정보를 담을 객체
    private  String shopKey;     // 샵키이자 primary키 (getPrimary)
    private PochaViewModel viewModel;
    private TextView categoryTv;     // 카테고리를 적을 textview
    private String category;      // shop 객체에서 가져올 카테고리
    private String storeImageUrl;    //  [http://] 로 시작하는 외관 사진 자체의 url (주소창에 입력시 사진이 뜨는 그 주소 url)
    private  ImageView pchImgview;     // 포차 이미지가 뜨는 view 창
    private String exteriorImageUrl;
    private Handler backgroundHandler;    //  이미지 불러오기 지연 시키기 위해 선언한 핸들러
    private DatabaseReference shopReference;
    private ImageView verifiedImg;      // 인증 이미지

    //    FirebaseDatabase ref = FirebaseDatabase.getInstance();
//    DatabaseReference shops = ref.getReference("shops");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        Log.d("ffffffffffon호출","호출");
        // 객체 생성 및 초기화
        pchDetailBtn = findViewById(R.id.btn_pochainfo_detailTab);
        pchReviewBtn = findViewById(R.id.btn_pochainfo_reviewTab);
        pchMeetingBtn = findViewById(R.id.btn_pochainfo_meetingTab);
        pchDetailFrg = new PochadetailFragment();
        pchReviewFrg = new PochareviewFragment();
        pchMeetingFrg = new PochameetingFragment();
        frgManager = getSupportFragmentManager();
        TextView pchNameTv = findViewById(R.id.tv_pochainfo_pochaname);  // 포차 이름 TextView
        pchImgview = findViewById(R.id.img_pochainfo_pochaImage); // 포차 이미지
        categoryTv=findViewById(R.id.tv_pochainfo_category);
        verifiedImg = findViewById(R.id.img_pochainfo_verified);    // 인증 이미지


        viewModel = new ViewModelProvider(this).get(PochaViewModel.class);




        // ▼ HomeFragment에서 전달 받은 포차 객체 받아 처리
        Intent intent = getIntent();
        if(intent != null){  // Serializable(객체 직렬화): 객체를 바이트로 저장하는 자바의 인터페이스
            shop = (Shop) intent.getSerializableExtra("shopInfo");  // 직렬화된 객체 수신
            shopKey= shop.getPrimaryKey(); // 포차 키 얻기
            String pchName = shop.getShopName(); // 포차 이름 얻기
            category= shop.getCategory();   // 포차 카테고리 얻기
            Log.d("ffffffff히히,", String.valueOf(shop.getVerified()));
            if(shop.getVerified()){
                verifiedImg.setVisibility(View.VISIBLE);
            } else {
                verifiedImg.setVisibility(View.GONE);

            }
            //포차 이미지 얻기 위한 firebase/firebaseStorage 호출
            FirebaseStorage storage = FirebaseStorage.getInstance();

            if( shop.getExteriorImagePath()!=null || shop.getExteriorImagePath()!="")
            {
                pchImagePath= shop.getExteriorImagePath(); // 포차 storage의 경로 얻기
            }


            if(pchImagePath != null && !pchImagePath.isEmpty())  //포차 이미지가 있는 경우(즉 경로가 null이 아닌 경우)
            {
                StorageReference storageRef = storage.getReference().child(pchImagePath);
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    exteriorImageUrl = uri.toString();
                    // 이미지 다운로드 URL 얻기에 성공하면 Glide를 사용하여 이미지를 로드하고 표시
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadAndDisplayImage(exteriorImageUrl);
                        }
                    });
                }).addOnFailureListener(exception -> {
                    // 이미지가 null은 아니지만 다운로드 URL 얻기에 실패할 경우 오류 처리
                    Log.e("PochaInfoActivity - FirebaseImageLoad", "이미지 다운로드 실패: " + exception.getMessage());
                });
            }
            else // 이미지 경로가 없는 경우 (사진 등록이 안되있는 경우)
            {
                pchImgview.setImageResource(R.drawable.full_heart);  // 이미지 없을때 띄워지는 에러 이미지
            }

            pchNameTv.setText(pchName);  //get으로 얻은 이름으로 포차이름 변경

        } //intent가 null이라면 else
        else
        {
            Toast.makeText(this, "해당 가게를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
        }


        // uid
        String uid=null; // FIREBASE에 USERID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("hong", String.valueOf(user));
        if(user!=null) {
            uid = user.getUid();

            // ▼ fragment에 데이터 전달 코드       // Bundle: Map형태로 여러가지의 타입의 값을 저장하는 클래스
            Bundle bundle = new Bundle();               // 전달하기 위해 포차 객체와 회원ID 담을 객체
            bundle.putSerializable("shopInfo", shop);   // 포차 객체
            bundle.putString("uid", uid);               // 회원 id
            // 프래그먼트에 포차 객체 넘기기
            pchDetailFrg.setArguments(bundle);          // 포차 상세 정보 프래그먼트에 전달
            pchReviewFrg.setArguments(bundle);          // 포차 리뷰 프래그먼트에 전달
            pchMeetingFrg.setArguments(bundle);         // 포차 번개 프래그먼트에 전달

        }
        else
        {
            Log.e("ReportShopFragment- userid 오류", "userid 값이 없음");
            user = FirebaseAuth.getInstance().getCurrentUser();

        }

        // 탭 버튼 클릭 시, 화면 전환 - 포차 상세 정보, 리뷰 리스트, 번개 리스트
        pchDetailBtn.setOnClickListener(onClickListener);       // 포차 상세 정보
        pchReviewBtn.setOnClickListener(onClickListener);       // 포차 리뷰 리스트
        pchMeetingBtn.setOnClickListener(onClickListener);      // 포차 번개 리스트
//        // ▼ 처음에 포차 상세 정보 Fragment를 보여주기 위한 코드
//        // Fragment 트랜잭션 객체 생성 및 초기화
//        frgTransaction = frgManager.beginTransaction();
//        // 포차 상세정보 Fragment로 화면 전환
//        frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commitNow();

        //heart이미지 설정
        ImageButton notgoodButton = findViewById(R.id.imgbtn_pochainfo_notgoodButton);
        ImageButton goodButton = findViewById(R.id.imgbtn_pochainfo_goodButton);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.isLikeShop(shop.getShopKey(), notgoodButton, goodButton);

        //heart리스너 설정
        View.OnClickListener heartL = homeFragment.setHeartListener(getApplicationContext(), shop.getShopKey(), notgoodButton, goodButton);
        notgoodButton.setOnClickListener(heartL);
        goodButton.setOnClickListener(heartL);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // v.getResources().getResourceEntryName(v.getId()) : id의 이름(문자)을 반환
            switch (v.getResources().getResourceEntryName(v.getId())) {
                case "btn_pochainfo_detailTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 상세정보 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.WHITE);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_reviewTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 리뷰 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.WHITE);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_meetingTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 번개 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchMeetingFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.WHITE);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    break;
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("ffffffresume","리숨임");
        // 뒤로가기 버튼

//        onBackPressed();


        if (shopKey != null && !shopKey.isEmpty()) {
            shopReference = FirebaseDatabase.getInstance().getReference("shops").child(shopKey);
            shopReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {   // PochaInfoUpdate로 데이터 변화가 생기면 새로 선택된 값을 보여주기 위해 작성된 부분

                    shop = dataSnapshot.getValue(Shop.class); // 최신의 Shop 데이터를 가져옴
                    category=shop.getCategory();
                    storeImageUrl= shop.getFbStoreImgurl();
                    boolean isverifiedImg= shop.getVerified();

                    if(isverifiedImg)
                    {
                        verifiedImg.setVisibility(View.VISIBLE);

                    }
                    else {
                        verifiedImg.setVisibility(View.GONE);

                    }

                    if(category!=null || category!="")
                    {
                        categoryTv.setText(category);
                    }

                    if (storeImageUrl != null || storeImageUrl != "") {
                        loadAndDisplayImage(storeImageUrl);  // 새로 선택된 이미지로 사진이 설정되게 하는 메서드로 이동
                    }
                    else {
                        pchImgview.setImageResource(R.drawable.empty_heart);
                    }

                    viewModel.updateShopData(shop); // ViewModel을 통해 데이터 업데이트
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PochaInfoActivity - Firebase Error", databaseError.getMessage());
                }
            });
        }
    }
    @Override
    public void onFragmentReload(String frgName) {
        if(frgManager != null) {
            switch (frgName){
                case "pchDatail":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 상세정보 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.WHITE);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "pchReview":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 리뷰 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.WHITE);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "pchMeeting":
                    // fragment transaction 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 번개 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchMeetingFrg).commit();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.WHITE);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    break;
            }

        }
    }



    // 이미지를 로드하고 표시하는 함수
    public void loadAndDisplayImage(String imagePath) {
        HandlerThread handlerThread = new HandlerThread("BackgroundThread");
        handlerThread.start();



        // 생성된 스레드의 Looper를 사용하여 Handler를 생성
        backgroundHandler = new Handler(handlerThread.getLooper());

        if(imagePath==null)
        {
            imagePath=exteriorImageUrl;
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("shops/" + shopKey + "/images/exterior.jpg");

            backgroundHandler.postDelayed(() -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    // 이미지 다운로드 URL 얻기에 성공하면 Glide를 사용하여 이미지를 로드하고 표시
                    new Handler(Looper.getMainLooper()).post(() -> {
                        // 액티비티가 파괴되었는지 확인 후 Glide 작업 시작
                        if (!isDestroyed()) {
                            Glide.with(this)
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .skipMemoryCache(true)
                                    .placeholder(R.drawable.img_loading)
                                    .error(R.drawable.pin_black)
                                    .fitCenter()
                                    .into(pchImgview);
                        }
                    });
                }).addOnFailureListener(exception -> {
                    // 이미지가 널은 아니지만 다운로드가 실패한경우
                    Log.e("PochaInfoActivity - glideException", "Glide 로딩 예외처리: " + exception.getMessage(), exception);

                });
            }, 1500); // PochaInfoUpdate에 db 저장되는 속도보다 여기서 이미지 다운후 불러오는 것이 더 빨라서 1.5초후 불러오는 것으로 코드 작성
        } else {
            pchImgview.setImageResource(R.drawable.error);  // 사진 등록이 안되어 있는 경우  에러 이미지로 띄운다

        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Assuming "mDatabaseReference" is your DatabaseReference and "mListener" is ValueEventListener
//
//        shopReference.removeEventListener(mValueEventListener);
//    }
}