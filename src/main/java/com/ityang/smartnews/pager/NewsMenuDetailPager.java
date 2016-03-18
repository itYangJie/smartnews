package com.ityang.smartnews.pager;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ityang.smartnews.MainActivity;
import com.ityang.smartnews.R;
import com.ityang.smartnews.domain.NewsData;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * 菜单详情�?-新闻
 *
 * @author Kevin
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private int currentItem;
    private ImageButton btn_next;
    private TabPageIndicator indicator;
    private ArrayList<TabDetailPager> mPagerList;

    private ArrayList<NewsData.NewsTabData> mNewsTabData;// ҳǩ��������

    public NewsMenuDetailPager(Activity activity,
                               ArrayList<NewsData.NewsTabData> children) {
        super(activity);

        mNewsTabData = children;
    }

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.news_menu_detail, null);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_menu_detail);
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        btn_next = (ImageButton) view.findViewById(R.id.btn_next);
        //���û�������
        indicator.setOnPageChangeListener(this);
        //�л�����һ��ҳǩ
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentItem = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(++currentItem);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        mPagerList = new ArrayList<TabDetailPager>();

        // ��ʼ��ҳǩ����
        for (int i = 0; i < mNewsTabData.size(); i++) {
            TabDetailPager pager = new TabDetailPager(mActivity, mNewsTabData.get(i),i);
            mPagerList.add(pager);
        }

        mViewPager.setAdapter(new MenuDetailAdapter());
        indicator.setViewPager(mViewPager);// ��viewpager��mIndicator��������,������viewpager������adapter����ܵ���
    }

    class MenuDetailAdapter extends PagerAdapter {
        /**
         * ��д�˷���,����ҳ�����,����viewpagerIndicator��ҳǩ��ʾ
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mNewsTabData.get(position).title;
        }

        @Override
        public int getCount() {
            return mPagerList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagerList.get(position);
            container.addView(pager.mRootView);
            //��ʼ�����ݣ���Ҫ�Ķ�
            pager.initData();
            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MainActivity uiActivity = (MainActivity) mActivity;
        SlidingMenu slidingMenu = uiActivity.getSlidingMenu();
        if (position == 0) { //ֻ���ڵ�һ��ҳ����������໬�˵�
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
