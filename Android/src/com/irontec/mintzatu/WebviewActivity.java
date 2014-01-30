package com.irontec.mintzatu;

import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.helpers.TwitterHelper;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {

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
	
	private Intent mIntent;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_webview);
		mIntent = getIntent();
		String url = (String)mIntent.getExtras().get("URL");
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient( new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if( url.contains(TwitterHelper.CALLBACK_URL)) {
					Uri uri = Uri.parse( url );
					String oauthVerifier = uri.getQueryParameter( "oauth_verifier" );
					mIntent.putExtra( "oauth_verifier", oauthVerifier );
					setResult( RESULT_OK, mIntent );
					finish();
					return true;
				}
				return false;
			}
		});
		webView.loadUrl(url);
	}

}
