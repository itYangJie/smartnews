package com.ityang.smartnews.pager;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2015/8/23.
 */
public abstract  class BaseMenuDetailPager {
    public Activity mActivity;

    public View mRootView;// �����ֶ���

    public BaseMenuDetailPager(Activity activity) {
        mActivity = activity;
        mRootView = initViews();
    }

    /**
     * ��ʼ������
     */
    public abstract View initViews();

    /**
     * ��ʼ������
     */
    public void initData() {

    }
}
