package com.ityang.smartnews.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ityang.smartnews.MainActivity;
import com.ityang.smartnews.R;
import com.ityang.smartnews.domain.NewsData;
import com.ityang.smartnews.pager.NewsCenterPager;

/**
 * Created by Administrator on 2015/8/21.
 */
public class SlideMenuFragment extends BaseFragment {
    private ListView meunList  =null;
    private MyBaseAdapter adapter ;
    public int currentPager;
    private NewsData newsData;
    private SharedPreferences sp;
    @Override
    public View initViews() {
        sp = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
        View view =  View.inflate(mActivity,R.layout.fg_slidemenu,null);
        meunList  = (ListView)view.findViewById(R.id.meunList);
        return view;
    }

    @Override
    public void initDate() {
        adapter = new MyBaseAdapter();

    }

    /**
     * 当从服务器获取到数据后调用该方法
     */
    public void  getDateFromNewsPager(NewsData data){
        newsData  = data;
        meunList.setAdapter(adapter);
        /*//设置新闻中心默认显示 新闻 页面
        setCurrentDetailPager(0);*/
        meunList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPager = position;
                adapter.notifyDataSetChanged();
                //此时关闭侧滑菜单
                ((MainActivity)mActivity).getSlidingMenu().toggle();
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("slideMenuWhich",position);
                editor.commit();
                setCurrentDetailPager(position);

            }
        });
    }


    /**
     * 工具侧滑菜单点击选择在新闻中心显示不同页面
     */
    public void setCurrentDetailPager(int position){
        MainActivity uiActivity  = (MainActivity)mActivity;
        MainFragment mainFragment = uiActivity.getMainFragment();
        NewsCenterPager newsCenterPager =  mainFragment.getNewsCenterPager();
        newsCenterPager.setCurrentShow(position);
    }

    class MyBaseAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return newsData.data.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity,R.layout.slidemenu_item,null);
            TextView tv  = (TextView) view.findViewById(R.id.tv_title);
            //设置标题
            tv.setText(newsData.data.get(position).title);
            if(currentPager==position){
                tv.setEnabled(true);    //当前页面则显示为红色
            }else {
                tv.setEnabled(false);//非当前页面则显示为白色
            }
            return view;
        }
    }


}
