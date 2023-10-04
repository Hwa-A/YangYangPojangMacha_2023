package com.yuhan.yangpojang.login;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yuhan.yangpojang.MainActivity;
import com.yuhan.yangpojang.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Calendar;

public class LogindetailAct extends AppCompatActivity {

    private String user_uid = null;
    private DatabaseReference mDatabase;
    TextView datetext;
    DatePickerDialog datePickerDialog;
    RadioGroup radioGroup;
    TextView sexchecked;

    public String usr_nick = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_uid = user.getUid();
        }

        datetext = findViewById(R.id.showDate);
        Button datepickBtn = findViewById(R.id.selectDateBtn);
        EditText userNickname = findViewById(R.id.userNickname);
        Button signCompleteBtn = findViewById(R.id.signCompleteBtn);
        radioGroup = findViewById(R.id.sexGroup);
        sexchecked = findViewById(R.id.sex);

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


        signCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUserName = userNickname.getText().toString();
                String getUserDate = datetext.getText().toString();
                String getUserSex = sexchecked.getText().toString();

                usr_nick = getUserName;
                writeNewUser(getUserName,getUserDate,getUserSex);

            }
        });
    }

    private void writeNewUser(String name, String birthday, String sex) {
        User user = new User(name, birthday,sex);
        mDatabase.child("user-info").child(user_uid).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(LogindetailAct.this, "회원가입 완료!.", Toast.LENGTH_SHORT).show();
                        Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent_main);
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