package com.yuhan.yangpojang.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.yuhan.yangpojang.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

    public class SearchActivity extends AppCompatActivity {

    SearchView searchView; // 검색창
    LinearLayout recentIsempty_linear; // "최근 검색어가 없습니다" 창
    ListView recentSearch_listView; //최근 검색어 - 리스트뷰
    SearchAdapter_RecentSearch searchAdapterRecentSearch; // 최근 검색어 - 어댑터
    View footer; //"히스토리 삭제" 레이아웃
    int initialCapacity = 10; // 최근 검색어 리스트 - 용량 설정
    ArrayList<String> recentSearches = new ArrayList<>(initialCapacity); // 최근 검색어 목록 ArrayList
    ArrayList<String> autoCompletes_name = new ArrayList<>();
    ArrayList<String> autoCompletes_add = new ArrayList<>();

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
        ArrayList<String> allValues = new ArrayList<>();

        // 파일에서 받아온 값을 list형태로 저장
        for(Map.Entry<String, ?> entry : allEntries.entrySet()){ //allEntries의 값 부분을 allValues에 추가
            String value = (String) entry.getValue(); //Object형식으로 반환되기 때문에 String으로 형 변환해줌
            if(value != null){
                allValues.add(value);
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

        //아이템 리스너 정의
        recentSearch_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = (String)parent.getItemAtPosition(position);

                //메인 액티비티로 다시 돌아갈 때 입력필드의 입력값을 되돌려준다
                Intent intent = new Intent();
                intent.putExtra("select_address", address);
                Log.d("SearchActivity", "보낸 주소: " + address);
                setResult(RESULT_OK, intent);
                finish(); //이전 액티비티로 돌아감

                //클릭한 아이템이 맨 위로 올라갈 수 있도록 설정
                String item = (String) parent.getItemAtPosition(position);
                recentSearches.remove(item);
                recentSearches.add(0, item);
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

                                    for(String key : address_hash.keySet()){
                                        String values = address_hash.get(key);
                                        autoCompletes_name.add(key);
                                        autoCompletes_add.add(values);
                                    }

                                    autoadapter.notifyDataSetChanged();

                                    Log.d("SearchAdapter", "아이템 리스트: " + autoCompletes_name + " && " + autoCompletes_add);
                                }
                            });

                        }
                    });
                }else{
                    settingVisibility(recentSearches);
                }
                return true;
            }

        });

    }

    AbsListView.OnScrollListener scrollL = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 키보드 내리기
            if(scrollState == SCROLL_STATE_TOUCH_SCROLL){
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

    public void settingVisibility(ArrayList<String> recent_searches){
        //기본 레이아웃 visibility설정

        recentSearch_listView = findViewById(R.id.recentsearch_listView);
        recentIsempty_linear = findViewById(R.id.recentIsempty_linear);
        footer = findViewById(R.id.remove_history);
        autoComplete_listView = findViewById(R.id.autocomplete_listView);
        autoComplete_listView.setVisibility(View.INVISIBLE);

        if(!recent_searches.isEmpty()){
            recentSearch_listView.setVisibility(View.VISIBLE);
            recentIsempty_linear.setVisibility(View.INVISIBLE);
            footer.setVisibility(View.VISIBLE);
        }else{
            recentSearch_listView.setVisibility(View.INVISIBLE);
            recentIsempty_linear.setVisibility(View.VISIBLE);
            footer.setVisibility(View.INVISIBLE);
        }
    }

    public void settingVisibility_change(){
        //검색어 제출 시 레이아웃 visibility설정

        autoComplete_listView = findViewById(R.id.autocomplete_listView);
        recentIsempty_linear = findViewById(R.id.recentIsempty_linear);
        recentSearch_listView = findViewById(R.id.recentsearch_listView);
        footer = findViewById(R.id.remove_history);


        autoComplete_listView.setVisibility(View.VISIBLE);
        recentIsempty_linear.setVisibility(View.INVISIBLE);
        recentSearch_listView.setVisibility(View.INVISIBLE);
        footer.setVisibility(View.INVISIBLE);

    }

    private void performSearch(String query) {
        // 검색어 제출

        settingVisibility_change();

        //최근 검색어 list가 용량을 초과하지 않고, list내부에 같은 값이 없을 경우
        if(recentSearches.size() < initialCapacity && !recentSearches.contains(query)){
            recentSearches.add(0, query); //해당 쿼리 추가
        }
        //최근 검색어 list가 최대 용량과 같거나 초과하고, list내부에 같은 값이 없을 경우
        else if(recentSearches.size() >= initialCapacity && !recentSearches.contains(query)){
            recentSearches.remove(initialCapacity -1); //맨 마지막 요소 삭제
            recentSearches.add(0,query); //첫번째 요소에 해당 쿼리 추가
        }
        //추가한 쿼리가 최근 검색어 list 중 동일한 값이 있을 경우
        else if(recentSearches.contains(query)){
            recentSearches.remove(query); //동일한 쿼리 삭제
            recentSearches.add(0,query); //첫번째 요소에 해당 쿼리 추가
        }

        searchAdapterRecentSearch.notifyDataSetChanged(); //어댑터에 변경 알림
        searchView.setQuery("",false); //검색어 제출 후 주소 입력칸 초기화
        //settingVisibility(recentSearches); //레이아웃 visibility설정

        saveRecentSearchesToSharedPreferences(); //종료하기 전에 recent_searches 저장
        Log.d("SearchAdapter", "총 아이템: " + recentSearches);
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
    public void saveRecentSearchesToSharedPreferences(){
        sharedPreferences = getSharedPreferences(savefile, 0); //savefile이라는 이름의 SharedPreferences파일을 가져옴
        SharedPreferences.Editor editor = sharedPreferences.edit(); //파일의 데이터를 수정, 저장하기 위한 Editore객체 생성

        editor.clear();
        for(int i = 0; i < recentSearches.size(); i++){
            String value;
            value = recentSearches.get(i); //recent_searches의 데이터를 하나씩 저장
            editor.putString(String.valueOf(i), value); //String.valueOf(i)이라는 키에 value값을 저장 -> SharedPreferences파일에 저장
        }

        editor.commit();
    }
}