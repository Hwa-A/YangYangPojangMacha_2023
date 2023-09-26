package com.yuhan.yangpojang;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.model.Shop;

public class FirebaseUtils {

    private static ShopDataListener shopDataListener;
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
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference shopImagesRef = storageRef.child("shops").child(shopKey).child("images");

                    // 외관 이미지 업로드
                    if (exteriorImageUri != null) {
                        StorageReference exteriorImageRef = shopImagesRef.child("exterior.jpg");
                        exteriorImageRef.putFile(exteriorImageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // 외관 이미지 업로드 성공
                                    exteriorImageRef.getDownloadUrl()
                                            .addOnSuccessListener(exteriorUri -> {
                                                String exteriorImageUrl = exteriorUri.toString();
                                                Log.d("FirebaseUtils", "Exterior Image URL: " + exteriorImageUrl);
                                                shop.setExteriorImageUrl(exteriorImageUrl);

                                                // 메뉴 이미지 업로드
                                                uploadMenuImage(shop, shopKey, menuImageUri);
                                            })
                                            .addOnFailureListener(exteriorImageException -> {
                                                handleImageUploadFailure("외관 이미지", exteriorImageException);
                                            });
                                })
                                .addOnFailureListener(exteriorImageUploadException -> {
                                    handleImageUploadFailure("외관 이미지 업로드", exteriorImageUploadException);
                                });
                    } else {
                        // 외관 이미지가 선택되지 않은 경우
                        shop.setExteriorImageUrl(null);

                        // 메뉴 이미지 업로드 처리만 진행
                        uploadMenuImage(shop, shopKey, menuImageUri);
                    }
                })
                .addOnFailureListener(databaseException -> {
                    // 가게 데이터 저장 실패
                    Log.e("FirebaseUtils", "가게 데이터 저장 실패: " + databaseException.getMessage());


                });
    }

    private static void uploadMenuImage(Shop shop, String shopKey, Uri menuImageUri) {
        // 메뉴 이미지 업로드
        if (menuImageUri != null) {
            // Firebase 스토리지 레퍼런스 얻기
            StorageReference menuImageRef = FirebaseStorage.getInstance().getReference()
                    .child("shops").child(shopKey).child("images").child("menu.jpg");
            menuImageRef.putFile(menuImageUri)
                    .addOnSuccessListener(taskSnapshot1 -> {
                        // 메뉴 이미지 업로드 성공
                        menuImageRef.getDownloadUrl()
                                .addOnSuccessListener(menuUri -> {
                                    String menuImageUrl = menuUri.toString();
                                    Log.d("FirebaseUtils", "Menu Image URL: " + menuImageUrl);
                                    shop.setMenuImageUrl(menuImageUrl);

                                    // Firebase 실시간 데이터베이스에서 가게 데이터 업데이트
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference shopRef = databaseReference.child("shops").child(shopKey);
                                    shopRef.setValue(shop)
                                            .addOnSuccessListener(aVoid -> {
                                                // 가게 데이터 업데이트 성공
                                                Log.d("FirebaseUtils", "가게 데이터가 성공적으로 업데이트되었습니다.");
                                                Log.d("Fdsfdsfsadfs", shopDataListener.toString());
                                                if (shopDataListener != null) {
                                                    Log.d("cooginef","cooginef");
                                                    shopDataListener.onShopDataSaved();
                                                }

                                            })
                                            .addOnFailureListener(databaseUpdateException -> {
                                                // 가게 데이터 업데이트 실패
                                                Log.e("FirebaseUtils", "가게 데이터 업데이트 실패: " + databaseUpdateException.getMessage());


                                            });
                                })
                                .addOnFailureListener(menuImageException -> {
                                    handleImageUploadFailure("메뉴 이미지", menuImageException);
                                });
                    })
                    .addOnFailureListener(menuImageUploadException -> {
                        handleImageUploadFailure("메뉴 이미지 업로드", menuImageUploadException);
                    });
        } else {
            // 메뉴 이미지가 선택되지 않은 경우
            shop.setMenuImageUrl(null);

            // Firebase 실시간 데이터베이스에서 가게 데이터 업데이트
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference shopRef = databaseReference.child("shops").child(shopKey);
            shopRef.setValue(shop)
                    .addOnSuccessListener(aVoid -> {
                        // 가게 데이터 업데이트 성공
                        Log.d("FirebaseUtils", "가게 데이터가(이미지 포함) 성공적으로 업데이트되었습니다.");
                        Log.d("Fdsfdsfsadfs", shopDataListener.toString());

                        if (shopDataListener != null) {
                            Log.d("cooginef","cooginef");
                            shopDataListener.onShopDataSaved();
                        }

                    })
                    .addOnFailureListener(databaseUpdateException -> {
                        // 가게 데이터 업데이트 실패
                        Log.e("FirebaseUtils", "가게 데이터 업데이트 실패: " + databaseUpdateException.getMessage());


                    });
        }
    }

    private static void handleImageUploadFailure(String imageType, Exception exception) {
        // 이미지 업로드 실패 처리
        Log.e("FirebaseUtils", imageType + " 업로드 실패: " + exception.getMessage());
        // 여기에서 실패 처리를 원하는 방식으로 수행하십시오.
        // 폼클리어 하기? -주석 지우기
    }



}