package com.qintingfm.explayer.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.mediastore.MediaStoreCon;
import com.qintingfm.explayer.mediastore.MediaStoreMessageNotification;

public class UpdateLoclMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_locl_media);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MediaStoreCon.attach(this,null);
//        MediaStoreMessageNotification.notify(this,"hello",4);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MediaStoreCon.detach(this);
    }
}
