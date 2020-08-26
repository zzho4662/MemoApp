package com.jhjang.memotest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jhjang.memotest.adapter.RecyclerViewAdapter;
import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.Memo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Memo> memoArrayList;

    EditText editSearch;
    ImageView imgAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgAdd = findViewById(R.id.imgAdd);
        recyclerView = findViewById(R.id.recyclerView);
        editSearch = findViewById(R.id.editSearch);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddMemo.class);
                startActivity(i);
            }
        });


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
                memoArrayList = dh.search(keyword);
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHandler dh = new DatabaseHandler(MainActivity.this);
        memoArrayList = dh.getAllMemo();
        // 어댑터를 연결해야지 화면에 표시가 됨.
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, memoArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}