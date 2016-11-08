package com.codepath.apps.MySimpleTweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.utils.TwitterClient;
import com.codepath.apps.MySimpleTweets.fragments.UserFragment;
import com.codepath.apps.MySimpleTweets.fragments.UserTImelineFragment;
import com.codepath.apps.MySimpleTweets.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @BindView(R.id.toolbar1) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {

            UserFragment userFragment = UserFragment.newInstance(screenName);
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.flUserHeader, userFragment);
            ft2.commit();

            UserTImelineFragment userTImelineFragment = UserTImelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flTimeline, userTImelineFragment);
            ft.commit();
        }
    }

}
