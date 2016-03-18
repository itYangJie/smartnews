package com.ityang.smartnews.pager;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2015/8/22.
 */
public class PolticiPager extends BasePager {

    public PolticiPager(Activity activity) {
        super(activity);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initDate() {
        tv_title.setText("政务");
        btn_menu.setVisibility(View.INVISIBLE);
        setEnableSlidemenu(false);


    }
}
