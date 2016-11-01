package com.codepath.apps.MySimpleTweets.models;

import com.codepath.apps.MySimpleTweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by badhri on 10/29/16.
 */

@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {
    @Column
    String body;

    @PrimaryKey
    @Column
    long uid;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    User user;

    @Column
    String createdAt;

    @Column
    boolean favorited;

    @Column
    int favourites_count;

    @Column
    boolean retweeted;

    @Column
    int retweet_count;

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }


    public boolean isFavorited() {
        return favorited;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Tweet () {
        //required by parceler
    }
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.favourites_count = jsonObject.getJSONObject("user").getInt("favourites_count");
            tweet.retweeted =  jsonObject.getBoolean("retweeted");
            tweet.retweet_count = jsonObject.getInt("retweet_count");
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray response) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet current = Tweet.fromJSON(response.getJSONObject(i));
                if (current != null) {
                    tweets.add(current);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return  tweets;
    }
}
