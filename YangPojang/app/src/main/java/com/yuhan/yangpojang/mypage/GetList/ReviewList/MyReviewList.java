package com.yuhan.yangpojang.mypage.GetList.ReviewList;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.yuhan.yangpojang.mypage.Model.MyReviewModel;

import java.util.ArrayList;

public class MyReviewList {

    private ArrayList<MyReviewModel> reviewItemList = new ArrayList<>(); // 가게 정보 전송용

    public void getMyReviewList(String UID, final DataLoadedCallback callback) {

        ZeroReviewList zeroReviewList = new ZeroReviewList();

        zeroReviewList.getZeroReviewList(UID, new ZeroReviewList.ZeroDataLoadedCallback() {

            @Override
            public void onZeroDataLoaded( final ArrayList<MyReviewModel> reviewDatas, final ArrayList<MyReviewModel> shopDatas){
                MyReviewModel model = new MyReviewModel();
                for (int i = 0; i < reviewDatas.size() ; i++) {
                    model.setShopName(shopDatas.get(i).getShopName());
                    model.setLatitude(shopDatas.get(i).getLatitude());
                    model.setLongitude(shopDatas.get(i).getLongitude());
                    model.setPwayMobile(shopDatas.get(i).isPwayMobile());
                    model.setPwayCard(shopDatas.get(i).isPwayCard());
                    model.setPwayAccount(shopDatas.get(i).isPwayAccount());
                    model.setPwayCash(shopDatas.get(i).isPwayCash());
                    model.setOpenMon(shopDatas.get(i).isOpenMon());
                    model.setOpenTue(shopDatas.get(i).isOpenTue());
                    model.setOpenWed(shopDatas.get(i).isOpenWed());
                    model.setOpenThu(shopDatas.get(i).isOpenThu());
                    model.setOpenFri(shopDatas.get(i).isOpenFri());
                    model.setOpenSat(shopDatas.get(i).isOpenSat());
                    model.setOpenSun(shopDatas.get(i).isOpenSun());
                    model.setStoreImageUri(shopDatas.get(i).getStoreImageUri());
                    model.setMenuImageUri(shopDatas.get(i).getMenuImageUri());
                    model.setAddressName(shopDatas.get(i).getAddressName());
                    model.setCategory(shopDatas.get(i).getCategory());
                    model.setHash(shopDatas.get(i).getHash());
                    model.setFbStoreImgurl(shopDatas.get(i).getFbStoreImgurl());
                    model.setFbMenuImgurl(shopDatas.get(i).getFbMenuImgurl());
                    model.setVerified(shopDatas.get(i).getVerified());
                    model.setHasMeeting(shopDatas.get(i).getHasMeeting());
                    model.setRating(shopDatas.get(i).getRating());
                    model.setGeohash(shopDatas.get(i).getGeohash());
                    model.setExteriorImagePath(shopDatas.get(i).getExteriorImagePath());
                    model.setPrimaryKey(shopDatas.get(i).getPrimaryKey());
                    model.setUid(shopDatas.get(i).getUid());
                    model.setShopID_reviewID(reviewDatas.get(i).getShopID_reviewID());
                    model.setPicUrl1(reviewDatas.get(i).getPicUrl1());
                    model.setMyRating(reviewDatas.get(i).getMyRating());
                    model.setSummary(reviewDatas.get(i).getSummary());
                    Log.d("마지막", "onZeroDataLoaded: " + model.getLongitude());
                    Log.d("마지막", "onZeroDataLoaded: " + model.getSummary());
                    Log.d("마지막", "onZeroDataLoaded: " + model.getShopName());
                    reviewItemList.add(model);
                }
                callback.onDataLoaded(reviewItemList);
            }
        });
    }

    public interface DataLoadedCallback {
        void onDataLoaded(ArrayList<MyReviewModel> reviewItemList);
    }
}
