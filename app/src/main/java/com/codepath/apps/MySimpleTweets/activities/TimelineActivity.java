package com.codepath.apps.MySimpleTweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
import com.codepath.apps.MySimpleTweets.adapters.TweetsPagerAdapter;
import com.codepath.apps.MySimpleTweets.fragments.ChangeStatusFragment;
import com.codepath.apps.MySimpleTweets.fragments.HomeTimelineFragment;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ChangeStatusFragment.PostTweetListener{

    private static final String TAG = "Twitter";

    private TwitterClient client;

    private String handle;
    private String profilePic;
    private TweetsPagerAdapter tweetsPagerAdapter;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabsStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
        client = TwitterApplication.getRestClient();
        getHandle();


        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tweetsPagerAdapter);
        tabsStrip.setViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.miCompose:
                if (handle == null) {
                    Toast.makeText(this, "Initialization in progress.. Try Again.. ", Toast.LENGTH_LONG).show();
                } else
                    showComposeDialog();
                return true;
            case R.id.miProfile:
                if (!isNetworkAvailable()) {
                    Toast.makeText(this, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
                    return true;
                }
                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("screen_name", handle);
                startActivity(i);
                return true;
        }
        return true;
    }

    private void showComposeDialog() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        ChangeStatusFragment changeStatusFragment = ChangeStatusFragment.newInstance(handle, -1);
        changeStatusFragment.show(fm, "fragment_edit_name");
    }


    public void tweetPosted(final Tweet tweet) {
        HomeTimelineFragment homeTimelineFragment = tweetsPagerAdapter.getHomeTimelineFragment();
        if (homeTimelineFragment != null)
            homeTimelineFragment.addTweetToTop(tweet);
    }


    public void postHandleDelayed(final int type) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (type == 0)
                    getHandle();
            }
        };
        handler.postDelayed(runnableCode, 60000);
    }

    private void getHandle() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
            return;
        }
        client.getAccountSettings(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    handle = response.getString("screen_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                //Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postHandleDelayed(0);
                }
            }
        });
    }

    private Boolean isNetworkAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
