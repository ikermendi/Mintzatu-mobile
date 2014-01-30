package com.irontec.buttons;

import com.irontec.mintzatu.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BadgeButton extends RelativeLayout {
	
	private Context mContext;
	private Button mButton;
	private TextView mBadgeNumberView;
	
	public BadgeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeButton);
		
		int buttonStyle = a.getResourceId(R.styleable.BadgeButton_style, -1);
		if (buttonStyle != -1) {
			mButton = new Button(mContext, null, buttonStyle);
		} else {
			mButton = new Button(mContext);
		}
		
		mButton.setId(5);
		mButton.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		LayoutParams params = new LayoutParams(a.getDimensionPixelSize(R.styleable.BadgeButton_width, 0), a.getDimensionPixelSize(R.styleable.BadgeButton_bheight, 0));
		mButton.setLayoutParams(params);
		mButton.setPadding(10, 0, 10, 0);
		mButton.setText(a.getString(R.styleable.BadgeButton_text));
		addView(mButton);
		
		String number = a.getString(R.styleable.BadgeButton_number);
		if (number != null) {
			addBadgeView(number);
		}
		
		a.recycle();
	}
	
	@SuppressWarnings("deprecation")
	private void addBadgeView(String number) {
		FrameLayout badgeLayout = new FrameLayout(mContext);
		badgeLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.blue_badge));
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_RIGHT, mButton.getId());
		params.setMargins(0, 0, 10, 0);
		badgeLayout.setLayoutParams(params);
		badgeLayout.setPadding(5, 5, 5, 5);
		mBadgeNumberView = new TextView(mContext);
		mBadgeNumberView.setText(number);
		badgeLayout.addView(mBadgeNumberView);
		addView(badgeLayout);	
	}
	
	public void setBadgeNumber(int number) {
		mBadgeNumberView.setText(String.valueOf(number));
	}
}
