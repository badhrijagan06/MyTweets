package com.codepath.apps.MySimpleTweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.MySimpleTweets.fragments.HomeTimelineFragment;
import com.codepath.apps.MySimpleTweets.fragments.MentionsTimelineFragment;

/**
 * Created by badhri on 11/5/16.
 */


public class TweetsPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = {"Home", "Mentions"};

    public HomeTimelineFragment getHomeTimelineFragment() {
        return homeTimelineFragment;
    }

    HomeTimelineFragment homeTimelineFragment;

    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            homeTimelineFragment = new HomeTimelineFragment();
            return homeTimelineFragment;
        } else if (position == 1) {
            return new MentionsTimelineFragment();
        } else
            return null;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }


}
