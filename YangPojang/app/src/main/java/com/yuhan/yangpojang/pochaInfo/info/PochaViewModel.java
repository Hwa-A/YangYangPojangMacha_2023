package com.yuhan.yangpojang.pochaInfo.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yuhan.yangpojang.model.Shop;

public class PochaViewModel extends ViewModel {
    private MutableLiveData<Shop> shopLiveData = new MutableLiveData<>();

    // Firebase에서 데이터를 업데이트하는 메서드
    public void updateShopData(Shop updatedShop) {
        shopLiveData.setValue(updatedShop);
    }

    public LiveData<Shop> getShopLiveData() {
        return shopLiveData;
    }
}
