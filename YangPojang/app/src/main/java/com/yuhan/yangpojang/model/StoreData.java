package com.yuhan.yangpojang.model;

//StoreData 클래스 - 가게 데이터 초기화 및 반환하는 클래스
public class StoreData {
    public static Store[] initializeStores() {

        double[] latitudes = {37.550127, 37.550326, 37.549418, 37.548075, 37.556935, 37.558137, 37.542061, 37.548474, 37.547318, 37.532925}; //위도 데이터
        double[] longitudes = {126.847837, 126.848521, 126.847276, 126.845555, 126.850366, 126.838544, 126.840511, 126.836384, 126.849815, 126.846310}; //경도 데이터
        boolean[] isVerifieds = new boolean[10]; //인증 여부 데이터
        boolean[] hasMeetings = new boolean[10]; //번개 여부 데이터
        String[] storeNames = {"고구려 포차", "백제 포자", "신라 포차", "양양 포차", "해피 포차", "맛있는 포차", "맛없는 포차", "먹자 포차", "놀자 포차", "번개 포차"}; //가게 이름 데이터
        String[] storeAddresses= {"서울특별시 강남구 역삼동 123-1", "서울특별시 강서구 화곡동 456-2", "서울특별시 서초구 방배동 789-3", "서울특별시 마포구 서교동 101-4", "서울특별시 종로구 관훈동 202-5", "서울특별시 송파구 가락동 303-6", "서울특별시 강동구 천호동 404-7", "서울특별시 영등포구 여의도동 505-8", "서울특별시 성북구 성신동 606-9", "서울특별시 동작구 상도동 707-10"}; //가게 이름 데이터
        String[] openingHours = {"09:00 - 10:00", "10:30 - 11:30","11:00 - 21:00", "08:30 - 10:30", "10:00 - 11:00", "11:30 - 22:30", "09:30 - 21:30", "08:00 - 10:30", "09:00 - 11:00", "10:30 - 21:00"}; //가게 영업시간 데이터
        float[] ratings = {1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F, 4.5F, 5.0F, 2.5F}; // 가게 별점 데이터

        //10개의 가게 생성
        Store[] stores = new Store[10];
        for (int i = 0; i < stores.length; i++) {
            //인증, 번개 여부 랜덤으로 설정
            isVerifieds[i] = (i % 2 == 0);
            hasMeetings[i] = (i % 3 == 0);

            //가게 객체 생성 및 배열에 추가
            stores[i] = new Store(latitudes[i], longitudes[i], isVerifieds[i], hasMeetings[i], storeNames[i], storeAddresses[i], openingHours[i], ratings[i]);

        }

        return stores;
    }

}
