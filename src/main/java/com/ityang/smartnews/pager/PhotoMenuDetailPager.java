package com.ityang.smartnews.pager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ityang.smartnews.R;
import com.ityang.smartnews.constant.Constant;
import com.ityang.smartnews.domain.PhotosData;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 *
 * 
 * @author Kevin
 * 
 */
public class PhotoMenuDetailPager extends BaseMenuDetailPager {

	private ImageButton btn_photo;
	private ListView listView;
	private GridView gridView;
	private boolean isListDisplay=true;
	//保存缓存数据
	private SharedPreferences sp = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
	private PhotosData photosData;
	private ArrayList<PhotosData.PhotoInfo> mPhotoList;
	private MyAdapter adapter;
	private ProgressBar pb_progress;

	public PhotoMenuDetailPager(Activity activity, ImageButton btn_photo) {
		super(activity);
		this.btn_photo = btn_photo;
	}

	@Override
	public View initViews() {
		View view = View.inflate(mActivity, R.layout.menu_photo_pager,null);
		pb_progress = (ProgressBar)view.findViewById(R.id.pb_progress);
		listView = (ListView)view.findViewById(R.id.lv_photo);
		gridView = (GridView)view.findViewById(R.id.gv_photo);
		return view;
	}

	/**
	 * 切换显示方式
	 */
	private void changeShowType() {
		if (isListDisplay) {
            //变为gridview显示
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            btn_photo.setImageResource(R.drawable.icon_pic_list_type);
            isListDisplay = false;
        } else {
            //变为listview显示
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            btn_photo.setImageResource(R.drawable.icon_pic_grid_type);
            isListDisplay = true;
        }
	}

	/**
	 * 初始化数据
	 */
	@Override
	public void initData() {
		//切换视图
		btn_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeShowType();
			}
		});
		String cacheData = sp.getString(Constant.PHOTOS_URL, "");
		if(!TextUtils.isEmpty(cacheData)){
			praseDate(cacheData);
		}
		getDateFromServer();
	}

	/**
	 *从服务器获取数据
	 */
	private void getDateFromServer(){
		pb_progress.setVisibility(View.VISIBLE);
		HttpUtils httpUtils  = new HttpUtils();
		httpUtils.send(HttpRequest.HttpMethod.GET, Constant.PHOTOS_URL, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				praseDate(responseInfo.result);
				// 保存缓存数据
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(Constant.PHOTOS_URL, responseInfo.result);
				editor.commit();
				pb_progress.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onFailure(HttpException e, String s) {
				pb_progress.setVisibility(View.INVISIBLE);
				Toast.makeText(mActivity, "对不起，网络连接失败", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		});
	}

	/**
	 * 解析json数据
	 * @param result
	 */
	private void praseDate(String result) {
		photosData = new Gson().fromJson(result,PhotosData.class);
		mPhotoList = photosData.data.news;// 获取组图列表集合
		//判断是否取到数据
		if(mPhotoList!=null){
			if(adapter==null){
				adapter = new MyAdapter();
				listView.setAdapter(adapter);
				gridView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 数据适配器
	 */
	class MyAdapter extends BaseAdapter{
		BitmapUtils utils;
		public MyAdapter(){
			utils = new BitmapUtils(mActivity);
		}
		@Override
		public int getCount() {
			return mPhotoList.size();
		}
		@Override
		public PhotosData.PhotoInfo getItem(int position) {
			return mPhotoList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				convertView = View.inflate(mActivity,R.layout.list_photo_item,null);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.iv_pic);
				holder.tv = (TextView) convertView.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(getItem(position).title);
			//网络加载图片
			utils.display(holder.img,getItem(position).listimage);
			return convertView;
		}
	}

	class ViewHolder{
		ImageView img;
		TextView tv;
	}
}
