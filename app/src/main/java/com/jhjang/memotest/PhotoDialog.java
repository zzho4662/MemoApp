package com.jhjang.memotest;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class PhotoDialog extends Dialog {

    private Button btnCamera;
    private Button btnGallery;
    private Button btnURL;

    private View.OnClickListener cameraListener;
    private View.OnClickListener galleryListener;
    private View.OnClickListener urlListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.activity_photo_dialog);

        //셋팅
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnURL = (Button) findViewById(R.id.btnURL);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        btnCamera.setOnClickListener(cameraListener);
        btnGallery.setOnClickListener(galleryListener);
        btnURL.setOnClickListener(urlListner);

    }
        //생성자 생성
         public PhotoDialog(@NonNull Context context, View.OnClickListener cameraListener, View.OnClickListener galleryListener,
                            View.OnClickListener urlListner) {
            super(context);
            this.cameraListener = cameraListener;
            this.galleryListener = galleryListener;
            this.urlListner = urlListner;
        }
    }