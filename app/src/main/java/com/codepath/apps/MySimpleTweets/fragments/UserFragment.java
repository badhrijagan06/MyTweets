package com.codepath.apps.MySimpleTweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
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

/**
 * Created by badhri on 11/6/16.
 */

public class UserFragment extends Fragment {

    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvFollowers) TextView tvFollowers;
    @BindView(R.id.tvFollowing) TextView tvFollowing;
    @BindView(R.id.tvTagline) TextView tvTagLine;
    @BindView(R.id.pbLoading) ProgressBar pbLoading;
    @BindView(R.id.ivBanner) ImageView ivBanner;

    private Unbinder unbinder;
    TwitterClient client;
    private String screenName;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        screenName = getArguments().getString("screen_name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, parent, false); //not attached yet
        unbinder = ButterKnife.bind(this, v);

        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                //getActivity().getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
                pbLoading.setVisibility(ProgressBar.GONE);
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

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static UserFragment newInstance(String screenName) {
        UserFragment userFragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    private void populateProfileHeader(User user) {
        tvScreenName.setText("@" + user.getScreenName());
        tvUserName.setText(user.getName());
        Picasso.with(getActivity()).load(user.getProfileImageUrl())
                .resize(100, 0)
                .transform(new RoundedCornersTransformation(10, 10))
                .into(ivProfileImage);
        if (!user.getBannerUrl().contentEquals("none"))
            Picasso.with(getActivity()).load(user.getBannerUrl())
                .into(ivBanner);
        tvTagLine.setText(user.getTagLine());
        tvFollowers.setText(user.getFollowersCount() + " Followers");
        tvFollowing.setText(user.getFollowingCount() + " Following");
    }

}
