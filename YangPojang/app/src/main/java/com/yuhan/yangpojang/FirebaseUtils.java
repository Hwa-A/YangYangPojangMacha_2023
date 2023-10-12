package com.yuhan.yangpojang;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.fragment.ReportShopFragment;
import com.yuhan.yangpojang.model.Shop;

public class FirebaseUtils {

    private static ShopDataListener shopDataListener;
    private static StorageReference storageRef; // FirebaseStorage.getInstance().getReference();
    private static StorageReference shopImagesRef; // = storageRef.child("shops").child(shopKey).child("images");
    Shop shop;

    public static void setShopDataListener(ShopDataListener listener) {
        shopDataListener = listener;
    }
    public static void saveShopData(Shop shop, Uri exteriorImageUri, Uri menuImageUri) {
        // Firebase 관련 작업을 수행하여 가게 데이터를 저장하고 이미지를 업로드하고 리스너에 알립니다.

        // Firebase 실시간 데이터베이스 레퍼런스 얻기
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // 가게 키 생성 및 레퍼런스 설정
        String shopKey = databaseReference.child("shops").push().getKey();
        DatabaseReference shopRef = databaseReference.child("shops").child(shopKey);

        // 가게 데이터 저장
        shopRef.setValue(shop)
                .addOnSuccessListener(aVoid -> {
                    // 가게 데이터 저장 성공
                    Log.d("FirebaseUtils", "가게 데이터가(이미지 제외) 성공적으로 저장되었습니다.");

                    // Firebase 스토리지 레퍼런스 얻기
                    storageRef = FirebaseStorage.getInstance().getReference();
                    //shopImagesRef: [shops/shopkey/images 경로의 storage]
                    shopImagesRef = storageRef.child("shops").child(shopKey).child("images");


                    // 외관 이미지 업로드
                    if (exteriorImageUri != null) {
                        Log.d("FirebaseUtils", "외관 이미지 업로드 시작");
                        uploadImageToStorage(shopImagesRef, exteriorImageUri,shopRef.child("exteriorImagePath"));

                        if(menuImageUri != null)
                        {
                            Log.d("FirebaseUtils", "메뉴 이미지 업로드 시작");
                            uploadImageToStorage(shopImagesRef, menuImageUri, shopRef.child("menuImagePath"));
                        }
                        else
                        {
                            Log.d("FirebaseUtils", "메뉴 이미지 미선택");

                        }

                    }
                    else {
                        Log.d("외관이미지는 선택되지 않았습니다","외관이미지는 선택되지 않았습니다");
                        if(menuImageUri != null)
                        {
                            Log.d("FirebaseUtils", "메뉴 이미지 업로드 시작");
                            uploadImageToStorage(shopImagesRef, menuImageUri, shopRef.child("menuImagePath"));
                        }
                        else
                        {
                            Log.d("메뉴이미지도 선택되지  않았습니다","메뉴이미지도 선택되지  않았습니다");
                        }


                    }
                    // 데이터 저장 실패 시에도 리스너 호출
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();}
                })
                .addOnFailureListener(databaseException -> {
                    // 가게 데이터 저장 실패
                    Log.e("FirebaseUtils", "가게 데이터 저장 실패: " + databaseException.getMessage());
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();}


                });
    }
    private static void uploadImageToStorage(StorageReference storageReference, Uri imageUri,DatabaseReference imagePathRef) {
        // 파일 이름을 설정하거나 원하는 이름으로 저장할 수 있습니다.
        // 여기서는 "exterior.jpg"라는 파일 이름을 사용합니다.
        StorageReference imageRef = storageReference.child("exterior.jpg");

        // 이미지를 업로드합니다.
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // 업로드 상태를 모니터링하고 업로드 완료 후 동작을 수행할 수 있습니다.
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // 이미지 업로드 성공
            Log.d("FirebaseUtils", "이미지가 성공적으로 업로드되었습니다.");

            // 이미지 경로를 firebase realtime db에 삽입
            String imagePath = imageRef.getPath(); // 이미지 경로 가져오기
            imagePathRef.setValue(imagePath); // 이미지 경로를 Realtime Database에 저장
            Log.d("ff","Ff");
            if (shopDataListener != null) {
                Log.d("cooginef","cooginef");
                shopDataListener.onShopDataSaved();
            }

        }).addOnFailureListener(exception -> {
            // 이미지 업로드 실패
//            handleImageUploadFailure();
            Log.e("FirebaseUtils", "이미지 업로드 실패: " + exception.getMessage());

        });
    }


    private static void handleImageUploadFailure(String imageType, Exception exception) {
        // 이미지 업로드 실패 처리
        Log.e("FirebaseUtils", imageType + " 업로드 실패: " + exception.getMessage());
        // 여기에서 실패 처리를 원하는 방식으로 수행하십시오.

        // 다음 코드는 없앨가능성이 큽니다 확인하시오.
        ReportShopFragment reportShopFragment= new ReportShopFragment();
        reportShopFragment.clearForm();
    }



}