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
        //��һ�ν��벥������
        if( sp.getBoolean("isFirstRead",true)){
            //��ʼ��������
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
        mTts.startSpeaking("��ӭʹ���ǻ����ţ�����Լ��",
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
     * ��ʼ��view
     */
    private void intiView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //�໬�˵�����
        setBehindContentView(R.layout.slidemenu);// ���ò����
        SlidingMenu slidingMenu = getSlidingMenu();// ��ȡ���������
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// ����ȫ������
        slidingMenu.setFadeEnabled(true);//�Ƿ��н���
        slidingMenu.setFadeDegree(0.5f);//���ý������
        // ��������
        slidingMenu.setBehindScrollScale((float) 0.5);
        slidingMenu.setBehindOffset(460);// ����Ԥ����Ļ�Ŀ��

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=54b8bca3");
    }
    /**
     * ��ʼ��FragmEnt
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
    ������back���˳�
     */
    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()���ۺ�ʱ���ã��϶�����2000
            {
                Toast.makeText(MainActivity.this, "�ٰ�һ���˳�Ӧ��", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //�˳�Ӧ���ǽ�SharedPreferences��slideMenuWhich���㣬�´ν���Ӧ��ʱ��ֱ����ʾ����ҳ��
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
     * �õ�SlideMenuFragment
     * @return
     */
    public SlideMenuFragment getSlideMenuFragment(){
        return  (SlideMenuFragment)getSupportFragmentManager().findFragmentByTag("slidMenuFragment");
    }

    /**
     * �õ�MainFragment
     * @return
     */
    public MainFragment getMainFragment(){
        return  (MainFragment)getSupportFragmentManager().findFragmentByTag("mainFragment");
    }
}
