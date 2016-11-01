/**
 * Created by badhri on 10/30/16.
 */

package com.codepath.apps.MySimpleTweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
import com.codepath.apps.MySimpleTweets.TwitterClient;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ChangeStatusFragment extends DialogFragment {

    private EditText etBody;
    private TextView tvHandle;
    private ImageView ivPic;
    private Button btTweet;
    private TextView tvRemaining;
    private ImageButton ibCancel;


    public ChangeStatusFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChangeStatusFragment newInstance(String handle, String picUrl) {
        ChangeStatusFragment frag = new ChangeStatusFragment();
        Bundle args = new Bundle();
        args.putString("handle", handle);
        args.putString("image", picUrl);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etBody = (EditText) view.findViewById(R.id.etBody);
        tvHandle = (TextView)view.findViewById(R.id.tvHandle);
        ivPic = (ImageView) view.findViewById(R.id.ivProfilePic);
        btTweet = (Button) view.findViewById(R.id.btTweet);
        tvRemaining = (TextView) view.findViewById(R.id.tvRemaining);

        // Fetch arguments from bundle and set title
        String handle = getArguments().getString("handle", "you");
        String url = getArguments().getString("image", "none");
        // Show soft keyboard automatically and request focus to field
        tvHandle.setText("@" + handle);
        etBody.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Picasso.with(getContext()).load(url).placeholder(R.drawable.proffile_default).error(R.drawable.proffile_default).into(ivPic);

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
                    btTweet.setTextColor(Color.GRAY);
                } else {
                    btTweet.setClickable(true);
                    btTweet.setTextColor(Color.BLUE);
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
                postStatus(etBody.getText().toString(), -1);
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

}