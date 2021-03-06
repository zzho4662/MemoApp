package com.jhjang.memotest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhjang.memotest.adapter.ImageRecyclerViewAdapter;
import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.ImageMemo;
import com.jhjang.memotest.model.Memo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddMemo extends AppCompatActivity {

    private EditText editTitle;
    private EditText editContent;
    private ImageView imgSave;
    private ImageView imgPicture;
    private String picture="";
    private RecyclerView recyclerView;
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    ArrayList<ImageMemo> imageMemoArrayList = new ArrayList<>();
    PhotoDialog photoDialog;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        imgSave = findViewById(R.id.imgUpdate);
        imgPicture = findViewById(R.id.imgPicture);
        recyclerView = findViewById(R.id.recyclerViewImage);

        // 이미지 양옆 위아래 공백 삭제
//        image.setAdjustViewBounds(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddMemo.this, LinearLayoutManager.HORIZONTAL, false) );

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();
                if(title.isEmpty() || content.isEmpty()){
                    Toast.makeText(AddMemo.this, R.string.email,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 저장.
                DatabaseHandler dh = new DatabaseHandler(AddMemo.this);
                Memo memo = new Memo();
                memo.setTitle(title);
                memo.setContent(content);
                dh.addMemo(memo);

                Memo memoId = dh.getMemoId();
                int integerId = memoId.getId();

                for (int i =0; i < imageMemoArrayList.size(); i++){
                  String image = imageMemoArrayList.get(i).getImage();
                  Log.i("AAA","image path : "+image);
                  dh.addImage(image,integerId);
                }

                // 메인액티비티 다시 보이도록
                finish();
            }
        });

        imgPicture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 카메라 / 갤러리 / 취소 버튼을 띄우는 다이얼로그
            photoDialog = new PhotoDialog(AddMemo.this, cameraListenr, galleryListner, urlListner);
            photoDialog.show();
        }
    });
}

    // 카메라 키는 버튼
    public View.OnClickListener cameraListenr = new View.OnClickListener() {
        public void onClick(View v) {
            int permissionCheck = ContextCompat.checkSelfPermission(
                    AddMemo.this, Manifest.permission.CAMERA);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddMemo.this, new String[]{Manifest.permission.CAMERA},
                        1000);
                Toast.makeText(AddMemo.this, R.string.needCamera, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(AddMemo.this.getPackageManager()) != null) {
                    // 사진의 파일명을 만들기
                    String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    photoFile = getPhotoFile(fileName);

                    Uri fileProvider = FileProvider.getUriForFile(AddMemo.this, "com.jhjang.memotest.fileprovider", photoFile);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                    startActivityForResult(i, 100);
                } else {
                    Toast.makeText(AddMemo.this, R.string.noCamera, Toast.LENGTH_SHORT).show();
                    return;
                }
                photoDialog.dismiss();
            }
        }
    };

    // 갤러리로 가는 버튼
    public View.OnClickListener galleryListner = new View.OnClickListener() {
        public void onClick(View v) {
            checkGalleryPermission();
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    displayFileChoose();
                } else {
                    requestPermission();
                }
            }
            photoDialog.dismiss();
        }
    };

    // URL
    private View.OnClickListener urlListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            photoDialog.dismiss();
        }
    };

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }
    private void requestPermission() {

        // 갤러리 1 코드에서 둘다 -1 -1 false  값인데 비교로 해놔서 true 값이 되버림
        int result = ContextCompat.checkSelfPermission(
                AddMemo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean r = true;
        if (result == -1 && PackageManager.PERMISSION_DENIED == -1){
            r = false;
        }

        if (!r) {
            Toast.makeText(AddMemo.this, "권한 수락이 필요합니다ㅑ", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddMemo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(
                AddMemo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else {
            return true;
        }



    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1000: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(AddMemo.this, R.string.permission,
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddMemo.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(AddMemo.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddMemo.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }

        //권한을 허용 했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 101 && resultCode == RESULT_OK){
            try{
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(requestCode == 101 && resultCode == RESULT_CANCELED){
            Toast.makeText(this,"cancel", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            String image_string = BitmapToString(photo);

            ImageMemo imageMemo = new ImageMemo();
            imageMemo.setImage(image_string);
            Log.i("AAA" , "camera : " + image_string);
            imageMemoArrayList.add(imageMemo);

            imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(AddMemo.this, imageMemoArrayList);
            recyclerView.setAdapter(imageRecyclerViewAdapter);
        }
            if (requestCode == 300 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imgPath = data.getData();

                // 실제 경로를 몰라도, 파일의 내용을 읽어와서, 임시파일 만들어서 서버로 보낸다.
                String id = DocumentsContract.getDocumentId(imgPath);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imgPath);
                    photoFile = new File(getCacheDir().getAbsolutePath()+"/"+id+".jpg");
                    writeFile(inputStream, photoFile);
//                String filePath = photoFile.getAbsolutePath();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String image = photoFile.getAbsolutePath();
                Log.i("AAA","gallery : " +  image);

            ImageMemo imageMemo = new ImageMemo();
            imageMemo.setImage(image);
            imageMemoArrayList.add(imageMemo);

            imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(AddMemo.this, imageMemoArrayList);
            recyclerView.setAdapter(imageRecyclerViewAdapter);

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //  비트맵을 스트링으로 변환
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }


    // 파일의 내용을 읽어와서, 임시파일 만들기 위함.
    public void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //권한에 대한 응답이 있을때 작동하는 함수
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//    }
    public void checkGalleryPermission() {

        String temp = "";

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }else {
            // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }


}
