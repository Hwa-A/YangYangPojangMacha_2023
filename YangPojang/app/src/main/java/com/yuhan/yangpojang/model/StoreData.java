package com.yuhan.yangpojang.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.geofire.core.GeoHash;
import com.firebase.geofire.core.GeoHashQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.map.overlay.Marker;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//StoreData 클래스 - 가게 데이터 초기화 및 반환하는 클래스
public class StoreData {
    private static ArrayList<Store> stores;
    static double focusLocation_latitude;
    static double focusLocation_longitude;
    static FirebaseFirestore db = FirebaseFirestore.getInstance(); //파이어스토어 연동
    static double radiusInM = 1500;

    static GeoLocation center;

    // 검색한 주소 위치 받아오기
    public void addLocation(double latiude, double longitude){
        focusLocation_latitude = latiude;
        focusLocation_longitude = longitude;
        center = new GeoLocation(latiude, longitude);
        Log.d("StoreData", "검색 위치 : " +  focusLocation_latitude + "  " + focusLocation_longitude);
    }

    /*static ArrayList<String> docs;
    static ArrayList<Double> latitudes;
    static ArrayList<Double> longitudes;
    static ArrayList<String> geoHash;
    public static void updateGeohash(final updateCallback callback){
        // db에 저장된 위도와 경도값 받아오기( -> 나중에는 가게 등록 시에 위도, 경도와 함께 Geohash값을 계산해서 넣어야함
        docs = new ArrayList<String>();
        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();
        for(int i = 1; i <= 15; i++){
            if(i < 10){
                docs.add("store0" + i);
            }else{
                docs.add("store" + i);
            }
        }

        //모든 데이터의 위도, 경도값을 받아온 후 각 위치에 대한 geoHash 생성
        geoHash = new ArrayList<String>();
        db.collection("StoreInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");

                            latitudes.add(latitude);
                            longitudes.add(longitude);
                        }
                        for(int i = 0; i < 15; i++){
                            String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitudes.get(i), longitudes.get(i)));
                            geoHash.add(hash);
                        }

                        callback.onDataUpdate(geoHash);
                    }
                });
    }*/

    public static ArrayList<Store> initializeStores(final dataLoadedCallback callback) {
        if(center != null){
            List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM); //getGeoHashQueryBounds : 주어진 중심 위치와 반경을 기반으로 GeoHash 쿼리 범위 생성
            // -> 한 개의 GeoHash 쿼리 범위로는 반경 내의 모든 데이터를 커버하기 어렵기 때문에 여러개 쿼리 범위 생성 후 리스트에 추가

            List<Task<QuerySnapshot>> tasks = new ArrayList<>(); //db 비동기 작업 수행 후 결과 수집
        /* -> 여러개의 GeoHash 범위에 대한 쿼리를 동시에 실행하고 그 결과를 추적하기 위해 List로 선언
           -> tasks 리스트는 병렬로 실행되는 여러 개의 쿼리 결과를 수집하고 모든 쿼리가 완료될 때 데이터를 처리하기 위해 사용 */

            for(GeoQueryBounds b : bounds){
                Query q = db.collection("StoreInfo")
                        .orderBy("geohash")
                        .startAt(b.startHash) //GeoHash 범위의 시작 부분
                        .endAt(b.endHash); //종료 부분
                tasks.add(q.get()); //각 쿼리의 결과를 추가하여 비동기 작업이 완료될 때까지 대기
            }

            Tasks.whenAllSuccess(tasks) //tasks 리스트 내의 모든 task가 성공하면 onCompleteListener 호출
                    .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Object>> task) {
                            stores = new ArrayList<>();
                            for(Task<QuerySnapshot> task1 : tasks){
                                QuerySnapshot snap = task1.getResult();
                                for(QueryDocumentSnapshot document : snap){
                                    Store store = new Store();
                                    store = document.toObject(Store.class);
                                    stores.add(store);
                                }
                            }
                            Log.d("StoreData",  "사이즈 : " + stores.size());
                            callback.onDataLoaded(stores);
                        }
                    });
        }

        /*//생성한 geoHash값을 db에 없데이트
        updateGeohash(new updateCallback() {
            @Override
            public void onDataUpdate(ArrayList<String> geoHash) {
                for(int i = 0; i < 15; i++){
                    db.collection("StoreInfo")
                            .document(docs.get(i))
                            .update("geohash", geoHash.get(i))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("StoreData", "GeoHash 업데이트 성공");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("StoreData", "GeoHash 업데이트 실패");
                                }
                            });
                }
            }
        });*/


        /*// 전체 데이터 받아오기
        db.collection("StoreInfo") //Firestore의 "StoreInfo" 컬렉션 참조
                .get() // 컬렉션에서 문서들을 가져오는 비동기 메서드 호출(task객체에 결과 저장)
                .addOnCompleteListener(task -> { // 비동기 작업이 완료될 때 실행할 작업 정의, task : 비동기 작업의 결과 또는 상태
                    if(task.isSuccessful()){ // 작업이 성공적으로 완료되었는지 확인
                        stores = new ArrayList<>();
                        for(QueryDocumentSnapshot document : task.getResult()){ //task.getResult()는 QuerySnapshot객체를 반환하며, QuerySnapshot은 여러 QueryDocumentSnapshot으로 구성되어 있음.
                            Store store = new Store();                          // QuerySnapshot : 쿼리를 실행한 결과로, 컬렉션 내의 여러 문서를 포함하는 객체, QueryDocumentSnapshot : Querysnapshot의 개별문서 -> 각각의 QueryDocumentSnapshot은 컬렉션 내의 한 문서에 대한 데이터를 가지고 있음
                            store = document.toObject(Store.class); //데이터를 읽어와서 Store 객체로 변환
                            stores.add(store);
                        }
                        Log.d("StoreData",  "사이즈 : " + stores.size());
                        callback.onDataLoaded(stores); //콜백 메서드 호출하여 데이터 로딩이 완료되었음을 알림
                    }
                });*/

        return stores; //콜백 함수를 통해 리턴

    }

    public interface dataLoadedCallback{
        void onDataLoaded(ArrayList<Store> stores);
    }

    /*public interface updateCallback{
        void onDataUpdate(ArrayList<String> geoHash);
    }*/

}
