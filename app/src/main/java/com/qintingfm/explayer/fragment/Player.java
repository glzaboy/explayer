package com.qintingfm.explayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.player.PlayerCore;
import com.qintingfm.explayer.player.PlayerEvent;

import java.lang.ref.WeakReference;

public class Player extends Fragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    private static final String TAG= Player.class.getName();
    private Player.OnFragmentInteractionListener mListener;
    SeekBar seekBar;
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
        seekBar = inflate.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        return inflate;
    }

    @Override
    public void onClick(View v) {
        if(mListener!=null){
            mListener.onFragmentInteraction(v);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerCore.attach(getActivity(),new PlayerHandler(this));
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerCore.detach(getActivity());
    }
    private static class PlayerHandler extends Handler{
        WeakReference<Player> playerWeakReference;
        private PlayerHandler(Player player) {
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
                case PlayerEvent.PLAYER_UI_DETACH:
                    break;
            }
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(View v);
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        void onStartTrackingTouch(SeekBar seekBar);
        void onStopTrackingTouch(SeekBar seekBar);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Player.OnFragmentInteractionListener) {
            mListener = (Player.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mListener.onProgressChanged(seekBar,progress,fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mListener.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mListener.onStopTrackingTouch(seekBar);
    }
}
