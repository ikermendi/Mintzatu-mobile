package com.irontec.fragments;

import java.lang.ref.WeakReference;

import com.actionbarsherlock.app.SherlockFragment;
import com.irontec.helpers.ImageResizer;
import com.irontec.mintzatu.R;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public final class LoginFragment extends SherlockFragment {

	private int mPosition = 0;
	private ImageView mSplash;
	private ImageView mLayout;

	public static LoginFragment newInstance(int position) {
		LoginFragment fragment = new LoginFragment();
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("position", position);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments() != null ? getArguments().getInt("position") : 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);;
		mLayout = (ImageView)view.findViewById(R.id.background);
		mSplash = (ImageView)view.findViewById(R.id.splash);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		switch (mPosition) {
		case 0:
			loadBitmap(R.drawable.background1, mLayout, true);
			loadBitmap(R.drawable.splash1, mSplash, false);
			break;
		case 1:
			loadBitmap(R.drawable.background2, mLayout, true);
			loadBitmap(R.drawable.splash2, mSplash, false);
			break;
		case 2:
			loadBitmap(R.drawable.background3, mLayout, true);
			loadBitmap(R.drawable.splash3, mSplash, false);
			break;
		case 3:
			loadBitmap(R.drawable.background4, mLayout, true);
			loadBitmap(R.drawable.splash4, mSplash, false);
			break;
		default:
			break;
		}
		
	}

	public void loadBitmap(int resId, ImageView imageView, boolean two) {
		BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		task.execute(resId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		clean();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		clean();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		clean();
	}

	public void clean() {
		unbindDrawables(mLayout);
		unbindDrawables(mSplash);
		System.gc();
	}
	
	private void unbindDrawables(ImageView view) {		
		if (view.getDrawable() != null) {
			view.getDrawable().setCallback(null);
		}	
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			if (getActivity() != null && !getActivity().isFinishing()) {
				return ImageResizer.decodeSampledBitmapFromResource(getResources(), data, 600, 600, null);
			} else {
				return null;
			}
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

}
