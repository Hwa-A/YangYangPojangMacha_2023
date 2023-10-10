package com.yuhan.yangpojang.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.yuhan.yangpojang.R;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView; // 검색창
    LinearLayout recentIsempty_linear; // "최근 검색어가 없습니다" 창
    ListView recentSearch_listView; //최근 검색어 - 리스트뷰
    SearchAdapter_RecentSearch searchAdapterRecentSearch; // 최근 검색어 - 어댑터
    View footer; //"히스토리 삭제" 레이아웃
    int initialCapacity = 10; // 최근 검색어 리스트 - 용량 설정
    ArrayList<HashMap<String, String>> recentSearches = new ArrayList<>(initialCapacity); // 최근 검색어 목록 ArrayList
    ArrayList<String> autoCompletes_name = new ArrayList<>();
    ArrayList<String> autoCompletes_add = new ArrayList<>();
    ArrayList<Double> autoCompletes_latitude = new ArrayList<>();
    ArrayList<Double> autoCompletes_longitude = new ArrayList<>();

    SearchAdapter_AutoComplete autoadapter; // 자동완성 목록 - 어댑터
    ListView autoComplete_listView; // 자동완성 목록 - 리스트뷰

    String savefile = "recentsearch_file"; //SharedPreferences파일의 이름("recentsearch_file"이라는 이름으로 SharedPreferences파일 생성)
    SharedPreferences sharedPreferences; //SharedPreferences 인스턴스 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 최근검색어 목록 가져오는 부분
        sharedPreferences = getSharedPreferences(savefile, 0); //savefile 파일 가져옴
        Map<String, ?> allEntries = sharedPreferences.getAll(); //파일에 저장된 모든 데이터를 키-값으로 가져옴
        ArrayList<HashMap<String, String>> allValues = new ArrayList<>();

        // 파일에서 받아온 값을 list형태로 저장
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) { //allEntries의 값 부분을 allValues에 추가
            String value = (String) entry.getValue(); //Object형식으로 반환되기 때문에 String으로 형 변환해줌
            if (value != null) {
                String[] parts = value.split("&");
                String name = parts[0];
                String add = parts[1];
                HashMap<String, String> addSet = new HashMap<>();
                addSet.put(name, add);

                allValues.add(addSet);
            }
        }
        Log.d("SearchAdapter", "받아온 recentsearch_file 데이터: " + allValues);
        recentSearches = allValues;

        //최근 검색어_리스트뷰 정의
        recentSearch_listView = findViewById(R.id.recentsearch_listView);
        recentSearch_listView.setOnScrollListener(scrollL); // 스크롤 시 리스너 등록

        //최근 검색어_리스트뷰의 footer 설정
        footer = getLayoutInflater().inflate(R.layout.recentsearch_listview_footer, null, false);
        recentSearch_listView.addFooterView(footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentSearches != null) {
                    recentSearches.clear();
                    searchAdapterRecentSearch.notifyDataSetChanged(); // 변경된 데이터를 어댑터에 알림
                    settingVisibility(recentSearches); //레이아웃 visibility설정

                    saveRecentSearchesToSharedPreferences(); //종료하기 전에 recent_searches 저장
                    Log.d("SearchAdapter", "총 아이템(히스토리 클릭): " + recentSearches);
                }
            }
        });
        recentIsempty_linear = findViewById(R.id.recentIsempty_linear);

        //최근 검색어 어댑터 정의, 연결
        searchAdapterRecentSearch = new SearchAdapter_RecentSearch(recentSearches, recentIsempty_linear, footer);
        recentSearch_listView.setAdapter(searchAdapterRecentSearch);

        //최근 검색어 - 아이템 리스너 정의
        recentSearch_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                String name = item.keySet().iterator().next();
                String add = item.get(name);

                //메인 액티비티로 다시 돌아갈 때 입력필드의 입력값을 되돌려준다
                Intent intent = new Intent();
                intent.putExtra("select_recent_address", add);
                Log.d("SearchActivity", "보낸 주소(최근검색 - 리스너): " + add);
                intent.putExtra("select_recent_name", name);
                Log.d("SearchActivity", "보낸 주소(최근검색 - 리스너): " + name);
                setResult(RESULT_OK, intent);
                finish(); //이전 액티비티로 돌아감

                //클릭한 아이템이 맨 위로 올라갈 수 있도록 설정
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(name, add);

                recentSearches.remove(hashMap);
                recentSearches.add(0, hashMap);
            }
        });

        //레이아웃 visibility설정
        settingVisibility(recentSearches);

        //Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //toolbar를 actionbar로 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화

        //자동완성 어댑터 정의
        autoadapter = new SearchAdapter_AutoComplete(getApplicationContext(), autoCompletes_name, autoCompletes_add);
        autoComplete_listView = findViewById(R.id.autocomplete_listView);
        autoComplete_listView.setAdapter(autoadapter);
        autoComplete_listView.setOnScrollListener(scrollL); // 스크롤 시 리스너 등록
        autoComplete_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String auto_address = null;
                String address = (String) parent.getItemAtPosition(position);
                Location location = new Location("map.ngii");

                HashMap<String, String> hashMap = new HashMap<>();

                for (int i = 0; i < autoCompletes_name.size(); i++) {
                    if (address == autoCompletes_name.get(i)) {
                        location.setLatitude(autoCompletes_latitude.get(i));
                        location.setLongitude(autoCompletes_longitude.get(i));
                        auto_address = autoCompletes_add.get(i);

                        hashMap.put(address, auto_address);
                    }
                }

                addRecentSearches(hashMap);

                //메인 액티비티로 다시 돌아갈 때 입력필드의 입력값을 되돌려준다
                Intent intent = new Intent();
                intent.putExtra("select_autocomplete_location", location);
                intent.putExtra("select_autocomplete_address", auto_address);
                Log.d("SearchActivity", "보낸 주소(자동완성): " + location);
                setResult(RESULT_OK, intent);
                finish(); //이전 액티비티로 돌아감

            }
        });

        //주소 검색창
        searchView = findViewById(R.id.searchView);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.requestFocus(); // 화면 열릴 때 자동 포커스
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색어가 제출되었을 때 처리할 작업을 여기에 작성
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 검색어가 변경될 때 처리할 작업을 여기에 작성
                settingVisibility_change();

                if (!TextUtils.isEmpty(newText)) { // 주소 검색창이 비어있지 않을 때만 호출
                    HttpResponse.sendData(newText, new HttpResponse.DataCallback() {
                        @Override
                        public void onDataLoaded(HashMap<String, String> address_hash) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    autoCompletes_name.clear();
                                    autoCompletes_add.clear();
                                    autoCompletes_latitude.clear();
                                    autoCompletes_longitude.clear();

                                    for (String key : address_hash.keySet()) {
                                        String values = address_hash.get(key);
                                        autoCompletes_name.add(key);

                                        String[] parts = values.split("/");
                                        autoCompletes_add.add(parts[0]); //주소(도로명 or 지번)
                                        coordinateTransformation(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                                        //autoCompletes_latitude.add(Double.parseDouble(parts[1])); // x좌표
                                        //autoCompletes_longitude.add(Double.parseDouble(parts[2])); // y좌표
                                    }

                                    autoadapter.notifyDataSetChanged();
                                    for (int i = 0; i < address_hash.size(); i++) {
                                        Log.d("SearchAdapter", "받은 주소목록: " + "[" + autoCompletes_name.get(i) + " && " + autoCompletes_add.get(i) + " && " + autoCompletes_latitude.get(i) + " && " + autoCompletes_longitude.get(i) + "]");
                                    }

                                }
                            });

                        }
                    });
                } else {
                    settingVisibility(recentSearches);
                }
                return true;
            }

        });
    }

    // 좌표계 변환
    public void coordinateTransformation(double xCoord, double yCoord) {
        // CRS(좌표 참조 시스템) 객체 생성
        CRSFactory crsFactory = new CRSFactory();

        // WGS84 system 정의
        String wgsName = "WGS84"; // 좌표 참조 시스템 이름
        String wgsProj = "+proj=longlat +datum=WGS84 +no_defs"; // 좌표 참조 시스템 세부 정보 정의( +proj=longlat : 위도, 경도 사용함을 의미 / +datum=WGS84 : 데이터텀을 WGS84로 설정함 / +no_defs : 정의 파일 사용 x )
        CoordinateReferenceSystem wgsSystem = crsFactory.createFromParameters(wgsName, wgsProj); // WGS84 좌표 참조 시스템 객체 생성

        // UTMK system 정의
        String utmName = "UTMk";
        String utmProj = "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs";
        CoordinateReferenceSystem utmkSystem = crsFactory.createFromParameters(utmName, utmProj);

        // 변환할 좌표계 정보 생성
        ProjCoordinate p = new ProjCoordinate(); // PrijCoorinate : 좌표를 저장하고 관리하기 위한 클래스
        p.x = xCoord; // 변환할 좌표 입력
        p.y = yCoord;

        // 변환된 좌표를 담을 객체 생성
        ProjCoordinate p2 = new ProjCoordinate(); // 변환된 좌표를 담을 객체 생성

        // 좌표 변환
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory(); // CoordinateTransformFactory : 좌표변환 수행
        CoordinateTransform coodinateTransform = ctFactory.createTransform(utmkSystem, wgsSystem); // UTMK -> WGS 좌표 변환 정의
        ProjCoordinate projCoordinate = coodinateTransform.transform(p, p2); // 좌표변환 수행

        // 변환된 좌표
        //double longitude = projCoordinate.x; // 변환된 경도
        //double latitude = projCoordinate.y; // 변환된 위도
        autoCompletes_latitude.add(projCoordinate.y); // x좌표
        autoCompletes_longitude.add(projCoordinate.x); // y좌표
        Log.d("SearchActivity", "위도 : " + autoCompletes_latitude + ", " + "경도 : " + autoCompletes_longitude);
    }

    // 스크롤 시 리스너 설정
    AbsListView.OnScrollListener scrollL = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 키보드 내리기
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    //기본 레이아웃 visibility설정
    public void settingVisibility(ArrayList<HashMap<String, String>> recent_searches) {

        recentSearch_listView = findViewById(R.id.recentsearch_listView);
        recentIsempty_linear = findViewById(R.id.recentIsempty_linear);
        footer = findViewById(R.id.remove_history);
        autoComplete_listView = findViewById(R.id.autocomplete_listView);
        autoComplete_listView.setVisibility(View.INVISIBLE);

        if (!recent_searches.isEmpty()) {
            recentSearch_listView.setVisibility(View.VISIBLE);
            recentIsempty_linear.setVisibility(View.INVISIBLE);
            footer.setVisibility(View.VISIBLE);
        } else {
            recentSearch_listView.setVisibility(View.INVISIBLE);
            recentIsempty_linear.setVisibility(View.VISIBLE);
            footer.setVisibility(View.INVISIBLE);
        }
    }

    //검색어 제출 시 레이아웃 visibility설정
    public void settingVisibility_change() {

        autoComplete_listView = findViewById(R.id.autocomplete_listView);
        recentIsempty_linear = findViewById(R.id.recentIsempty_linear);
        recentSearch_listView = findViewById(R.id.recentsearch_listView);
        footer = findViewById(R.id.remove_history);

        autoComplete_listView.setVisibility(View.VISIBLE);
        recentIsempty_linear.setVisibility(View.INVISIBLE);
        recentSearch_listView.setVisibility(View.INVISIBLE);
        footer.setVisibility(View.INVISIBLE);

    }

    // 주소가 No address인지 여부를 파악해서 PlaceName으로 어떤값을 보낼지 정하는 메서드
    public String getAddress(HashMap<String, String> addressInfo) {
        String addressName = null;

        // HashMap의 첫 번째 요소를 추출하여 주소 문자열을 가져옴
        if (!addressInfo.isEmpty()) {
            Map.Entry<String, String> entry = addressInfo.entrySet().iterator().next();
            addressName = entry.getValue();
        }
        // 가져온 주소값이 No address일 경우에만 주소 추출
        if (addressName != null && addressName.equals("No address")) {
            return transformPlaceNameToAddress(addressName);

        } else {
            return addressName;
        }

    }


    // 지명 -> 주소 추출
    public String transformPlaceNameToAddress(String addressName) {
        List<Address> address;
        Geocoder geocoder = new Geocoder(this, new Locale("ko", "KR"));
        String fullAddress = null;

        try {
            address = geocoder.getFromLocationName(addressName, 1);
            Log.d("SearchActivity", "어드레스 : " + address);
            if (address != null && address.size() > 0) {
                fullAddress = address.get(0).getAddressLine(0);
                if (fullAddress.startsWith("대한민국")) { // 대한민국 인 것만 저장
                    fullAddress = fullAddress.replace("대한민국", "");
                } else { // 아닐 경우 No address
                    fullAddress = "No address";
                }
            } else { // 변환 실패 시 No address
                fullAddress = "No address";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fullAddress;
    }

    // 검색어 제출
    private void performSearch(String placeName) {

        HashMap<String, String> placeInfo = new HashMap<>(); // 지명, 주소를 담을 HashMap
        String address = transformPlaceNameToAddress(placeName); // 지명 -> 주소 추출
        placeInfo.put(placeName, address); // 검색어에 주소를 추출한 후 hashMap에 저장

        String addressName = getAddress(placeInfo);

        //메인 액티비티로 다시 돌아갈 때 입력필드의 입력값을 되돌려준다
        Intent intent = new Intent();
        intent.putExtra("select_recent_address", addressName);
        Log.d("SearchActivity", "보낸 주소(최근검색 - submit): " + addressName);
        intent.putExtra("select_recent_name", placeName);
        Log.d("SearchActivity", "보낸 주소(최근검색 - submit): " + placeName);
        setResult(RESULT_OK, intent);

        finish(); //이전 액티비티로 돌아감

        // 종료 후 UI 갱신 작업을 지연 실행
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRecentSearches(placeInfo); // recent_searches 설정
                searchAdapterRecentSearch.notifyDataSetChanged(); // 어댑터에 변경 알림
                searchView.setQuery("", false); // 검색어 제출 후 주소 입력칸 초기화
                saveRecentSearchesToSharedPreferences(); // 종료하기 전에 recent_searches 저장
                Log.d("SearchAdapter", "총 아이템: " + recentSearches);
            }
        }, 500); // 500 밀리초 (0.5초) 지연 후 실행

    }

    // 최근검색어 목록에 아이템 추가
    public void addRecentSearches(HashMap<String, String> query) {

        String queryKey = query.keySet().iterator().next();

        // 최근 검색어 list 중 동일한 값이 있는 경우, 동일한 값 삭제
        removeByKey(queryKey);

        //최근 검색어 list가 용량을 초과하지 않고, list내부에 같은 값이 없을 경우
        if (recentSearches.size() < initialCapacity) {
            recentSearches.add(0, query); //해당 쿼리 추가
        }
        //최근 검색어 list가 최대 용량과 같거나 초과하고, list내부에 같은 값이 없을 경우
        else {
            recentSearches.remove(initialCapacity - 1); //맨 마지막 요소 삭제
            recentSearches.add(0, query); //첫번째 요소에 해당 쿼리 추가
        }

    }

    // 주어진 키를 가진 요소를 최근 검색어 리스트에서 제거하는 메서드
    private void removeByKey(String key) {
        Iterator<HashMap<String, String>> iterator = recentSearches.iterator();
        while (iterator.hasNext()) {
            HashMap<String, String> query = iterator.next();
            if (query.keySet().iterator().next().equals(key)) {
                iterator.remove();
                break;
            }
        }
    }


    //Toolbar에서 뒤로가기 버튼 클릭 시 전 페이지로 돌아감
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveRecentSearchesToSharedPreferences();
    }

    //종료하기 전에 recent_searches 저장
    public void saveRecentSearchesToSharedPreferences() {
        sharedPreferences = getSharedPreferences(savefile, 0); //savefile이라는 이름의 SharedPreferences파일을 가져옴
        SharedPreferences.Editor editor = sharedPreferences.edit(); //파일의 데이터를 수정, 저장하기 위한 Editore객체 생성

        editor.clear();
        for (int i = 0; i < recentSearches.size(); i++) {
            HashMap<String, String> adds = recentSearches.get(i);
            String name = adds.keySet().iterator().next();
            String add = adds.get(name);
            String value = name + "&" + add;

            editor.putString(String.valueOf(i), value); //String.valueOf(i)이라는 키에 value값을 저장 -> SharedPreferences파일에 저장
            Log.d("SearchAdapter", "보낼 preperence: " + recentSearches);
        }

        editor.commit();
    }
}