package com.irontec.fragments;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.adapters.ProfileItemsAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.helpers.CameraHelper;
import com.irontec.mintzatu.BadgeActivity;
import com.irontec.mintzatu.FriendRequestActivity;
import com.irontec.mintzatu.GalleryActivity;
import com.irontec.mintzatu.LoginActivity;
import com.irontec.mintzatu.MayorshipActivity;
import com.irontec.mintzatu.MyCheckinActivity;
import com.irontec.mintzatu.R;
import com.irontec.models.FriendProfile;
import com.irontec.models.Me;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class ProfilaFragment extends SherlockFragment {

	private static final String TAG = ProfilaFragment.class.getSimpleName();
	private static final int SELECT_PHOTO = 100;
	private static final int CAPTURE_PHOTO = 101;
	private static Context mContext;
	private TextView mIzena;
	private TextView mLekua;
	private TextView mLekuaText;
	private LinearLayout mDominak;
	private ImageView mArgazkia;
	private ListView mZerrenda;
	private LinearLayout mEskaerak;
	private LinearLayout mLagunEgin;
	private LinearLayout mAlkatetzak;
	private ArrayList<String> mIzenak;
	private ViewSwitcher mViewSwitcher;
	private TextView mZenbatAlkatetzak;
	private TextView mZenbatDomin;
	private TextView mZenbat;
	private Long mFriendId;
	private static Uri mFileUri;
	private static File mMediaStorageDir;
	private Boolean mPhotoHasBeenTaken = false;
	private Bitmap mBitmap;
	private int mOrientation = 0;
	private Dialog mDialog;

	public ProfilaFragment() {}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(getActivity()).activityStart(getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(getActivity()).activityStop(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profila, container, false);

		mDialog = new Dialog(getActivity());
		mDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
		mDialog.setCancelable(false);
		mDialog.setContentView(R.layout.dialog_simple_loading);

		mContext = getActivity().getBaseContext();

		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher1);
		mIzena = (TextView)rootView.findViewById(R.id.izena);
		mArgazkia = (ImageView)rootView.findViewById(R.id.avatar);
		mZerrenda = (ListView)rootView.findViewById(R.id.zerrenda);
		mDominak = (LinearLayout)rootView.findViewById(R.id.dominak);
		mEskaerak = (LinearLayout)rootView.findViewById(R.id.eskaerak);
		mLagunEgin = (LinearLayout)rootView.findViewById(R.id.lagunEskaera);
		mAlkatetzak = (LinearLayout)rootView.findViewById(R.id.alkatetza);
		mZenbatAlkatetzak = (TextView)rootView.findViewById(R.id.zenbatAlkatetzak);
		mZenbatDomin = (TextView)rootView.findViewById(R.id.zenbatDomin);
		mZenbat = (TextView)rootView.findViewById(R.id.zenbat);
		mLekua = (TextView)rootView.findViewById(R.id.lekua);
		mLekuaText = (TextView)rootView.findViewById(R.id.lekuatxt);

		loadMyProfile();

		mLagunEgin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendFriendRequest();
			}
		});
		mDominak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BadgeActivity.class);
				if (mFriendId != null) {
					intent.putExtra("friendId", mFriendId);
				}
				startActivity(intent);
				getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});
		mAlkatetzak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MayorshipActivity.class);
				if (mFriendId != null) {
					intent.putExtra("friendId", mFriendId);
				}
				startActivity(intent);
				getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});
		mArgazkia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(getActivity());
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

		return rootView;

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

	private void loadFriendProfile() {
		
		RequestParams params = new RequestParams();
		params.put("id", MintzatuAPI.getUserid(mContext).toString());
		params.put("idProfile", mFriendId.toString());
		params.put("token", MintzatuAPI.getToken(mContext));

		MintzatuAPI.post(MintzatuAPI.PROFILE, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						final FriendProfile friendProfile = new FriendProfile(response);
						mIzena.setText(friendProfile.fullname);
						mZenbatAlkatetzak.setText(friendProfile.mayorships.toString());
						mZenbatDomin.setText(friendProfile.badges.toString());
						if (friendProfile.lastPlaceName != null && !friendProfile.lastPlaceName.equals("")) {
							mLekua.setText(friendProfile.lastPlaceName);
							mLekuaText.setVisibility(View.VISIBLE);
						} else {
							if (isAdded()) {
								mLekua.setText(getResources().getString(R.string.no_checkin_yet));
							}
						}
						if (friendProfile.friends != null && friendProfile.friends) {
							mLagunEgin.setVisibility(View.GONE);
						} else if (friendProfile.friends != null && !friendProfile.friends) {
							mLagunEgin.setVisibility(View.VISIBLE);
						} else if (friendProfile.friendshipState != null && friendProfile.friendshipState == 1) {
							if (isAdded()) {
								mLagunEgin.setBackgroundColor(mContext.getResources().getColor(R.color.mintzatu_orange));
							}
						}

						Picasso.with(mContext)
						.load(friendProfile.img)
						.placeholder(R.drawable.user_placeholder)
						.error(R.drawable.user_placeholder)
						.skipCache()
						.into(mArgazkia);

						mIzenak = new ArrayList<String>();
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
						}
					}
					mViewSwitcher.showNext();
				} catch (JSONException e) {
					e.printStackTrace();
					mViewSwitcher.showNext();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mViewSwitcher.showNext();
			}
		});		
	}

	private void loadMyProfile() {
		
		RequestParams params = new RequestParams();
		params.put("id", MintzatuAPI.getUserid(mContext).toString());
		params.put("idProfile", MintzatuAPI.getUserid(mContext).toString());
		params.put("token", MintzatuAPI.getToken(mContext));

		MintzatuAPI.post(MintzatuAPI.PROFILE, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int code = response.getInt("error");
					if(code == 0) {
						final Me me = new Me(response);
						mIzena.setText(me.fullname);
						mZenbat.setText(me.friendRequests.toString());
						mEskaerak.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (me.friendRequests > 0) {
									Intent intent = new Intent(mContext, FriendRequestActivity.class);
									startActivity(intent);
									getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
								} else {
									if (isAdded()) {
										Toast.makeText(mContext, mContext.getResources().getString(R.string.api_requests_not_found), Toast.LENGTH_SHORT).show();
									}
								}

							}
						});

						mZenbatAlkatetzak.setText(me.mayorships.toString());
						mZenbatDomin.setText(me.badges.toString());
						if (me.lastPlaceName != null && !me.lastPlaceName.equals("")) {
							mLekua.setText(me.lastPlaceName);
							mLekuaText.setVisibility(View.VISIBLE);
						} else {
							mLekua.setText(getResources().getString(R.string.no_checkin_yet));
						}

						Picasso.with(mContext)
						.load(me.img)
						.placeholder(R.drawable.user_placeholder)
						.error(R.drawable.user_placeholder)
						.skipCache()
						.into(mArgazkia);

						mIzenak = new ArrayList<String>();
						//mIzenak.add(getResources().getString(R.string.mezuak));
						if (isAdded()) {
							mIzenak.add(getResources().getString(R.string.checkin));
							mIzenak.add(getResources().getString(R.string.irudiak));
						}

						ProfileItemsAdapter profileItemsAdater = new ProfileItemsAdapter(getActivity().getBaseContext(), mIzenak, Integer.valueOf(0));

						mZerrenda.setAdapter(profileItemsAdater);
						mZerrenda.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View view, int position, long id) {
								//								switch (position) {
								//								case 0:
								//									Toast.makeText(getActivity().getBaseContext(), mIzenak.get(position), Toast.LENGTH_SHORT).show();
								//									break;
								//								case 1:
								//									Toast.makeText(getActivity().getBaseContext(), mIzenak.get(position), Toast.LENGTH_SHORT).show();
								//									break;
								//								case 2:
								//									Intent intent = new Intent(mContext, GalleryActivity.class);
								//									startActivity(intent);
								//									break;
								//								}
								switch (position) {
								case 0:
									Intent intentMyCheckins = new Intent(mContext, MyCheckinActivity.class);
									startActivity(intentMyCheckins);
									getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
									break;
								case 1:
									Intent intentGallery = new Intent(mContext, GalleryActivity.class);
									startActivity(intentGallery);
									getActivity().overridePendingTransition (R.anim.open_next, R.anim.close_main);
									break;
								}

							}
						});
					} else if (code == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
						}
					}
					mViewSwitcher.showNext();
				} catch (JSONException e) {
					e.printStackTrace();
					mViewSwitcher.showNext();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				mViewSwitcher.showNext();
			}
		});
	}

	public void sendFriendRequest() {
		RequestParams makeFriendsParams = new RequestParams();
		makeFriendsParams.put("id", MintzatuAPI.getUserid(mContext).toString());
		makeFriendsParams.put("token", MintzatuAPI.getToken(mContext).toString());
		makeFriendsParams.put("idProfile", mFriendId.toString());

		MintzatuAPI.post(MintzatuAPI.SEND_FRIEND_REQUEST, makeFriendsParams, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				try {
					int error = response.getInt("error");
					if (error == 0) {
						if (isAdded()) {
							mLagunEgin.setBackgroundColor(mContext.getResources().getColor(R.color.mintzatu_orange));
						}
					} else if (error == MintzatuAPI.ERROR_TOKEN_EXPIRED) {
						MintzatuAPI.logout(mContext);
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_session_expired), Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(mContext, LoginCircles.class);
						startActivity(intent);
					} else {
						if (isAdded()) {
							Toast.makeText(mContext, mContext.getResources().getString(R.string.api_failed), Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_PHOTO) {
			if(data != null && data.getData()!= null) {
				mFileUri = data.getData();
				if (mFileUri != null) {
					Cursor cursor = getActivity().getContentResolver().query(mFileUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
					cursor.moveToFirst();
					final String imageFilePath = cursor.getString(0);
					mOrientation = CameraHelper.getCameraPhotoOrientation(getActivity().getBaseContext(), mFileUri, imageFilePath);
					File photos = new File(imageFilePath);
					mBitmap = CameraHelper.decodeFile(photos);
					cursor.close();
					mPhotoHasBeenTaken = true;
				}
			} else {
				Toast.makeText(getActivity(), getString(R.string.no_photo_selected), Toast.LENGTH_SHORT).show();
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
			mArgazkia.setImageBitmap(mBitmap);
			BitmapDrawable result = CameraHelper.sizeChanger(mContext, mArgazkia, 250, mOrientation);
			mArgazkia.setImageDrawable(result);
			mArgazkia.invalidate();
			new PhotoUploadTask().execute(MintzatuAPI.getAbsoluteUrl(MintzatuAPI.UPLOAD_USER_AVATAR).toString());
		}
	}

	public class PhotoUploadTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog.show();
		}

		@Override
		protected String doInBackground(String... arg) {
			String url = arg[0];
			String result = "";

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			BitmapDrawable bmDrawable = (BitmapDrawable)mArgazkia.getDrawable();
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
			//JSONTokener tokener = new JSONTokener(json);
			//JSONObject finalResult = null;
			mDialog.dismiss();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

}
