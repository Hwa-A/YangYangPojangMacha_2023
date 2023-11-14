package com.yuhan.yangpojang.login;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yuhan.yangpojang.MainActivity;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.SplashImage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogindetailAct extends AppCompatActivity {

    private String user_info_uid = null;
    private DatabaseReference mDatabase;
    TextView datetext;
    DatePickerDialog datePickerDialog;
    RadioGroup radioGroup;
    TextView sexchecked;

    //서빈추가 필요
    TextView textView;

    ImageView profileImage;
    private Uri User_profileuri;
    private String User_img;
    String imageUri = "drawable://" + R.drawable.profile_image;
    private Uri imguri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }

        imguri = Uri.parse(imageUri);
        User_profileuri = imguri;
        datetext = findViewById(R.id.showDate);
        Button datepickBtn = findViewById(R.id.selectDateBtn);
        EditText userNickname = findViewById(R.id.userNickname);
        Button signCompleteBtn = findViewById(R.id.signCompleteBtn);
        radioGroup = findViewById(R.id.sexGroup);
        sexchecked = findViewById(R.id.sex);
        profileImage = findViewById(R.id.profileImagesetting);
        User_img = "/profile/"+user_info_uid;
        textView= findViewById(R.id.textView);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sexM){
                    Toast.makeText(LogindetailAct.this, "남자 선택!.", Toast.LENGTH_SHORT).show();
                    sexchecked.setText("남자");
                }else if (checkedId == R.id.sexF){
                    Toast.makeText(LogindetailAct.this, "여자 선택!.", Toast.LENGTH_SHORT).show();
                    sexchecked.setText("여자");
                }
            }
        });

        datepickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //오늘 날짜(년,월,일) 변수에 담기
                Calendar calendar = Calendar.getInstance();
                int pYear = calendar.get(Calendar.YEAR); //년
                int pMonth = calendar.get(Calendar.MONTH);//월
                int pDay = calendar.get(Calendar.DAY_OF_MONTH);//일

                datePickerDialog = new DatePickerDialog(LogindetailAct.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                //1월은 0부터 시작하기 때문에 +1을 해준다.
                                month = month + 1;
                                String date = year + "/" + month + "/" + day;

                                datetext.setText(date);
                            }
                        }, pYear, pMonth, pDay);
                datePickerDialog.show();
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select();
            }
        });


        signCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUserName = userNickname.getText().toString();
                String getUserDate = datetext.getText().toString();
                String getUserSex = sexchecked.getText().toString();
                String getUserimg = User_img;


                if(userNickname.length()==0){Toast.makeText(getApplicationContext(),"닉네임을 입력하세요",Toast.LENGTH_LONG).show();}
                else {
                    if(getUserDate == ""){Toast.makeText(getApplicationContext(),"날짜를 선택해주세요",Toast.LENGTH_LONG).show();}
                    else {
                        if(getUserSex != "남자" && getUserSex != "여자" ){Toast.makeText(getApplicationContext(),"성별을 선택하여주세요",Toast.LENGTH_LONG).show(); }
                        else {
                            if(User_profileuri == imguri){ writeNewUser(getUserName,getUserDate,getUserSex,getUserimg); upload_profile(); }
                            else{ writeNewUser(getUserName,getUserDate,getUserSex,getUserimg); upload_selected(); }
                        }
                    }
                }

            }
        });
    }


    private void select() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT );
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void upload_selected() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile");
        storageReference.child(user_info_uid).putFile(User_profileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LogindetailAct.this, "업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LogindetailAct.this, "업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upload_profile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile");
        Uri uri = Uri.parse("android.resource://com.yuhan.yangpojang/" + R.drawable.profile_image);

        storageReference.child(user_info_uid).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LogindetailAct.this, "업로드에 성공했습니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LogindetailAct.this, "업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        User_profileuri = result.getData().getData();
                        profileImage.setImageURI(User_profileuri);
                        // 서빈 추가
                        profileImage.setBackground(null);
                        textView.setVisibility(View.GONE);

                    }
                }
            });


    private void writeNewUser(String name, String birthday, String sex, String img) {
        User user = new User(name, birthday,sex, img);
        mDatabase.child("user-info").child(user_info_uid).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LogindetailAct.this, "회원가입 완료!.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(LogindetailAct.this, "회원가입 실패!", Toast.LENGTH_SHORT).show();
                    }
                });



    }
}


