package com.yuhan.yangpojang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.Profile_setting;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.login.User;


public class ProfileShowFragment extends Fragment {

    private TextView profilenickname;
    private TextView profilebirth;
    private TextView profilesex;
    private Button setting;

    String user_nickname;
    String user_birth;
    String user_sex;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String user_info_uid = null;



    @Nullable  // null 체크유도, 경고를 통해 누락된 체크를 알려줄수 있음
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }


        mDatabase.child("user-info").child(user_info_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                user_nickname = user.getUser_Nickname();
                user_birth = user.getUser_birthday();
                user_sex = user.getUser_sex();

                profilenickname.setText(user_nickname);
                profilebirth.setText(user_birth);
                profilesex.setText(user_sex);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        View view = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        //개인 정보를 보여주는 텍스트뷰
        profilenickname= view.findViewById(R.id.profile_nickname);
        profilebirth = view.findViewById(R.id.profile_birth);
        profilesex = view.findViewById(R.id.profile_sex);
        setting = view.findViewById(R.id.settingButton);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Profile_setting.class); //fragment라서 activity intent와는 다른 방식
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        return view;

    }

}