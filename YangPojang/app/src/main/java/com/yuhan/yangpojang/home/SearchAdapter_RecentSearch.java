package com.yuhan.yangpojang.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuhan.yangpojang.R;

import java.util.ArrayList;

public class SearchAdapter_RecentSearch extends BaseAdapter {
    private ArrayList<String> recentsearches;
    private LinearLayout recentIsEmpty_linear;
    private View footer;
    public SearchAdapter_RecentSearch(ArrayList<String> recentSearches, LinearLayout recentIsemtpy_linear, View footer){
        this.recentsearches = recentSearches;
        this.recentIsEmpty_linear = recentIsemtpy_linear;
        this.footer = footer;
    }

    @Override
    public int getCount() {
        return recentsearches.size();
    }

    @Override
    public Object getItem(int position) {
        return recentsearches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recentview_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.recent_name);
        String item = (String) getItem(position);
        text.setText(item);

        ImageView xbtn = (ImageView) convertView.findViewById(R.id.xbtn);
        xbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentsearches.remove(position);
                Log.d("SearchAdapter", "총 아이템: " + recentsearches);
                notifyDataSetChanged();

                //종료하기 전에 recent_searches 저장
                SearchActivity searchActivity = (SearchActivity) context;
                searchActivity.saveRecentSearchesToSharedPreferences();

                // 가시성 설정
                if(recentsearches.size() == 0){
                    footer.setVisibility(View.INVISIBLE);
                    recentIsEmpty_linear.setVisibility(View.VISIBLE);
                }
            }
        });

        return convertView;
    }
}
