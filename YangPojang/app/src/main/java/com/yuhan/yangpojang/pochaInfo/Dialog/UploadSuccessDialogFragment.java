package com.yuhan.yangpojang.pochaInfo.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.fragment.ProfileShowFragment;

// firebase 업로드 성공한 경우, 출력할 Dialog
public class UploadSuccessDialogFragment extends DialogFragment {
    private String confirmPlace;        // 어느 activity인지 확인  ex.MeetingwriteActivity에서 불렸는지
    private String uploadAction;        // 어떤 동작을 하는지 확인  ex. 수정 or 업로드

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // AlertDialog.Builder: AlertDialog 생성 가능한 API 제공해주는 클래스
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(confirmPlace + " "+ uploadAction+" 성공")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "확인" 버튼을 클릭한 경우
                        dialog.dismiss();   // 다이얼로그 종료
                        if (getActivity() != null){
                             getActivity().finish();     // 해당 액티비티 삭제
                           
                        }
                    }
                });

        // 다이얼로그 생성 후 반환
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);    // 외부 터치 불가능
        return dialog;
    }


    // ▼ 다이얼로그를 호출한 액티비티를 분별(번개 or 리뷰인지)
    public void setDialogCallPlace(String confirmPlace, String uploadAction) {
        this.confirmPlace = confirmPlace;
        this.uploadAction = uploadAction;
    }
}
