package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;
import com.yuhan.yangpojang.mypage.fixReview.ReviewFixPage;
import com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity;

import java.util.ArrayList;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.MyReviewHolder> {

    private ArrayList<MyReviewModel> reviewItemList = new ArrayList<>();
    private Context context;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    public MyReviewAdapter(ArrayList<MyReviewModel> reviewItemList, Context context) {
        this.reviewItemList = reviewItemList;
        this.context = context;
    }

    public class MyReviewHolder extends RecyclerView.ViewHolder {
        TextView myshop_name;
        TextView myshop_category;
        TextView myshop_add;
        ImageView pic1;
        TextView summary;
        RatingBar rating;
        ImageButton fixReviewBtn;

        public MyReviewHolder(View itemView) {
            super(itemView);

            myshop_name = itemView.findViewById(R.id.myReviewShopName);
            myshop_category = itemView.findViewById(R.id.myReviewCategory);
            myshop_add = itemView.findViewById(R.id.myReviewShopAdd);
            pic1 = itemView.findViewById(R.id.myReviewImage);
            summary = itemView.findViewById(R.id.myReviewSummary);
            rating = itemView.findViewById(R.id.myReviewRatingBar);
            fixReviewBtn = itemView.findViewById(R.id.myReviewFixBtn);
        }
    }

    @NonNull
    @Override
    public MyReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.profile_review_item_view, parent, false); //xml레이아웃 파일을 뷰 객체로 인스턴스화
        MyReviewAdapter.MyReviewHolder reviewHolder = new MyReviewAdapter.MyReviewHolder(view);

        return reviewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewAdapter.MyReviewHolder holder, int position) {
        MyReviewModel myReviewModel = reviewItemList.get(position);
        holder.myshop_name.setText(reviewItemList.get(position).getShopName());
        holder.myshop_category.setText(reviewItemList.get(position).getCategory());
        holder.myshop_add.setText(reviewItemList.get(position).getAddressName());
        holder.summary.setText(reviewItemList.get(position).getSummary());
        holder.rating.setRating(reviewItemList.get(position).getMyRating());

        Log.d("리뷰 사진 2", "onBindViewHolder: " + reviewItemList.get(position).getPicUrl3());

        // 사진 받아오기
        String ImgPath = reviewItemList.get(position).getPicUrl1();
        Log.d("리뷰 사진", "ImgPath : " + ImgPath);

        if (ImgPath == null || ImgPath.isEmpty()) {
            Glide.with(context)
                    .load(R.drawable.no_image_pic)
                    .into(holder.pic1);
        } else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(ImgPath);

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .fitCenter()
                            .into(holder.pic1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 사진을 가져오는데 실패했을 때의 동작을 여기에 정의합니다.
                    // 예를 들어, 기본 이미지를 로드할 수 있습니다.
                    Glide.with(context)
                            .load(R.drawable.no_image_pic)
                            .into(holder.pic1);
                }
            });
        }





        // 아이템 뷰 클릭시 상세페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭한 항목의 가게 정보를 가져옵니다.
                MyReviewModel reviewModel = reviewItemList.get(position);


                // 가게의 고유 식별자를 사용하여 가게 세부 정보 화면으로 이동하는 인텐트를 생성합니다.
                Intent intent = new Intent(context, PochainfoActivity.class);
                intent.putExtra("shopInfo", reviewModel);  // MyReportShopModel 객체를 추가 데이터로 전달

                context.startActivity(intent); // 인텐트 실행


            }
        });

        // 버튼 클릭한 경우, 리뷰 정보 수정 액티비티로 이동
        holder.fixReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭한 버튼의 위치에 대한 로그 출력
                Log.d("리뷰 어댑터", "onClick: " + position);
                MyReviewModel reviewModel = reviewItemList.get(position);

                Intent intent = new Intent(context, ReviewFixPage.class);
                intent.putExtra("myReviewInfo", reviewModel);  // MyReportShopModel 객체를 추가 데이터로 전달

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return (reviewItemList != null ? reviewItemList.size() : 0);
    }



}

