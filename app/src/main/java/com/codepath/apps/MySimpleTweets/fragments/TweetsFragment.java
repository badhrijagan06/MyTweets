package com.codepath.apps.MySimpleTweets.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
import com.codepath.apps.MySimpleTweets.adapters.TweetsAdapter;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.utils.DividerItemDecoration;
import com.codepath.apps.MySimpleTweets.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.MySimpleTweets.utils.TwitterClient;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by badhri on 11/5/16.
 */

public abstract class TweetsFragment extends Fragment {
    protected com.codepath.apps.MySimpleTweets.adapters.TweetsAdapter tweetsAdapter;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.pbLoadingTweets) ProgressBar pbLoadingTweets;

    ArrayList<Tweet> mTweets;
    private Unbinder unbinder;
    LinearLayoutManager linearLayoutManager;
    protected TwitterClient client;
    ProgressBar progressBarFooter;

    private static final String TAG = "Twitter";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false); //not attached yet
        unbinder = ButterKnife.bind(this, v);
        tweetsAdapter = new TweetsAdapter(getActivity(), mTweets);
        rvTweets.setAdapter(tweetsAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                linearLayoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);
        rvTweets.setItemAnimator(new DefaultItemAnimator());

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentid, int totalItemsCount) {
                if (currentid == 0) {
                    populateTimeline(-1, 0);
                } else {
                    Tweet last = mTweets.get(mTweets.size()-1);
                    loadMore(last.getUid());
                    log.d(TAG, "max_id:" + Long.toString(last.getUid()));
                }
            }
        });

        populateTimeline(-1, 0);
        setUpSwipe();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweets = new ArrayList<>();
        client = TwitterApplication.getRestClient();

    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract void loadRecent();

    private void setUpSwipe() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadRecent();

            }
        });
    }

    protected void loadMore(final long max_id) {
        populateTimeline(0, max_id);
    }

    protected abstract void populateTimeline(final long since_id, final long max_id);

    protected void postDelayed(final long since_id, final long max_id) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                populateTimeline(since_id, max_id);
            }
        };
        handler.postDelayed(runnableCode, 60000);
    }

    protected Boolean isNetworkAvailable() {
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
