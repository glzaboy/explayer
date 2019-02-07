package com.qintingfm.explayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.mediastore.MediaStoreCon;
import com.qintingfm.explayer.mediastore.MediaStoreConstant;

import java.lang.ref.WeakReference;

public class UpdateLocalMediaActivity extends AppCompatActivity {
    final static String TAG= UpdateLocalMediaActivity.class.getName();
//    final Handler mHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MediaStoreConstant.HANDLE_READY:
//                    Log.d(UpdateLocalMediaActivity.TAG,"Media store SERVICE READY ");
//                    MediaStoreCon.scanfLocalMedia();
//                    break;
//                case MediaStoreConstant.HANDLE_SCAN_FINISHED:
//                    finish();
//                break;
//            }
//            super.handleMessage(msg);
//        }
//    };
    WeakReference<Handler> mHandler = new WeakReference<Handler>(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MediaStoreConstant.HANDLE_READY:
                    Log.d(TAG,"Media store SERVICE READY ");
                    MediaStoreCon.scanfLocalMedia();
                    break;
                case MediaStoreConstant.HANDLE_SCAN_FINISHED:
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    });
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
        MediaStoreCon.attach(this,mHandler.get());
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaStoreCon.detach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
