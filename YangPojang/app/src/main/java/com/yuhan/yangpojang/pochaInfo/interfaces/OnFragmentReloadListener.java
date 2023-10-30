package com.yuhan.yangpojang.pochaInfo.interfaces;

public interface OnFragmentReloadListener {
    // 프래그먼트 다시 실행할 인터페이스 추상 메소드
    // public abstract가 생략되어 있음 / 오버라이딩하는 경우, 반드시 public 표기
    void onFragmentReload(String frgName);

}
