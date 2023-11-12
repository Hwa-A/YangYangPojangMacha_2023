package com.yuhan.yangpojang.pochaInfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;
import com.yuhan.yangpojang.pochaInfo.model.ReviewListModel;
import com.yuhan.yangpojang.pochaInfo.review.ReviewGetList;

import java.util.ArrayList;
import java.util.List;

/*
    tv: TextView
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private ArrayList<ReviewListModel> reviewdatas = new ArrayList<>();    // 리뷰 리스트에 보여줄 아이템 리스트
    private Context context;

    public ReviewAdapter(ArrayList<ReviewListModel> reviewdatas, Context context){
        this.reviewdatas = reviewdatas;
        this.context = context;
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // item xml파일을 인스턴스화
        View view = inflater.inflate(R.layout.item_pochareview, parent, false);
        ReviewAdapter.ReviewViewHolder reviewViewHolder = new ReviewAdapter.ReviewViewHolder(view);

        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewListModel reviewmodel = reviewdatas.get(position);

        holder.userNameTv.setText(reviewmodel.getUserName());
        holder.yearDateTv.setText(reviewmodel.getYearDate());
        float rating = reviewmodel.getRating();
        holder.ratingTv.setText(String.valueOf(rating));
        holder.summary.setText(reviewmodel.getSummary());

        // 이미지1이 있는 경우
        if(reviewmodel.getPicUrl1() != null){
            Glide.with(holder.itemView.getContext())
                    .load(reviewmodel.getPicUrl1())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                            .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                    .into(holder.picUriImg1);
        }else {
            // 이미지가 없는 경우, 화면에서 아예 사라짐
            holder.picUriImg1.setVisibility(View.GONE);
        }
        // 이미지2이 있는 경우
        if(reviewmodel.getPicUrl2() != null){
            Glide.with(holder.itemView.getContext())
                    .load(reviewmodel.getPicUrl2())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                            .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                    .into(holder.picUriImg2);
        }else {
            // 이미지가 없는 경우, 화면에서 아예 사라짐
            holder.picUriImg2.setVisibility(View.GONE);
        }
        // 이미지3이 있는 경우
        if(reviewmodel.getPicUrl3() != null){
            Glide.with(holder.itemView.getContext())
                    .load(reviewmodel.getPicUrl3())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                            .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                    .into(holder.picUriImg3);
        }else {
            // 이미지가 없는 경우, 화면에서 아예 사라짐
            holder.picUriImg3.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (reviewdatas.size() > 0 ? reviewdatas.size() : 0);
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTv;    // 유저 닉네임
        private TextView yearDateTv;    // 리뷰 작성 날짜
        private TextView ratingTv;      // 리뷰 별점
        private TextView summary;       // 리뷰 내용
        private ImageView picUriImg1;   // 리뷰 이미지1
        private ImageView picUriImg2;   // 리뷰 이미지2
        private ImageView picUriImg3;   // 리뷰 이미지3

        private ImageButton reportBtn;  // 신고 버튼

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.tv_pochareview_writerName);
            yearDateTv = itemView.findViewById(R.id.tv_pochareview_date);
            ratingTv = itemView.findViewById(R.id.tv_pochareview_rating);
            summary = itemView.findViewById(R.id.tv_pochareview_summary);
            picUriImg1 = itemView.findViewById(R.id.img_pochareview_picUri1);
            picUriImg2 = itemView.findViewById(R.id.img_pochareview_picUri2);
            picUriImg3 = itemView.findViewById(R.id.img_pochareview_picUri3);
            reportBtn = itemView.findViewById(R.id.imgbtn_pochareview_report);
        }
    }

}
