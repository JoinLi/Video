package com.ly.video.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ly.video.fragment.IndexFragment;


/**
 * @Auther: cpacm
 * @Date: 2016/7/9.
 * @description: 主页viewpager适配器
 */
public class BeatsFragmentAdapter extends FragmentPagerAdapter {

    private IndexFragment indexFragment; //日报


    private final String[] titles;

    public BeatsFragmentAdapter(FragmentManager fm) {
        super(fm);
        titles = new String[]{IndexFragment.TITLE};
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (indexFragment == null)
                    indexFragment = IndexFragment.newInstance();
                return indexFragment;


        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
