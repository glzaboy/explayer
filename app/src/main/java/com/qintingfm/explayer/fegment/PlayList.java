package com.qintingfm.explayer.fegment;

import android.arch.persistence.room.Room;
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
import com.qintingfm.explayer.player.PlayerEumu;
import com.qintingfm.explayer.player.PlayerService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayList extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.fegment_playlist,container,false);
        final RecyclerView viewById = view.findViewById(R.id.playlist);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewById.setItemAnimator(new DefaultItemAnimator());
        viewById.addItemDecoration(new DividerItemDecoration(this.getContext(),0));
        final RecyclerAdapter recyclerAdpater = new RecyclerAdapter(new ArrayList<LocalMedia>());
        recyclerAdpater.setmOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                TextView view1 = (TextView) view;
                switch (view1.getId()){
                    case R.id.title:
                        Toast.makeText(PlayList.this.getActivity(),view1.getText(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.artist:
                        Toast.makeText(PlayList.this.getActivity(),view1.getText(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.displayName:
                        Toast.makeText(PlayList.this.getActivity(),view1.getText(),Toast.LENGTH_LONG).show();
                        break;
                    case R.id.data:
                        PlayerCore.startService(PlayList.this.getActivity().getApplicationContext());
                        Intent intent=new Intent(PlayList.this.getActivity(), PlayerService.class);
                        intent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY_FROM_URI));
                        intent.setData(Uri.parse(view1.getText().toString()));
                        PlayList.this.getActivity().startService(intent);
                        Toast.makeText(PlayList.this.getActivity(),view1.getText(),Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        viewById.setAdapter(recyclerAdpater );
        viewById.setLayoutManager(linearLayoutManager);
        final MediaStoreDatabase media_store_database = Room.databaseBuilder(this.getContext().getApplicationContext(), MediaStoreDatabase.class, "Media Store Database").build();
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
                        recyclerAdpater.clearData();
                    }

                    @Override
                    public void onNext(LocalMedia localMedia) {
                        recyclerAdpater.addData(localMedia);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        recyclerAdpater.notifyDataSetChanged();
                        media_store_database.close();
                    }
                }
        );
        return view;
    }
}
