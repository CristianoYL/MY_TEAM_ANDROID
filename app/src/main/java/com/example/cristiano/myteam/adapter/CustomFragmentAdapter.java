package com.example.cristiano.myteam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.cristiano.myteam.fragment.PieChartFragment;

/**
 * Created by Cristiano on 2017/4/7.
 */

public class CustomFragmentAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;
    public CustomFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(Fragment[] fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if ( position < fragments.length ) {
            return fragments[position];
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
