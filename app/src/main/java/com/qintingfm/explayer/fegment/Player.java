package com.qintingfm.explayer.fegment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.player.PlayerCore;
import com.qintingfm.explayer.player.PlayerEvent;
import com.qintingfm.explayer.player.PlayerService;

import java.lang.ref.WeakReference;

public class Player extends Fragment implements View.OnClickListener{
    private static final String TAG= Player.class.getName();
    SeekBar seekBar;
    private PlayerHandler mPlayerHandler=new PlayerHandler(this);

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
        seekBar = (SeekBar)inflate.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent=new Intent(Player.this.getActivity(), PlayerService.class);
                intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SEEK_TO));
                intent.putExtra("seek",seekBar.getProgress());
                if(Player.this.getActivity()!=null){
                    Player.this.getActivity().startService(intent);
                }

            }
        });
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

    @Override
    public void onStart() {
        super.onStart();
        PlayerCore.attach(this.getActivity().getApplicationContext(),new PlayerHandler(this));
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerCore.detach(this.getActivity().getApplicationContext());
    }
    private static class PlayerHandler extends Handler{
        WeakReference<Player> playerWeakReference;
        public PlayerHandler(Player player) {
            playerWeakReference=new WeakReference<>(player);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG,"UI test");
            switch (msg.what) {
                case PlayerEvent.PLAYER_UPDATE_SEEK_BAR:

                    Player player = playerWeakReference.get();

                    if(player!=null){
                        player.seekBar.setEnabled(true);
                        player.seekBar.setMax(msg.arg1);
                        player.seekBar.setProgress(msg.arg2);
                    }
                    break;
            }
        }
    }
}
