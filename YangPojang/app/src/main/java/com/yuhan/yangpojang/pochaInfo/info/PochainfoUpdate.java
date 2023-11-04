package com.yuhan.yangpojang.pochaInfo.info;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.pochaInfo.interfaces.OnFragmentReloadListener;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingFragment;
import com.yuhan.yangpojang.pochaInfo.review.PochareviewFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// pch: pojangmacha
// frg: fragment
// tv: TextView
public class PochainfoUpdate extends AppCompatActivity {

    private Shop shop ; // 가게 정보를 담을 Shop 객체
    // Firebase 데이터베이스를 초기화합니다

    Spinner categorySpinner;
    CheckBox pwayCash, pwayCard, pwayMobile, pwayAccount;
    CheckBox mon, tue, wed, thu, fri, sat, sun;
    ImageView storeExteriorPhoto, menuBoardPhoto;
    Button reportBtn; // 정보 수정 버튼
    String selectedCategory ;
    private FirebaseDatabase database ;
    private DatabaseReference shopsRef ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_shop_detail);

        database = FirebaseDatabase.getInstance();
        shopsRef = database.getReference("shops");

        //UI요소들 찾기
        categorySpinner = findViewById(R.id.categorySpinner);
        pwayCash = findViewById(R.id.pwayCash);
        pwayCard = findViewById(R.id.pwayCard);
        pwayMobile = findViewById(R.id.pwayMobile);
        pwayAccount = findViewById(R.id.pwayAccount);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.Fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        storeExteriorPhoto = findViewById(R.id.storeExteriorPhoto);
        menuBoardPhoto = findViewById(R.id.menuBoardPhoto);
        reportBtn = findViewById(R.id.reportBtn);

        // Spinner에 카테고리 목록 설정 -> res/values/strings에 목록있음
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() // 카테고리 스피너 선택 리스너 설정
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                selectedCategory = categorySpinner.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // 카테고리 - 아무것도 선택되지 않았을 때
                selectedCategory = "...";
            }
        });
        Log.d("spinner 선택","a"+selectedCategory);
        // Set an onClickListener for the report button
        reportBtn.setOnClickListener(view -> {
            // Get the updated data from the UI components

            String paymentMethods = "";
            if (pwayCash.isChecked()) {
                paymentMethods += "현금, ";
            }
            if (pwayCard.isChecked()) {
                paymentMethods += "카드, ";
            }
            if (pwayMobile.isChecked()) {
                paymentMethods += "모바일, ";
            }
            if (pwayAccount.isChecked()) {
                paymentMethods += "계좌이체, ";
            }
            // Remove the last comma and space if present
            if (!paymentMethods.isEmpty()|| paymentMethods!=null) {
                paymentMethods = paymentMethods.substring(0, paymentMethods.length() - 2);
            }

            ArrayList<String> selectedDays = new ArrayList<>();
            if (mon.isChecked()) {
                selectedDays.add("월");
            }
            if (tue.isChecked()) {
                selectedDays.add("화");
            }
            if (wed.isChecked()) {
                selectedDays.add("수");
            }
            if (thu.isChecked()) {
                selectedDays.add("목");
            }
            if (fri.isChecked()) {
                selectedDays.add("금");
            }
            if (sat.isChecked()) {
                selectedDays.add("토");
            }
            if (sun.isChecked()) {
                selectedDays.add("일");
            }

            Log.d("selefdskf",selectedCategory+selectedDays+paymentMethods);
            // Update the Firebase database with the new data
            updateInformation(selectedCategory, paymentMethods, selectedDays);

            // Optionally, you can add additional logic to handle image uploads for storeExteriorPhoto and menuBoardPhoto
            // storeExteriorPhoto and menuBoardPhoto are ImageViews where users can select images from their device

            // Once the update is done, you might want to close this activity or show a success message to the user
            // finish();
            // Toast.makeText(PochainfoUpdate.this, "Information Updated Successfully", Toast.LENGTH_SHORT).show();
        });



//
        Intent intent = getIntent();
        if(intent!=null)
        {
            String shopkey = intent.getStringExtra("shopKey");
            if(shopkey!=null)
            {
                Log.d("seturakdfj",shopkey);
                DatabaseReference detailShopRef= shopsRef.child(shopkey);
                String abc= detailShopRef.child("category").getDatabase().toString();
                String abcd= detailShopRef.child("category").getKey().toString();


                Log.d("pleas","a"+abcd);
                Log.d("pleas","a"+abc);
            }
        }


    }


    // Function to update the information in the Firebase Database
    private void updateInformation(String selectedCategory, String paymentMethods, List<String> selectedDays) {
//        Log.d("heismad",selectedCategory+paymentMethods+selectedDays);




        // 업데이트할 데이터를 Map으로 만듭니다.
        Map<String, Object> updates = new HashMap<>();
        updates.put("category", selectedCategory);
        updates.put("paymentMethods", paymentMethods);
        updates.put("openDays", selectedDays);






//        shopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String storeId = snapshot.getKey();
//                    // 여기서 storeId(고유 ID)를 사용하거나 출력할 수 있습니다.
//                    Log.d("Store ID", storeId);
//                    // storeId를 사용하여 업데이트하는 등의 작업을 할 수 있습니다.
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // 데이터 읽기를 실패한 경우 처리
//                Log.e("Firebase", "Failed to read value.", databaseError.toException());
//            }
//        });

    }



}