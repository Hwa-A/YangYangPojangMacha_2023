package com.yuhan.yangpojang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pochareview, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");
        reviewDTOArrayList = new ArrayList<>();

        // 넘어온 데이터(포차 이름, 회원 ID) 변수에 담기
        String pchName = this.getArguments().getString("pchName");
       // String uid = this.getArguments().getString("uid");

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

        // 버튼 클릭 시, ReviewwriteActivity(리뷰 작성 엑티비티)로 전환
        reviewWriteFabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReviewwriteActivity.class);
                // intent에 ReviewwriteActivity에 전달할 데이터 추가
                intent.putExtra("pchName", pchName);     // 포차 이름(추후 변경)
             //   intent.putExtra("uid", uid);         // 회원 ID(추후 변경)
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
