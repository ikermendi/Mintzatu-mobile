package com.irontec.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TwitterHelper {
	
	public static String CONSUMER_KEY = "CONSUMER_KEY";
	public static String CONSUMER_SECRET = "CONSUMER_SECRET";

	public static String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";
	
	public static final String CALLBACK_URL = "twitter-callback:///";
	
	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	
	private static SharedPreferences mSharedPreferences;
	
	public static boolean isConnected(Context context) {
		mSharedPreferences = context.getSharedPreferences(TwitterHelper.PREFERENCE_NAME, context.MODE_PRIVATE);
		return mSharedPreferences.getString(TwitterHelper.PREF_KEY_TOKEN, null) != null;
	}
	
	public static Editor getTwitterPrerefencesEditor(Context context) {
		mSharedPreferences = context.getSharedPreferences(TwitterHelper.PREFERENCE_NAME, context.MODE_PRIVATE);
		return mSharedPreferences.edit();
	}
	
	public static String getToken(Context context) {
		mSharedPreferences = context.getSharedPreferences(TwitterHelper.PREFERENCE_NAME, context.MODE_PRIVATE);
		return mSharedPreferences.getString(TwitterHelper.PREF_KEY_TOKEN, "");
	}
	
	public static String getSecret(Context context) {
		mSharedPreferences = context.getSharedPreferences(TwitterHelper.PREFERENCE_NAME, context.MODE_PRIVATE);
		return mSharedPreferences.getString(TwitterHelper.PREF_KEY_SECRET, "");
	}
	
	public static void disconnectTwitter(Context context) {
		mSharedPreferences = context.getSharedPreferences(TwitterHelper.PREFERENCE_NAME, context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.remove(TwitterHelper.PREF_KEY_TOKEN);
		editor.remove(TwitterHelper.PREF_KEY_SECRET);
		editor.commit();
	}
}
