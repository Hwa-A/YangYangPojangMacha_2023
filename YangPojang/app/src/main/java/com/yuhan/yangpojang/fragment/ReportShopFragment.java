package com.yuhan.yangpojang.fragment;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.FirebaseUtils;
import com.yuhan.yangpojang.MainActivity;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.ShopDataListener;
import com.yuhan.yangpojang.login.User;
import com.yuhan.yangpojang.model.ReportShop;
import com.yuhan.yangpojang.model.Shop;

public class ReportShopFragment extends Fragment implements ShopDataListener
{
    private boolean shouldClearForm = true; // 작성 폼 초기화 여부를 판단하기 윟마
    ScrollView scrollView; // 스크롤 뷰
    ActivityResultLauncher<Intent> galleryLauncher; // 갤러리 오픈을 위한 intent launcher
    private static final int PICK_EXTERIOR_IMAGE_REQUEST = 1;    // 가게-외관이미지 선택
    private TextView shopLocationText; // [위치선택 버튼으로 위치를 선택하세요] 글씨
    private EditText shopNameEditText; // [ex)) 영희네 or 별빛아파트 앞] => 가게이름을 설정하는 부분
    private CheckBox pwayCashCheckBox; // 결제방식 - 현금
    private CheckBox pwayCardCheckBox; // 결제방식 - 카드
    private CheckBox pwayAccountCheckBox; // 결제방식 - 계좌
    private CheckBox pwayMobileCheckBox; // 결제방식 - 모바일
    private CheckBox monCheckBox;  // 요일선택 - 월
    private CheckBox tueCheckBox; // 요일선택 - 화
    private CheckBox wedCheckBox; // 요일선택 - 수
    private CheckBox thuCheckBox; // 요일선택 - 목
    private CheckBox friCheckBox; // 요일선택 - 금
    private CheckBox satCheckBox; // 요일선택 - 토
    private CheckBox sunCheckBox; // 요일선택 - 일
    private Spinner categorySpinner; // 가게 카테고리 선택하는 스피너
    private String selectedCategory; // 선택한 카테고리를 저장하는 변수

    //db와는 무관하게 ui에 무슨 이미지를 선택했는지 나타내기 위한 storeExteriorPhoto
    private ImageView storeExteriorPhoto; // 가게 외관 이미지를 표시하는 이미지뷰

    //db에 넣을때 필요한 이미지들의 uri:  storeExteriorImageUri
    private Uri storeExteriorImageUri; // 가게 외관 이미지의 URI
    private String exteriorImagePath;

    private Uri imageUri;
    private String uid; // uid
    private TextView storePhotoTextView; // 가게 사진 선택하기 글씨
    private Button reportBtn;  // 가게 정보를 제보하는 버튼
    private Button editShopPlaceBtn; // 위치선택 버튼
    private String addressName; // 가게 위치 주소 ( 불광로 4) 정보를 넣는 변수
    private int requestCode; // 이미지 선택 요청 코드( 1 or 2) ->  PICK_EXTERIOR_IMAGE_REQUEST = 1
    private BottomNavigationView bottomNavigationView;   // 하단 네비게이션 뷰
    private double latitude; // 가게 위도
    private double longitude; // 가게 경도
    private MapLocationPopupFragment mapLocationPopupFragment;
    private boolean isVerified; // 인증된 가게인가
    private boolean hasMeeting;  // 번개가 잡힌 가게인가
    private int countVerified; // 인증횟수
    private int countSingo; // 신고횟수

    private float rating; // 별점
    private String geohash; // 지오해쉬
    private Shop shop;
    private  HomeFragment homeFragment;
    private DatabaseReference databaseReference ;
    private String shopKey;
    private DatabaseReference shopReference ;

    private ProgressDialog progressDialog;      // 등록 로딩 다이얼로그



    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d("fffffff리포트샵온크리에트","");
        // 해당 class(ReprotShopFragment)에서 연결시킬 layout을 inflate한다
        // 즉 MainActivity 하단 네비게이션 바에서 클릭된 fragment ==>  fragment_report_shop.xml 레이아웃을 인플레이트 하면 되는것
        View viewReprotShop = inflater.inflate(R.layout.fragment_report_shop, container, false);
        scrollView = viewReprotShop.findViewById(R.id.scrollView2); // scrollView: 배경화면 아무대나 눌러도 키보드가 사라지는 hidekeyboard 구현을 위해 사용
        shopLocationText= viewReprotShop.findViewById(R.id.shopplace); // [불광로 4] 같이 주소 텍스트
        shopNameEditText = viewReprotShop.findViewById(R.id.editTextText2); // [영희네 가게]  같이 가게이름
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
        editShopPlaceBtn = viewReprotShop.findViewById(R.id.editShopplace);
        storeExteriorPhoto = viewReprotShop.findViewById(R.id.storeExteriorPhoto); // 선택된 외관 이미지를 보기 위함 - db와 무관
        reportBtn = viewReprotShop.findViewById(R.id.reportBtn); // 제보하기 버튼
        categorySpinner = viewReprotShop.findViewById(R.id.categorySpinner);  // 카테고리 스피너 -  res/values/strings에 카테고리 목록 지정해놓음
        mapLocationPopupFragment = new MapLocationPopupFragment();  // 제보할 가게 위치 선택을 위한 지도
        storePhotoTextView= viewReprotShop.findViewById(R.id.storeBoardText); // 가게 사진 선택하기 글씨
        bottomNavigationView= getActivity().findViewById(R.id.bottomNavigationView); // 하단 네비게이션 바
        bottomNavigationView.setVisibility(View.VISIBLE);
        progressDialog=new ProgressDialog(getActivity());
        
        // 가게 제보 진행바 추가
        progressDialog.setMessage("가게 등록 중 ...");
        progressDialog.setCancelable(false);    // 취소 불가능
        progressDialog.setCanceledOnTouchOutside(false);     // 외부 터치 불가능
        
        databaseReference = FirebaseDatabase.getInstance().getReference();
        shopKey = databaseReference.child("shops").push().getKey();

        shopReference = databaseReference.child("shops").child(shopKey);

        //hash= GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude,longitude));  // 나은 언니 hash 넣을 때 사용
        if(addressName!=null)
        {
            shopLocationText.setText(addressName);
        }
        else
        {
            shopLocationText.setText("위치선택 버튼을 클릭하세요");
        }

        uid=null; // FIREBASE에 USERID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("hong", String.valueOf(user));
        if(user!=null) {
            uid = user.getUid();
        }
        else
        {
            Log.d("ReportShopFragment- userid 오류", "userid 값이 없음");
        }
        FirebaseUtils.setShopDataListener(this);  // FirebaseUtils 클래스: 제보한 가게를 파이어베이스에 넣는 로직을 별도 class로 작성함(모든 부분의 파이어베이스가 아닌 제보부분만입니다)

        // Spinner에 카테고리 목록 설정 -> res/values/strings에 목록있음#edada6
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<CharSequence>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.category_array)) {
            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (getItem(position).toString().contains("술")) {
                    view.setTextColor(Color.parseColor("#FF0000")); // 원하는 색상 코드를 입력하세요.
                } else {
                    view.setTextColor(Color.parseColor("#000000")); // 기본 글자색을 입력하세요.
                }
                return view;
            }
        };
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
                selectedCategory = null;


            }
        });
        storeExteriorPhoto.setOnClickListener(new View.OnClickListener() // 가게 외관 사진 등록하기 누르면  갤러리 오픈
        {
            @Override
            public void onClick(View v)
            {
                requestCode=PICK_EXTERIOR_IMAGE_REQUEST;  // 요청코드: 외관 사진을 선택하는 것
                openGallery();
            }
        });

        /*순서
         *  1. openGallery 호출로 갤러리가 열리고 사용자가 이미지를 선택한다
         *  2. 이미지 선택을 결과에 대한 코드가 다음줄부터 진행된다*/

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>()
                {
                    @Override
                    public void onActivityResult(ActivityResult result)
                    {
                        String filename = "";
//                        String path="";
                        shouldClearForm=false;
                        if (result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent data = result.getData();
                            if (data != null)
                            {
                                imageUri = data.getData();
                                if (imageUri != null)
                                {
                                    // 선택한 이미지 URI를 저장
                                    if (requestCode == PICK_EXTERIOR_IMAGE_REQUEST) // 1 requestCode가 외관 사진
                                    {
                                        // storeExteriorImageUri : [content://media/external/images/media/~] 형태 - 안드로이드 내에서 선택된 이미지 경로
                                        storeExteriorImageUri = imageUri;  // db에 넣기위해선 이미지를 uri로
                                        storeExteriorPhoto.setImageURI(imageUri);  // 이미지뷰에 선택한 이미지 단순표시
                                        storeExteriorPhoto.setBackground(null);
                                        storePhotoTextView.setVisibility(View.GONE);
                                        filename= "exterior.jpg";
//                                        path="exteriorImagePath";
                                    }


                                }
                            }
                            if(filename!=null || filename=="")
                            {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                                DatabaseReference shopRef = databaseReference.child("shops").child(shopKey).child(path);
                                StorageReference shopImagesRef = storageRef.child("shops").child(shopKey).child("images").child(filename);
                                if(filename=="exterior.jpg")
                                {
                                    String originalString= String.valueOf(shopImagesRef);
                                    String trimmedString = originalString.substring(originalString.indexOf("/shops"));
                                    exteriorImagePath= trimmedString;
//                                    shop.setExteriorImagePath(trimmedString);
                                }

                                }

                            }


                        }
                });



        // 제보버튼 눌렀을때 발생
        reportBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveShopData();
            }
        });

        //가게위치 선택하기 글자를 누르면 지도가 뜨게 구현 + 하단 네비게이션 바 감춤
        editShopPlaceBtn.setOnClickListener(v ->
        {
            showMapLocationPopup(); // 위치선택용 지도 pop
        });
        // 화면 아무대나 눌러도 키보드가 사라지게 구현을 위해 작성 - enter 키로만 키보드가 사라지면 굉장히 불편하여 추가함
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard(); //키보드 감춤 메서드
                }
                return false;
            }
        });
        return viewReprotShop;

    }

    @Override
    public void onStart (){
        super.onStart();

        // OnBackPressedCallback 생성
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 뒤로가기 버튼이 눌렸을 때 홈 프래그먼트로 이동
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                if(bottomNavigationView!=null) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_map);
                    clearForm();  ////fffffffffffffffffff
                }
            }
        };

        // OnBackPressedCallback 추가
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    // 위치 선택 지도 팝업을 띄우는 메서드
    private void showMapLocationPopup() {

        shouldClearForm = false; // Set the flag to skip form clearing

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapLocationPopupFragment popupFragment = new MapLocationPopupFragment();

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, popupFragment);
        fragmentTransaction.commit();
        setImagesFromMapFragment();
        // 위치 선택 결과를 처리하는 리스너 등록
        getParentFragmentManager().setFragmentResultListener("locationResult", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle resultBundle) {
                shouldClearForm=false;
                if (resultBundle != null)
                {
                    addressName= resultBundle.getString("selectedLocationAdd");
                    latitude= resultBundle.getDouble("selectedLatitude");
                    longitude= resultBundle.getDouble("selectedLongitude");

                    Bundle reverseResultBundle = new Bundle();  // resultBundle : ReportShopFragment.java에 제보된 위치 넘기기 위해 번들 생성
                    reverseResultBundle.putDouble("reverseLatitude", latitude);  // ReportShopFragment.java에 위도 넘기기 ("selectedLatitude" 이름으로 넘김)
                    reverseResultBundle.putDouble("reverseLongitude", longitude);   // ReportShopFragment.java에 위도 넘기기 ("selectedLongitude" 이름으로 넘김)
                    reverseResultBundle.putString("reverseLocationAdd", addressName); // // ReportShopFragment.java에 주소 넘기기 ("selectedLocationAdd" 이름으로 넘김)
                    Log.d("whatisthis","a"+addressName);
                    if(addressName==""|| addressName==null ||addressName=="위치선택 버튼을 클릭하세요")
                    {
                        Log.d("wthis","a"+addressName);
                        shopLocationText.setText("위치선택 버튼을 클릭하세요");

                    }
                    getParentFragmentManager().setFragmentResult("reverseResult",reverseResultBundle);  //  ReportShopFragment.java에  setFragmentResultListener호출
                    Log.d("sFishandchips","a"+addressName);
                }
                geohash= GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
                if(!(addressName==null) || !(addressName=="")||!(addressName=="위치선택 버튼을 클릭하세요")) //"위치를 선택하세요 or로 추가(보류)"
                {
                    Log.d("1istha","a"+addressName);
                    shopLocationText.setText(addressName);
                    //맨밑 a부분 그대로 긁어넣기 - 지도에 기존 선택햇던 주소 넣을 거면
                }
                else
                {
                    shopLocationText.setText("오류: 위치를 가져올 수 없음");
                }
                setImagesFromMapFragment();
                shouldClearForm=true;
            }

        });
    }

    void setImagesFromMapFragment() {
        // Store the current image URIs
        Uri tempStoreExteriorImageUri = storeExteriorImageUri;

        // Instead of resetting image URIs, use the temp URIs set prior to opening the map fragment
        storeExteriorImageUri = tempStoreExteriorImageUri;

        // Update image views only if the URIs are not null
        if (storeExteriorImageUri != null) {
            storeExteriorPhoto.setImageURI(storeExteriorImageUri);
            storeExteriorPhoto.setBackground(null);
            storePhotoTextView.setVisibility(View.GONE);
        }


    }


    // onPasue: 다른 화면으로 넘어갔을때 폼을 지울지 여부를 판단
    @Override
    public void onPause() {
        super.onPause();
        if (!shouldClearForm) {
            Log.d("f","F");
            shouldClearForm=true;
        }
        else if(shouldClearForm) {
            clearForm();
        }
        super.onPause();
    }

    // 핸드폰 갤러리 여는 역할
    private void openGallery() {
        shouldClearForm=false;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent); // onCreateView안에 작성된 galleryLauncher~~ 부분으로 이동

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
        isVerified=false;
        hasMeeting=false;
        rating = 0;
        addressName= shopLocationText.getText().toString();


        Log.d("bibi",addressName);
        if (addressName.equals("위치선택 버튼을 클릭하세요"))
        {
            Toast.makeText(getContext(), "위치를 설정해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(addressName=="null"||addressName==null) {
            Toast.makeText(getContext(), "위치를 설정해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(getContext(), "위치를 설정해주세요.", Toast.LENGTH_SHORT).show();
        }

        else if ((shopName.isEmpty())||shopName==" ") {
            Toast.makeText(getContext(), "가게이름을 작성해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if (!(isPwayMobile || isPwayCard || isPwayAccount || isPwayCash))
        {
            Toast.makeText(getContext(), "결제 방식을 하나 이상 선택하세요..", Toast.LENGTH_SHORT).show();
        }
        else if (!(isOpenMon || isOpenTue || isOpenWed || isOpenThu || isOpenFri || isOpenSat || isOpenSun))
        {
            Toast.makeText(getContext(), "요일을 하나 이상 선택하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(storeExteriorImageUri==null)
        {
            Toast.makeText(getContext(), " 가게 사진을 선택하세요.", Toast.LENGTH_SHORT).show();
        }

        else // 제보에 필요한 정보를 모두 입력했을시
        {
            progressDialog.show();


            shop = new Shop(uid,shopName, latitude,longitude,addressName,isPwayMobile, isPwayCard, isPwayAccount, isPwayCash,
                    isOpenMon, isOpenTue, isOpenWed, isOpenThu, isOpenFri, isOpenSat, isOpenSun,selectedCategory,
                    isVerified,  hasMeeting, rating ,geohash,countVerified,countSingo,
                     (exteriorImagePath!= null) ? exteriorImagePath.toString() : ""
             );

            shop.setExteriorImagePath(exteriorImagePath);

            ReportShop reportShop= new ReportShop(uid);
            User user= new User();

            scrollView.setBackgroundColor(Color.parseColor("#000000")); // 제보버튼 누르면 배경색이 약간 어두어지게 연출

            FirebaseUtils.saveShopData(shop, reportShop ,user, storeExteriorImageUri,shopKey); //FirebaseUtils에 별도로 firebase에 넣는 코드 작성함


        }
    }

    @Override
    public void onShopDataSaved()
    {
        progressDialog.dismiss();       // 로딩 화면 숨기기
        Toast.makeText(getActivity(), "가게가 정상적으로 등록되었습니다! ", Toast.LENGTH_SHORT).show();

        reportBtn.setClickable(false); // 제보 여러번 연타 못하게 버튼 클릭 비활성화
        clearForm();  // 저장이 되는경우 기존에 작성된 폼 지움
        replaceFragment(); // 저장이 되는 경우 home으로 이동 하기 위한 메서드 호출
        }



    //  dkdk
    private void replaceFragment() {

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        }
    }


    public void clearForm()  // 제보화면에 작성폼 초기화
    {
        reportBtn.setClickable(true);
        scrollView.setBackgroundColor(Color.parseColor("#ffffff")); // 배경색을 원래대로 하얀색으로
        shopNameEditText.setText(""); // 가게 이름 입력란 초기화
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
        addressName="위치선택 버튼을 클릭하세요";
        storeExteriorPhoto.setImageDrawable(null);
        storeExteriorPhoto.setBackgroundResource(R.drawable.border_rounded_coner);
        storeExteriorImageUri=null;
        categorySpinner.setSelection(0); // Spinner 초기화 (카테고리 선택을 "선택 안함"으로)
        shopLocationText.setText("위치선택 버튼을 클릭하세요");  // 주소 텍스트 초기화
        latitude = 0.0; // 위도 초기화
        longitude = 0.0; //경도 초기화
        storePhotoTextView.setVisibility(View.VISIBLE); // 외관사진 표시 지우기
        isVerified=false;
        hasMeeting=false;
        geohash="";
    }
    private void hideKeyboard()    // 키보드 숨기는 메서드 - enter 키 / 빈 바탕을 눌렀을때 키보드를 숨기는것이 편리할것 같아 추가
    {
        View view = requireActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
