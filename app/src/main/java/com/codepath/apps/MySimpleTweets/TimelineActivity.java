package com.codepath.apps.MySimpleTweets;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.MySimpleTweets.adapters.TweetsAdapter;
import com.codepath.apps.MySimpleTweets.fragments.ChangeStatusFragment;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.models.Tweet_Table;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.utils.DividerItemDecoration;
import com.codepath.apps.MySimpleTweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TimelineActivity extends AppCompatActivity implements ChangeStatusFragment.PostTweetListener{

    private static final String TAG = "Twitter";

    private TwitterClient client;
    private com.codepath.apps.MySimpleTweets.adapters.TweetsAdapter tweetsAdapter;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    ArrayList<Tweet> mTweets;
    private String handle;
    private String profilePic;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        mTweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, mTweets);
        rvTweets.setAdapter(tweetsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        client = TwitterApplication.getRestClient();
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentid, int totalItemsCount) {
                if (currentid == 0) {
                    populateTimeline();
                } else {
                    Tweet last = mTweets.get(mTweets.size()-1);
                    loadMore(last.getUid());
                    log.d(TAG, "max_id:" + Long.toString(last.getUid()));
                }
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                linearLayoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);
        rvTweets.setItemAnimator(new DefaultItemAnimator());

        getHandle();
        getProfilePic();
        if (!isOnline()) {
            Delete.tables(Tweet.class, User.class);
            populateTimeline();
        } else {
            populateFromDB();
        }
        setUpSwipe();
    }
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void populateFromDB() {
        List<Tweet> tweets = SQLite.select().
                from(Tweet.class).
                orderBy(Tweet_Table.uid, false).
                queryList();
        mTweets.addAll(tweets);
        tweetsAdapter.notifyItemRangeInserted(0,
                tweets.size());
    }

    private void loadRecent() {
        long since_id = -1;
        Tweet first = mTweets.get(0);
        if (first != null) {
            since_id = first.getUid();
            client.getHomeTimeline(new JsonHttpResponseHandler() {
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
            }, since_id );
        }
    }

    private void setUpSwipe() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (handle == null) {
            Toast.makeText(this, "Initialization in progress.. Try Again.. ", Toast.LENGTH_LONG).show();
        } else
            showEditDialog();
        return true;
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ChangeStatusFragment changeStatusFragment = ChangeStatusFragment.newInstance(handle, profilePic);
        changeStatusFragment.show(fm, "fragment_edit_name");
    }


    public void tweetPosted(final Tweet tweet) {

        Handler handler = new Handler();
        Log.d(TAG, "callback called");

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                mTweets.add(0, tweet);
                tweetsAdapter.notifyItemInserted(0);
                rvTweets.smoothScrollToPosition(0);
                Log.d(TAG, "Adapter updated");
            }
        };
        handler.post(runnableCode);
    }

    public void postDelayed(final long max_id) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (max_id == -1)
                    populateTimeline();
                else
                    loadMore(max_id);
            }
        };
        handler.postDelayed(runnableCode, 60000);
    }

    private void loadMore(final long max_id) {
        client.getMoreItems(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, response.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                mTweets.addAll(tweets);
                tweetsAdapter.notifyItemRangeInserted(tweetsAdapter.getItemCount(),
                        tweets.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postDelayed(max_id);
                }
            }
        }, max_id);
    }
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, response.toString());
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                mTweets.addAll(tweets);
                tweetsAdapter.notifyItemRangeInserted(tweetsAdapter.getItemCount(),
                        tweets.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postDelayed(-1);
                }
            }
        }, -1);
    }

    public void postHandleDelayed(final int type) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                if (type == 0)
                    getHandle();
                else if (type == 1)
                    getProfilePic();
            }
        };
        handler.postDelayed(runnableCode, 60000);
    }

    private void getHandle() {
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
                Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postHandleDelayed(0);
                }
            }
        });
    }

    private void getProfilePic() {
        client.getProfilePicUrl(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    profilePic = response.getString("profile_image_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Log.d(TAG, error.toString());
                if (statusCode == 88 ) {
                    postHandleDelayed(1);
                }
            }
        },handle);
    }
}
