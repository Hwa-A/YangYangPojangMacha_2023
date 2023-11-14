package com.yuhan.yangpojang;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.fragment.ReportShopFragment;
import com.yuhan.yangpojang.login.User;
import com.yuhan.yangpojang.model.ReportShop;
import com.yuhan.yangpojang.model.Shop;

public class FirebaseUtils {

    private static Shop shop;
    private static ShopDataListener shopDataListener;
    private static StorageReference storageRef;
    private static StorageReference shopImagesRef;

    public static void setShopDataListener(ShopDataListener listener) {
        shopDataListener = listener;
    }

    public static void saveShopData(Shop shop, ReportShop reportShop, User user, Uri exteriorImageUri, String shopKey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("fffffffffdskfjask2", String.valueOf(exteriorImageUri));

        Log.d("fffffffffdskfjas4k",shopKey);
        shop.setShopKey(shopKey);
        String shopkey=shop.getShopKey();
        Log.d("ffffffffffs5k",shopKey);

        DatabaseReference shopRef = databaseReference.child("shops").child(shopKey);
        reportShop.setShopKey(shopKey);
//        DatabaseReference reportShopRef = databaseReference.child("reportShop").child(reportShop.getUid()).child(shopKey);

        DatabaseReference reportShopRef = databaseReference.child("reportShop").child(reportShop.getUid()).child(shopKey);
        shopRef.setValue(shop)
                .addOnSuccessListener(aVoid -> {
                    Log.d("fffffffffbbbbbbbb", "가게 데이터가(이미지 제외) 성공적으로 저장되었습니다.");

                    reportShop.setShopKey(shopKey);

                    Log.d("bbbbddddddbbbb", "가게 데이터가(이미지 제외) 성공적으로 저장되었습니다.");

                    Log.d("fffffffffdskfjask 2323 e5r FirebaseUtils", "가게 데이터가(이미지 제외) 성공적으로 저장되었습니다.");

                    storageRef = FirebaseStorage.getInstance().getReference();
                    shopImagesRef = storageRef.child("shops").child(shopKey).child("images");

                    // 외관 이미지  업로드
                    if (exteriorImageUri != null)
                    {
                        Log.d(" fffffffffdskfjask 2323 FirebaseUtils", "외관 이미지 업로드 시작");
                        uploadImageToStorage(shop,shopImagesRef, exteriorImageUri, shopRef.child("fbStoreImgurl"), "exterior");

                    }
                    else {

                        Log.d(" fffffffffdskfjask 6060 외관이미지는 선택되지 않았습니다", "외관이미지는 선택되지 않았습니다");

                    }
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();
                    }
                })
                .addOnFailureListener(databaseException -> {
                    Log.e("fffffffffskfjask4094290982dfddefeFirebaseUtils", "가게 이미지 업로드 데이터 저장 실패: " + databaseException.getMessage());
                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();
                    }
                });



        // ReportShop 데이터를 저장
        reportShopRef.setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseUtils", "ReportShop 데이터가 성공적으로 저장되었습니다.");
//                    if (shopDataListener != null) {
//                        shopDataListener.onShopDataSaved();
//                    }
                })
                .addOnFailureListener(databaseException -> {
                    Log.e("FirebaseUtils", "ReportShop 데이터 저장 실패: " + databaseException.getMessage());
//                    if (shopDataListener != null) {
//                        shopDataListener.onShopDataSaved();
//                    }
                });

    }

    private static void uploadImageToStorage(Shop shop,StorageReference storageReference, Uri imageUri, DatabaseReference imagePathRef, String imageType) {
        String fileName;
        if (imageType.equals("exterior")) {
            fileName = "exterior.jpg";
        }
        else {
            return; // 이미지 타입이 올바르지 않을 경우 아무 작업도 수행하지 않음
        }

        StorageReference imageRef = storageReference.child(fileName);

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d("FirebaseUtils-uploadtask 부분", "이미지가 성공적으로 업로드되었습니다.");

            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // 이 uri가 웹에서 접근 가능한 URL입니다.
                    String downloadUrl = uri.toString();
                    Log.d("Download URL", downloadUrl);

                    // 이미지 경로 대신 다운로드 URL을 저장합니다.
                    imagePathRef.setValue(downloadUrl);

                    if (shopDataListener != null) {
                        shopDataListener.onShopDataSaved();
                    }
                }
            });
        }).addOnFailureListener(exception -> {
            Log.e("FirebaseUtils", imageType + " 업로드 실패: " + exception.getMessage());
        });
    }
}