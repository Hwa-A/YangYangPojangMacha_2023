package com.yuhan.yangpojang.pochaInfo.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;

import java.util.ArrayList;

/*
    fabtn: FloatingActionButton
    tv: TextView
 */

public class PochareviewFragment extends Fragment {
    TextView writerIdTv;            // 리뷰 작성자
    ///// RecyclerView 관련
    private RecyclerView recyclerView;
    private ArrayList<ReviewDTO> reviewDTOArrayList;
    DatabaseReference databaseReference;
    // ReviewAdapter reviewAdapter;
    // RecyclerView.LayoutManager layoutManager;
    Shop shop;      // 포차 정보를 가진 객체
    String uid;     // 회원 id
    private OnFragmentReloadListener onFrgReloadListener;   // 프래그먼트 재실행하는 인터페이스

    // ▼ 인터페이스 객체 초기화 코드
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentReloadListener){
            onFrgReloadListener = (OnFragmentReloadListener) context;   // 초기화
        }else {
            // 에러 처리
            throw new RuntimeException(context.toString() + "must implement OnFragmentReloadListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochareview, container, false);

        // ▼ PochainfoActivity.java에서 전달한 데이터를 받는 코드
        Bundle bundle = getArguments();
        if(bundle != null){
            shop = (Shop)bundle.getSerializable("shopInfo");    // 포차 객체 초기화
            uid = (String) bundle.getString("uid");             // 회원 id 초기화
        }else {
            // bundle이 null인 경우, 프래그먼트 재실행
            onFrgReloadListener.onFragmentReload("pchReview");
            return view;
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");
        reviewDTOArrayList = new ArrayList<>();



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ReviewDTO reviewDTO = dataSnapshot.getValue(ReviewDTO.class);
                    reviewDTOArrayList.add(reviewDTO);
                  //  Toast.makeText(getActivity(), reviewDTO.getContent(), Toast.LENGTH_SHORT).show();
                }
                /*
                recyclerView = (RecyclerView)view.findViewById(R.id.recyv_pochareview_reviewList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(), reviewDTOArrayList);
                recyclerView.setAdapter(reviewAdapter);
                // reviewAdapter.notifyDataSetChanged();

                 */
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "DB 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });


/*
        ///// RecyclerView 관련
        recyclerView = (RecyclerView)view.findViewById(R.id.recyv_pochareview_reviewList);
        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");
        reviewDTOArrayList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(0);

        // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewAdapter = new ReviewAdapter(getActivity(), reviewDTOArrayList);
        recyclerView.setAdapter(reviewAdapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ReviewDTO reviewDTO = dataSnapshot.getValue(ReviewDTO.class);
                    reviewDTOArrayList.add(reviewDTO);
                    // Toast.makeText(getActivity(), reviewDTO.getContent(), Toast.LENGTH_SHORT).show();
                }
                // reviewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "DB 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
*/
        // ▼ 리뷰 작성 activity에 포차 이름과 회원 ID 값을 주기 위한 코드
        // 객체 생성 및 초기화
        FloatingActionButton reviewWriteFabtn;          // 리뷰 작성 activity 전환 버튼
        reviewWriteFabtn = (FloatingActionButton) view.findViewById(R.id.fabtn_pochareview_writeButton);

        // 버튼 클릭 시, 리뷰 작성 페이지(ReviewwriteActivity)로 이동 및 포차 데이터 전달 코드
        reviewWriteFabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewwriteActivity.class);
                // intent에 ReviewwriteActivity에 전달할 데이터 추가
                intent.putExtra("pchKey", shop.getPrimaryKey());    // 포차 고유키
                intent.putExtra("pchName", shop.getShopName());     // 포차 이름
                intent.putExtra("uid", uid);                        // 회원 id
                // Activity로 전환 및 데이터 전달
                startActivity(intent);
            }
        });

        return view;
    }
/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // dataInitialize();
        Toast.makeText(getActivity(), uid, Toast.LENGTH_SHORT).show();


        recyclerView = (RecyclerView)view.findViewById(R.id.recyv_pochareview_reviewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(), reviewDTOArrayList);
        recyclerView.setAdapter(reviewAdapter);
        // reviewAdapter.notifyDataSetChanged();

    }

 */
}
