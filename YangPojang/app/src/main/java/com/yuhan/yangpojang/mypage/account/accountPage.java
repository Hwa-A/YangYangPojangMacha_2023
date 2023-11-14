package com.yuhan.yangpojang.mypage.account;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.SplashImage;

import java.util.ArrayList;
import java.util.List;


public class accountPage extends AppCompatActivity {

    private String user_info_uid = null;
    private DatabaseReference mDatabase;

    private ListView settingsListView;




    private FirebaseAuth mAuth ;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount gsa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        settingsListView = findViewById(R.id.account_page_listview);


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(accountPage.this, googleSignInOptions);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }
        else {

        }


        // Create a list of items (e.g., Logout and Withdraw)
        List<String> itemList = new ArrayList<>();
        itemList.add("\uD83D\uDD13 구글 계정 로그아웃");
        itemList.add("⚠️ 회원탈퇴");
        itemList.add("\uD83D\uDCF1 어플 설명");

        // Create an ArrayAdapter to bind the items to the ListView

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        settingsListView.setAdapter(adapter);

        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()  // 자동완성 위치 리스트에서  선택한 주소(아이템)에 대한 처리
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleItemClick(position);
            }
        });
    }

    private void handleItemClick(int position) {
        switch (position) {
            case 0:
                Log.d("ffffffffff","!");
                // Handle Google sign out
                showLogoutDialog();
                break;
            case 1:
                Log.d("ffffffffff2","!");

                // Handle account deletion
                showDeleteAccountDialog();
                break;
            case 2:
                Log.d("ffffffffff3","!");
                showPopup();
                break;
        }
    }

    private void showPopup() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.app_introduction_popup, null);
        builder.setView(customView);
        final android.app.AlertDialog dialog = builder.create();

        final TextView description = customView.findViewById(R.id.description);
        final TextView authmeeting = customView.findViewById(R.id.authmeeting);
        final ImageView auth = customView.findViewById(R.id.auth);
        final ImageView meet = customView.findViewById(R.id.meet);
        final Button end = customView.findViewById(R.id.end);

        final String[] descriptions = {this.getResources().getString(R.string.description1), this.getResources().getString(R.string.description2), this.getResources().getString(R.string.description3)};
        final String[] authmeetings = {this.getResources().getString(R.string.title1), this.getResources().getString(R.string.title2), this.getResources().getString(R.string.title3)};
        final int[] currentIndex = {0};

        Button cancel = customView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.dismiss(); }
        });

        Button next = customView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 배열에서 다음 텍스트를 가져와 설정
                if (currentIndex[0] < descriptions.length) {
                    currentIndex[0]++;
                    if (currentIndex[0] == 1) {
                        auth.setVisibility(VISIBLE);
                        authmeeting.setVisibility(VISIBLE);
                    } else if (currentIndex[0] == 2) {
                        auth.setVisibility(INVISIBLE);
                        meet.setVisibility(VISIBLE);
                        cancel.setVisibility(INVISIBLE);
                        next.setVisibility(INVISIBLE);
                        end.setVisibility(VISIBLE);
                    } else {
                        auth.setVisibility(View.GONE);
                        meet.setVisibility(View.GONE);
                        authmeeting.setVisibility(View.GONE);
                    }

                    description.setText(descriptions[currentIndex[0]]);
                    authmeeting.setText(authmeetings[currentIndex[0]]);
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.dismiss(); }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }






    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(accountPage.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setTitle("로그아웃")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        signOut();
                        Intent intent = new Intent(getApplicationContext(), SplashImage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("로그아웃");
        alert.show();
    }



    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(accountPage.this);
        builder.setMessage("회원탈퇴 하시겠습니까?");
        builder.setTitle("회원탈퇴")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        signOut();
                        deleteAccount();
                        Intent intent = new Intent(getApplicationContext(), SplashImage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("회원탈퇴");
        alert.show();
    }



    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.getInstance().signOut();
                    gsa = null;
                });
    }


    private void deleteAccount() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile").child(user_info_uid);
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mGoogleSignInClient.revokeAccess();
                storageReference.delete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                user.delete();
                mGoogleSignInClient.revokeAccess();
                storageReference.delete();
            }
        });
    }


}


