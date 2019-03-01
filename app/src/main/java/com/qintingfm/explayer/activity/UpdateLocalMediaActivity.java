package com.qintingfm.explayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.mediastore.MediaStoreCon;
import com.qintingfm.explayer.mediastore.MediaStoreConstant;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UpdateLocalMediaActivity extends AppCompatActivity {
    final static String TAG = UpdateLocalMediaActivity.class.getName();

    private static class MediaScanHandler extends Handler {
        WeakReference<UpdateLocalMediaActivity> mUpdateLocalMediaActivityWeakReference;

        private MediaScanHandler(UpdateLocalMediaActivity updateLocalMediaActivity) {
            this.mUpdateLocalMediaActivityWeakReference = new WeakReference<>(updateLocalMediaActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MediaStoreConstant.HANDLE_READY:
                    Log.d(TAG, "Media store SERVICE READY ");
                    MediaStoreCon.scanfLocalMedia();
                    break;
                case MediaStoreConstant.HANDLE_SCAN_FINISHED:
                    UpdateLocalMediaActivity updateLocalMediaActivity = mUpdateLocalMediaActivityWeakReference.get();
                    if (updateLocalMediaActivity != null) {
                        Intent intent = new Intent(updateLocalMediaActivity, NavActivity.class);
                        intent.setData(updateLocalMediaActivity.getIntent().getData());
                        intent.putExtra("play_source", "UpdateLocalMedia");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        updateLocalMediaActivity.startActivity(intent);
                        updateLocalMediaActivity.finish();
                    }
                    break;
            }
        }
    }

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
        MediaStoreCon.attach(this, new MediaScanHandler(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaStoreCon.detach(this);
    }
}
