package com.yuhan.yangpojang.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private Context Context;
    private final ArrayList<String> categoryLists;
    private int selectedPosition = -1; // 선택된 아이템 위치 확인 변수
    public CategoryListAdapter(Context context){
        this.Context = context;
        categoryLists = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.category_items)));
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        Button category_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_item = itemView.findViewById(R.id.category_item);
        }
    }

    static Context context;
    @NonNull
    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.categorylist_item, parent, false);
        CategoryListAdapter.ViewHolder viewHolder = new CategoryListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.ViewHolder holder, int position) {
        String item = categoryLists.get(position);
        holder.category_item.setText(item);
        final int currentPosition = holder.getAdapterPosition();

        // 현재 아이템이 선택한 아이템과 일치?
        if(position == selectedPosition){
            holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.btn_color)));
            holder.category_item.setTextColor(Color.WHITE);
        }else{
            // 선택되지 않은 아이템일 경우 원래 색깔로 복원
            holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.category_item.setTextColor(Color.parseColor("#878787"));
        }

        holder.category_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = currentPosition;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }
}
