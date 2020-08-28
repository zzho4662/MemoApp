package com.jhjang.memotest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jhjang.memotest.R;
import com.jhjang.memotest.model.ImageMemo;

import java.sql.Blob;
import java.util.ArrayList;

public class imageRecyclerViewAdapter extends RecyclerView.Adapter<imageRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<ImageMemo> imageMemoArrayList;

    public imageRecyclerViewAdapter(Context context, ArrayList<ImageMemo> imageMemoArrayList) {
        this.context = context;
        this.imageMemoArrayList = imageMemoArrayList;
    }

    @NonNull
    @Override
    public imageRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull imageRecyclerViewAdapter.ViewHolder holder, int position) {
        ImageMemo imageMemo = imageMemoArrayList.get(position);
        int id = imageMemo.getId();
        Blob image = imageMemo.getImage();
        int memo_id = imageMemo.getMemo_id();

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap imgMemo = BitmapFactory.decodeFile(img,options);




        holder.imgPhoto.setImageBitmap(image);

    }

    @Override
    public int getItemCount() {
        return imageMemoArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);



        }
    }

}
