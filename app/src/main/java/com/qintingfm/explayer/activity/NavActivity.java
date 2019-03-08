package com.qintingfm.explayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.media.MediaBrowserServiceCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.fragment.HomeFragment;
import com.qintingfm.explayer.fragment.MediaBrowserConnectionCallback;
import com.qintingfm.explayer.fragment.PlayList;
import com.qintingfm.explayer.fragment.Player;
import com.qintingfm.explayer.player.PlayerCore;
import com.qintingfm.explayer.player.PlayerService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.qintingfm.explayer.tiny_player.TinyPlayerService;

public class NavActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, Player.OnFragmentInteractionListener,PlayList.OnFragmentInteractionListener {
    MediaBrowserCompat mediaBrowserCompat;
    MediaBrowserCompat.ConnectionCallback mediaBrowserConnectionCallback=new MediaBrowserConnectionCallback();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.fragment_layout, homeFragment);
                    break;
                case R.id.navigation_player:
                    Player fragment_player = new Player();
                    fragmentTransaction.replace(R.id.fragment_layout, fragment_player);
                    break;
                case R.id.navigation_playlist:
                    PlayList playList = new PlayList();
                    fragmentTransaction.replace(R.id.fragment_layout, playList);
                    break;
            }
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ex Player");
            getSupportActionBar().setIcon(R.drawable.ic_music_black_24dp);
        }
        setContentView(R.layout.activity_nav);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String play_source = intent.getExtras().getString("play_source");
            if (play_source != null) {
                switch (play_source) {
                    case "LAUNCHER":
                        if (intent.getData() != null) {
                            navigation.setSelectedItemId(R.id.navigation_player);
                        } else {
                            navigation.setSelectedItemId(R.id.navigation_home);
                        }


                        break;
                    case "UpdateLocalMedia":
                        navigation.setSelectedItemId(R.id.navigation_playlist);
                        break;
                    default:
                        navigation.setSelectedItemId(R.id.navigation_home);
                }
            }

        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onHomeInteraction(View v) {
        Intent updateLocalMediaIntent = new Intent(this, UpdateLocalMediaActivity.class);
        switch (v.getId()) {
            case R.id.update_local_media:
                this.startActivity(updateLocalMediaIntent);
                break;
            case R.id.button:
                mediaBrowserCompat=new MediaBrowserCompat(this,new ComponentName(this, TinyPlayerService.class),mediaBrowserConnectionCallback,null);
                mediaBrowserCompat.connect();
                break;
        }
    }

    @Override
    public void onPlayerInteraction(View v) {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        switch (v.getId()) {
            case R.id.play_pause:
                playerServiceIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_PAUSE));
                startService(playerServiceIntent);
                break;
            case R.id.prev:
                playerServiceIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
                startService(playerServiceIntent);
                break;
            case R.id.next:
                playerServiceIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
                startService(playerServiceIntent);
                break;
        }
    }

    @Override
    public void onPlayListInteraction(View v) {
        PlayerCore.startService(this);
        Intent intent=new Intent(this, PlayerService.class);
        intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_FROM_URI));
        intent.setData(Uri.parse(((TextView)v.findViewById(R.id.data)).getText().toString()));
        intent.putExtra("title",((TextView)v.findViewById(R.id.title)).getText().toString());
        intent.putExtra("artist",((TextView)v.findViewById(R.id.artist)).getText().toString());
        intent.putExtra("position",Integer.valueOf(((TextView)v.findViewById(R.id.id)).getText().toString()));

        startService(intent);
        Toast.makeText(this,((TextView)v.findViewById(R.id.data)).getText(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlayerProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onPlayerStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPlayerStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekBar:
                Intent intent = new Intent(this, PlayerService.class);
                intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SEEK_TO));
                intent.putExtra("seek", seekBar.getProgress());
                startService(intent);
                break;
            default:
        }

    }
}
