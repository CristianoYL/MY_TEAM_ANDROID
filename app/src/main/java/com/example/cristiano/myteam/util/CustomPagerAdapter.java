package com.example.cristiano.myteam.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Cristiano on 2017/4/7.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;
    public CustomPagerAdapter(FragmentManager fm) {
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
