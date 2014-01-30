package com.irontec.mintzatu;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.CameraHelper;
import com.irontec.helpers.FacebookHelper;
import com.irontec.helpers.TwitterHelper;
import com.irontec.models.Badge;
import com.irontec.models.Place;



public class CheckinActivity extends SherlockActivity {

	private Context mContext;
	private static final int SELECT_PHOTO = 100;
	private static final int CAPTURE_PHOTO = 101;
	private static final int TWITTER_AUTH = 110;
	private ToggleButton mToggle;
	private ImageButton mTwitter;
	private ImageButton mFacebook;
	private Button mCheckin;
	private LinearLayout mSareakLayout;
	private ImageButton mArgazkia;
	private EditText mAzalpena;
	private ImageView mLekuaArgazkia;
	private static Uri mFileUri;
	private TextView mIzena;
	private static File mMediaStorageDir;
	private Bitmap mBitmap;
	private int mOrientation = 0;
	private static Long mPlaceId;
	private static String mPlaceName;
	private Place mPlace;
	private String mPlaceCatName;
	private static Twitter twitter;
	private static RequestToken requestToken;
	private Boolean mIsTwitterEnabledForPosting = false;
	private Boolean mIsFacebookEnabledForPosting = false;
	private Dialog mDialog;
	private Boolean mPhotoHasBeenTaken = false;
	private ArrayList<Badge> mBadges;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mContext = getBaseContext();

		mDialog = new Dialog(CheckinActivity.this);
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);
		
		Intent intent = getIntent();
		if (intent != null) {
			mPlace = intent.getParcelableExtra("place");
			mPlaceId = mPlace.id_lekua;
			mPlaceName = mPlace.izena;
			if (!mPlace.kategoriak.isEmpty()) {
				mPlaceCatName = mPlace.kategoriak.get(0).izena;	
			} else {
				mPlaceCatName = "";
			}
		}

		mIzena = (TextView)findViewById(R.id.izena);
		mIzena.setText(mPlaceName);
		mSareakLayout = (LinearLayout)findViewById(R.id.sareakLayout);
		mToggle = (ToggleButton)findViewById(R.id.toggleButton1);
		mToggle.setChecked(false);
		mToggle.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					buttonView.setBackgroundColor(getResources().getColor(R.color.button_blue));
					if (TwitterHelper.isConnected(mContext)) {
						mIsTwitterEnabledForPosting = true;
					}
					if (FacebookHelper.isConnected(CheckinActivity.this)) {
						mIsFacebookEnabledForPosting = true;
					}
					if (!mIsTwitterEnabledForPosting && !mIsFacebookEnabledForPosting) {
						Intent intent = new Intent(mContext, EzarpenakDetailActivity.class);
						intent.putExtra("detail_type", 0);
						startActivity(intent);
					}
				} else {
					buttonView.setBackgroundColor(Color.GRAY);
				}
			}
		});
		
		mArgazkia = (ImageButton)findViewById(R.id.takePhoto);
		mArgazkia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(CheckinActivity.this);
				dialog.setContentView(R.layout.dialog_photo_chooser);
				dialog.setTitle("Aukeratu...");
				ImageView galeria = (ImageView) dialog.findViewById(R.id.galeria);
				ImageView kamera = (ImageView) dialog.findViewById(R.id.kamera);
				galeria.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						photoPickerIntent.setType("image/*");
						photoPickerIntent.putExtra("return-data", true);
						startActivityForResult(photoPickerIntent, SELECT_PHOTO);
						dialog.dismiss();
					}
				});
				kamera.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						mFileUri = getOutputMediaFileUri();
						takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, mFileUri );
						startActivityForResult(takePictureIntent, CAPTURE_PHOTO);
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		mAzalpena = (EditText)findViewById(R.id.azalpena);

		mLekuaArgazkia = (ImageView)findViewById(R.id.lekuaArgazkia);
		if (savedInstanceState != null) {
			mFileUri = savedInstanceState.getParcelable("fileUri");
			if (mFileUri != null) {
				File file = new File(mFileUri.getPath());
				mBitmap = CameraHelper.decodeFile(file);
				mOrientation = CameraHelper.getCameraPhotoOrientation(mContext, mFileUri, file.getPath());
				if (mBitmap != null) {
					mLekuaArgazkia.setImageBitmap(mBitmap);
					BitmapDrawable result = CameraHelper.sizeChanger(mContext, mLekuaArgazkia, 250, mOrientation);
					mLekuaArgazkia.setImageDrawable(result);
					mLekuaArgazkia.invalidate();
				}
			}
		}

		mCheckin = (Button)findViewById(R.id.checkin);
		mCheckin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.show();
				new CheckinTask().execute(MintzatuAPI.getAbsoluteUrl(MintzatuAPI.CHECKIN).toString());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mFileUri != null && mLekuaArgazkia != null) {
			File file = new File(mFileUri.getPath());
			mBitmap = CameraHelper.decodeFile(file);
			mOrientation = CameraHelper.getCameraPhotoOrientation(mContext, mFileUri, file.getPath());
			if (mBitmap != null) {
				mLekuaArgazkia.setImageBitmap(mBitmap);
				BitmapDrawable result = CameraHelper.sizeChanger(mContext, mLekuaArgazkia, 250, mOrientation);
				mLekuaArgazkia.setImageDrawable(result);
				mLekuaArgazkia.invalidate();
			}
		}
		if (mPlaceName != null && mPlaceName != "" && mIzena != null) {
			mIzena.setText(mPlaceName);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 

		if (requestCode == SELECT_PHOTO) {
			if(data != null && data.getData()!= null) {
				mFileUri = data.getData();
				if (mFileUri != null) {
					Cursor cursor = getContentResolver().query(mFileUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
					cursor.moveToFirst();
					final String imageFilePath = cursor.getString(0);
					mOrientation = CameraHelper.getCameraPhotoOrientation(getBaseContext(), mFileUri, imageFilePath);
					File photos = new File(imageFilePath);
					mBitmap = CameraHelper.decodeFile(photos);
					cursor.close();
					mPhotoHasBeenTaken = true;
				}
			} else {
				Toast.makeText(this, getString(R.string.no_photo_selected), Toast.LENGTH_SHORT).show();
			}
		}else if (requestCode == TWITTER_AUTH) {
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
								mIsTwitterEnabledForPosting = true;
								mTwitter.setBackgroundColor(getResources().getColor(R.color.button_blue));
							} catch (TwitterException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		} else if (requestCode == CAPTURE_PHOTO) {
			if (resultCode == Activity.RESULT_OK) {
				if (mFileUri != null) {
					File file = new File(mFileUri.getPath());
					mBitmap = CameraHelper.decodeFile(file);
					mOrientation = CameraHelper.getCameraPhotoOrientation(mContext, mFileUri, file.getPath());
					mPhotoHasBeenTaken = true;
				}
			}
		}
		if (mBitmap != null) {
			mLekuaArgazkia.setImageBitmap(mBitmap);
			BitmapDrawable result = CameraHelper.sizeChanger(mContext, mLekuaArgazkia, 250, mOrientation);
			mLekuaArgazkia.setImageDrawable(result);
			mLekuaArgazkia.invalidate();
		}
	}

	public class CheckinTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... arg) {
			String url = arg[0];
			String result = "";

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			BitmapDrawable bmDrawable = (BitmapDrawable)mLekuaArgazkia.getDrawable();
			byte[] byteArray = null;
			if (bmDrawable != null) {
				Bitmap bitmap = bmDrawable.getBitmap();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byteArray = stream.toByteArray();
			}
			try {
				if (byteArray != null && mPhotoHasBeenTaken) {
					entity.addPart("image", new ByteArrayBody(byteArray, "image/jpg", "image.jpg"));
				}
				entity.addPart("token", new StringBody(MintzatuAPI.getToken(mContext).toString()));
				entity.addPart("id", new StringBody(MintzatuAPI.getUserid(mContext).toString()));
				if (mPlaceId != null) {
					entity.addPart("idPlace", new StringBody(mPlaceId.toString()));
				} else {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
					CheckinActivity.this.finish();
				}
				if (mAzalpena != null && mAzalpena.getText() != null) {
					entity.addPart("comment", new StringBody(mAzalpena.getText().toString()));
				} else {
					//entity.addPart("comment", new StringBody(""));
				}

				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				result = reader.readLine();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String json) {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject finalResult = null;
			try {
				finalResult = new JSONObject(tokener);
				int error = finalResult.getInt("error");
				if (error == 0) {
					if (!finalResult.isNull("badges")) {
						JSONArray badgesJson = finalResult.getJSONArray("badges");
						mBadges = new ArrayList<Badge>();
						for (int i = 0; i < badgesJson.length(); i++) {
							JSONObject badgeJson = badgesJson.getJSONObject(i);
							Badge badge = new Badge(badgeJson);
							mBadges.add(badge);
						}
					}
//					mBadges = new ArrayList<Badge>();
//					Badge develBadge = new Badge(1l, "Badge 1", "Badge 1 description", "", "", "http://2.bp.blogspot.com/_NDc0-akUurk/S8dAfdQnCqI/AAAAAAAAAZU/KslSnWiceRI/s200/swarm_big.png");
//					mBadges.add(develBadge);
					if (mIsTwitterEnabledForPosting) {
						new updateTwitterStatus().execute(mContext.getResources().getString(R.string.tweet2, mPlaceName, mPlaceCatName));
					}
					if (mIsFacebookEnabledForPosting) {
						FacebookHelper.publishFeed(CheckinActivity.this, mContext.getResources().getString(R.string.fb2, mPlaceName, mPlaceCatName), mPlace);
					}
					mDialog.dismiss();
					finishWithResult();
				} else if (error == MintzatuAPI.ERROR_CHECKIN_TIME) {
					mDialog.dismiss();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.api_checkin_time), Toast.LENGTH_LONG).show();
				} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
					MintzatuAPI.logout(mContext);
					mDialog.dismiss();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				} else {
					mDialog.dismiss();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelable("fileUri", mFileUri);
		savedInstanceState.putString("placeName", mPlaceName);
	}

	private void finishWithResult() {
		mFileUri = null;
		Intent intent = new Intent();
		if (mBadges != null && !mBadges.isEmpty()) {
			Bundle conData = new Bundle();
			conData.putParcelableArrayList("checkin_results", mBadges);
			intent.putExtras(conData);
		}
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.checkin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition (R.anim.open_main, R.anim.close_next);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}
	
	private static Uri getOutputMediaFileUri(){
		return Uri.fromFile(getOutputMediaFile());
	}

	private static File getOutputMediaFile(){
		mMediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "Mintzatu");
		if (! mMediaStorageDir.exists()){
			if (! mMediaStorageDir.mkdirs()){
				Log.d("Mintzatu", "failed to create directory");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = new File(mMediaStorageDir.getPath() + File.separator +
				"IMG_"+ timeStamp + ".jpg");
		return mediaFile;
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
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

	class updateTwitterStatus extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... args) {
			String text = args[0];
			String url = null;
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TwitterHelper.CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TwitterHelper.CONSUMER_SECRET);

				String access_token = TwitterHelper.getToken(mContext);
				String access_token_secret = TwitterHelper.getSecret(mContext);

				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
				
				url = MintzatuAPI.BASE_URL + MintzatuAPI.SOCIAL_PLACE_URL + URLEncoder.encode(mPlace.url);
				text += " " + url;
				/*if (mFileUri != null) {
					File file = new File(mFileUri.getPath());
					try {
						ImageUploadFactory factory = new ImageUploadFactory();
			            ImageUpload upload = factory.getInstance(MediaProvider.TWITTER, twitter.getAuthorization());
						url = upload.upload(file);
						text += " " + url;
					} catch (TwitterException te) {
						te.printStackTrace();
					}
				}*/
				twitter4j.Status response = twitter.updateStatus(new StatusUpdate(text));
			} catch (TwitterException e) {
				Log.d("Twitter Update Error", e.getMessage());
				mDialog.dismiss();
			}
			mDialog.dismiss();
			return null;
		}

		protected void onPostExecute(String file_url) {
			//mDialog.dismiss();
			//finishWithResult();
		}

	}

}
