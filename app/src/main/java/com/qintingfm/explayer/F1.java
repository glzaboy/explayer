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
        EditText viewById = (EditText)this.getActivity().findViewById(R.id.editText);
        io.reactivex.Observable observable= io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String student) {
                Log.d(student);
            }

            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observer<String> stringObserver=new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(stringObserver);

        NewsDatabase build = Room.databaseBuilder(this.getActivity().getApplicationContext(), NewsDatabase.class,"UserDataBase").allowMainThreadQueries().build();
        NewsDao newsDao = build.getnewsDao();
        News byid = newsDao.findByid(1);
        if(byid==null){
            News news=new News();
            news.id=1;
            news.setTitle("text");
            news.setContent(viewById.getText().toString());
            newsDao.insertNew(news);
        }else {
            byid.setContent(viewById.getText().toString());
            newsDao.updateNews(byid);
        }

        Toast.makeText(getActivity(),"数据保存成功",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        EditText viewById = (EditText)this.getActivity().findViewById(R.id.editText);
        NewsDatabase build = Room.databaseBuilder(this.getActivity().getApplicationContext(), NewsDatabase.class,"UserDataBase").allowMainThreadQueries().build();
        NewsDao newsDao = build.getnewsDao();
        News byid = newsDao.findByid(1);
        if(byid == null){
            viewById.setText("Welcome");
        }else{
            viewById.setText(byid.getContent());
        }

    }
}
