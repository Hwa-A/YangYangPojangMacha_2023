package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;
import com.yuhan.yangpojang.pochaInfo.info.PochainfoActivity;

import java.util.ArrayList;

//https://beluga9.tistory.com/281
//https://t-okk.tistory.com/12
//https://www.youtube.com/watch?v=HcPWt69d_wY&list=PLC51MBz7PMyyyR2l4gGBMFMMUfYmBkZxm&index=39
public class MyLikeShopAdapter extends RecyclerView.Adapter<MyLikeShopAdapter.MyLikeShopHolder> {

    private ArrayList<MyLikeShopModel> likeList = new ArrayList<>();
    private Context context;
    //어댑터에서 액티비티 액션을 가져올 때 context가 필요한데 어댑터에는 context가 없다.
    //선택한 액티비티에 대한 context를 가져올 때 필요하다.


    public MyLikeShopAdapter(ArrayList<MyLikeShopModel> likeList, Context context) {
        Log.d("LikeAdapter", "MyLikeShopAdapter: 진입");
        this.likeList = likeList;
        this.context = context;
    }



    public static class MyLikeShopHolder extends RecyclerView.ViewHolder{
        TextView myshop_name;
        TextView myshop_category;
        TextView myshop_add;

        MyLikeShopHolder(View itemview){
            super(itemview);

//            itemview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            myshop_name = itemview.findViewById(R.id.shopName);
            myshop_category = itemview.findViewById(R.id.shopCategory);
            myshop_add = itemview.findViewById(R.id.shopAdd);
        }

    }


    // onCreateViewHolder = 실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    @NonNull
    @Override
    public MyLikeShopAdapter.MyLikeShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_shop_item_view, parent, false);
        MyLikeShopAdapter.MyLikeShopHolder likeShopHolder = new MyLikeShopAdapter.MyLikeShopHolder(view);

        return likeShopHolder;
    }


    // onBindViewHolder = 실제적으로 각 아이템들에 대한 매칭을 시켜준다
    @Override
    public void onBindViewHolder(@NonNull MyLikeShopAdapter.MyLikeShopHolder holder, int position) {
        MyLikeShopModel myLikeShopModel = likeList.get(position);
        Log.d("LikeAdapter", "MyLikeShopAdapter: ViewHolder진입");
        holder.myshop_name.setText(likeList.get(position).getShopName());
        holder.myshop_category.setText(likeList.get(position).getCategory());
        holder.myshop_add.setText(likeList.get(position).getAddressName());

        // 아이템 뷰 클릭시 상세페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭한 항목의 가게 정보를 가져옵니다.
                MyLikeShopModel selectedShop = likeList.get(position);


                // 가게의 고유 식별자를 사용하여 가게 세부 정보 화면으로 이동하는 인텐트를 생성합니다.
                Intent intent = new Intent(context, PochainfoActivity.class);
                intent.putExtra("shopInfo", selectedShop);  // MyLikeShopModel 객체를 추가 데이터로 전달

                context.startActivity(intent); // 인텐트 실행
            }
        });
    }


    @Override
    public int getItemCount() {
        return (likeList != null ? likeList.size() : 0);
    }

}