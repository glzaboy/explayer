package com.qintingfm.explayer.fegment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qintingfm.explayer.R;

import java.util.List;

public class RecyclerAdpater extends RecyclerView.Adapter<RecyclerAdpater.Vh> {
    private List<String> mDatas;
    public RecyclerAdpater(List<String> data) {
        this.mDatas=data;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item, viewGroup, false);
        return new Vh(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        int itemViewType = getItemViewType(i);
        vh.title.setText(mDatas.get(i));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class Vh extends RecyclerView.ViewHolder{
        TextView title;
        public Vh(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
