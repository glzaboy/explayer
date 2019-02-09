package com.qintingfm.explayer.fegment;

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
    private OnItemClickListener mOnItemClickListener;
    public RecyclerAdapter(List<LocalMedia> data) {
        this.data=data;
    }

    public List<LocalMedia> getData() {
        return data;
    }

    public void setData(List<LocalMedia> data) {
        this.data = data;
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
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    View viewById = v.findViewById(R.id.data);
                    mOnItemClickListener.onItemClick(viewById);
                }

            }
        });
//        int itemViewType = getItemViewType(i);
        vh.title.setText(data.get(i).getTitle());
        vh.title.setTag(i);
//        vh.title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mOnItemClickListener!=null){
////                    int tag = (int)v.getTag();
//                    mOnItemClickListener.onItemClick(v);
//                }
//            }
//        });
        vh.artist.setText(data.get(i).getArtist());
        vh.artist.setTag(i);
        vh.displayName.setText(data.get(i).getDisplayName());
        vh.displayName.setTag(i);
//        vh.displayName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mOnItemClickListener!=null){
////                    int tag = (int)v.getTag();
//                    mOnItemClickListener.onItemClick(v);
//                }
//            }
//        });
        vh.data.setText(data.get(i).getData());
        vh.data.setTag(i);
        vh.duration.setText(String.valueOf(data.get(i).getDuration()));
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
        return data.size();
    }

    public static class Vh extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView artist;
        private TextView displayName;
        private TextView data;
        private TextView duration;
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
        }
    }

    protected void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);

    }
}
