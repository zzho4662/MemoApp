package com.jhjang.memotest.model;

import java.sql.Blob;

public class ImageMemo {

    int id;
    Blob image;
    int memo_id;

    public ImageMemo() {
    }

    public ImageMemo(int id, Blob image, int memo_id) {
        this.id = id;
        this.image = image;
        this.memo_id = memo_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getMemo_id() {
        return memo_id;
    }

    public void setMemo_id(int memo_id) {
        this.memo_id = memo_id;
    }
}
