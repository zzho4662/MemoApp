package com.jhjang.memotest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.Memo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddMemo extends AppCompatActivity {

    private EditText editTitle;
    private EditText editContent;
    private ImageView imgSave;
    private ImageView imgPicture;
    private String picture="";
    private RecyclerView recyclerView;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        imgSave = findViewById(R.id.imgUpdate);
        imgPicture = findViewById(R.id.imgPicture);


        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(AddMemo.this, "제목과 내용은 필수입니다.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 저장.
                DatabaseHandler dh = new DatabaseHandler(AddMemo.this);
                Memo memo = new Memo();
                memo.setTitle(title);
                memo.setContent(content);
                dh.addMemo(memo);

                // 잘 저장했다고 토스트
                Toast.makeText(AddMemo.this, "잘 저장되었습니다.",
                        Toast.LENGTH_SHORT).show();

                // 메인액티비티 다시 보이도록
                finish();
            }
        });

        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        displayFileChoose();
                    }
                    requestPermission();
                }
            }
        });
    }
    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddMemo.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(AddMemo.this, "권한 수락이 필요합니다ㅑ", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddMemo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddMemo.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_DENIED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imgPhoto.setImageBitmap(photo);

        } else if (requestCode == 300 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imgPath = data.getData();
            image.setImageURI(imgPath);

            // 실제 경로를 몰라도, 파일의 내용을 읽어와서, 임시파일 만들어서 서버로 보낸다.
            String id = DocumentsContract.getDocumentId(imgPath);
            try {
                InputStream inputStream = getContentResolver().openInputStream(imgPath);
                photoFile = new File(getCacheDir().getAbsolutePath() + "/" + id + ".jpg");
                writeFile(inputStream, photoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

}

