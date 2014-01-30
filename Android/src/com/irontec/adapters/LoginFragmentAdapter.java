package com.irontec.adapters;

import com.irontec.fragments.LoginFragment;
import com.irontec.mintzatu.R;
import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class LoginFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	protected static final int[] PAGES = new int[] { 0, 1, 2, 3 };

    private int mCount = PAGES.length;

    public LoginFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return LoginFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return null;
    }

    @Override
    public int getIconResId(int index) {
      return index;
    }
    
//    @Override
//    public void destroyItem(View collection, int position, Object o) {
//        View view = (View)o;
//        ((ViewPager) collection).removeView(view);
//        view = null;
//    }

}