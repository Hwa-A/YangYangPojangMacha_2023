package com.yuhan.yangpojang.pochaInfo.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yuhan.yangpojang.R;

public class ReviewImageExpandDialog {
    public static void show(Context context, String imageUrl){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.setContentView(R.layout.review_image_expand_dialog);

        // 다이얼로그 외부 화면이 어둡도록 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 외부 터치 불가능
        dialog.setCanceledOnTouchOutside(false);

        ImageView imageView = dialog.findViewById(R.id.img_pochareview_imageExpand);
        ImageButton closeBtn = dialog.findViewById(R.id.imgbtn_pochareviw_imageExpandedCancel);

        // 이미지 로드
        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.img_loading)  // 로딩 중 표시할 이미지
                        .error(R.drawable.loading)    // 이미지 로드 실패 시 표시할 이미지
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 디스크 캐시 사용 안 함
                        .skipMemoryCache(true)) // 메모리 캐시 사용 안 함
                .into(imageView);

        // 다이얼로그 닫기
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 다이얼로그 위치 조절
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());    // 현재 다이얼로그 창의 속성을 가져와 변수에 복사
        lp.gravity = Gravity.CENTER;    // 화면 중앙에 위치하도록

        dialog.getWindow().setAttributes(lp);

        // 다이얼로그 표시
        dialog.show();
    }
}
