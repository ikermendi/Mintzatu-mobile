package com.irontec.helpers;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.SessionLoginBehavior;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.R;
import com.irontec.models.Badge;
import com.irontec.models.Place;
import com.sromku.simple.fb.Permissions;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnProfileRequestListener;
import com.sromku.simple.fb.SimpleFacebook.OnPublishListener;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.utils.Logger;

public class FacebookHelper {
	
	protected static final String TAG = FacebookHelper.class.getSimpleName();
	public static String APP_ID = "FACEBOOK_APP_ID";
	public static String ACCESS_TOKEN = "access_token";
	public static Editor mEditor;
	private static SimpleFacebook mSimpleFacebook;
	
	private static SharedPreferences mSharedPreferences;
	
	static Permissions[] permissions = new Permissions[] {
			Permissions.BASIC_INFO,
			Permissions.EMAIL,
			Permissions.USER_BIRTHDAY,
			Permissions.PUBLISH_ACTION
	};
	
	public static boolean isConnected(Activity activity) {
		if (mSimpleFacebook == null) {
			mSimpleFacebook = getSimpleFacebookInstance(activity);
		}
		return mSimpleFacebook.isLogin();
	}
	
	public static SimpleFacebook getSimpleFacebookInstance(Activity activity) {
		if (mSimpleFacebook == null) {
			SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(APP_ID)
			.setPermissions(permissions)
			.build();
			mSimpleFacebook = SimpleFacebook.getInstance(activity);
			mSimpleFacebook.setConfiguration(configuration);
			return mSimpleFacebook;
		} else {
			return mSimpleFacebook;
		}
	}
	
	public static void setFacebookAccessToken(Context context, SimpleFacebook mSimpleFacebook) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	mEditor = mSharedPreferences.edit();
    	mEditor.putString(ACCESS_TOKEN, mSimpleFacebook.getAccessToken());
    	mEditor.commit();
	}
	
	public static void disconnectFacebook(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mSharedPreferences.edit();
		mEditor.remove(ACCESS_TOKEN);
		mEditor.commit();
	}
	
	public static void publishFeed(Activity activity, String name, Place place) {
		OnPublishListener onPublishListener = new SimpleFacebook.OnPublishListener()
		{

		    @Override
		    public void onFail(String reason) {
		    	
		    }

		    @Override
		    public void onException(Throwable throwable) {
		    	
		    }

		    @Override
		    public void onThinking() {}

		    @Override
		    public void onComplete(String postId) {
		    	
		    }
		};

		String caption = MintzatuAPI.BASE_URL + MintzatuAPI.SOCIAL_PLACE_URL + URLEncoder.encode(place.url);
		String description = activity.getBaseContext().getResources().getString(R.string.fbtext2);
		Feed feed = null;
		feed = new Feed.Builder()
	    .setMessage("")
	    .setName(name)
	    .setCaption(caption)
	    .setDescription(description)
	    .setPicture(place.irudia)
	    .setLink(caption)
	    .build();
		
		/*if (mBadges == null || mBadges.isEmpty()) {
			feed = new Feed.Builder()
			    .setMessage(message)
			    .build();

		} else {
			String name = activity.getBaseContext().getResources().getString(R.string.fbtext);
			feed = new Feed.Builder()
		    .setMessage(message)
		    .setName(name)
		    .setCaption(mBadges.get(0).name)
		    .setDescription(mBadges.get(0).desc)
		    .setPicture(mBadges.get(0).img)
		    .setLink("http://mintzatu.com/")
		    .build();
		}*/
		
		if (feed != null) {
			if (mSimpleFacebook == null) {
				mSimpleFacebook = getSimpleFacebookInstance(activity);
			}
			mSimpleFacebook.publish(feed, onPublishListener);
		}
		
	}

}
