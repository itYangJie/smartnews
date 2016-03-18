package com.ityang.smartnews.pager;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ityang.smartnews.MainActivity;
import com.ityang.smartnews.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by Administrator on 2015/8/22.
 */
public class BasePager {

    public Activity mActivity;
    public View rootView;
    public ImageButton btn_menu;
    public TextView tv_title;
    public FrameLayout fl_content;
    public ImageButton btn_photo;
    public BasePager(Activity activity) {
        mActivity = activity;

        initView();
    }


    private void initView() {
        rootView = View.inflate(mActivity, R.layout.basepager, null);
        btn_menu = (ImageButton) rootView.findViewById(R.id.btn_menu);
        btn_photo=(ImageButton)rootView.findViewById(R.id.btn_photo);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
    }

    public void initDate() {


    }

    /**
     * ���ô�ҳ���Ƿ�����ò໬�˵�
     *
     * @param isEnable
     */
    public void setEnableSlidemenu(boolean isEnable) {
        MainActivity uiActivity = (MainActivity) mActivity;
        SlidingMenu slidingMenu = uiActivity.getSlidingMenu();
        if (isEnable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    /**
     * �򿪻�رղ໬�˵�
     */
    public void openSlideMenu(){
        MainActivity uiActivity = (MainActivity) mActivity;
        SlidingMenu slidingMenu = uiActivity.getSlidingMenu();

        slidingMenu.showMenu();
    }


}
