package com.qintingfm.explayer;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.qintingfm.explayer.dao.NewsDao;
import com.qintingfm.explayer.database.NewsDatabase;
import com.qintingfm.explayer.entity.News;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class F1 extends Fragment {
    static final String TAG=F1.class.getName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.f1, container, false);
        return inflate;
    }
    @Override
    public void onStop() {
        super.onStop();
        final EditText viewById = (EditText)this.getActivity().findViewById(R.id.editText);
        io.reactivex.Observable observable= io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
                subscriber.onNext(viewById.getText().toString());
                subscriber.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io());
        final NewsDatabase build = Room.databaseBuilder(this.getActivity().getApplicationContext(), NewsDatabase.class,"UserDataBase").build();
        observable.subscribe(new Observer<String>() {

            NewsDao newsDao = build.getnewsDao();
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe"+d.toString());
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG,s.toString());
                News byid = newsDao.findByid(1);
                if(byid==null){
                    News news=new News();
                    news.id=1;
                    news.setTitle("text");
                    news.setContent(s);
                    newsDao.insertNew(news);
                }else {
                    byid.setContent(s);
                    newsDao.updateNews(byid);
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"onError");

                build.close();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
                build.close();
            }
        });
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String o) {

            }

            @Override
            public void onError(Throwable e) {
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "onError", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onComplete() {
                if(getActivity()!=null){
                    Toast.makeText(getActivity(),"数据保存成功1",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        final EditText viewById = (EditText)this.getActivity().findViewById(R.id.editText);
        final NewsDatabase build = Room.databaseBuilder(this.getActivity().getApplicationContext(), NewsDatabase.class,"UserDataBase").build();
        io.reactivex.Observable observable= io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
                NewsDao newsDao = build.getnewsDao();
                News byid = newsDao.findByid(1);
                if(byid == null){
                    subscriber.onNext("Welcome");
                }else{
                    subscriber.onNext(byid.getContent());
                }
                build.close();
                subscriber.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<String>() {


            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe"+d.toString());

            }

            @Override
            public void onNext(final String s) {
                Log.d(TAG,s.toString());
                if(viewById!=null){
                    viewById.setText(s);
                }

//                F1.this.getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//                viewById.setText(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"onError");
            }

            @Override
            public void onComplete() {
                //               Toast.makeText(getActivity(),"数据保存成功",Toast.LENGTH_LONG).show();
                Log.d(TAG,"onComplete");
            }
        });


    }
}
