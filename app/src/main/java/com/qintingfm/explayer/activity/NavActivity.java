package com.qintingfm.explayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.fragment.HomeFragment;
import com.qintingfm.explayer.fragment.PlayList;
import com.qintingfm.explayer.fragment.Player;
import com.qintingfm.explayer.player.PlayerService;

public class NavActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, Player.OnFragmentInteractionListener {


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
    public void onFragmentInteraction(View v) {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        Intent updateLocalMediaIntent = new Intent(this, UpdateLocalMediaActivity.class);
        switch (v.getId()) {
            case R.id.update_local_media:
                this.startActivity(updateLocalMediaIntent);
                break;
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
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
