package com.yuhan.yangpojang.mypage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.mypage.Model.MyLikeShopModel;

import java.util.ArrayList;

//https://beluga9.tistory.com/281
//https://t-okk.tistory.com/12
//https://www.youtube.com/watch?v=HcPWt69d_wY&list=PLC51MBz7PMyyyR2l4gGBMFMMUfYmBkZxm&index=39
public class MyLikeShopAdapter extends RecyclerView.Adapter<MyLikeShopAdapter.MyLikeShopHolder> {

    private ArrayList<MyLikeShopModel> likeList = new ArrayList<>();
    private Context context;
    //어댑터에서 액티비티 액션을 가져올 때 context가 필요한데 어댑터에는 context가 없다.
    //선택한 액티비티에 대한 context를 가져올 때 필요하다.

    public MyLikeShopAdapter(ArrayList<MyLikeShopModel> likeList) {
        this.likeList = likeList;
        this.context = context;

    }


    public static class MyLikeShopHolder extends RecyclerView.ViewHolder{
        TextView myshop_name;
        TextView myshop_category;
        TextView myshop_add;

        MyLikeShopHolder(View itemview){
            super(itemview);

            myshop_name = itemview.findViewById(R.id.shopName);
            myshop_category = itemview.findViewById(R.id.shopCategory);
            myshop_add = itemview.findViewById(R.id.shopAdd);
        }
    }


    // onCreateViewHolder = 실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    @NonNull
    @Override
    public MyLikeShopAdapter.MyLikeShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.profile_shop_item_view, parent, false); //xml레이아웃 파일을 뷰 객체로 인스턴스화
        MyLikeShopAdapter.MyLikeShopHolder likeShopHolder = new MyLikeShopAdapter.MyLikeShopHolder(view);
        return likeShopHolder;
    }


    // onBindViewHolder = 실제적으로 각 아이템들에 대한 매칭을 시켜준다
    @Override
    public void onBindViewHolder(@NonNull MyLikeShopAdapter.MyLikeShopHolder holder, int position) {
        MyLikeShopModel myLikeShopModel = likeList.get(position);

        holder.myshop_name.setText(likeList.get(position).getShopName());
        holder.myshop_category.setText(likeList.get(position).getCategory());
        holder.myshop_add.setText(likeList.get(position).getAddressName());
    }


    @Override
    public int getItemCount() {
        return (likeList != null ? likeList.size() : 0);
    }
}
