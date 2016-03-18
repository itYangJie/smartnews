package com.ityang.smartnews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.ityang.smartnews.fragment.MainFragment;
import com.ityang.smartnews.fragment.SlideMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class MainActivity extends SlidingFragmentActivity {
    SharedPreferences sp = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intiView();
        initFragment();
        //第一次进入播放语音
        if( sp.getBoolean("isFirstRead",true)){
            //开始播放语音
            startRead();
        }
    }

    private void startRead() {
        SpeechSynthesizer mTts = SpeechSynthesizer
                .createSynthesizer(this, null);

        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "60");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.startSpeaking("欢迎使用智慧新闻，我们约吧",
                mSynthesizerListener);
    }

    private SynthesizerListener mSynthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakResumed() {
            // TODO Auto-generated method stub
        }
        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onSpeakPaused() {
            // TODO Auto-generated method stub
        }
        @Override
        public void onSpeakBegin() {
            // TODO Auto-generated method stub
        }
        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onCompleted(SpeechError arg0) {
            // TODO Auto-generated method stub
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstRead",false);
            editor.commit();
        }
        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
            // TODO Auto-generated method stub
        }
    };
    /**
     * 初始化view
     */
    private void intiView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //侧滑菜单设置
        setBehindContentView(R.layout.slidemenu);// 设置侧边栏
        SlidingMenu slidingMenu = getSlidingMenu();// 获取侧边栏对象
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸
        slidingMenu.setFadeEnabled(true);//是否有渐变
        slidingMenu.setFadeDegree(0.5f);//设置渐变比率
        // 放缩比例
        slidingMenu.setBehindScrollScale((float) 0.5);
        slidingMenu.setBehindOffset(460);// 设置预留屏幕的宽度

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=54b8bca3");
    }
    /**
     * 初始化FragmEnt
     */
    private void initFragment() {
        FragmentManager fm  = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fl_slidemenu, new SlideMenuFragment(), "slidMenuFragment");
        ft.replace(R.id.fl_main, new MainFragment(), "mainFragment");
        ft.commit();
        //fm.findFragmentByTag();
    }
    /*
    按两次back键退出
     */
    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //退出应用是将SharedPreferences的slideMenuWhich清零，下次进入应用时，直接显示新闻页面
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("slideMenuWhich",0);
                editor.commit();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 得到SlideMenuFragment
     * @return
     */
    public SlideMenuFragment getSlideMenuFragment(){
        return  (SlideMenuFragment)getSupportFragmentManager().findFragmentByTag("slidMenuFragment");
    }

    /**
     * 得到MainFragment
     * @return
     */
    public MainFragment getMainFragment(){
        return  (MainFragment)getSupportFragmentManager().findFragmentByTag("mainFragment");
    }
}
