package com.yuhan.yangpojang;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuhan.yangpojang.login.LoginActivity;


public class Splashoutimage extends Activity {

    private static final int DELETE_ACCOUNT = 1;
    private static final int SPLASH_TIMEOUT = 3000; // 스플래시 화면을 보여줄 시간 (밀리초)
    private DatabaseReference mDatabase;
    private String user_info_uid = null;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        user_info_uid = intent.getStringExtra("uid");

        // AsyncTask를 사용하여 deleteaccount() 함수를 백그라운드에서 실행
        new DeleteAccountTask().execute();
    }

    // AsyncTask를 상속받아 구현
    private class DeleteAccountTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // 백그라운드에서 수행할 작업

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            deleteDB();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // 작업이 완료된 후에 호출되는 메소드
            // 스플래시 화면 종료 및 다음 화면으로 이동
            Intent intent = new Intent(Splashoutimage.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void deleteDB() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile").child(user_info_uid);
        storageReference.delete();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("uid",user_info_uid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();


    }


}
