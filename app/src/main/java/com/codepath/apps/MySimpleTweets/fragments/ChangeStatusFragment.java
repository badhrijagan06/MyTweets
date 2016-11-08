/**
 * Created by badhri on 10/30/16.
 */

package com.codepath.apps.MySimpleTweets.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.codepath.apps.MySimpleTweets.models.User;
import com.codepath.apps.MySimpleTweets.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class ChangeStatusFragment extends DialogFragment {

    @BindView(R.id.etBody) EditText etBody;
    @BindView(R.id.tvHandle) TextView tvHandle;
    @BindView(R.id.ivProfilePic) ImageView ivPic;
    @BindView(R.id.btTweet) ImageButton btTweet;
    @BindView(R.id.tvRemaining) TextView tvRemaining;
    @BindView(R.id.pbLoadingCompose) ProgressBar progressBar;

    private Unbinder unbinder;
    private String screenName;
    private long inReplyTo;
    private TwitterClient client;
    private User user;


    public ChangeStatusFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChangeStatusFragment newInstance(String screen_name, long in_reply_to) {
        ChangeStatusFragment frag = new ChangeStatusFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        args.putLong("in_reply_to", in_reply_to);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compose, container);
        unbinder = ButterKnife.bind(this, v);
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                //getActivity().getSupportActionBar().setTitle("@" + user.getScreenName());
                populateUserDetails(user);
                progressBar.setVisibility(ProgressBar.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                if (error != null)
                    Log.d("Badhri", error.toString());
                /*if (statusCode == 88 ) {
                    postDelayed(since_id, max_id);
                }*/
            }
        }, screenName);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        screenName = getArguments().getString("screen_name");
        inReplyTo = getArguments().getLong("in_reply_to", -1);
    }

    public void populateUserDetails(User user) {
        tvHandle.setText("@" + user.getScreenName());
        if (inReplyTo != -1) {
            etBody.setText("@" + user.getScreenName());
        }
        etBody.requestFocus();

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Picasso.with(getContext()).load(user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.proffile_default)
                .error(R.drawable.proffile_default).into(ivPic);

        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = etBody.getText().length();
                if (length > 140) {
                    btTweet.setClickable(false);
                    tvRemaining.setText("0 characters remaining");
                } else {
                    btTweet.setClickable(true);
                    int remaining = 140 - length;
                    tvRemaining.setText(remaining + " characters remaining");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postStatus(etBody.getText().toString(), inReplyTo);
            }
        });
    }

    public void postHandleDelayed(final String text, final long id) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                postStatus(text, id);
            }
        };
        handler.postDelayed(runnableCode, 200);
    }

    private void postStatus(final String text, final long id) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postStatus(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet =  Tweet.fromJSON(response);
                PostTweetListener postTweetListener = (PostTweetListener)getActivity();
                postTweetListener.tweetPosted(tweet);
                dismiss();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                if (statusCode == 88 ) {
                    postHandleDelayed(text, id);
                }
            }
        }, text, id);
    }

    public interface PostTweetListener {
        void tweetPosted(Tweet tweet);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}