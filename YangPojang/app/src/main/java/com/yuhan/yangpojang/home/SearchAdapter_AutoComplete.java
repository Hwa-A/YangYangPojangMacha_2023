package com.yuhan.yangpojang.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuhan.yangpojang.R;

import java.util.List;

public class SearchAdapter_AutoComplete extends BaseAdapter {
    private final Context context;
    private final List<String> nameList;
    private final List<String> addressList;

    public SearchAdapter_AutoComplete(Context context, List<String> nameList, List<String> addressList){
        this.context = context;
        this.nameList = nameList;
        this.addressList = addressList;
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autocompleteview_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.auto_name);
        TextView addressTextView = convertView.findViewById(R.id.auto_add);

        nameTextView.setText(nameList.get(position));
        addressTextView.setText(addressList.get(position));

        return convertView;
    }
}
