package com.yuhan.yangpojang.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private Context Context;
    private ArrayList<String> currentCategoryLists; // 현재 띄울 카테고리 리스트
    private final ArrayList<String> categoryLists; // 일반 카테고리 리스트
    private ArrayList<String> categoryAlcoholLists; // 술 카테고리 리스트
    private int selectedPosition = -1; // 선택된 아이템 위치 확인 변수
    
    public CategoryListAdapter(Context context){
        this.Context = context;
        categoryLists = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.category_items)));
        currentCategoryLists = new ArrayList<>(categoryLists);
        categoryAlcoholLists = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.category_alcohol_items)));
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
        String item = currentCategoryLists.get(position);
        holder.category_item.setText(item);

        final int currentPosition = holder.getAdapterPosition();

        // 기본 화면
        if (Objects.equals(item, "전체")) {
            if (selectedPosition == -1) { // 선택된 아이템이 없을 경우 -> "전체"에 포커스
                holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.btn_color)));
                holder.category_item.setTextColor(Color.WHITE);
            } else { // 선택된 아이템이 있을 경우 -> 선택된 아이템에 포커스
                holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                holder.category_item.setTextColor(Color.parseColor("#878787"));
            }
        } else { // 다른 아이템의 경우
            holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.category_item.setTextColor(Color.parseColor("#878787"));
        }


        // 클릭 이벤트 처리
        holder.category_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(item, "술")) { // "술" 클릭 시 리스트 교체
                    currentCategoryLists.clear();
                    currentCategoryLists.addAll(categoryAlcoholLists);
                } else if (Objects.equals(item, "←")) { // "<-" 클릭 시 리스트 복원
                    currentCategoryLists.clear();
                    currentCategoryLists.addAll(categoryLists);
                }

                if (selectedPosition == currentPosition) {
                    selectedPosition = -1; // 이미 선택된 아이템을 다시 클릭하면 선택 해제 -> "전체"에 포커스
                } else {
                    selectedPosition = currentPosition; // 선택된 위치 업데이트
                }

                notifyDataSetChanged(); // 어댑터 갱신
            }
        });

        // 선택된 위치에 포커스
        if (currentPosition == selectedPosition) {
            holder.category_item.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.btn_color)));
            holder.category_item.setTextColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return currentCategoryLists.size();
    }


}
