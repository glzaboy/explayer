package com.qintingfm.explayer.fegment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qintingfm.explayer.R;

import java.util.ArrayList;
import java.util.List;

public class PlayList extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        View inflate = inflater.inflate(R.layout.fegment_playlist, container, false);
        View view=inflater.inflate(R.layout.fegment_playlist,container,false);
        RecyclerView viewById = (RecyclerView) view.findViewById(R.id.playlist);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewById.setItemAnimator(new DefaultItemAnimator());
        viewById.addItemDecoration(new DividerItemDecoration(this.getActivity(),0));
        List<String> stringList=new ArrayList<>();
        for (int i=0;i<2000;i++
             ) {
            stringList.add("china"+i);

        }


        RecyclerAdpater recyclerAdpater = new RecyclerAdpater(stringList);
        viewById.setAdapter(recyclerAdpater );
        viewById.setLayoutManager(linearLayoutManager);
        return view;
    }
}
