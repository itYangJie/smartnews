package com.ityang.smartnews.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.ityang.smartnews.R;
import com.ityang.smartnews.pager.BasePager;
import com.ityang.smartnews.pager.NewsCenterPager;
import com.ityang.smartnews.pager.PolticiPager;
import com.ityang.smartnews.pager.ServerPager;
import com.ityang.smartnews.pager.SettingPager;
import com.ityang.smartnews.ui.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/21.
 */
public class MainFragment extends BaseFragment {
    private NoScrollViewPager viewPager;
    private RadioGroup rg_group;
    protected List<BasePager>  pagers;
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fg_main,null);
        viewPager = (NoScrollViewPager)view.findViewById(R.id.fg_main_vp);
        rg_group = (RadioGroup)view.findViewById(R.id.rg_group);
        //�����ʼѡ����ҳ
        rg_group.check(R.id.rb_home);
        //����viepager�ĸ���ҳ�����
        pagers = new ArrayList<BasePager>();
        pagers.add(new NewsCenterPager(mActivity));
        pagers.add(new ServerPager(mActivity));
        pagers.add(new PolticiPager(mActivity));
        pagers.add(new SettingPager(mActivity));

        //����ҳǩ����¼�
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        viewPager.setCurrentItem(0,false);
                        break;
                    case R.id.rb_smart:
                        viewPager.setCurrentItem(1,false);
                        break;
                    case R.id.rb_gov:
                        viewPager.setCurrentItem(2,false);
                        break;
                    case R.id.rb_setting:
                        viewPager.setCurrentItem(3,false);
                        break;
                }
            }
        });
        //ҳ��ı�ʱ�ص�
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                //��ʱ��ʼ������
                pagers.get(position).initDate();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void initDate() {
        viewPager.setAdapter(new MyPageAdapter());
        //�ʼҪ��ʼ����ҳҳǩ������
        pagers.get(0).initDate();
    }

    class MyPageAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return pagers.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pagers.get(position).rootView;
            container.addView(view);
            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    /**
     * ��ȡ��������ҳ��
     *
     * @return
     */
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) pagers.get(0);
    }
}
