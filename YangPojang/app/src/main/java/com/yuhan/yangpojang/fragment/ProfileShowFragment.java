package com.yuhan.yangpojang.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;

import com.yuhan.yangpojang.mypage.Adapter.MyMeetingAdapter;
import com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection.GetAllMyMeetingItems;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.AllMeetingItemModel;
import com.yuhan.yangpojang.mypage.Adapter.MyReviewAdapter;
import com.yuhan.yangpojang.mypage.GetList.ReviewList.MyReviewList;

import com.yuhan.yangpojang.mypage.Model.MyReviewModel;
import com.yuhan.yangpojang.mypage.account.accountPage;
import com.yuhan.yangpojang.mypage.Adapter.MyLikeShopAdapter;
import com.yuhan.yangpojang.mypage.Adapter.MyReportShopAdapter;
import com.yuhan.yangpojang.mypage.GetList.MyLikeShopGetList;
import com.yuhan.yangpojang.mypage.GetList.MyReportShopGetList;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;
import com.yuhan.yangpojang.mypage.Model.MyReportShopModel;
import com.yuhan.yangpojang.mypage.UserProfile.ChangeImgDialog;
import com.yuhan.yangpojang.mypage.UserProfile.LoadUserProfile;

import java.util.ArrayList;


public class ProfileShowFragment extends Fragment {
    private String user_info_uid;

    private LoadUserProfile loadUserProfile; // 프로필을 부르기 위한 클래스
    private TextView userNick;
    public ImageView userImg;


    // 현재 프래그먼트에서 작동하는 버튼들
    private ImageButton accountBtn;
    private ImageButton changeUserImg;

    // 리사이클러 뷰와 리사이클러 뷰 어뎁터
    private RecyclerView likeRecyclerView, reportRecyclerView, reviewRecyclerView, meetingRecyclerView;

    private RecyclerView.Adapter likeAdapter, reportAdapter,reviewAdapter;
    private MyMeetingAdapter meetingAdapter;

    View view;

    private ActivityResultLauncher<String> getPicture;
    private SwipeRefreshLayout swipeRefreshLayout;



    @Nullable  // null 체크유도, 경고를 통해 누락된 체크를 알려줄수 있음
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);


        // 화면 내의 활성화 되는 버튼들
        // accountBtn : 클릭 시 계정 설정 페이지로 넘어감 (accountPage.java , account_page.xml)
        accountBtn = view.findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), accountPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // changeUserImg : 이미지 수정 버튼
        changeUserImg = view.findViewById(R.id.changeImg);
        changeUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("프로필", "onClick: changeUserImg");
                ChangeImgDialog changeImgDialog = new ChangeImgDialog(getContext(), getPicture, user_info_uid);
                changeImgDialog.show();
                changeImgDialog.setuserImg(userImg);
            }
        });
        // 화면 내의 활성화 되는 버튼들



        // 사용자 정보 불러오기
        userNick = view.findViewById(R.id.userNickname);
        userImg = view.findViewById(R.id.showUserImg);

        loadUserProfile = new LoadUserProfile(user_info_uid, new LoadUserProfile.dataLoadedCallback() {
            @Override
            public void onDataLoaded(String nick, String img) {
                if (isAdded()) { // 프래그먼트가 활성화되어 있을 경우에만 작동
                    // 프로필 사진을 위한 storage 연결
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference().child(img);    // LoadUserProfile에서 받아온 이미지 경로를 storage에 연결
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("프로필", "onSuccess: " + img);
                            if (isAdded()) { // 다시 확인
                                // 프로필 출력 칸에 보여지도록 설정
                                Glide.with(requireContext())
                                        .load(uri)
                                        .circleCrop()
                                        .into(userImg);
                            }
                        }
                    });

                    userNick.setText(nick);
                    Log.d("프로필", "변경되야 함" + nick + img);
                } else {
                    // 만약 프래그먼트가 활성화 되지 않았다면 프래그먼트를 다시 연결
                    view = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);
                }
            }

        });
        // 사용자 정보 불러오기



        // 4개의 리사이클러뷰 출력
        //myLikeRecyclerView (내가 좋아요한 가게)
        likeRecyclerView = view.findViewById(R.id.myLikeRecycle);
        likeRecyclerView.setHasFixedSize(true);
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false)); //리사이클 뷰의 아이템 배치 결정 (가로 스크롤 목록을 생성, 역방향 스크롤 비활성화)
        MyLikeShopGetList myLikeShopGetList = new MyLikeShopGetList();

        myLikeShopGetList.getMyLikeShopList(user_info_uid, new MyLikeShopGetList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<MyLikeShopModel> shopDatas) {
                if (shopDatas != null) {

                    Log.d("프로필", "onDataLoaded: in main");
                    likeAdapter = new MyLikeShopAdapter(shopDatas, getContext());
                    likeRecyclerView.setAdapter(likeAdapter);
                }
            }
        });


        //myReportRecyclerView (내가 제보한 가게)
        reportRecyclerView = view.findViewById(R.id.myReportRecycle);
        reportRecyclerView.setHasFixedSize(true);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        MyReportShopGetList myReportShopGetList = new MyReportShopGetList();

        myReportShopGetList.GetMyReportShopList(user_info_uid, new MyReportShopGetList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<MyReportShopModel> shopDatas) {
                if (shopDatas != null) {
                    Log.d("프로필", "onDataLoaded: myReportRecycle");
                    reportAdapter = new MyReportShopAdapter(shopDatas, getContext());
                    reportRecyclerView.setAdapter(reportAdapter);
                } else {
                    Log.d("프로필", "shopDatas null");
                }
            }
        });



        // myReviewRecyclerView (내가 작성한 리뷰)
        reviewRecyclerView = view.findViewById(R.id.myReviewRecycle);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        MyReviewList myReviewList = new MyReviewList();
        myReviewList.getReviewItemInfo(user_info_uid, new MyReviewList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<MyReviewModel> shopDatas) {
                if (shopDatas != null) {
                    Log.d("프로필3", "onDataLoaded: myReportRecycle");
                    reviewAdapter = new MyReviewAdapter(shopDatas, getContext());
                    reviewRecyclerView.setAdapter(reviewAdapter);
                } else {
                    Log.d("프로필3", "shopDatas null");
                }
            }
        });

        //myMeetingRecyclerView (내 번개)
        meetingRecyclerView = view.findViewById(R.id.myMeetingRecycle);
        meetingRecyclerView.setHasFixedSize(true);
        meetingRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));


        GetAllMyMeetingItems getAllMyMeetingItems = new GetAllMyMeetingItems();
        getAllMyMeetingItems.getMeetingInfo(new GetAllMyMeetingItems.allMeetingItemLoadCallback() {
            @Override
            public void onAllLoaded(ArrayList<AllMeetingItemModel> allMeetingItemModels) {
                meetingAdapter = null;
                meetingAdapter = new MyMeetingAdapter(getActivity(), allMeetingItemModels);
                meetingRecyclerView.setAdapter(meetingAdapter);
            }
        });


        return view;
    }

    @Override
    public void onStart (){
        super.onStart();

        // 뒤로가기 구현
        // OnBackPressedCallback 생성
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 뒤로가기 버튼이 눌렸을 때 홈 프래그먼트로 이동
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                if(bottomNavigationView!=null)
                {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_map);
                }
            }
        };

        // OnBackPressedCallback 추가
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UID 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }


        // 갤러리에서 이미지를 선택하는 동작 처리(ActivityResultLauncher 정의)
        getPicture = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        ChangeImgDialog.getPicUri(result);
                        ChangeImgDialog.changeImg.setImageURI(result);
                        ChangeImgDialog.changeImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                });



    }



    }

