package com.ityang.smartnews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SplashActivity extends Activity {

    private RelativeLayout splash_bg;
    private TextView splash_tv;
    private SharedPreferences sp;
    private ImageView splash_img ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        startAnimation();
    }


    /**
     * ��ʼ��view
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        //MyApplication application = (MyApplication) getApplication();

        splash_bg = (RelativeLayout)findViewById(R.id.splash_bg);
        splash_tv = (TextView)findViewById(R.id.splash_tv);
        splash_img = (ImageView)findViewById(R.id.splash_img);
        sp = getSharedPreferences("config",MODE_PRIVATE);
    }

    /**
     * ��������Ч��
     */
    private void startAnimation() {
        AlphaAnimation alpha = new AlphaAnimation(0.0f,1.0f);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);
        splash_bg.startAnimation(alpha);

        TranslateAnimation ta_tv = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -0.45f);
        ta_tv.setDuration(1200);
        ta_tv.setFillAfter(true);
        splash_tv.startAnimation(ta_tv);

        TranslateAnimation ta_img = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.6f);
        ta_img.setDuration(1200);
        ta_img.setFillAfter(true);
        splash_img.startAnimation(ta_img);

        //���ö���������
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //��������,�ж��Ƿ��ǵ�һ�δ�Ӧ�ã�������������������ҳ
                boolean isFirstUse = sp.getBoolean("isFirstUse",true);
                if(isFirstUse){
                    startActivity(new Intent(SplashActivity.this,NewGuideActivity.class));
                    finish();
                }else {
                    //����������
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


}
