package com.irontec.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MintzatuAPI {

	private static final String TAG = MintzatuAPI.class.getSimpleName();
	protected static final String SENDER_ID = "GCM_SENDER_ID";
	private static final String TOKEN = "mintzatu_api_token";
	private static final String ID = "mintzatu_api_userid";
	private static final String USERNAME = "mintzatu_api_username";
	private static final String GCM_REGID = "mintzatu_api_gcm_regid";
	private static final String IS_GCM_REGISTERED = "mintzatu_api_isgcm_registered";

	public static final String BASE_URL = "http://www.mintzatu.com/";
	private static final String TYPE = "api/";
	private static final String STATIC_MAP_URL = "http://maps.google.com/maps/api/staticmap?center=";
	private static final String STATIC_MAP_OPTIONS = "&zoom=16&size=250x150&sensor=false&scale=2&markers=";
	public static final String LOGIN = "login";
	public static final String SIGN_IN = "register";
	public static final String PROFILE = "profile";
	public static final String GET_BADGES = "get-badges";
	public static final String EXPLORE = "explore-places";
	public static final String PLACE = "place";
	public static final String PHOTOS = "get-place-pictures";
	public static final String HISTORIES = "get-place-activity";
	public static final String CHECKIN = "checkin";
	public static final String MAYORSHIPS = "get-mayorships";
	public static final String FRIEND_REQUEST = "get-friend-requests";
	public static final String ANSWER_REQUEST = "answer-request";
	public static final String FRIENDS = "all-my-friends";
	public static final String SEARCH_PEOPLE = "search-people";
	public static final String SEARCH_PLACES = "search-place";
	public static final String ADD_PLACE = "new-place";
	public static final String MY_CHECK_INS = "my-checkins";
	public static final String MY_PHOTOS = "my-pictures";
	public static final String SEND_FRIEND_REQUEST = "add-friend";
	public static final String PLACE_COMMENTS = "get-place-comments";
	public static final String GET_CATEGORIES = "get-categories";
	public static final String ADD_COMMENT = "comment-place";
	public static final String RESET_PASSWORD = "remind-password";
	public static final String UPLOAD_USER_AVATAR = "profile-picture";
	
	public static final String SOCIAL_PLACE_URL = "lekuak/ikusi/lekua/";

	public static final Integer ERROR_TOKEN_EXPIRED = -10;
	public static final Integer ERROR_REGISTERED_EMAIL = -600;
	public static final Integer ERROR_REGISTERED_USER = -601;
	public static final Integer ERROR_PASSWORD_TOO_SHORT = -602;
	public static final Integer ERROR_CHECKIN_TIME = -202;
	public static final Integer ERROR_INVALID_RESET_PASSWORD_EMAIL = -801;
	public static final Integer ERROR_MALFORMED_EMAIL = -800;
	public static final Integer ERROR_BAD_PARAMS = -1001;
	public static final Integer ERROR_DUPLICATED_PLACE = -666;

	private static SharedPreferences mPreferences;
	private static Editor mEditor;

	private static String suuid;

	private static AsyncHttpClient client = new AsyncHttpClient();


	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static String getAbsoluteUrl(String relativeUrl) {
		Log.d(TAG, BASE_URL + TYPE + relativeUrl);
		return BASE_URL + TYPE + relativeUrl;
	}

	public static String getToken(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mPreferences.getString(TOKEN, null);
	}

	public static void setToken(String token, Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPreferences.edit();
		mEditor.putString(TOKEN, token);
		mEditor.commit();
	}

	public static Integer getUserid(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mPreferences.getInt(ID, 0);
	}

	public static void setUserid(Integer id, Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPreferences.edit();
		mEditor.putInt(ID, id);
		mEditor.commit();
	}

	public static String getStaticMapUrl(LatLng coords) throws UnsupportedEncodingException {
		String latlng = URLEncoder.encode(coords.latitude + "," + coords.longitude, "utf-8");
		String markers = URLEncoder.encode("color:blue|"+ coords.latitude + ',' + coords.longitude, "utf-8");
		return STATIC_MAP_URL + latlng + STATIC_MAP_OPTIONS + markers;
	}

	public static void setUserName(String fullname, Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPreferences.edit();
		mEditor.putString(USERNAME, fullname);
		mEditor.commit();
	}

	public static String getUserName(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mPreferences.getString(USERNAME, null);
	}

	public static void setGCMRegid(Context context, String regId) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPreferences.edit();
		mEditor.putString(GCM_REGID, regId);
		mEditor.commit();
	}

	public static String getGCMRegid(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return mPreferences.getString(GCM_REGID, null);
	}

	public static void logout(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPreferences.edit();
		mEditor.remove(USERNAME);
		mEditor.remove(TOKEN);
		mEditor.remove(ID);
		mEditor.commit();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerGCM(final Activity activity, final Handler handler) {
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
				String regId = "NO_PUSH_API";
				try {
					regId = gcm.register(SENDER_ID);
				} catch (IOException e) {
				}
				setGCMRegid(activity.getBaseContext(), regId);
				handler.sendEmptyMessage(0);
				return null;
			}
		}.execute(null, null, null);
	}
}