package com.qintingfm.explayer.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Vh> {
    private static final String TAG=RecyclerAdapter.class.getName();
    private List<LocalMedia> data;
    private View.OnClickListener mOnItemClickListener;
    private View.OnClickListener mOnClickListener;
    public RecyclerAdapter(List<LocalMedia> data) {
        this.data=data;
    }

    public List<LocalMedia> getData() {
        return data;
    }

    public void clearData() {
        this.data = new ArrayList<>();
    }
    public void addData(LocalMedia localMedia) {
        this.data.add(localMedia);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item, viewGroup, false);
        return new Vh(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        if(mOnClickListener!=null){
            vh.itemView.setOnClickListener(mOnClickListener);
        }
        if(mOnItemClickListener!=null){
            vh.title.setOnClickListener(mOnItemClickListener);
            vh.artist.setOnClickListener(mOnItemClickListener);
            vh.displayName.setOnClickListener(mOnItemClickListener);
            vh.data.setOnClickListener(mOnItemClickListener);
            vh.duration.setOnClickListener(mOnItemClickListener);
        }
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG,"Login click"+getItemCount());
                return false;
            }
        });

        vh.title.setText(data.get(i).getTitle());

        vh.artist.setText(data.get(i).getArtist());
        vh.displayName.setText(data.get(i).getDisplayName());
        vh.data.setText(data.get(i).getData());
        vh.duration.setText(String.valueOf(data.get(i).getDuration()));
        vh.id.setText(String.valueOf(data.get(i).getId()));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Vh extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView artist;
        private TextView displayName;
        private TextView data;
        private TextView duration;
        private TextView id;
        public Vh(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"TEST"+getAdapterPosition());
                }
            });
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.artist);
            displayName = itemView.findViewById(R.id.displayName);
            data = itemView.findViewById(R.id.data);
            duration = itemView.findViewById(R.id.duration);
            id = itemView.findViewById(R.id.id);
        }
    }

    protected void setItemOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnItemClickListener = mOnClickListener;
    }
    protected void setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
}
