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
    private static StorageReference storageRef;
    private static StorageReference shopImagesRef;
    Shop shop;

    public static void setShopDataListener(ShopDataListener listener) {
        shopDataListener = listener;
    }

    public static void saveShopData(Shop shop, Uri exteriorImageUri, Uri menuImageUri) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String shopKey = databaseReference.child("shops").push().getKey();
        DatabaseReference shopRef = databaseReference.child("shops").child(shopKey);

        shopRef.setValue(shop)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseUtils", "가게 데이터가(이미지 제외) 성공적으로 저장되었습니다.");

                    storageRef = FirebaseStorage.getInstance().getReference();
                    shopImagesRef = storageRef.child("shops").child(shopKey).child("images");

                    // 외관 이미지 업로드
                    if (exteriorImageUri != null) {
                        Log.d("FirebaseUtils", "외관 이미지 업로드 시작");
                        uploadImageToStorage(shopImagesRef, exteriorImageUri, shopRef.child("exteriorImagePath"), "exterior");

                        if (menuImageUri != null) {
                            Log.d("FirebaseUtils", "메뉴 이미지 업로드 시작");
                            uploadImageToStorage(shopImagesRef, menuImageUri, shopRef.child("menuImagePath"), "menu");
                        } else {
                            Log.d("FirebaseUtils", "메뉴 이미지 미선택");
                        }
                    } else {
                        Log.d("외관이미지는 선택되지 않았습니다", "외관이미지는 선택되지 않았습니다");
                        if (menuImageUri != null) {
                            Log.d("FirebaseUtils", "메뉴 이미지 업로드 시작");
                            uploadImageToStorage(shopImagesRef, menuImageUri, shopRef.child("menuImagePath"), "menu");
                        } else {
                            Log.d("메뉴이미지도 선택되지 않았습니다", "메뉴이미지도 선택되지 않았습니다");
                        }
                    }
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();
                    }
                })
                .addOnFailureListener(databaseException -> {
                    Log.e("FirebaseUtils", "가게 이미지 업로드 데이터 저장 실패: " + databaseException.getMessage());
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();
                    }
                });
    }

    private static void uploadImageToStorage(StorageReference storageReference, Uri imageUri, DatabaseReference imagePathRef, String imageType) {
        String fileName;
        if (imageType.equals("exterior")) {
            fileName = "exterior.jpg";
        } else if (imageType.equals("menu")) {
            fileName = "menu.jpg";
        } else {
            return; // 이미지 타입이 올바르지 않을 경우 아무 작업도 수행하지 않음
        }

        StorageReference imageRef = storageReference.child(fileName);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d("FirebaseUtils", "이미지가 성공적으로 업로드되었습니다.");

            String imagePath = imageRef.getPath();
            imagePathRef.setValue(imagePath);
            if (shopDataListener != null) {
                shopDataListener.onShopDataSaved();
            }
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseUtils", imageType + " 업로드 실패: " + exception.getMessage());
        });
    }
}
