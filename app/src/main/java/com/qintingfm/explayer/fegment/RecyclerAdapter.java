package com.qintingfm.explayer.fegment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Vh> {
    private List<LocalMedia> mDatas;
    OnItemClickListener mOnItemClickListener;
    public RecyclerAdapter(List<LocalMedia> data) {
        this.mDatas=data;
    }

    public List<LocalMedia> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<LocalMedia> mDatas) {
        this.mDatas = mDatas;
    }
    public void clearData() {
        this.mDatas = new ArrayList<>();
    }
    public void addData(LocalMedia localMedia) {
        this.mDatas.add(localMedia);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item, viewGroup, false);
        return new Vh(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
//        int itemViewType = getItemViewType(i);
        vh.title.setText(mDatas.get(i).getTitle());
        vh.title.setTag(i);
        vh.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
//                    int tag = (int)v.getTag();
                    mOnItemClickListener.onItemClick(v);
                }
            }
        });
        vh.artist.setText(mDatas.get(i).getArtist());
        vh.artist.setTag(i);
        vh.displayName.setText(mDatas.get(i).getDisplayName());
        vh.displayName.setTag(i);
        vh.displayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
//                    int tag = (int)v.getTag();
                    mOnItemClickListener.onItemClick(v);
                }
            }
        });
        vh.data.setText(mDatas.get(i).getData());
        vh.data.setTag(i);
        vh.duration.setText(String.valueOf(mDatas.get(i).getDuration()));
        vh.duration.setTag(i);
//        vh.title.setOnClickListener(new View.OnClickListener(View v){
//            @Override
//            public void onClick(TextView v) {
////                Toast.makeText(RecyclerAdapter.this,v.getText(),Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class Vh extends RecyclerView.ViewHolder{
        TextView title;
        TextView artist;
        TextView displayName;
        TextView data;
        TextView duration;
        public Vh(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
            displayName = itemView.findViewById(R.id.displayName);
            data = itemView.findViewById(R.id.data);
            duration = itemView.findViewById(R.id.duration);
        }
    }

    protected void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);

    }
}
