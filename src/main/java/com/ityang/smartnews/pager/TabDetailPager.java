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
 * 页签详情�?
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
	private ArrayList<TabData.TopNewsData> mTopNewsList;// 头条新闻数据集合
	private ArrayList<TabData.TabNewsData> mTabNewsList;
	private RefreshListView tabNewsList;
	private TextView topNew_text; //ͷ�����ű���
	private CirclePageIndicator indicator;
	private MyBaseAdapter adapter;
	private String mNewsMore = "";//���ظ���
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
				//Я��url
				intent.putExtra("url",mTabNewsList.get(position).url);
				mActivity.startActivity(intent);
			}
		});
		tabNewsList.setRefreshingListener(new RefreshListView.OnRefreshingListener() {
			@Override
			public void onRefreshing() {
				//����ˢ��
				getInfoFromServer(FLAG_REFRESH);
				tabNewsList.changeStateToStart();
			}
			@Override
			public void onLoadingMore() {
				//���ظ���
				if(mNewsMore==null|| TextUtils.isEmpty(mNewsMore)){
					//û�и�����
					Toast.makeText(mActivity,"û�и����ˣ���",Toast.LENGTH_SHORT).show();
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
	 * 从服务器获取数据
	 */
	private void getInfoFromServer(final int flag) {
		HttpUtils httpUtils = new HttpUtils();
		String url;
		if(flag == FLAG_FIRSTGET){
			String cache = sp.getString(mUrl,"");
			if (!TextUtils.isEmpty(cache)) {
				System.out.println("������");
				praseDate(cache);
			}else {
				System.out.println("û������");
			}
			//��һ�μ���
			url=Constant.BASE_URL + mTabData.url;
		}else if(flag == FLAG_GETMORE) {
			//���ظ���
			url =  Constant.BASE_URL +mNewsMore;
		}else{
			//����ˢ��
			url = Constant.BASE_URL + mTabData.url;
		}
		httpUtils.send(HttpRequest.HttpMethod.GET,url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				praseDate(responseInfo.result);
				// ��һ�μ��� ���滺������
				if(flag == FLAG_FIRSTGET){
					System.out.println("���滺������");
					SharedPreferences.Editor editor = sp.edit();
					editor.putString(mUrl, responseInfo.result);
					editor.commit();
				}
			}
			@Override
			public void onFailure(HttpException e, String s) {
				Toast.makeText(mActivity,"�����޷�����",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		});
	}

	/**
	 * 解析数据
	 * @param result
	 */
	private void praseDate(String result) {
		Gson gson  = new Gson();
		tabData = gson.fromJson(result, TabData.class);
		if(state_more==true){
			//׷������
			 mTabNewsList.addAll(tabData.data.news);
			 state_more=false;
			 adapter.notifyDataSetChanged();
		}else {
			mTabNewsList = tabData.data.news;
			//ͷ�����ż���
			mTopNewsList = tabData.data.topnews;
			if(mTopNewsList!=null){
				//����ͷ�����ű���Ĭ������
				topNew_text.setText(mTopNewsList.get(0).title);
				tabTopPager.setAdapter(new TopNewsAdapter());
				indicator.setViewPager(tabTopPager);
				indicator.setSnap(true);// ֧�ֿ�����ʾ
				indicator.setOnPageChangeListener(this);
				indicator.onPageSelected(0);// ��ָʾ�����¶�λ����һ����
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
						//System.out.println("����item:"+currentItem);
						tabTopPager.setCurrentItem(currentItem);// �л�����һ��ҳ��
						handler.sendEmptyMessageDelayed(0,2000);// ������ʱ2�뷢��Ϣ,

					}
				};
				//��ʼͼƬ�ֲ�
				if(flag_page==0){
					handler.sendEmptyMessageDelayed(0,2000);
				}
			}
		}
		//���ظ���
		mNewsMore = tabData.data.more;
	}



	/**
	 * �����б��������
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
	 * 头条新闻适配�?
	 *
	 * @author Kevin
	 *
	 */
	class TopNewsAdapter extends PagerAdapter {
		public TopNewsAdapter() {
			bitMapUtils = new BitmapUtils(mActivity);
			bitMapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);// 设置默认图片
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
			image.setScaleType(ImageView.ScaleType.FIT_XY);// 基于控件大小填充图片
			TabData.TopNewsData topNewsData = mTopNewsList.get(position);
			bitMapUtils.display(image,topNewsData.topimage);// 传�?�imagView对象和图片地�?

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
		//�ı�ͷ�����ű���
		topNew_text.setText(mTopNewsList.get(position).title);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
