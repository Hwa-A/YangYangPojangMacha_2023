package com.yuhan.yangpojang.mypage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.home.PochaListAdapter;

import java.util.ArrayList;

//https://beluga9.tistory.com/281
//https://t-okk.tistory.com/12
//https://www.youtube.com/watch?v=HcPWt69d_wY&list=PLC51MBz7PMyyyR2l4gGBMFMMUfYmBkZxm&index=39
public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MyPageViewHolder> {

    private ArrayList<MyShop> mdata = new ArrayList<>();
    private Context context;

    public static class MyPageViewHolder extends RecyclerView.ViewHolder{
        TextView myshop_name;
        TextView myshop_category;
        TextView myshop_add;

        MyPageViewHolder(View itemview){
            super(itemview);

            myshop_name = itemview.findViewById(R.id.shopName);
            myshop_category = itemview.findViewById(R.id.ShopCategory);
            myshop_add = itemview.findViewById(R.id.shopAdd);
        }
    }


    @NonNull
    @Override
    public MyPageAdapter.MyPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.profile_shop_item_view, parent, false); //xml레이아웃 파일을 뷰 객체로 인스턴스화
        MyPageAdapter.MyPageViewHolder myPageViewHolder = new MyPageAdapter.MyPageViewHolder(view);
        return myPageViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyPageAdapter.MyPageViewHolder holder, int position) {
        MyShop myShop = mdata.get(position);

        Glide.with(context)
                .load(i)
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
