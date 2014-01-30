package com.irontec.fragments;

import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;

import android.R.mipmap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.irontec.adapters.SimpleLicenseAdapter;
import com.irontec.api.MintzatuAPI;
import com.irontec.mintzatu.EzarpenakDetailActivity;
import com.irontec.mintzatu.HoniBuruzActivity;
import com.irontec.mintzatu.R;


public class EzarpenakFragment extends SherlockFragment {

	private Context mContext;
	private ViewSwitcher mViewSwitcher;
	private ListView mEzarpenZerrenda;
	private ArrayList<BasicNameValuePair> mItems;

	public EzarpenakFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		mContext = getActivity().getBaseContext();

		View rootView = inflater.inflate(R.layout.fragment_ezarpenak, container, false);
		mViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher1);
		mEzarpenZerrenda = (ListView) rootView.findViewById(R.id.ezarpenak_list);

		mItems = new ArrayList<BasicNameValuePair>();

		BasicNameValuePair item1 = new BasicNameValuePair("Saioa itxi", "");
		BasicNameValuePair item2 = new BasicNameValuePair("Sare sozialak", "Zure sare sozialak kudeatu");
		BasicNameValuePair item3 = new BasicNameValuePair("Lizentziak", "Erabilitako baliabideak");
		BasicNameValuePair item4 = new BasicNameValuePair("Honi buruz", "");

		mItems.add(item1);
		mItems.add(item2);
		mItems.add(item3);
		mItems.add(item4);
		
		mEzarpenZerrenda.setAdapter(new SimpleLicenseAdapter(mContext, mItems));
		mEzarpenZerrenda.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Intent intent = null;
				if (position == 0) {
					MintzatuAPI.logout(mContext);
					intent = new Intent(getActivity().getBaseContext(), LoginCircles.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					if (getActivity() != null) {
						getActivity().finish();
					}
				} else  if (position == 3) {
					intent = new Intent(mContext, HoniBuruzActivity.class);
				} else {
					intent = new Intent(mContext, EzarpenakDetailActivity.class);
				}
				
				switch (position) {
				case 1:
					intent.putExtra("detail_type", 0);
					break;
				case 2:
					intent.putExtra("detail_type", 1);
					break;
				default:
					break;
				}
				
				if (intent != null) {
					startActivity(intent);
				}
			}
		});

		mViewSwitcher.showNext();
		
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.lagunak, menu);
	}

}
