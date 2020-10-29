package com.jhjang.memotest.util;

public class Util {

    public static final String DATABASE_NAME = "memo_db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEMO = "memo";
    public static final String TABLE_IMAGE = "image";

    // 컬럼 이름 : 컬럼명은 String
    // memo 테입블 컬럼
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    //image 테이블의 컬럼
    public static final String KEY_IMAGE_ID = "id";
    public static final String KEY_IMAGE = "image_url";
    public static final String KEY_MEMO_ID = "memo_id";

}