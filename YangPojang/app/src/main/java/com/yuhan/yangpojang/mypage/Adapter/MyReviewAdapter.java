package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;
import com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity;

import java.util.ArrayList;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.MyReviewHolder>{

    private ArrayList<MyReviewModel> reviewItemList = new ArrayList<>();
    private Context context;

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

        public MyReviewHolder(View itemView) {
            super(itemView);

            myshop_name = itemView.findViewById(R.id.MyReviewShopName);
            myshop_category = itemView.findViewById(R.id.MyReviewCategory);
            myshop_add = itemView.findViewById(R.id.MyReviewShopAdd);
            pic1 = itemView.findViewById(R.id.MyReviewImage);
            summary = itemView.findViewById(R.id.MyReviewSummary);
            rating = itemView.findViewById(R.id.MyReviewRatingBar);
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
        Log.d("마마마지막", "onBindViewHolder: " + reviewItemList.get(position).getShopName());
        holder.myshop_category.setText(reviewItemList.get(position).getCategory());
        holder.myshop_add.setText(reviewItemList.get(position).getAddressName());
        holder.summary.setText(reviewItemList.get(position).getSummary());
        Glide.with(context)
                .load(reviewItemList.get(position))
                .into(holder.pic1);
        holder.rating.setRating((float)reviewItemList.get(position).getMyRating());


        // 아이템 뷰 클릭시 상세페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭한 항목의 가게 정보를 가져옵니다.
                MyReviewModel selectedShop = reviewItemList.get(position);


                // 가게의 고유 식별자를 사용하여 가게 세부 정보 화면으로 이동하는 인텐트를 생성합니다.
                Intent intent = new Intent(context, PochainfoActivity.class);
                intent.putExtra("shopInfo", selectedShop);  // MyReportShopModel 객체를 추가 데이터로 전달

                context.startActivity(intent); // 인텐트 실행
            }
        });
    }

    @Override
    public int getItemCount() {  return (reviewItemList != null ? reviewItemList.size() : 0);  }

}
