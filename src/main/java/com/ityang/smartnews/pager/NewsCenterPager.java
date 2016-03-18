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
    //���滺������
    private SharedPreferences sp = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);

    /**
     *
     */
    @Override
    public void initDate() {

        btn_menu.setVisibility(View.VISIBLE);
        setEnableSlidemenu(true);
        String cache = sp.getString(Constant.MCATEGORIES_URL,"");

        if (!TextUtils.isEmpty(cache)) {// ����������,ֱ�ӽ�������, ���������·
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
     * ��ʾ�໬�˵�������л�ҳ��
     */
    public void setCurrentShow(int position){
        tv_title.setText(newsData.data.get(position).title);
        //�л�����ͼҳ����Ҫչʾ�л���ʾ��button
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
     *�ӷ�������ȡ����
     */
    private void getDateFromServer(){
        HttpUtils httpUtils  = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Constant.MCATEGORIES_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                praseDate(responseInfo.result);
                // ���滺������
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constant.MCATEGORIES_URL, responseInfo.result);
                editor.commit();
            }
            @Override
            public void onFailure(HttpException e, String s) {
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
        newsData = new Gson().fromJson(result,NewsData.class);

        MainActivity uiActivity = (MainActivity)mActivity;
        //�������ݸ��໬�˵�
        uiActivity.getSlideMenuFragment().getDateFromNewsPager(newsData);

        menuPagers.add(new NewsMenuDetailPager(mActivity, newsData.data.get(0).children));
        menuPagers.add(new TopicMenuDetailPager(mActivity));
        menuPagers.add(new PhotoMenuDetailPager(mActivity,btn_photo));
        menuPagers.add(new InteractMenuDetailPager(mActivity));
        //�����ϴ�ѡ��slidemenu����һ��,Ĭ����ʾ��һ�Ĭ��չʾ��������ҳ
        int slideMenuWhich = sp.getInt("slideMenuWhich",0);
        setCurrentShow(slideMenuWhich);
    }
}
