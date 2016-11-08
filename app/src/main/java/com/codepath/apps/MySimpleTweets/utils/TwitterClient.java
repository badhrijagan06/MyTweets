package com.codepath.apps.MySimpleTweets.utils;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "twjECUdtbgHEpGn45C8zzXO0s";       // Change this
	public static final String REST_CONSUMER_SECRET = "PVTY9Z7xigQy3aeZoaKxvclrBv4djCf0CIxo33U60lA5e2KN1J"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	public void getHomeTimeline(AsyncHttpResponseHandler handler, long since_id, long max_id) {

		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        } else {
            if (since_id == -1){
                since_id = 1;
            }
            params.put("count", 25);
            params.put("since_id", since_id);
        }
		getClient().get(apiUrl, params, handler);
	}

    public void getMentionsTimeline(AsyncHttpResponseHandler handler, long since_id, long max_id) {

        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        } else {
            if (since_id == -1){
                since_id = 1;
            }
            params.put("count", 25);
            params.put("since_id", since_id);
        }
        getClient().get(apiUrl, params, handler);
    }


    public void getAccountSettings(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/settings.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    public void getProfilePicUrl(AsyncHttpResponseHandler handler, String handle) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    public void postStatus(AsyncHttpResponseHandler handler, String text, long id) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        if(id != -1)
            params.put("in_reply_to_status_id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void postFavoriteCreate(AsyncHttpResponseHandler handler, long id, boolean create) {
        String apiUrl;
        if (create)
                apiUrl = getApiUrl("favorites/create.json");
        else
            apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void postRetweetCreate(AsyncHttpResponseHandler handler, long id, boolean create) {
        String apiUrl;
        if (create)
            apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", id));
        else
            apiUrl = getApiUrl(String.format("statuses/unretweet/%d.json", id));
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler, long since_id, long max_id) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (max_id != 0) {
            params.put("max_id", max_id - 1);
        } else {
            if (since_id == -1){
                since_id = 1;
            }
            params.put("count", 25);
            params.put("since_id", since_id);
        }
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);

    }

    public void getUserInfo(AsyncHttpResponseHandler handler, String screenName) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = null;
        if (screenName != null) {
            params = new RequestParams();
            params.put("screen_name", screenName);
        }
        getClient().get(apiUrl, params, handler);
    }
}
