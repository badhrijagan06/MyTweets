package com.codepath.apps.MySimpleTweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.MySimpleTweets.R;
import com.codepath.apps.MySimpleTweets.TwitterApplication;
import com.codepath.apps.MySimpleTweets.activities.ProfileActivity;
import com.codepath.apps.MySimpleTweets.fragments.ChangeStatusFragment;
import com.codepath.apps.MySimpleTweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * Created by badhri on 10/29/16.
 */

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
    public TweetsAdapter(Context context, List<Tweet> tweets
                           /*AdapterView.OnItemClickListener onItemClickListener*/) {
        mTweets = tweets;
        mContext = context;
    }

    class cache {
        Long id;
        boolean set;
    }

    class cache2 {
        Long id;
        String screen_name;
    }

    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
        @BindView(R.id.tvBody) public TextView tvBody;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvTimeElapsed) TextView tvTimeElapsed;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        TextView tvRetweet;
        TextView tvFavorite;
        ImageView ivRetweet;
        ImageView ivFavorite;
        ImageView ivReply;
        Context context;
        cache mcache1, mcache2;
        cache2 mcache3;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
            tvRetweet = (TextView)itemView.findViewById(R.id.tvRetweet);
            tvFavorite = (TextView)itemView.findViewById(R.id.tvFavorite);
            ivRetweet = (ImageView)itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView)itemView.findViewById(R.id.ivFavorite);
            ivReply = (ImageView)itemView.findViewById(R.id.ivReply);
            mcache1 = new cache();
            mcache2 = new cache();
            mcache3 = new cache2();
            /*tvHeadline.setOnClickListener(this);
            ivThumbnail.setOnClickListener(this); */

        }

        /*public void onClick(View view) {
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }*/
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;


        View itemView = inflater.inflate(R.layout.tweet_item, parent, false);
        viewHolder = new ViewHolder(itemView, context);
        return viewHolder;
    }

    public static CharSequence formatCreatedAt(String createdAt) {
        //Tue Aug 28 21:16:23 +0000 2012
        final String style="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat format = new SimpleDateFormat(style);
        format.setLenient(true);
        Date date = null;
        CharSequence since = null;
        try {
            date = format.parse(createdAt);
            long epoch = date.getTime();
            since = DateUtils.getRelativeTimeSpanString(epoch,  System.currentTimeMillis(), SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return since;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        ViewHolder v = (ViewHolder)holder;
        v.tvBody.setText(tweet.getBody());
        v.tvUserName.setText(tweet.getUser().getName());
        v.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        v.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(mContext).load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(10, 10))
                .resize(0, 80)
                .into(v.ivProfileImage);
        v.ivProfileImage.setTag(tweet.getUser().getScreenName());

        v.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(final View v) {
                                                    final String screenName = (String) v.getTag();
                                                    Intent i = new Intent(mContext, ProfileActivity.class);
                                                    i.putExtra("screen_name", screenName);
                                                    mContext.startActivity(i);
                                                }
                                            });

        CharSequence since = TweetsAdapter.formatCreatedAt(tweet.getCreatedAt());
        if (since != null)
            v.tvTimeElapsed.setText(since);
        if (tweet.isFavorited()) {
            Picasso.with(mContext).load(R.drawable.like_red)
                    .into(v.ivFavorite);
        } else {
            Picasso.with(mContext).load(R.drawable.like_gray)
                    .into(v.ivFavorite);
        }
        if (tweet.isRetweeted()) {
            Picasso.with(mContext).load(R.drawable.retweet_blue)
                    .into(v.ivRetweet);
        } else {
            Picasso.with(mContext).load(R.drawable.retweet_gray)
                    .into(v.ivRetweet);
        }
        Picasso.with(mContext).load(R.drawable.reply_gray)
                .into(v.ivReply);
        v.tvFavorite.setText(String.format("%d", tweet.getFavourites_count()));
        v.tvRetweet.setText(String.format("%d",tweet.getRetweet_count()));
        v.mcache1.id = tweet.getUid();
        v.mcache1.set = tweet.isFavorited();
        v.ivFavorite.setTag(v.mcache1);

        v.mcache2.id = tweet.getUid();
        v.mcache2.set = tweet.isRetweeted();
        v.ivRetweet.setTag(v.mcache2);

        v.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final cache cache = (cache) v.getTag();
                long id = cache.id;
                final Boolean set = cache.set;
                if (!isNetworkAvailable()) {
                    Toast.makeText(mContext, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
                    return;
                }
                TwitterApplication.getRestClient().postFavoriteCreate(new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                       if (set) {
                           Picasso.with(mContext).load(R.drawable.like_gray)
                                   .into((ImageView)v);
                       } else
                           Picasso.with(mContext).load(R.drawable.like_red)
                                   .into((ImageView)v);
                        cache.set = !set;
                        v.setTag(cache);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {

                    }
                }, id, !set);
            }
        });

        v.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final cache cache = (cache) v.getTag();
                long id = cache.id;
                final Boolean set = cache.set;
                if (!isNetworkAvailable()) {
                    Toast.makeText(mContext, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
                    return;
                }
                TwitterApplication.getRestClient().postRetweetCreate(new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (set) {
                            Picasso.with(mContext).load(R.drawable.retweet_gray)
                                    .into((ImageView)v);
                        } else
                            Picasso.with(mContext).load(R.drawable.retweet_blue)
                                    .into((ImageView)v);
                        cache.set = !set;
                        v.setTag(cache);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {

                    }
                }, id, !set);
            }
        });


        v.mcache3.screen_name = tweet.getUser().getScreenName();
        v.mcache3.id = tweet.getUid();
        v.ivReply.setTag(v.mcache3);


        v.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(mContext, "Network not available! Try again later! ",Toast.LENGTH_LONG).show();
                    return;
                }
                final cache2 cache = (cache2) v.getTag();
                long id = cache.id;
                String screenName = cache.screen_name;
                AppCompatActivity activity = (AppCompatActivity)mContext;
                FragmentManager fm = activity.getSupportFragmentManager();
                ChangeStatusFragment changeStatusFragment = ChangeStatusFragment.newInstance(screenName, id);
                changeStatusFragment.show(fm, "fragment_edit_name");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
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
