package com.yuhan.yangpojang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yuhan.yangpojang.model.Shop;

public class PochainfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        Intent intent = getIntent();
        Shop shop = (Shop) intent.getSerializableExtra("shopInfo");

        if(shop != null){
            TextView textView = findViewById(R.id.text);
            textView.setText(shop.getPrimaryKey());

        }

        Log.d("PochainfoActivity", "좌표값 : " + shop.getLatitude() + ", " + shop.getLongitude());
        Log.d("PochainfoActivity", "가게이름 : " + shop.getShopName());
        Log.d("PochainfoActivity", "가게 주소 : " + shop.getAddressName());
        Log.d("PochainfoActivity", "카테고리 : " + shop.getCategory());
        Log.d("PochainfoActivity", "별점 : " + shop.getRating());
        Log.d("PochainfoActivity", "제보 uid : " + shop.getUid());
        Log.d("PochainfoActivity", "결제방법 : " + shop.isPwayMobile() + shop.isPwayCard() + shop.isPwayAccount() + shop.isPwayCash());
        Log.d("PochainfoActivity", "요일 : " + shop.isOpenMon() + shop.isOpenTue() + shop.isOpenWed() + shop.isOpenThu() + shop.isOpenFri() + shop.isOpenSat() + shop.isOpenSun());

    }
}