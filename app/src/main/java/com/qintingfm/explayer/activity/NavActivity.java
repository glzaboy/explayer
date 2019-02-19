package com.qintingfm.explayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.fegment.HomeFragment;
import com.qintingfm.explayer.fegment.PlayList;
import com.qintingfm.explayer.fegment.Player;

public class NavActivity extends AppCompatActivity {


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
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }else{
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ex Player");
            getSupportActionBar().setIcon(R.drawable.ic_music_black_24dp);
        }


//        }
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
}
