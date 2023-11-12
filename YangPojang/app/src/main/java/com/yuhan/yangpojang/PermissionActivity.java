package com.yuhan.yangpojang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    private static final int PERMISSION_REQUEST_CODE = 100;

    private Button permissionAccessButton;
    private String user_info_uid = null;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("권한", "onCreate: 권한");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }


        permissionAccessButton = findViewById(R.id.permissionAccess);

        permissionAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();

            }
        });
    }

    public void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            mDatabase.child("user-info").child(user_info_uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    // 사용자 권한 요청에 대한 응답 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDatabase.child("user-info").child(user_info_uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
            else {
                mDatabase.child("user-info").child(user_info_uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    }

}

