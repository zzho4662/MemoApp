package com.jhjang.memotest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.Memo;

public class AddMemo extends AppCompatActivity {

    EditText editTitle;
    EditText editContent;
    ImageView imgSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        imgSave = findViewById(R.id.imgUpdate);

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
    }
}

