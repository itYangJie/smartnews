package com.ityang.smartnews.constant;

/**
 * Created by Administrator on 2015/8/23.
 */
public class Constant {

    private Constant(){

    }
    public static final String BASE_URL = "http://192.168.1.101:8080/zhbj";
    public  static final String MCATEGORIES_URL = BASE_URL +"/categories.json";
    public static final String PHOTOS_URL = BASE_URL
            + "/photos/photos_1.json";// 获取组图信息的接口
}
