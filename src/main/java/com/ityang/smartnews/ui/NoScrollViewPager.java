package com.ityang.smartnews.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/8/22.
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
    // ��ʾ�¼��Ƿ�����, ����false��ʾ������
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }

}
