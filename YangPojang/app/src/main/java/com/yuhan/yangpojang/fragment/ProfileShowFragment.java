package com.yuhan.yangpojang.fragment;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.Adapter.MyLikeShopAdapter;
import com.yuhan.yangpojang.mypage.Adapter.MyReportShopAdapter;
import com.yuhan.yangpojang.mypage.GetList.MyLikeShopGetList;
import com.yuhan.yangpojang.mypage.GetList.MyReportShopGetList;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;
import com.yuhan.yangpojang.mypage.Model.MyReportShopModel;
import com.yuhan.yangpojang.mypage.UserProfile.LoadUserProfile;
import com.yuhan.yangpojang.mypage.account.accountPage;

import java.util.ArrayList;


public class ProfileShowFragment extends Fragment
{
    private String user_info_uid;

    private LoadUserProfile loadUserProfile; // 프로필을 부르기 위한 클래스
    private TextView userNick;
    private ImageView userImg;


    // 현재 프래그먼트에서 작동하는 버튼들
    private ImageButton accountBtn;
    private ImageButton changeUserImg;

    // 리사이클러 뷰와 리사이클러 뷰 어뎁터
    private RecyclerView likeRecyclerView, reportRecyclerView, reviewRecyclerView, meetingRecyclerView;
    private RecyclerView.Adapter likeAdapter, reportAdapter,reviewAdapter, meetingAdapter;

    View view;

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
            }
        });
        // 화면 내의 활성화 되는 버튼들




        //사용자 정보 불러오기
        userNick = view.findViewById(R.id.userNickname);
        userImg = view.findViewById(R.id.showUserImg);

        loadUserProfile = new LoadUserProfile(user_info_uid, new LoadUserProfile.dataLoadedCallback() {
            @Override
            public void onDataLoaded(String nick, String img) {
                // 프로필 사진을 위한 storage 연결
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child(img);    // LoadUserProfile에서 받아온 이미지 경로를 storage에 연결
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("프로필", "onSuccess: " + img);
                        // 프로필 출력 칸에 보여지도록 설정
                        Glide.with(requireContext())
                                .load(uri)
                                .into(userImg);
                    }
                });

                userNick.setText(nick);
                Log.d("프로필", "변경되야 함" + nick + img);

            }
        });
        //사용자 정보 불러오기




        // 4개의 리사이클러뷰 출력
        //myLikeRecyclerView (내가 좋아요한 가게)
        likeRecyclerView = view.findViewById(R.id.myLikeRecycle);
        likeRecyclerView.setHasFixedSize(true);
        likeRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false)); //리사이클 뷰의 아이템 배치 결정 (가로 스크롤 목록을 생성, 역방향 스크롤 비활성화)

        MyLikeShopGetList myLikeShopGetList = new MyLikeShopGetList();

        myLikeShopGetList.GetMyLikeShopList(user_info_uid , new MyLikeShopGetList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<MyLikeShopModel> shopDatas) {
                if(shopDatas != null ){

                    Log.d(TAG, "onDataLoaded: in main");
                    likeAdapter = new MyLikeShopAdapter(shopDatas,getContext());
                    likeRecyclerView.setAdapter(likeAdapter);
                }
            }
        });


        //myReportRecyclerView (내가 제보한 가게)
        reportRecyclerView = view.findViewById(R.id.myReportRecycle);
        reportRecyclerView.setHasFixedSize(true);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        MyReportShopGetList myReportShopGetList = new MyReportShopGetList();

        myReportShopGetList.GetMyReportShopList(user_info_uid , new MyReportShopGetList.dataLoadedCallback() {
            @Override
            public void onDataLoaded(ArrayList<MyReportShopModel> shopDatas) {
                if(shopDatas != null ){

                    Log.d(TAG, "onDataLoaded: myReportRecycle");
                    reportAdapter = new MyReportShopAdapter(shopDatas,getContext());
                    reportRecyclerView.setAdapter(reportAdapter);
                }
            }
        });





//
//        // myReviewRecyclerView (내가 작성한 리뷰)
//        reviewRecyclerView = view.findViewById(R.id.myReviewRecycle);
//        reviewRecyclerView.setHasFixedSize(true);
//        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//
//        //myMeetingRecyclerView (내 번개)
//        meetingRecyclerView = view.findViewById(R.id.myMeetingRecycle);
//        meetingRecyclerView.setHasFixedSize(true);
//        meetingRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UID 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }


    }


}