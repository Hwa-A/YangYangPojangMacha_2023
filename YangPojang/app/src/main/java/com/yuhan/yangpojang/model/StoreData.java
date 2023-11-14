package com.yuhan.yangpojang.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.geohash.GeoHash;


//StoreData 클래스 - 가게 데이터 초기화 및 반환하는 클래스
public class StoreData {
    private static ArrayList<Shop> stores;
    static DatabaseReference db_shop = FirebaseDatabase.getInstance().getReference("shops"); // 파이어베이스 연동
    static GeoLocation centerLocation; // 기준 위치의 GeoLocation
    static double searchRadiusInMeters; // 검색 기준 반경

    // 검색한 주소 위치 받아오기
    public static void addLocation(double latitude, double longitude, float searchRadiusInMeters){
        centerLocation = new GeoLocation(latitude, longitude);
        StoreData.searchRadiusInMeters = searchRadiusInMeters * 0.8;

        GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 12);
        // GeoHash 값을 문자열로 얻기
        String geohashString = geoHash.toBase32();
        Log.d("StoreData", "검색 위치 지오해시값(geohash) : " + geohashString);
    }

    // GeoHash 쿼리 생성, 결과 처리
    private static void queryStoresByGeoHash(final GeoLocation centerLocation, final double searchRadiusInMeters, final dataLoadedCallback callback){
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(centerLocation, searchRadiusInMeters); //getGeoHashQueryBounds : 주어진 중심 위치와 반경을 기반으로 GeoHash 쿼리 범위 생성
        // -> 한 개의 GeoHash 쿼리 범위로는 반경 내의 모든 데이터를 커버하기 어렵기 때문에 여러개 쿼리 범위 생성 후 리스트에 추가

        // 각 쿼리의 결과인 DataSnapshot에 대한 작업을 저장하기 위한 tasks 리스트 객체 선언
        List<Task<DataSnapshot>> tasks = new ArrayList<>(); // Task : 비동기 작업, DataSnapshot : 데이터를 나타내는 객체
        for(GeoQueryBounds b : bounds){
            Query q = db_shop.orderByChild("geohash") //geohash필드 기준으로 정렬
                    .startAt(b.startHash) //GeoHash 범위의 시작 부분
                    .endAt(b.endHash); //종료 부분
            tasks.add(q.get()); //각 쿼리의 결과를 추가하여 비동기 작업이 완료될 때까지 대기, q.get() : 쿼리의 결과를 Task<DataSnapshot>형태로 가져옴(Task가 완료되면 해당 쿼리 결과가 DataSnapshot으로 반환됨)
            Log.d("MainStoreData" , "지오해쉬 시작 : " + b.startHash);
            Log.d("MainStoreData" , "지오해쉬 끝 : " + b.endHash);
        }

        Tasks.whenAllSuccess(tasks) //모든 작업을 한 번에 실행하고, 모든 작업이 완료될 때 결과를 처리
                .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        stores = new ArrayList<>();
                        for(Task<DataSnapshot> task1 : tasks){
                            DataSnapshot snap = task1.getResult(); //task1 작업의 결과로 DataSnapshot이 저장 -- 하나의 레코드
                            for(DataSnapshot dataSnapshot : snap.getChildren()){ // 하나의 레코드의 각 필드에 접근
                                Shop mainStore = dataSnapshot.getValue(Shop.class); // 모든 필드를 Shop.class와 한번에 매핑
                                if(mainStore != null){
                                    mainStore.setPrimaryKey(dataSnapshot.getKey());// 가게 기본키 얻기


                                }

                                stores.add(mainStore);
                                Log.d("MainStoreData" , "쿼리 결과 : " + mainStore.getGeohash() + "  " + mainStore.getShopName());
                            }
                        }
                        ArrayList<Shop> filteredStores = filterStoresByRadius(stores, centerLocation, searchRadiusInMeters);
                        callback.onDataLoaded(filteredStores);


                    }

                })
                .addOnFailureListener(exception ->{
                    Log.d("StoreData", "데이터 가져오기 실패" + exception.getMessage());

                });
    }

    // 반경에 맞게 데이터 필터링하는 메서드
    private static ArrayList<Shop> filterStoresByRadius(ArrayList<Shop> stores, GeoLocation centerLocation, double searchRadiusInMeters) {
        ArrayList<Shop> filteredStores = new ArrayList<>();
        for (Shop store : stores) {
            GeoLocation storeLocation = new GeoLocation(store.getLatitude(), store.getLongitude());
            double distanceInMeters = GeoFireUtils.getDistanceBetween(centerLocation, storeLocation);
            if (distanceInMeters <= searchRadiusInMeters) {
                filteredStores.add(store); // 반경 내에 있는 가게만 추가
            }
        }
        return filteredStores;
    }

    public static void initializeStores(final dataLoadedCallback callback) {
        if(centerLocation != null){
            queryStoresByGeoHash(centerLocation, searchRadiusInMeters, callback);
        }

    }

    public interface dataLoadedCallback{
        void onDataLoaded(ArrayList<Shop> mainStores);
    }

}