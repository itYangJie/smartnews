package com.ityang.smartnews.pager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ityang.smartnews.MainActivity;
import com.ityang.smartnews.constant.Constant;
import com.ityang.smartnews.domain.NewsData;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/22.
 */
public class NewsCenterPager extends BasePager {

    private NewsData newsData = null;
    private ArrayList<BaseMenuDetailPager> menuPagers = new  ArrayList<BaseMenuDetailPager>();
    public NewsCenterPager(Activity activity) {
        super(activity);
    }
    //保存缓存数据
    private SharedPreferences sp = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);

    /**
     *
     */
    @Override
    public void initDate() {

        btn_menu.setVisibility(View.VISIBLE);
        setEnableSlidemenu(true);
        String cache = sp.getString(Constant.MCATEGORIES_URL,"");

        if (!TextUtils.isEmpty(cache)) {// 如果缓存存在,直接解析数据, 无需访问网路
            praseDate(cache);
        }
        getDateFromServer();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSlideMenu();
            }
        });
    }

    /**
     * 显示侧滑菜单点击后切换页面
     */
    public void setCurrentShow(int position){
        tv_title.setText(newsData.data.get(position).title);
        //切换到组图页面是要展示切换显示的button
        if(position==2){
            btn_photo.setVisibility(View.VISIBLE);
        }else {
            btn_photo.setVisibility(View.GONE);
        }
        fl_content.removeAllViews();
        fl_content.addView(menuPagers.get(position).mRootView);
        menuPagers.get(position).initData();
    }
    /**
     *从服务器获取数据
     */
    private void getDateFromServer(){
        HttpUtils httpUtils  = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Constant.MCATEGORIES_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                praseDate(responseInfo.result);
                // 保存缓存数据
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.MCATEGORIES_URL, responseInfo.result);
                editor.commit();
            }
            @Override
            public void onFailure(HttpException e, String s) {
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
        newsData = new Gson().fromJson(result,NewsData.class);

        MainActivity uiActivity = (MainActivity)mActivity;
        //传递数据给侧滑菜单
        uiActivity.getSlideMenuFragment().getDateFromNewsPager(newsData);

        menuPagers.add(new NewsMenuDetailPager(mActivity, newsData.data.get(0).children));
        menuPagers.add(new TopicMenuDetailPager(mActivity));
        menuPagers.add(new PhotoMenuDetailPager(mActivity,btn_photo));
        menuPagers.add(new InteractMenuDetailPager(mActivity));
        //获曲上次选择slidemenu的哪一项,默认显示这一项，默认展示新闻详情页
        int slideMenuWhich = sp.getInt("slideMenuWhich",0);
        setCurrentShow(slideMenuWhich);
    }
}
