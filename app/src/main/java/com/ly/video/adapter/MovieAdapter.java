package com.ly.video.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.ly.video.bean.MovieBean;
import com.ly.video.viewholder.MovieviewHolder;

/**
 * Created by Mr.Jude on 2015/7/18.
 */
public class MovieAdapter extends RecyclerArrayAdapter<MovieBean> {
    public MovieAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieviewHolder(parent);
    }
}
