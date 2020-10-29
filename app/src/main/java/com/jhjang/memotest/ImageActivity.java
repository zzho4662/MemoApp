package com.jhjang.memotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.ImageMemo;

import java.io.Serializable;
import java.util.ArrayList;

import static com.jhjang.memotest.adapter.ImageRecyclerViewAdapter.StringToBitmap;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnBack;
    Button btnDelete;
    ArrayList<ImageMemo> imageMemoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = getIntent().getIntExtra("id", -1);
                int index = getIntent().getIntExtra("index", -1);

                DatabaseHandler dh = new DatabaseHandler(ImageActivity.this);

                Intent i = new Intent(ImageActivity.this, UpdateMemo.class);
                i.putExtra("true", true);
                i.putExtra("delete", index);
                i.putExtra("id",id);
                startActivity(i);

//                imageMemoArrayList.remove(index);

                //setResult(); => onActivityResult() : 어레이리스트에서 삭제하고, 어댑터 노티파이
                finish();
            }
        });

        if (getIntent().getExtras().containsKey("gallery")){
            String image = getIntent().getStringExtra("gallery");
            Uri image_uri = Uri.parse(image).buildUpon().build();
            imageView.setImageURI(image_uri);
        }else {
            byte[] b = getIntent().getByteArrayExtra("camera");
            Bitmap image_bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageView.setImageBitmap(image_bitmap);
        }
    }
    public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length ) ;
        return bitmap ;
    }
}