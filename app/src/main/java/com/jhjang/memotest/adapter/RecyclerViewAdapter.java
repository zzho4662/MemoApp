package com.jhjang.memotest.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jhjang.memotest.R;
import com.jhjang.memotest.UpdateMemo;
import com.jhjang.memotest.data.DatabaseHandler;
import com.jhjang.memotest.model.Memo;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Memo> memoList;

    public RecyclerViewAdapter(Context context, ArrayList<Memo> memoList){
        this.context = context;
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 첫번째 파라미터인, parent로 부터 뷰(화면:하나의 셀)를 생성한다.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_row, parent, false);
        // 리턴에, 위에서 생성한 뷰를, 뷰홀더에 담아서 리턴한다.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        String title = memo.getTitle();
        String content = memo.getContent();
        // 뷰홀더에 표시하라.
        holder.txtTitle.setText(title);
        holder.txtContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTitle;
        public TextView txtContent;
        public ImageView imgDelete;
        public CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();

                    Memo memo = memoList.get(index);
                    int id = memo.getId();
                    String title = memo.getTitle();
                    String content = memo.getContent();

                    Intent i = new Intent(context, UpdateMemo.class);
                    i.putExtra("id", id);
                    i.putExtra("title", title);
                    i.putExtra("content", content);
                    context.startActivity(i);
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder deleteAlert = new AlertDialog.Builder(context);
                    deleteAlert.setTitle("메모 삭제");
                    deleteAlert.setMessage("정말 삭제하시겠습니까??");
                    deleteAlert.setPositiveButton("Yes.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 데이터베이스에서 삭제.
                            int index = getAdapterPosition();
                            Memo memo = memoList.get(index);
                            DatabaseHandler dh = new DatabaseHandler(context);
                            dh.deleteMemo(memo);
                            // 데이터셋이 바꼈다는것을 알려주는 메소드 실행.
                            // 1번째 방법
                            memoList = dh.getAllMemo();
                            notifyDataSetChanged();

                            // 2번째 방법
                            //((MainActivity)context).refresh();

                        }
                    });
                    deleteAlert.setNegativeButton("No.", null);
                    deleteAlert.setCancelable(false);
                    deleteAlert.show();
                }
            });
        }
    }

}