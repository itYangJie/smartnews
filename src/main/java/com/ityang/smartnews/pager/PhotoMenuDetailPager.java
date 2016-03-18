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
	//���滺������
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
	 * �л���ʾ��ʽ
	 */
	private void changeShowType() {
		if (isListDisplay) {
            //��Ϊgridview��ʾ
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            btn_photo.setImageResource(R.drawable.icon_pic_list_type);
            isListDisplay = false;
        } else {
            //��Ϊlistview��ʾ
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            btn_photo.setImageResource(R.drawable.icon_pic_grid_type);
            isListDisplay = true;
        }
	}

	/**
	 * ��ʼ������
	 */
	@Override
	public void initData() {
		//�л���ͼ
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
	 *�ӷ�������ȡ����
	 */
	private void getDateFromServer(){
		pb_progress.setVisibility(View.VISIBLE);
		HttpUtils httpUtils  = new HttpUtils();
		httpUtils.send(HttpRequest.HttpMethod.GET, Constant.PHOTOS_URL, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				praseDate(responseInfo.result);
				// ���滺������
				SharedPreferences.Editor editor = sp.edit();
				editor.putString(Constant.PHOTOS_URL, responseInfo.result);
				editor.commit();
				pb_progress.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onFailure(HttpException e, String s) {
				pb_progress.setVisibility(View.INVISIBLE);
				Toast.makeText(mActivity, "�Բ�����������ʧ��", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		});
	}

	/**
	 * ����json����
	 * @param result
	 */
	private void praseDate(String result) {
		photosData = new Gson().fromJson(result,PhotosData.class);
		mPhotoList = photosData.data.news;// ��ȡ��ͼ�б���
		//�ж��Ƿ�ȡ������
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
	 * ����������
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
			//�������ͼƬ
			utils.display(holder.img,getItem(position).listimage);
			return convertView;
		}
	}

	class ViewHolder{
		ImageView img;
		TextView tv;
	}
}
