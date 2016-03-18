package com.ityang.smartnews.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/8/22.
 */
public class HoriViewPager extends ViewPager {
    public HoriViewPager(Context context) {
        super(context);
    }

    public HoriViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentItem() != 0) {
            //
            getParent().requestDisallowInterceptTouchEvent(true);
        }else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }
}
