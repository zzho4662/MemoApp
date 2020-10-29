package com.jhjang.memotest.model;

import java.io.Serializable;
import java.sql.Blob;

public class ImageMemo implements Serializable {

    int id;
    String image;
    int memo_id;

    public ImageMemo() {
    }

    public ImageMemo(int id, String image, int memo_id) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMemo_id() {
        return memo_id;
    }

    public void setMemo_id(int memo_id) {
        this.memo_id = memo_id;
    }
}
