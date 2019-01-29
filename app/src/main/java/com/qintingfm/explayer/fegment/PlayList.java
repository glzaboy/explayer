package com.qintingfm.explayer.fegment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qintingfm.explayer.R;

public class PlayList extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        View inflate = inflater.inflate(R.layout.fegment_playlist, container, false);
        View view=inflater.inflate(R.layout.fegment_playlist,container,false);
        return view;
    }
}
