package com.jhjang.memotest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jhjang.memotest.model.ImageMemo;
import com.jhjang.memotest.model.Memo;
import com.jhjang.memotest.util.Util;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String CREATE_MEMO_TABLE = "create table " +
            Util.TABLE_MEMO + "(" +
            Util.KEY_ID + " integer not null primary key autoincrement," +
            Util.KEY_TITLE + " text, " +
            Util.KEY_CONTENT + " text )";

    public static final String CREATE_IMAGE_MEMO = "create table " +
            Util.TABLE_IMAGE + "(" +
            Util.KEY_IMAGE_ID + " integer not null primary key autoincrement," +
            Util.KEY_IMAGE + " text, " +
            Util.KEY_MEMO_ID + " integer )";

    public DatabaseHandler(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEMO_TABLE);
        db.execSQL(CREATE_IMAGE_MEMO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_MEMO = "drop table " + Util.TABLE_MEMO;

        String DROP_TABLE_IMAGE_MEMO = "drop table " + Util.TABLE_IMAGE;

        db.execSQL(DROP_TABLE_MEMO);
        db.execSQL(DROP_TABLE_IMAGE_MEMO);

        // 테이블 새로 다시 생성.
        onCreate(db);
    }

    // 여기서부터는 기획에 맞게 데이터베이스에 넣고, 업데이트, 가져오고, 지우고
    // 메소드 만들기.

    public void addMemo(Memo memo){
        // 1. 저장하기 위해서, writable db 를 가져온다.
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. db에 저장하기 위해서는, ContentValues를 이용한다.
        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, memo.getTitle());
        values.put(Util.KEY_CONTENT, memo.getContent());
        // 3. db에 실제로 저장한다.
        db.insert(Util.TABLE_MEMO, null, values);
        db.close();
        Log.i("myDB", "inserted.");
    }

    // 이미지 저장
    public void addImage(String image, int memo_id){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMAGE, image);
        values.put(Util.KEY_MEMO_ID, memo_id);

        db.insert(Util.TABLE_IMAGE, null, values);
        db.close();
        Log.i("myDB", "inserted.");
    }

    // 메모 저장할때 ID 값 가져오는 함수
    public Memo getMemoId(){
        // 1. 데이터베이스 가져온다. 조회니까, readable 한 db로 가져온다.
        SQLiteDatabase db = this.getReadableDatabase();
        String selectId = "select "+Util.KEY_ID+" from " + Util.TABLE_MEMO + " order by " + Util.KEY_ID + " desc limit 1" ;

        // select id, title, content from memo where id = 2;
        // 2. 데이터를 셀렉트(조회) 할때는, Cursor 를 이용해야 한다.
        Cursor cursor = db.rawQuery(selectId,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        int selectedId = Integer.parseInt(cursor.getString(0));
        // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
        Memo memo = new Memo();
        memo.setId(selectedId);

        return memo;
    }

    // 메모1개 불러오는 메소드
    public Memo getMemo(int id){
        // 1. 데이터베이스 가져온다. 조회니까, readable 한 db로 가져온다.
        SQLiteDatabase db = this.getReadableDatabase();

        // select id, title, content from memo where id = 2;
        // 2. 데이터를 셀렉트(조회) 할때는, Cursor 를 이용해야 한다.
        Cursor cursor = db.query(Util.TABLE_MEMO,
                new String[] {"id", "title", "content"},
                Util.KEY_ID + " = ? ",
                new String[]{String.valueOf(id)},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }
        int selectedId = Integer.parseInt(cursor.getString(0));
        String selectedTitle = cursor.getString(1);
        String selectedContent = cursor.getString(2);

        // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
        Memo memo = new Memo();
        memo.setId(selectedId);
        memo.setTitle(selectedTitle);
        memo.setContent(selectedContent);

        return memo;
    }

    // 전체 저장된 데이터 모두 가져오기.
    public ArrayList<Memo> getAllMemo(){
        // 1. 비어 있는 어레이 리스트를 먼저 한개 만든다.
        ArrayList<Memo> memoList = new ArrayList<>();

        // 2. 데이터베이스에 select (조회) 해서,
        String selectAll = "select * from " + Util.TABLE_MEMO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);

        // 3. 여러개의 데이터를 루프 돌면서, Main 클래스에 정보를 하나씩 담고
        if(cursor.moveToFirst()){
            do {
                int selectedId = Integer.parseInt(cursor.getString(0));
                String selectedTitle = cursor.getString(1);
                String selectedContent = cursor.getString(2);
                Log.i("myDB", "do while : " + selectedTitle);
                // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
                Memo memo = new Memo();
                memo.setId(selectedId);
                memo.setTitle(selectedTitle);
                memo.setContent(selectedContent);

                // 4. 위의 빈 어레이리스트에 하나씩 추가를 시킨다.
                memoList.add(memo);

            }while(cursor.moveToNext());
        }
        return memoList;
    }

    // 저장된 이미지 가져오기.
    public ArrayList<ImageMemo> getAllImage(int id){
        // 1. 비어 있는 어레이 리스트를 먼저 한개 만든다.
        ArrayList<ImageMemo> imageMemoArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. 데이터베이스에 select (조회) 해서,
        String selectImage = "select * from " + Util.TABLE_IMAGE + " where "+Util.KEY_MEMO_ID + " = ? ";

        Cursor cursor = db.rawQuery(selectImage, new String[]{String.valueOf(id)});

        // 3. 여러개의 데이터를 루프 돌면서, Contact 클래스에 정보를 하나씩 담고
        if(cursor.moveToFirst()){
            do {
                Log.i("AAA","cousor" + cursor.getString(0));
                int selectedId = Integer.parseInt(cursor.getString(0));
                String selectedImage = cursor.getString(1);
                int selectedMemoId = cursor.getInt(2);
                Log.i("myDB", "do while : " + selectedImage);
                // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
                ImageMemo imageMemo = new ImageMemo();
                imageMemo.setId(selectedId);
                imageMemo.setImage(selectedImage);
                imageMemo.setMemo_id(selectedMemoId);

                // 4. 위의 빈 어레이리스트에 하나씩 추가를 시킨다.
                imageMemoArrayList.add(imageMemo);
            }while(cursor.moveToNext());
        }
        Log.i("AAA","sss" +imageMemoArrayList.size());
        return imageMemoArrayList;
    }

    // 썸네일 불러오기
    public ImageMemo getThumbnail(int memo_id){
        // 1. 데이터베이스 가져온다. 조회니까, readable 한 db로 가져온다.
        SQLiteDatabase db = this.getReadableDatabase();
        String selectThumb = "select * from " + Util.TABLE_IMAGE + " where "+ Util.KEY_MEMO_ID + " = ? order by " + Util.KEY_IMAGE_ID + " limit 1" ;

        // select image from imageMemo where memo_id = ? order by id limit 1 ;
        // 2. 데이터를 셀렉트(조회) 할때는, Cursor 를 이용해야 한다.
        Cursor cursor = db.rawQuery(selectThumb,new String[]{String.valueOf(memo_id)});
        if(cursor != null){
            cursor.moveToFirst();
        }
        int selectedId = Integer.parseInt(cursor.getString(0));
        String selectedImage = cursor.getString(1);
        int selectedMemoId = cursor.getInt(2);
        // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
        ImageMemo imageMemo = new ImageMemo();
        imageMemo.setId(selectedId);
        imageMemo.setImage(selectedImage);
        imageMemo.setMemo_id(selectedMemoId);

        return imageMemo;
    }

    // 데이터를 업데이트 하는 메서드.
    public int updateMemo(Memo memo){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, memo.getTitle());
        values.put(Util.KEY_CONTENT, memo.getContent());

        // 데이터베이스 테이블 업데이트.
        // update memo set title="홍길동", content="asdfasdf" where id = 3;
        int ret = db.update(Util.TABLE_MEMO,    // 테이블명
                values,     // 업데이트할 값
                Util.KEY_ID + " = ? ",   // where
                new String[]{String.valueOf(memo.getId())}); // ? 에 들어갈 값
        db.close();
        return ret;
    }

    // 메모 삭제 메서드
    public void deleteMemo(Memo memo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_MEMO,  // 테이블 명
                Util.KEY_ID + " = ?",   // where id = ?
                new String[]{String.valueOf(memo.getId())});  // ? 에 해당하는 값.
        db.close();
    }

    // 이미지 삭제 메서드
    public void deleteImage(int image_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_IMAGE,  // 테이블 명
                Util.KEY_IMAGE_ID + " = ?",   // where id = ?
                new String[]{String.valueOf(image_id)});  // ? 에 해당하는 값.
        db.close();
    }

    // 테이블에 저장된 데이터의 전체 갯수를 리턴하는 메소드.
    public int getCount(){
        // select count(*) from memo;
        String countQuery = "select * from " + Util.TABLE_MEMO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    // 테이블에 저장된 데이터의 전체 갯수를 리턴하는 메소드.
    public int getImageCnt(int id){
        // select count(*) from memo;
        String countQuery = "select "+Util.KEY_IMAGE +" from " + Util.TABLE_IMAGE + " where "+ Util.KEY_MEMO_ID + "= ? " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(id)});
        int count = cursor.getCount();
        db.close();
        return count;
    }

    // 검색용 메소드 추가
    public ArrayList<Memo> search(String keyword){
        String searchQuery = "select id, title, content from "+Util.TABLE_MEMO+
                " where content like ? or title like ? ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(searchQuery, new String[]{"%"+keyword+"%", "%"+keyword+"%"} );
        ArrayList<Memo> memoList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                int selectedId = Integer.parseInt(cursor.getString(0));
                String selectedTitle = cursor.getString(1);
                String selectedContent = cursor.getString(2);
                Log.i("myDB", "do while : " + selectedTitle);
                // db에서 읽어온 데이터를, 자바 클래스로 처리한다.
                Memo memo = new Memo();
                memo.setId(selectedId);
                memo.setTitle(selectedTitle);
                memo.setContent(selectedContent);

                // 4. 위의 빈 어레이리스트에 하나씩 추가를 시킨다.
                memoList.add(memo);

            }while(cursor.moveToNext());
        }
        return memoList;
    }
    //업데이트 동작 (삭제 후 다시 저장)
    // 먼저 삭제한다.
    public void updateDeleteImage (int memo_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TABLE_IMAGE,  // 테이블 명
                Util.KEY_MEMO_ID + " = ?",   // where id = ?
                new String[]{String.valueOf(memo_id)});  // ? 에 해당하는 값.
        db.close();
    }
    // 이미지를 다시 저장해준다.
    public void updateInsertImage(String image, int memo_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_IMAGE, image);
        values.put(Util.KEY_MEMO_ID, memo_id);

        db.insert(Util.TABLE_IMAGE, null, values);
        db.close();
    }

}