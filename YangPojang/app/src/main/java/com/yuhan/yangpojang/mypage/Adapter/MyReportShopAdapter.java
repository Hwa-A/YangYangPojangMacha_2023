package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.Model.MyReportShopModel;

import java.util.ArrayList;

public class MyReportShopAdapter extends RecyclerView.Adapter<MyReportShopAdapter.MyReportShopHolder> {

    private ArrayList<MyReportShopModel> reportList = new ArrayList<>();
    private Context context;


    public MyReportShopAdapter(ArrayList<MyReportShopModel> reportList, Context context) {
        this.reportList = reportList;
        this.context = context;
    }


    public class MyReportShopHolder extends RecyclerView.ViewHolder {
        TextView myshop_name;
        TextView myshop_category;
        TextView myshop_add;

        public MyReportShopHolder(View itemView) {
            super(itemView);

            myshop_name = itemView.findViewById(R.id.shopName);
            myshop_category = itemView.findViewById(R.id.shopCategory);
            myshop_add = itemView.findViewById(R.id.shopAdd);
        }
    }

    @NonNull
    @Override
    public MyReportShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.profile_shop_item_view, parent, false); //xml레이아웃 파일을 뷰 객체로 인스턴스화
        MyReportShopAdapter.MyReportShopHolder reportShopHolder = new MyReportShopAdapter.MyReportShopHolder(view);
        return reportShopHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyReportShopHolder holder, int position) {
        MyReportShopModel myReportShopModel = reportList.get(position);

        holder.myshop_name.setText(reportList.get(position).getShopName());
        holder.myshop_category.setText(reportList.get(position).getCategory());
        holder.myshop_add.setText(reportList.get(position).getAddressName());
    }

    @Override
    public int getItemCount() {
        return (reportList != null ? reportList.size() : 0);
    }
}