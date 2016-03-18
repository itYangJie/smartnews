package com.ityang.smartnews.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * èœå•è¯¦æƒ…é¡?-äº’åŠ¨
 * 
 * @author Kevin
 * 
 */
public class InteractMenuDetailPager extends BaseMenuDetailPager {

	public InteractMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initViews() {
		TextView text = new TextView(mActivity);
		text.setText("²Ëµ¥ÏêÇéÒ³-»¥¶¯");
		text.setTextColor(Color.RED);
		text.setTextSize(25);
		text.setGravity(Gravity.CENTER);

		return text;
	}

	@Override
	public void initData() {
		super.initData();
	}
}
