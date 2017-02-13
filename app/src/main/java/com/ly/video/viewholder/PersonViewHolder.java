package com.ly.video.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.ly.video.R;
import com.ly.video.bean.InforBean;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<InforBean> {
    private TextView mTv_name;
    private ImageView mImg_face;
    private TextView mTv_sign;


    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_card);
        mTv_name = $(R.id.text_title);
//        mTv_sign = $(R.id.person_sign);
        mImg_face = $(R.id.img_title);

    }

    @Override
    public void setData(final InforBean person) {
        mTv_name.setText(person.getTitle());
//        mTv_sign.setText(person.getMovie_url());
        Glide.with(getContext())
                .load(person.getImg_url())
                .placeholder(R.mipmap.ic_launcher)
                .into(mImg_face);
    }


}
