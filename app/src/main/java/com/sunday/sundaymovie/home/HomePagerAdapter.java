package com.sunday.sundaymovie.home;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by agentchen on 2017/3/28.
 * Email agentchen97@gmail.com
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles;
    private Context mContext;

    public HomePagerAdapter(FragmentManager fm, String[] titles, Context context) {
        super(fm);
        mTitles = titles;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ShowTimeFragment showTimeFragment = new ShowTimeFragment();
                showTimeFragment.setPresenter(new ShowTimePresenter(showTimeFragment, mContext));
                return showTimeFragment;
            case 1:
                ComingFragment comingFragment = new ComingFragment();
                comingFragment.setPresenter(new ComingPresenter(comingFragment, mContext));
                return comingFragment;
            default:
                return null;
        }
    }

}