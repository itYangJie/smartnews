package com.ityang.smartnews.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/8/25.
 */
public class TopNewsViewPager extends ViewPager {
    private int startX;
    private int startY;
    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //首先请求父控件不要拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                startX =(int) ev.getRawX();
                startY =(int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int)ev.getRawX();
                int endY =(int) ev.getRawY();
                if(Math.abs(endX-startX)>Math.abs(endY-startY)){
                    //左右滑动
                    if(endX>startX){
                        //向右滑动
                        if(getCurrentItem()==0){
                            //在第一个页面，请求父控件拦截处理
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }else {
                        //向左滑动
                        if(getCurrentItem()==getAdapter().getCount()-1){
                            //在最后一个页面，请求父控件拦截处理
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }else {
                    //上下滑动，请求父控件拦截处理
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
