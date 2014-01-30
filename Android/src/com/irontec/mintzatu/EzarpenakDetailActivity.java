package com.irontec.mintzatu;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.SimpleLicenseAdapter;
import com.irontec.helpers.FacebookHelper;
import com.irontec.helpers.TwitterHelper;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnLogoutListener;

public class EzarpenakDetailActivity extends SherlockActivity implements OnItemClickListener{

	private final static String TAG = EzarpenakDetailActivity.class.getSimpleName();
	private Integer mType = 0;
	private int mLayout;
	private Context mContext;
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static final int TWITTER_AUTH = 110;
	private CheckBox twCheck;
	private CheckBox fbCheck;
	private ListView mLicenseList;
	private SimpleFacebook mSimpleFacebook;

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSimpleFacebook = FacebookHelper.getSimpleFacebookInstance(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		Intent intent = getIntent();
		if (intent != null) {
			mType = intent.getIntExtra("detail_type", 0);
		}

		if (mType == 0) {
			loadSocialNetworks();
			getSupportActionBar().setTitle(mContext.getResources().getString(R.string.ezarpenak_sare_sozialak));

			twCheck = (CheckBox)findViewById(R.id.twCheck);
			fbCheck = (CheckBox)findViewById(R.id.fbCheck);
			
			mSimpleFacebook = FacebookHelper.getSimpleFacebookInstance(this);

			if (mSimpleFacebook.isLogin()) {
				fbCheck.setChecked(true);
			} else {
				fbCheck.setChecked(false);
			}

			if (TwitterHelper.isConnected(mContext)) {
				twCheck.setChecked(true);
			} else {
				twCheck.setChecked(false);
			}
			twCheck.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!twCheck.isChecked()) {
						TwitterHelper.disconnectTwitter(mContext);
						twCheck.setChecked(false);
					} else {
						new Thread(new Runnable() {
							public void run() {
								askOAuth();
								twCheck.setChecked(true);
							}
						}).start();
					}
				}
			});
			fbCheck.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!fbCheck.isChecked()) {
						mSimpleFacebook.logout(onLogoutListener);
						fbCheck.setChecked(false);
					} else {
						mSimpleFacebook.login(mOnLoginListener);
					}
				}
			});
		} else if (mType == 1) {
			loadLicensesLayout();
			getSupportActionBar().setTitle(mContext.getResources().getString(R.string.ezarpenak_lizentziak));
			mLicenseList = (ListView)findViewById(R.id.license_list);

			ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();

			BasicNameValuePair license1 = new BasicNameValuePair("Android Open Source Project", "http://source.android.com");
			BasicNameValuePair license2 = new BasicNameValuePair("Twitter4J", "http://twitter4j.org");
			BasicNameValuePair license3 = new BasicNameValuePair("android-async-http", "http://loopj.com/android-async-http/");
			BasicNameValuePair license4 = new BasicNameValuePair("Picasso", "http://square.github.io/picasso/");
			BasicNameValuePair license5 = new BasicNameValuePair("Apache HttpComponents", "http://hc.apache.org/");
			BasicNameValuePair license6 = new BasicNameValuePair("android-simple-facebook", "https://github.com/sromku/android-simple-facebook#login-1");
			BasicNameValuePair license7 = new BasicNameValuePair("android-protips-location", "https://code.google.com/p/android-protips-location/source/browse/trunk/src/com/radioactiveyak/location_best_practices/utils/base/ILastLocationFinder.java?r=3");
			values.add(license1);
			values.add(license2);
			values.add(license3);
			values.add(license4);
			values.add(license5);
			values.add(license6);
			values.add(license7);

			mLicenseList.setAdapter(new SimpleLicenseAdapter(mContext, values));
			mLicenseList.setOnItemClickListener(EzarpenakDetailActivity.this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		BasicNameValuePair value = (BasicNameValuePair) mLicenseList.getItemAtPosition(position);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value.getValue()));
		startActivity(browserIntent);
	}

	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(TwitterHelper.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(TwitterHelper.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(TwitterHelper.CALLBACK_URL);
			Intent i = new Intent(this, WebviewActivity.class);
			i.putExtra("URL", requestToken.getAuthenticationURL());
			startActivityForResult(i, TWITTER_AUTH);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data); 
		super.onActivityResult(requestCode, resultCode, data); 

		if (requestCode == TWITTER_AUTH) {
			if (resultCode == Activity.RESULT_OK) {
				final String oauthVerifier = (String) data.getExtras().get("oauth_verifier");
				if (oauthVerifier != null && oauthVerifier != "") {
					new Thread(new Runnable() {
						AccessToken accessToken = null;
						public void run() {
							try {
								accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
								Editor e = TwitterHelper.getTwitterPrerefencesEditor(mContext);
								e.putString(TwitterHelper.PREF_KEY_TOKEN, accessToken.getToken()); 
								e.putString(TwitterHelper.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
								e.commit();
							} catch (TwitterException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		}
	}

	private OnLoginListener mOnLoginListener = new OnLoginListener() {
		@Override
		public void onFail(String reason) {
			Log.e(TAG, reason);
		}

		@Override
		public void onException(Throwable throwable) {
			Log.e(TAG, throwable.toString());
		}

		@Override
		public void onThinking() {
			Log.d(TAG, "Thinking");
		}

		@Override
		public void onLogin() {
			FacebookHelper.setFacebookAccessToken(mContext, mSimpleFacebook);
			fbCheck.setChecked(true);
		}

		@Override
		public void onNotAcceptingPermissions() {
			Log.d(TAG, "No permisions");
		}
	};
	
	OnLogoutListener onLogoutListener = new SimpleFacebook.OnLogoutListener()
	{

	    @Override
	    public void onFail(String reason) {
	    	Log.e(TAG, reason);
	    }

	    @Override
	    public void onException(Throwable throwable) {
	    	Log.e(TAG, throwable.toString());
	    }

	    @Override
	    public void onThinking() {
	    	Log.d(TAG, "Thinking");
	    }

	    @Override
	    public void onLogout()
	    {
	    	FacebookHelper.disconnectFacebook(mContext);
	    }

	};


	public void loadSocialNetworks() {
		setContentView(R.layout.include_ezarpenak_social);

	}

	public void loadLicensesLayout() {
		setContentView(R.layout.include_ezarpenak_licenses);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.ezarpenak_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
