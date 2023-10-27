package com.yuhan.yangpojang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.login.LogindetailAct;
import com.yuhan.yangpojang.login.User;

public class PermissionActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private Button requestLocationPermissionButton;
    private Switch locationSwitch;

    private static final int REQUEST_GALLERY_PERMISSION_CODE = 2;
    private Button requestGalleryPermissionButton;
    private Switch gallerySwitch;

    private Button permissionAccessButton;
    private String user_info_uid = null;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }

        requestLocationPermissionButton = findViewById(R.id.requestLocationPermission);
        locationSwitch = findViewById(R.id.locationswitch);

        requestGalleryPermissionButton = findViewById(R.id.requestGalleryPermission);
        gallerySwitch = findViewById(R.id.galleryswitch);

        permissionAccessButton = findViewById(R.id.permissionAccess);

        // 위치 권한
        requestLocationPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 위치 권한 요청
                requestLocationPermission();
            }
        });

        // 갤러리 권한
        requestGalleryPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리 권한 요청
                requestGalleryPermission();
            }
        });

        permissionAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLocationPermissionEnabled = locationSwitch.isChecked();

                //위치 권한이 허용된 경우
                if (isLocationPermissionEnabled) {

                    //uid를 기준으로 RealTimeDatabase 에 정보가 있는경우는 Main으로 없는 경우는 Detail로 이동
                    mDatabase.child("user-info").child(user_info_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user_check = snapshot.getValue(User.class);

                        if(user_check == null)
                        {
                            Intent intent_main = new Intent(getApplication(), LogindetailAct.class);
                            startActivity(intent_main);
                            finish();
                        }
                        else
                        {
                            Intent intent_main = new Intent(getApplication(), MainActivity.class);
                            startActivity(intent_main);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                }
                //위치 권한이 허용되지 않은 경우
                else
                {
                    Toast.makeText(PermissionActivity.this, "위치권한을 허용해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않은 경우 권한 요청 다이얼로그를 표시
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        } else {
            // 이미 권한이 부여된 경우 스위치를 true로 변경
            locationSwitch.setChecked(true);
            // 위치 정보를 가져오는 작업 수행
        }
    }

    private void requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않은 경우 권한 요청 다이얼로그를 표시
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION_CODE);
        } else {
            // 이미 권한이 부여된 경우 스위치를 true로 변경
            gallerySwitch.setChecked(true);
            // 갤러리 권한을 사용한 작업 수행
        }
    }

    // 사용자 권한 요청에 대한 응답 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우 스위치를 true로 변경
                locationSwitch.setChecked(true);
                // 위치 정보를 가져오는 작업 수행
            } else {
                // 권한이 거부된 경우 스위치를 false로 변경
                locationSwitch.setChecked(false);
                // 사용자에게 권한이 필요하다는 메시지를 표시할 수 있음
            }
        }

        if (requestCode == REQUEST_GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우 스위치를 true로 변경
                gallerySwitch.setChecked(true);
                // 갤러리 권한을 사용한 작업 수행
            } else {
                // 권한이 거부된 경우 스위치를 false로 변경
                gallerySwitch.setChecked(false);
                // 사용자에게 권한이 필요하다는 메시지를 표시할 수 있음
            }
        }

    }

}

