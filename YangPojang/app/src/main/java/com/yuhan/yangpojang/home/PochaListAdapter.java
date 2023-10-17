package com.yuhan.yangpojang.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.HomeFragment;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.onPochaListItemClickListener;

import java.util.ArrayList;

public class PochaListAdapter extends RecyclerView.Adapter<PochaListAdapter.ViewHolder> {
    private ArrayList<Shop> mData = new ArrayList<Shop>();
    private static onPochaListItemClickListener mItemListener;

    // 생성자에서 데이터리스트 객체를 전달받음
    public PochaListAdapter(ArrayList<Shop> list, onPochaListItemClickListener listener){
        this.mData = list;
        mItemListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView pochalist_image;
        TextView pochalist_name;
        TextView pochalist_category;
        TextView pochalist_add;
        RatingBar pochalist_rating;

        // 각 아이템 뷰의 구성 요소에 대한 참조 보관
        ViewHolder(View itemview){
            super(itemview);

            // 아이템 뷰가 클릭됐을 때 onPochaListItemClick()메서드 호출하도록 설정
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onPochaListItemClick(v, getBindingAdapterPosition());
                }
            });

            pochalist_image = itemview.findViewById(R.id.pochalist_image);
            pochalist_name = itemview.findViewById(R.id.pochalist_name);
            pochalist_category = itemview.findViewById(R.id.pochalist_category);
            pochalist_add = itemview.findViewById(R.id.pochalist_add);
            pochalist_rating = itemview.findViewById(R.id.pochalist_rating);
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    Context context;
    @NonNull
    @Override
    public PochaListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); //부모의 context 얻어옴 - 각 아이템들을 담을 recyclerview를 의미
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //레이아웃(XML파일)을 실제 뷰 객체로 변환하는데 사용되는 LayoutInflater를 가져옴

        View view = inflater.inflate(R.layout.pochalist_item, parent, false); //xml레이아웃 파일을 뷰 객체로 인스턴스화
        PochaListAdapter.ViewHolder viewHolder = new PochaListAdapter.ViewHolder(view); // 아이템 뷰의 뷰홀더를 생성하고, 이를 해당 아이템 뷰에 연결

        return viewHolder;
    }

    // position에 해당하는 데이터를 뷰 홀더의 아이템뷰에 표시(업데이트)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { //holder : onCreateViewHolder에서 생성한 ViewHolder객체(아이템 뷰 내 요소들에 대한 참조를 갖고있음), position : 현재 아이템의 위치를 나타내는 인덱스
        Shop mainStore = mData.get(position);

        String ExteriorImagePath = mainStore.getExteriorImagePath(); // URL 문자열을 가져옴
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.downloadFireStorage(context, ExteriorImagePath, holder.pochalist_image);

        holder.pochalist_name.setText(mainStore.getShopName());
        holder.pochalist_category.setText(mainStore.getCategory());
        holder.pochalist_add.setText(mainStore.getAddressName());
        holder.pochalist_rating.setRating(mainStore.getRating());
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }
}