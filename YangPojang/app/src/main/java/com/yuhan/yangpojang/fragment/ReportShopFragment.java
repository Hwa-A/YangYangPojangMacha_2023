package com.yuhan.yangpojang.fragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.MapFragment;
import com.yuhan.yangpojang.FirebaseUtils;
import com.yuhan.yangpojang.R;
//import com.yuhan.yangpojang.ReportShopByFirebase;
//import com.yuhan.yangpojang.ShopDataListener;
import com.yuhan.yangpojang.ShopDataListener;
import com.yuhan.yangpojang.model.Shop;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportShopFragment extends Fragment implements ShopDataListener
{

    private static final int PICK_EXTERIOR_IMAGE_REQUEST = 1;
    private static final int PICK_MENU_IMAGE_REQUEST = 2;


    private TextView shopLocationText;
    private EditText shopNameEditText;
    private CheckBox pwayMobileCheckBox;
    private CheckBox pwayCardCheckBox;
    private CheckBox pwayAccountCheckBox;
    private CheckBox pwayCashCheckBox;
    private CheckBox monCheckBox;
    private CheckBox tueCheckBox;
    private CheckBox wedCheckBox;
    private CheckBox thuCheckBox;
    private CheckBox friCheckBox;
    private CheckBox satCheckBox;
    private CheckBox sunCheckBox;

    private Spinner categorySpinner;
    // 선택한 카테고리를 저장한 변수
    private String selectedCategory;


    private ImageView storeExteriorPhoto;
    private ImageView menuBoardPhoto;
    private Button reportBtn;

    private Uri storeExteriorImageUri;
    private Uri menuBoardImageUri;
    private String addressName;

//    private ShopDataListener shopDataListener;

    private BottomNavigationView bottomNavigationView;

    private double latitude; // latitude를 저장하는 변수
    private double longitude; // longitude를 저장하는 변수


    private Fragment mapFragment;
    private ShopDataListener shopDataListener;

    private MapLocationPopupFragment mapLocationPopupFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // 이 부분에 연결된 activity 레이아웃 인플레이트
        //즉 MainActivity에서 클릭된 fragment 인 fragment_report_shop.xml 레이아웃을 인플레이트 하면 되는것
        View viewReprotShop = inflater.inflate(R.layout.fragment_report_shop, container, false);
        shopLocationText= viewReprotShop.findViewById(R.id.shopplace);
        shopNameEditText = viewReprotShop.findViewById(R.id.editTextText2);
        pwayMobileCheckBox = viewReprotShop.findViewById(R.id.pwayMobile);
        pwayCardCheckBox = viewReprotShop.findViewById(R.id.pwayCard);
        pwayAccountCheckBox = viewReprotShop.findViewById(R.id.pwayAccount);
        pwayCashCheckBox = viewReprotShop.findViewById(R.id.pwayCash);
        monCheckBox = viewReprotShop.findViewById(R.id.mon);
        tueCheckBox = viewReprotShop.findViewById(R.id.tue);
        wedCheckBox = viewReprotShop.findViewById(R.id.wed);
        thuCheckBox = viewReprotShop.findViewById(R.id.thu);
        friCheckBox = viewReprotShop.findViewById(R.id.Fri);
        satCheckBox = viewReprotShop.findViewById(R.id.sat);
        sunCheckBox = viewReprotShop.findViewById(R.id.sun);
        storeExteriorPhoto = viewReprotShop.findViewById(R.id.storeExteriorPhoto);
        menuBoardPhoto = viewReprotShop.findViewById(R.id.menuBoardPhoto);
        reportBtn = viewReprotShop.findViewById(R.id.reportBtn);
        categorySpinner = viewReprotShop.findViewById(R.id.categorySpinner);
        mapLocationPopupFragment = new MapLocationPopupFragment();
        // 해당 activity   -  activity_main.xml 에 구현되어있는 부분이므로 getActivity().findViewById로 작성가능
        // mainactivity에 선택된 fragment 가 reportshop이지만 activity는 main에 있는걸로 추정..? => 이 주석은 확신되면 지울것
        bottomNavigationView= getActivity().findViewById(R.id.bottomNavigationView);



        //
        FirebaseUtils.setShopDataListener(this);

        // Spinner에 카테고리 목록 설정
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        // 카테고리 스피너 선택 리스너 설정
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = categorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때
                selectedCategory = null;

            }
        });


        //가게 외관 사진 업로드 하기 틀 누르면 갤러리 오픈
        storeExteriorPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery(PICK_EXTERIOR_IMAGE_REQUEST);
            }
        });
        //메뉴 사진 업로드하기  사진 틀 누르면 갤러리 오픈
        menuBoardPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery(PICK_MENU_IMAGE_REQUEST);
            }
        });

        // 제보하기 버튼 누르면 saveShopData 메서드 실행
        reportBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveShopData();
//                clearForm();

//                // 우선 먼저 mapFragment로 이동하는 코드 추가
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container,mapFragment)
//                        .commitAllowingStateLoss();

            }
        });

        shopLocationText.setOnClickListener(v ->
        {
            Log.d("rkskekfkak","fdsfdsf");
            bottomNavigationView.setVisibility(View.GONE);
            showMapLocationPopup();
        });
        return viewReprotShop;


    }

    // 위치 선택 지도 팝업을 띄우는 메서드
    private void showMapLocationPopup() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MapLocationPopupFragment popupFragment = new MapLocationPopupFragment();
        popupFragment.setTargetFragment(this, 0);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, popupFragment);
        fragmentTransaction.commit();

        // 위치 선택 결과를 처리하는 리스너 등록
        getParentFragmentManager().setFragmentResultListener("locationResult", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                latitude = bundle.getDouble("latitude");
                longitude = bundle.getDouble("longitude");
                Log.d("zjfjzkzf", "Selected Latitude: " + latitude);
                Log.d("zjfjzkzf", "Selected Longitude: " + longitude);

                // 역지오코딩을 통해 좌표로 주소 가져오기
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String addressText = address.getAddressLine(0); // 주소 문자열 가져오기
                        shopLocationText.setText(addressText);
                    } else {
                        shopLocationText.setText("위치 선택하기");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    shopLocationText.setText("주소 변환 오류");
                }
            }
        });
    }


    // 핸드폰 갤러리 여는 역할 -P
    private void openGallery(int requestCode)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }


    @Override
    //이미지 선택 액티비티 종료후 결과를 나타냄
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // 이미지 선택 액티비티 결과가 ok   &&  데이터가 들어와서 null이 아니면 true
        if (resultCode == Activity.RESULT_OK && data != null)
        {
            //  종료된 엑티비티로 부터 전달된 데이터에서 선택한 이미지 uri를 가져옴
            Uri selectedImageUri = data.getData();
            // 외관 이미지를 선택하는 requestCode인 경우
            if (requestCode == PICK_EXTERIOR_IMAGE_REQUEST)
            {
                storeExteriorImageUri = selectedImageUri;
                storeExteriorPhoto.setImageURI(selectedImageUri);
            }
            // 메뉴 이미지를 선택하는 requestCode인 경우
            else if (requestCode == PICK_MENU_IMAGE_REQUEST)
            {
                menuBoardImageUri = selectedImageUri;
                menuBoardPhoto.setImageURI(selectedImageUri);
            }
        }
    }

    // Toast메세지들을 가운데 정렬 시키는 메서드
    private void showCenteredToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); // 가운데 정렬
        toast.show();
    }


    private void saveShopData()
    {
        String shopName = shopNameEditText.getText().toString();
        boolean isPwayMobile = pwayMobileCheckBox.isChecked();
        boolean isPwayCard = pwayCardCheckBox.isChecked();
        boolean isPwayAccount = pwayAccountCheckBox.isChecked();
        boolean isPwayCash = pwayCashCheckBox.isChecked();
        boolean isOpenMon = monCheckBox.isChecked();
        boolean isOpenTue = tueCheckBox.isChecked();
        boolean isOpenWed = wedCheckBox.isChecked();
        boolean isOpenThu = thuCheckBox.isChecked();
        boolean isOpenFri = friCheckBox.isChecked();
        boolean isOpenSat = satCheckBox.isChecked();
        boolean isOpenSun = sunCheckBox.isChecked();
        addressName= shopLocationText.getText().toString();

        if (latitude == 0.0 && longitude == 0.0) {
            // 위치가 설정되지 않았을 때 메시지 표시
            showCenteredToast("위치를 설정해주세요.");
        }
        else if(addressName=="위치 선택하기")
        {
            // 가게 이름이 빈 문자열인 경우 처리
            showCenteredToast("위치를 설정해주세요");
        }
        else if (shopName.isEmpty()) {
            // 가게 이름이 빈 문자열인 경우 처리
            showCenteredToast("가게 이름을 입력하세요.");
        }
        else if (!(isPwayMobile || isPwayCard || isPwayAccount || isPwayCash))
        {
            // 결제 방식 중 하나 이상이 선택되지 않은 경우 처리
            showCenteredToast("결제 방식을 하나 이상 선택하세요.");
        }
        else if (!(isOpenMon || isOpenTue || isOpenWed || isOpenThu || isOpenFri || isOpenSat || isOpenSun)) {
            // 요일 및 결제 방식 중 하나 이상이 선택되지 않은 경우 처리
            showCenteredToast("요일을 하나 이상 선택하세요.");
        }
        // 모든 필수 입력 사항이 제대로 입력되었을 경우 Firebase에 데이터 저장
        else
        {
            // 위도,경도,카테고리,이미지정보는 위에서 구한것으로 전역변수(보라색)으로 나타냄
            Shop shop = new Shop(
                    shopName, latitude,longitude,addressName,isPwayMobile, isPwayCard, isPwayAccount, isPwayCash,
                    isOpenMon, isOpenTue, isOpenWed, isOpenThu, isOpenFri, isOpenSat, isOpenSun,selectedCategory,
                    (storeExteriorImageUri != null) ? storeExteriorImageUri.toString() : "",
                    (menuBoardImageUri != null) ? menuBoardImageUri.toString() : ""
            );


            FirebaseUtils.saveShopData(shop, storeExteriorImageUri, menuBoardImageUri);
            // 제보가 완료되었다는 Toast 메시지를 표시합니다. adddd
//            Toast.makeText(getContext(), "제보가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onShopDataSaved() {
        Toast.makeText(getContext(), "Shop data saved!", Toast.LENGTH_SHORT).show();
        clearForm(); // For example, you might want to clear the form after data is saved.
    }


    public void clearForm()
    {
        // 폼 초기화 코드 작성
        shopNameEditText.setText(""); // 가게 이름 입력란 초기화
        // 다른 EditText 요소들도 동일하게 초기화

        // CheckBox 초기화
        pwayMobileCheckBox.setChecked(false);
        pwayCardCheckBox.setChecked(false);
        pwayAccountCheckBox.setChecked(false);
        pwayCashCheckBox.setChecked(false);
        monCheckBox.setChecked(false);
        tueCheckBox.setChecked(false);
        wedCheckBox.setChecked(false);
        thuCheckBox.setChecked(false);
        friCheckBox.setChecked(false);
        satCheckBox.setChecked(false);
        sunCheckBox.setChecked(false);

        // ImageView 초기화
        storeExteriorPhoto.setImageDrawable(null);
        menuBoardPhoto.setImageDrawable(null);

        // Spinner 초기화 (카테고리 선택을 "선택안함"으로)
        categorySpinner.setSelection(0);

        // 주소 텍스트 초기화
        shopLocationText.setText("위치 선택하기");

        // 위도와 경도 초기화
        latitude = 0.0;
        longitude = 0.0;

        // 초기화된 폼으로 스크롤하기 (선택사항)
        ScrollView scrollView = requireView().findViewById(R.id.scrollView2); // 스크롤 뷰 ID를 설정하세요.
        scrollView.smoothScrollTo(0, 0); // 스크롤을 맨 위로 이동

    }

}