package com.ityang.smartnews;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/8/27.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("MyApplicationµ÷ÓÃ");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
