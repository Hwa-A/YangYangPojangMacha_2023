package com.yuhan.yangpojang.mypage.fixReview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;

public class ReviewFixPage  extends AppCompatActivity {

    private String shopName;
    private Float myRating;
    private String summary;
    private String pic1, pic2, pic3;
    private MyReviewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        model = (MyReviewModel) intent.getSerializableExtra("MyReviewModel");
        Log.d("리뷰Fixpage", "onCreate: " + model.getSummary());
    }
}
