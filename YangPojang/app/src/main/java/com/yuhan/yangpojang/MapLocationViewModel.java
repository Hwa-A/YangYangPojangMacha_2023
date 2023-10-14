package com.yuhan.yangpojang;

import androidx.lifecycle.ViewModel;

import com.naver.maps.map.NaverMap;

public class MapLocationViewModel extends ViewModel {
    private NaverMap popNaverMap;

    public NaverMap getNaverMap() {
        return popNaverMap;
    }

    public void setNaverMap(NaverMap naverMap) {
        popNaverMap = naverMap;
    }
}
