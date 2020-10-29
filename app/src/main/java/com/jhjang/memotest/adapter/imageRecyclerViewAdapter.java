package com.jhjang.memotest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jhjang.memotest.ImageActivity;
import com.jhjang.memotest.R;
import com.jhjang.memotest.UpdateMemo;
import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.ImageMemo;
import com.jhjang.memotest.model.Memo;
import com.jhjang.memotest.util.Util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<ImageMemo> imageMemoArrayList;

    public ArrayList<ImageMemo> getImageMemoArrayList() {
        return imageMemoArrayList;
    }

    public void setImageMemoArrayList(ArrayList<ImageMemo> imageMemoArrayList) {
        this.imageMemoArrayList = imageMemoArrayList;
    }

    public ImageRecyclerViewAdapter(Context context, ArrayList<ImageMemo> imageMemoArrayList) {
        this.context = context;
        this.imageMemoArrayList = imageMemoArrayList;
    }

    @NonNull
    @Override
    public ImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerViewAdapter.ViewHolder holder, int position) {

        ImageMemo imageMemo = imageMemoArrayList.get(position);
        int id = imageMemo.getId();
        String image = imageMemo.getImage();
        Log.i("AAA","IMAGE : " + image);
        int memo_id = imageMemo.getMemo_id();

        if (image.contains("image")){
            Uri image_uri = Uri.parse(image).buildUpon().build();
            holder.imgPhoto.setImageURI(image_uri);
        }else {
            Bitmap image_bitmap = StringToBitmap(image);
            holder.imgPhoto.setImageBitmap(image_bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return imageMemoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgPhoto;
        public CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();

                    ImageMemo imageMemo = imageMemoArrayList.get(index);
                    int id = imageMemo.getMemo_id();
                    String image = imageMemo.getImage();

                    Intent i = new Intent(context, ImageActivity.class);
                    i.putExtra("index", index);
                    i.putExtra("id", id);

                    if (image.contains("image")){
                        i.putExtra("gallery", image);
                    }else {
                        Bitmap bit = StringToBitmap(image);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] img_byte = stream.toByteArray();
                        i.putExtra("camera", img_byte);
                    }
                    context.startActivity(i);
                    ((Activity)context).finish();
                }
            });
        }
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
