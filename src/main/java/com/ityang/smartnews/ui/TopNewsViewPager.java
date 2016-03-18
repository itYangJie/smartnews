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
                //�������󸸿ؼ���Ҫ����
                getParent().requestDisallowInterceptTouchEvent(true);
                startX =(int) ev.getRawX();
                startY =(int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int)ev.getRawX();
                int endY =(int) ev.getRawY();
                if(Math.abs(endX-startX)>Math.abs(endY-startY)){
                    //���һ���
                    if(endX>startX){
                        //���һ���
                        if(getCurrentItem()==0){
                            //�ڵ�һ��ҳ�棬���󸸿ؼ����ش���
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }else {
                        //���󻬶�
                        if(getCurrentItem()==getAdapter().getCount()-1){
                            //�����һ��ҳ�棬���󸸿ؼ����ش���
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }else {
                    //���»��������󸸿ؼ����ش���
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
