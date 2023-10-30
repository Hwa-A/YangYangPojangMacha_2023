package com.yuhan.yangpojang.pochaInfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.ReviewDTO;

import java.util.ArrayList;

/*
    tv: TextView
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    Context context;
    ArrayList<ReviewDTO> reviewDTOArrayList;

    public ReviewAdapter(Context context, ArrayList<ReviewDTO> reviewDTOArrayList) {
        this.context = context;
        this.reviewDTOArrayList = reviewDTOArrayList;

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_pochareview, parent, false);
        return new ReviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {

        ReviewDTO reviewDTO = reviewDTOArrayList.get(position);
//        holder.writerNameTv.setText(reviewDTO.uid);
//        holder.contentTv.setText(reviewDTO.content);

    }

    @Override
    public int getItemCount() {
        return reviewDTOArrayList.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder{

        TextView writerNameTv, contentTv;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            writerNameTv = itemView.findViewById(R.id.tv_pochareview_writerName);
            contentTv = itemView.findViewById(R.id.tv_reviewwrite_textCount);
        }
    }



    /*
    Context context;
    ArrayList<ReviewDTO> list;

    // 생성자
    public ReviewAdapter(Context context, ArrayList<ReviewDTO> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pochareview, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        ReviewDTO reviewDTO = list.get(position);
        holder.writerNameTv.setText(reviewDTO.getUid());
        holder.contentTv.setText(reviewDTO.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView writerNameTv, contentTv;
        public ReviewViewHolder(@NonNull View itemView){
            super(itemView);

            writerNameTv = itemView.findViewById(R.id.tv_pochareview_writerName);
            contentTv = itemView.findViewById(R.id.tv_reviewwrite_textCount);
        }
    }

*/
}
