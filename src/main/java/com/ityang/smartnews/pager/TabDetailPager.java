package com.ityang.smartnews.pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ityang.smartnews.NewsReadActivity;
import com.ityang.smartnews.R;
import com.ityang.smartnews.constant.Constant;
import com.ityang.smartnews.domain.NewsData;
import com.ityang.smartnews.domain.TabData;
import com.ityang.smartnews.ui.RefreshListView;
import com.ityang.smartnews.ui.TopNewsViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 椤电璇︽儏椤?
 *
 * @author Kevin
 *
 */
public class TabDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

	private static final int FLAG_FIRSTGET =0 ;
	private static final int FLAG_GETMORE = 1;
	private static final int FLAG_REFRESH =2 ;
	private boolean state_more =false ;
	private NewsData.NewsTabData  mTabData;
	private TopNewsViewPager tabTopPager;
	private TabData tabData;
	private BitmapUtils bitMapUtils;
	private ArrayList<TabData.TopNewsData> mTopNewsList;// 澶存潯鏂伴椈鏁版嵁闆嗗悎
	private ArrayList<TabData.TabNewsData> mTabNewsList;
	private RefreshListView tabNewsList;
	private TextView topNew_text; //头条新闻标题
	private CirclePageIndicator indicator;
	private MyBaseAdapter adapter;
	private String mNewsMore = "";//加载更多
	private String mUrl;
	private SharedPreferences sp = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
	private Handler handler;
	private int flag_page;
	private Timer timer;
	private TimerTask task;

	public TabDetailPager(Activity activity, NewsData.NewsTabData newsTabData,int i) {
		super(activity);
		flag_page=i;
		mTabData = newsTabData;
		mUrl=Constant.BASE_URL + mTabData.url;
	}

	@Override
	public View initViews() {

		View view = View.inflate(mActivity, R.layout.tabnews, null);
		tabNewsList = (RefreshListView)view.findViewById(R.id.tabNewsList);
		tabNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewHolder holder = (ViewHolder)view.getTag();
				holder.newsTitle.setTextColor(Color.GRAY);
				Intent intent = new Intent(mActivity, NewsReadActivity.class);
				//携带url
				intent.putExtra("url",mTabNewsList.get(position).url);
				mActivity.startActivity(intent);
			}
		});
		tabNewsList.setRefreshingListener(new RefreshListView.OnRefreshingListener() {
			@Override
			public void onRefreshing() {
				//下拉刷新
				getInfoFromServer(FLAG_REFRESH);
				tabNewsList.changeStateToStart();
			}
			@Override
			public void onLoadingMore() {
				//加载更多
				if(mNewsMore==null|| TextUtils.isEmpty(mNewsMore)){
					//没有更多了
					Toast.makeText(mActivity,"没有更多了，亲",Toast.LENGTH_SHORT).show();
				}else {
					state_more  = true;
					getInfoFromServer(FLAG_GETMORE);
				}
				tabNewsList.changeStateToStart();
			}
		});
		View headerView  = View.inflate(mActivity,R.layout.list_header,null);
		topNew_text =(TextView) headerView.findViewById(R.id.topNew_text);
		indicator = (CirclePageIndicator)headerView.findViewById(R.id.indicator);
		tabTopPager = (TopNewsViewPager)headerView.findViewById(R.id.tabTopPager);

		tabNewsList.addHeaderView(headerView);
		return view;
	}

	@Override
	public void initData() {
		getInfoFromServer(FLAG_FIRSTGET);
	}

	/**
	 * 浠庢湇鍔″櫒鑾峰彇鏁版嵁
	 */
	private void getInfoFromServer(final int flag) {
		HttpUtils httpUtils = new HttpUtils();
		String url;
		if(flag == FLAG_FIRSTGET){
			String cache = sp.getString(mUrl,"");
			if (!TextUtils.isEmpty(cache)) {
				System.out.println("有数据");
				praseDate(cache);
			}else {
				System.out.println("没有数据");
			}
			//第一次加载
			url=Constant.BASE_URL + mTabData.url;
		}else if(flag == FLAG_GETMORE) {
			//加载更多
			url =  Constant.BASE_URL +mNewsMore;
		}else{
			//下拉刷新
			url = Constant.BASE_URL + mTabData.url;
		}
		httpUtils.send(HttpRequest.HttpMethod.GET,url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				praseDate(responseInfo.result);
				// 第一次加载 保存缓存数据
				if(flag == FLAG_FIRSTGET){
					System.out.println("保存缓存数据");
					SharedPreferences.Editor editor = sp.edit();
					editor.putString(mUrl, responseInfo.result);
					editor.commit();
				}
			}
			@Override
			public void onFailure(HttpException e, String s) {
				Toast.makeText(mActivity,"网络无法连接",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		});
	}

	/**
	 * 瑙ｆ瀽鏁版嵁
	 * @param result
	 */
	private void praseDate(String result) {
		Gson gson  = new Gson();
		tabData = gson.fromJson(result, TabData.class);
		if(state_more==true){
			//追加数据
			 mTabNewsList.addAll(tabData.data.news);
			 state_more=false;
			 adapter.notifyDataSetChanged();
		}else {
			mTabNewsList = tabData.data.news;
			//头条新闻集合
			mTopNewsList = tabData.data.topnews;
			if(mTopNewsList!=null){
				//设置头条新闻标题默认内容
				topNew_text.setText(mTopNewsList.get(0).title);
				tabTopPager.setAdapter(new TopNewsAdapter());
				indicator.setViewPager(tabTopPager);
				indicator.setSnap(true);// 支持快照显示
				indicator.setOnPageChangeListener(this);
				indicator.onPageSelected(0);// 让指示器重新定位到第一个点
			}
			if(adapter==null){
				adapter = new MyBaseAdapter();
			}
			tabNewsList.setAdapter(adapter);
			if(handler==null){
				handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						int currentItem = tabTopPager.getCurrentItem();
						if (currentItem < tabTopPager.getAdapter().getCount() - 1) {
							++currentItem;
						} else {
							currentItem = 0;
						}
						//System.out.println("这是item:"+currentItem);
						tabTopPager.setCurrentItem(currentItem);// 切换到下一个页面
						handler.sendEmptyMessageDelayed(0,2000);// 继续延时2秒发消息,

					}
				};
				//开始图片轮播
				if(flag_page==0){
					handler.sendEmptyMessageDelayed(0,2000);
				}
			}
		}
		//加载更多
		mNewsMore = tabData.data.more;
	}



	/**
	 * 新闻列表的适配器
	 *
	 * @author Kevin
	 *
	 */
	class MyBaseAdapter extends BaseAdapter{
		private BitmapUtils utils;
		public  MyBaseAdapter(){
			utils = new BitmapUtils(mActivity);
			utils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
		}
		@Override
		public int getCount() {
			return mTabNewsList.size();
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
			ViewHolder holder;
			if(convertView==null){
				convertView = View.inflate(mActivity,R.layout.list_news_item,null);
				holder = new ViewHolder();
				holder.newsImg  = (ImageView) convertView.findViewById(R.id.iv_pic);
				holder.newsTitle  = (TextView) convertView.findViewById(R.id.tv_title);
				holder.newsTime  = (TextView) convertView.findViewById(R.id.tv_date);
				convertView.setTag(holder);
			}else {
				holder =(ViewHolder) convertView.getTag();
			}
			utils.display(holder.newsImg,mTabNewsList.get(position).listimage);
			holder.newsTitle.setText(mTabNewsList.get(position).title);
			holder.newsTime.setText(mTabNewsList.get(position).pubdate);
			return convertView;
		}
	}

	static class ViewHolder{
		ImageView newsImg;
		TextView newsTitle;
		TextView newsTime;
	}
	/**
	 * 澶存潯鏂伴椈閫傞厤鍣?
	 *
	 * @author Kevin
	 *
	 */
	class TopNewsAdapter extends PagerAdapter {
		public TopNewsAdapter() {
			bitMapUtils = new BitmapUtils(mActivity);
			bitMapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);// 璁剧疆榛樿鍥剧墖
		}
		@Override
		public int getCount() {
			return tabData.data.topnews.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView image = new ImageView(mActivity);
			image.setScaleType(ImageView.ScaleType.FIT_XY);// 鍩轰簬鎺т欢澶у皬濉厖鍥剧墖
			TabData.TopNewsData topNewsData = mTopNewsList.get(position);
			bitMapUtils.display(image,topNewsData.topimage);// 浼犻?抜magView瀵硅薄鍜屽浘鐗囧湴鍧?

			container.addView(image);
			return image;
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
		//改变头条新闻标题
		topNew_text.setText(mTopNewsList.get(position).title);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
