package com.yuhan.yangpojang.pochaInfo.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.Dialog.ReviewImageExpandDialog;
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
        holder.summaryTv.setText(reviewmodel.getSummary());

        // firebase storage 참조 객체 생성 및 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // 이미지1이 있는 경우
        if(reviewmodel.getPicUrl1() != null){
            StorageReference storageRef = storage.getReference().child(reviewmodel.getPicUrl1());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // firebase storage에서 제공하는 이미지 url 얻음
                    String imageUri1 = uri.toString();
                    Glide.with(holder.itemView.getContext())
                            .load(imageUri1)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.img_loading)  // 로딩 중 표시할 이미지
                                    .error(R.drawable.loading)    // 이미지 로드 실패 시 표시할 이미지
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                                    .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                            .into(holder.picUriImg1);

                    // 이미지 뷰 클릭한 경우, 해당 이미지 확대
                    holder.picUriImg1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReviewImageExpandDialog.show(context, imageUri1);
                        }
                    });
                }
            });
        }else {
            // 이미지가 없는 경우, 화면에서 아예 사라짐
            holder.picUriImg1.setVisibility(View.GONE);
        }
        // 이미지2이 있는 경우
        if(reviewmodel.getPicUrl2() != null){
            StorageReference storageRef = storage.getReference().child(reviewmodel.getPicUrl2());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // firebase storage에서 제공하는 이미지 url 얻음
                    String imageUri2 = uri.toString();
                    Glide.with(holder.itemView.getContext())
                            .load(imageUri2)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.img_loading)  // 로딩 중 표시할 이미지
                                    .error(R.drawable.loading)    // 이미지 로드 실패 시 표시할 이미지
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                                    .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                            .into(holder.picUriImg2);
                    // 이미지 뷰 클릭한 경우, 해당 이미지 확대
                    holder.picUriImg2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReviewImageExpandDialog.show(context, imageUri2);
                        }
                    });
                }
            });
        }else {
            // 이미지가 없는 경우, 화면에서 아예 사라짐
            holder.picUriImg2.setVisibility(View.GONE);
        }
        // 이미지3이 있는 경우
        if(reviewmodel.getPicUrl3() != null){
            StorageReference storageRef = storage.getReference().child(reviewmodel.getPicUrl3());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // firebase storage에서 제공하는 이미지 url 얻음
                    String imageUri3 = uri.toString();
                    Glide.with(holder.itemView.getContext())
                            .load(imageUri3)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.img_loading)  // 로딩 중 표시할 이미지
                                    .error(R.drawable.loading)    // 이미지 로드 실패 시 표시할 이미지
                                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                                    .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                            .into(holder.picUriImg3);
                    // 이미지 뷰 클릭한 경우, 해당 이미지 확대
                    holder.picUriImg3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReviewImageExpandDialog.show(context, imageUri3);
                        }
                    });
                }
            });
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
        private TextView summaryTv;       // 리뷰 내용
        private ImageView picUriImg1;   // 리뷰 이미지1
        private ImageView picUriImg2;   // 리뷰 이미지2
        private ImageView picUriImg3;   // 리뷰 이미지3


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.tv_pochareview_writerName);
            yearDateTv = itemView.findViewById(R.id.tv_pochareview_date);
            ratingTv = itemView.findViewById(R.id.tv_pochareview_rating);
            summaryTv = itemView.findViewById(R.id.tv_pochareview_summary);
            picUriImg1 = itemView.findViewById(R.id.img_pochareview_picUri1);
            picUriImg2 = itemView.findViewById(R.id.img_pochareview_picUri2);
            picUriImg3 = itemView.findViewById(R.id.img_pochareview_picUri3);
        }
    }

}
