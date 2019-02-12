package com.qintingfm.explayer.fegment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.player.PlayerEumu;
import com.qintingfm.explayer.player.PlayerService;

public class Player extends Fragment implements View.OnClickListener{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fegment_player, container, false);
        inflate.findViewById(R.id.play_pause).setOnClickListener(this);
        inflate.findViewById(R.id.next).setOnClickListener(this);
        inflate.findViewById(R.id.prev).setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this.getActivity(), PlayerService.class);
        switch (v.getId()){
            case R.id.play_pause:
                intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_PAUSE));
                break;
            case R.id.prev:
                intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
                break;
            case R.id.next:
                intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
                break;
        }
        this.getActivity().startService(intent);
    }
}
