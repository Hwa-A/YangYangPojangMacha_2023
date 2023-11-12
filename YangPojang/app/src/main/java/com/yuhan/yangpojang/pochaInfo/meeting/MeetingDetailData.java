//package com.yuhan.yangpojang.pochaInfo.meeting;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.yuhan.yangpojang.R;
//
//public class MeetingDetailData extends AppCompatActivity {
//
//    TextView meeting_title;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.item_pochameeting_detail);
//
//        Intent intent = getIntent();
//        String Title = intent.getStringExtra("title");
//
//        meeting_title = findViewById(R.id.meeting_Title);
//        meeting_title.setText(Title);
//
//
//    }
//
//}
