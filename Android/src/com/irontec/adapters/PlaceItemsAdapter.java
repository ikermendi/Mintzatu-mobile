package com.irontec.adapters;

import java.util.ArrayList;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.irontec.mintzatu.PlaceActivity;
import com.irontec.mintzatu.ProfilaActivity;
import com.irontec.mintzatu.R;
import com.irontec.models.Place;
import com.irontec.models.PlaceHistory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PlaceItemsAdapter extends BaseAdapter {

	private final Activity mActivity;
	private final Context mContext;
	private final int DEFAULT_ROW_COUNT = 3;
	private LayoutInflater mInflater;
	private final Place mLekua;
	private final ArrayList<Bitmap> mArgazkiak;
	private final ArrayList<PlaceHistory> mHistories;
	private TextView izena;
	private TextView helbidea;
	private TextView deskribapena;
	private TextView iruzkina;
	private TextView noiz;
	private TextView ekintza;
	private TableLayout tableLayout;
	private ImageView argazkia;
	private ImageView avatar;
	private LinearLayout photoLayout;
	private LinearLayout lerroa;
	private LinearLayout lerroaNoPhoto;
	private LinearLayout iruzkinLayout;
	private Bitmap mBm;
	private Boolean mIsScrollingUp = false;
	private Target target = new Target() {
		@Override
		public void onError() {}
		@Override
		public void onSuccess(Bitmap bm) {
			mBm = bm;
			argazkia.setImageBitmap(bm);
		}
	};

	public PlaceItemsAdapter(Activity activity, Place lekua, ArrayList<Bitmap> argazkiak, ArrayList<PlaceHistory> histories) {
		super();
		this.mActivity = activity;
		this.mContext = activity.getBaseContext();
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLekua = lekua;
		this.mArgazkiak = argazkiak;
		this.mHistories = histories;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int theType = getItemViewType(position);
		if (theType == 0) {
			convertView = mInflater.inflate(R.layout.row_place_header, null);
			helbidea = (TextView)convertView.findViewById(R.id.helbidea);
			izena = (TextView)convertView.findViewById(R.id.izena);
			deskribapena = (TextView)convertView.findViewById(R.id.deskribapena);
			deskribapena.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mLekua != null) {
						if (mLekua.deskribapena.length() > 140) {
							Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.place_textview_animation);
							if (deskribapena != null && deskribapena.getText().length() < mLekua.deskribapena.length()) {
								deskribapena.setText(mLekua.deskribapena);
								deskribapena.startAnimation(anim);
							} else {
								deskribapena.setText(mLekua.getShortPlaceDescription());
								deskribapena.startAnimation(anim);
							}
						} else {
							deskribapena.setText(mLekua.deskribapena);
						}
					}
				}
			});
			avatar = (ImageView)convertView.findViewById(R.id.avatar);
		}else if (theType == 1) {
			convertView = mInflater.inflate(R.layout.row_place_item, null);
			izena = (TextView)convertView.findViewById(R.id.izenburua);
		} else if (theType == 2) {
			convertView = mInflater.inflate(R.layout.row_place_images_container, null);
			tableLayout = (TableLayout) convertView.findViewById(R.id.argazkiak);
			lerroa = (LinearLayout)convertView.findViewById(R.id.lerroa);
			lerroaNoPhoto = (LinearLayout)convertView.findViewById(R.id.lerroaNoPhoto);
		} else {
			if (mHistories != null && !mHistories.isEmpty()) {
				final int pos = position;
				convertView = mInflater.inflate(R.layout.row_place_type_checkin, null);
				izena = (TextView)convertView.findViewById(R.id.izena);
				izena.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PlaceHistory placeHistory = mHistories.get(pos - DEFAULT_ROW_COUNT);
						if (placeHistory != null && placeHistory.idWho != null) {
							changeToProfileFragment(placeHistory.idWho);
						}
					}
				});
				iruzkina = (TextView)convertView.findViewById(R.id.iruzkina);
				noiz = (TextView)convertView.findViewById(R.id.noiz);
				ekintza = (TextView)convertView.findViewById(R.id.ekintza);
				argazkia = (ImageView)convertView.findViewById(R.id.placeAvatar);
				argazkia.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PlaceHistory placeHistory = mHistories.get(pos - DEFAULT_ROW_COUNT);
						((PlaceActivity) mActivity).handleImageTransformation(mBm, placeHistory.normalImg);
					}
				});
				avatar = (ImageView)convertView.findViewById(R.id.argazkia);
				avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PlaceHistory placeHistory = mHistories.get(pos - DEFAULT_ROW_COUNT);
						if (placeHistory != null && placeHistory.idWho != null) {
							changeToProfileFragment(placeHistory.idWho);
						}
					}
				});
				photoLayout = (LinearLayout) convertView.findViewById(R.id.photoLayout);
				iruzkinLayout = (LinearLayout) convertView.findViewById(R.id.iruzkinLayout);
			}
		}
		if (theType == 0) {
			izena.setText(mLekua.izena);
			helbidea.setText(mLekua.helbidea);
			deskribapena.setText(mLekua.getShortPlaceDescription());
			if(mLekua.irudia != null && mLekua.irudia != "") {
				Picasso.with(mContext)
				.load(mLekua.irudia)
				.resize(100, 100)
				.centerCrop()
				.placeholder(R.drawable.lekuaholder)
				.error(R.drawable.lekuaholder)
				.into(avatar);
			}
		} else if (theType == 1) {
			izena.setText(mContext.getResources().getString(R.string.azalpenak));
		} else if (theType == 2) {
			if (tableLayout.getChildCount() < 2) {
				if (!mArgazkiak.isEmpty()) {
					inflateImageLayout(mContext, tableLayout, 10);
					lerroa.setVisibility(View.VISIBLE);
				} else {
					lerroaNoPhoto.setVisibility(View.VISIBLE);
				}
			}
		} else{
			PlaceHistory placeHistory = mHistories.get(position - DEFAULT_ROW_COUNT);
			if(placeHistory.normalImg != null) {
				Picasso.with(mContext)
				.load(placeHistory.normalImg)
				.resize(250, 250)
				.error(R.drawable.placeholder)
				.into(target);
				photoLayout.setVisibility(View.VISIBLE);
			}
			if (placeHistory.whoImg != null) {
				Picasso.with(mContext)
				.load(placeHistory.whoImg)
				.resize(100, 100)
				.centerInside()
				.placeholder(R.drawable.user_placeholder)
				.error(R.drawable.user_placeholder)
				.into(avatar);
			}
			izena.setText(placeHistory.who);
			if (placeHistory.comment != null && !placeHistory.comment.trim().equals("")) {
				iruzkina.setText(placeHistory.comment);
				iruzkinLayout.setVisibility(View.VISIBLE);
			}
			if (placeHistory.when != null) {
				DateTime dt = new DateTime(Long.valueOf(placeHistory.when) * 1000);
				DateTime fixedDate = dt.plusHours(2);
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:ss:mm");
				noiz.setText(fixedDate.toString(fmt));
			}
			switch (placeHistory.type) {
			case 1:
				ekintza.setText("Iruzkina egin du");
				break;
			case 2:
				ekintza.setText("Argazkia igo du");
				break;
			case 3:
				ekintza.setText("Check-in egin du");
				break;
			}
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		if (position >= 3) {
			return 3; // > 3 default view
		} else {
			return position;
		}
	}

	@Override
	public int getCount() {
		return DEFAULT_ROW_COUNT + mHistories.size();
	}

	@Override
	public PlaceHistory getItem(int position) {
		if ((position - DEFAULT_ROW_COUNT) > 0) {
			return mHistories.get(position - DEFAULT_ROW_COUNT);
		} else {
			if (mHistories.size() > 0) {
				return mHistories.get(0);
			} else {
				return null;
			}
		}
	}

	public Place getPlaceItem() {
		return mLekua;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void inflateImageLayout(Context context, TableLayout layout, int numBadges) {
		int rowCount = 0;
		TableRow tableRow = new TableRow(context); 	
		for(int i = 0; i < numBadges; i++) {
			if(rowCount == 5){
				layout.addView(tableRow);
				tableRow = new TableRow(context);
				rowCount=0;
			}
			View view = LayoutInflater.from(context).inflate(R.layout.row_place_image, null);
			if (mArgazkiak != null && mArgazkiak.size() > i && mArgazkiak.get(i) != null) {
				ImageView image = (ImageView) view.findViewById(R.id.placeAvatar);
				image.setImageBitmap(mArgazkiak.get(i));
			} else {
				LinearLayout imageLayout = (LinearLayout)view.findViewById(R.id.imageLayout);
				imageLayout.setVisibility(View.INVISIBLE);
			}
			tableRow.addView(view);
			rowCount++;
		}
		layout.addView(tableRow);
	}

	public void addItemList(ArrayList<PlaceHistory> histories) {
		for(PlaceHistory placeHistory : histories) {
			mHistories.add(placeHistory);
			notifyDataSetChanged();
		}
	}

	public void clear() {
		this.mHistories.clear();
		notifyDataSetChanged();
	}
	
	private void changeToProfileFragment(Long friendId) {
		Bundle conData = new Bundle();
		conData.putLong("search_result", friendId);
		Intent intent = new Intent(mContext, ProfilaActivity.class);
		intent.putExtras(conData);
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition (R.anim.open_next, R.anim.close_main);
	}
}