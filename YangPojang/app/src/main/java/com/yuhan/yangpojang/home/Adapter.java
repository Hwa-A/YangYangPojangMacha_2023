package com.yuhan.yangpojang.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Store;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Store> mData = null;

    Adapter(ArrayList<Store> list){
        mData = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView category;
        TextView add;
        TextView open_hours;

        ViewHolder(View itemview){
            super(itemview);

            name = itemview.findViewById(R.id.pocha_name);
            category = itemview.findViewById(R.id.pocha_category);
            add = itemview.findViewById(R.id.pocha_add);
            open_hours = itemview.findViewById(R.id.pocha_hours);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.itemview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store item = mData.get(position);

        holder.name.setText(item.getStoreName());
        holder.category.setText(item.getCategory());
        holder.add.setText(item.getStoreAddress());
        holder.open_hours.setText(item.getOpeningHours());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
