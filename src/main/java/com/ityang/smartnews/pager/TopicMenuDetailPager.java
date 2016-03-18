package com.ityang.smartnews.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 菜单详情�?-专题
 * 
 * @author Kevin
 * 
 */
public class TopicMenuDetailPager extends BaseMenuDetailPager {

	public TopicMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initViews() {
		TextView text = new TextView(mActivity);
		text.setText("�˵�����ҳ-ר�⢘");
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
