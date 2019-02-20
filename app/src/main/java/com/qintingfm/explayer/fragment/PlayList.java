package com.qintingfm.explayer.fragment;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.qintingfm.explayer.R;
import com.qintingfm.explayer.dao.LocalMediaDao;
import com.qintingfm.explayer.database.MediaStoreDatabase;
import com.qintingfm.explayer.entity.LocalMedia;
import com.qintingfm.explayer.player.PlayerCore;
import com.qintingfm.explayer.player.PlayerService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class PlayList extends Fragment {
    private PlayList.OnFragmentInteractionListener mListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fegment_playlist,container,false);
        final RecyclerView viewById = view.findViewById(R.id.playlist);
        final Context context = getActivity();
        if(context!=null){
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            viewById.setItemAnimator(new DefaultItemAnimator());
            viewById.addItemDecoration(new DividerItemDecoration(context,0));
            final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(new ArrayList<LocalMedia>());

            recyclerAdapter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mListener.onPlayListInteraction(v);
                }
            });
//        recyclerAdapter.setItemOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView view1 = (TextView) v;
//                switch (view1.getId()){
//                    case R.id.data:
//                        break;
//                }
//            }
//        });
            viewById.setAdapter(recyclerAdapter );
            viewById.setLayoutManager(linearLayoutManager);
            final MediaStoreDatabase media_store_database = Room.databaseBuilder(context.getApplicationContext(), MediaStoreDatabase.class, "Media Store Database").build();
            final LocalMediaDao localMediaDao = media_store_database.getLocalMediaDao();
            Observable<LocalMedia> localMediaObservable = Observable.create(new ObservableOnSubscribe<LocalMedia>() {
                @Override
                public void subscribe(ObservableEmitter<LocalMedia> e) {
                    List<LocalMedia> all = localMediaDao.findAll();
                    for (LocalMedia localMedia:all){
                        e.onNext(localMedia);
                    }
                    e.onComplete();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            localMediaObservable.subscribe(
                    new Observer<LocalMedia>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            recyclerAdapter.clearData();
                        }

                        @Override
                        public void onNext(LocalMedia localMedia) {
                            recyclerAdapter.addData(localMedia);
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            recyclerAdapter.notifyDataSetChanged();
                            media_store_database.close();
                        }
                    }
            );
        }
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onPlayListInteraction(View v);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayList.OnFragmentInteractionListener) {
            mListener = (PlayList.OnFragmentInteractionListener) context;
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
}
