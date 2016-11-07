package com.codepath.apps.MySimpleTweets.fragments;

import android.util.Log;
import android.view.View;

import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by badhri on 11/5/16.
 */

public class MentionsTimelineFragment extends  TweetsFragment {

    private static final String TAG = "Twitter";

    @Override
    protected void populateTimeline(final long since_id, final long max_id) {

        client.getMentionsTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, response.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);

                mTweets.addAll(tweets);
                tweetsAdapter.notifyItemRangeInserted(tweetsAdapter.getItemCount(),
                        tweets.size());
                tweetsAdapter.notifyItemRangeInserted(tweetsAdapter.getItemCount(),
                        tweets.size());
                pbLoadingTweets.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                if (error != null)
                    Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postDelayed(since_id, max_id);
                }
            }
        }, since_id, max_id);
    }



    protected void loadRecent() {
        Tweet first = mTweets.get(0);
        if (first != null) {
            long since_id = first.getUid();
            client.getMentionsTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(TAG, response.toString());
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    if (tweets.size() != 0) {
                        mTweets.addAll(0, tweets);
                        tweetsAdapter.notifyItemRangeInserted(0,
                                tweets.size());
                        Log.d(TAG, "loading more");
                        loadRecent();
                    } else {
                        Log.d(TAG, "done loading");
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                    Log.d(TAG, error.toString());
                    swipeContainer.setRefreshing(false);
                }
            }, since_id, 0 );
        }
    }

}
