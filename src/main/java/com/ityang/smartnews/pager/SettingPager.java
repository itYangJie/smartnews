package com.ityang.smartnews.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.ityang.smartnews.R;
import com.ityang.smartnews.VoiceChatActivity;

/**
 * Created by Administrator on 2015/8/22.
 */
public class SettingPager extends BasePager {

    private View myView;
    private Button voice_chat;

    public SettingPager(Activity activity) {
        super(activity);
    }

    /**
     * 初始化数�?
     */
    @Override
    public void initDate() {
        tv_title.setText("�߼�����");
        btn_menu.setVisibility(View.INVISIBLE);
        setEnableSlidemenu(false);

        myView = View.inflate(mActivity, R.layout.setting_pager,null);
        voice_chat = (Button)myView.findViewById(R.id.voice_chat);
        voice_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //����������������˽���
                Intent intent = new Intent(mActivity, VoiceChatActivity.class);
                mActivity.startActivity(intent);
            }
        });
        fl_content.addView(myView);
    }
}
