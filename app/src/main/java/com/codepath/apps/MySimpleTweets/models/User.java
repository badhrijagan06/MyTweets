package com.codepath.apps.MySimpleTweets.models;

import com.codepath.apps.MySimpleTweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by badhri on 10/29/16.
 */

@Table(database = MyDatabase.class)
public class User extends BaseModel{
    @Column
    String name;

    @Column
    @PrimaryKey
    long uid;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    @Column
    int followersCount;

    @Column
    int followingCount;

    @Column
    String tagLine;

    @Column
    String bannerUrl;

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static User fromJSON(JSONObject object) {
        User u = new User();
        try {
            u.name = object.getString("name");
            u.uid = object.getLong("id");
            u.screenName = object.getString("screen_name");
            u.profileImageUrl = object.getString("profile_image_url");
            u.tagLine = object.getString("description");
            u.followersCount = object.getInt("followers_count");
            u.followingCount = object.getInt("friends_count");
            try {
                u.bannerUrl = object.getString("profile_banner_url");
            } catch (JSONException e){
                u.bannerUrl = "none";
            }
            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
