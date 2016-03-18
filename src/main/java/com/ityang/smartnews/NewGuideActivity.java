package com.ityang.smartnews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class NewGuideActivity extends Activity {

    private static final String TAG ="NewGuideActivity" ;
    private ViewPager newguide_vp;
    private Button newguide_btn;
    //????dots??linearlayout
    private LinearLayout ll_dots;
    private int imageIds [] = {R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3};
    private RelativeLayout rl_dots;
    private View dotRed; //????????????§³???
    private int dotWidth;
    private SharedPreferences sp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initDate();
        initListener();
    }


    /**
     * ?????view
     */
    private void initView() {
        setContentView(R.layout.activity_new_guide);
        newguide_vp = (ViewPager)findViewById(R.id.newguide_vp);
        newguide_btn = (Button)findViewById(R.id.newguide_btn);
        ll_dots = (LinearLayout)findViewById(R.id.ll_dots);
        //??????????????
        ll_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dotWidth = ll_dots.getChildAt(1).getLeft() - ll_dots.getChildAt(0).getLeft();
                ll_dots.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.i(TAG, dotWidth + "");
            }
        });
        rl_dots = (RelativeLayout)findViewById(R.id.rl_dots);
        //???????button
        newguide_btn.setEnabled(false);
        newguide_btn.setVisibility(View.INVISIBLE);

        sp = getSharedPreferences("config",MODE_PRIVATE);
    }

    /**
     * ?????????
     */
    private void initDate() {
        //?vp??????????
        newguide_vp.setAdapter(new MyPageAdapter());

        //??????????????dots
        for(int i=0;i<imageIds.length;i++){
            View dot = new View(NewGuideActivity.this);
            //????dot????
            LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(25,25);
            //????dot?????
            dot.setBackgroundResource(R.drawable.shape_newguide_dot);

            if(i>0){
                params.leftMargin=25;
            }
            dot.setLayoutParams(params);
            ll_dots.addView(dot);
        }
        //??????
         dotRed = new View(NewGuideActivity.this);
        //????dot????
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(30,30);
        //????dot?????
        dotRed.setBackgroundResource(R.drawable.shape_newguide_dotred);
        dotRed.setLayoutParams(params);
        rl_dots.addView(dotRed);
    }

    /**
     * ???????????
     */
    private void initListener() {
        //?vp???¨¹??????
        newguide_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //§³????????????
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)dotRed.getLayoutParams();
                params.leftMargin=(int)(dotWidth * positionOffset+position * dotWidth);
                dotRed.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == imageIds.length - 1) {
                    //???btn
                    newguide_btn.setEnabled(true);
                    newguide_btn.setVisibility(View.VISIBLE);
                } else {
                    //????btn
                    newguide_btn.setEnabled(false);
                    newguide_btn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /**
         * ??????????ï…?????????sharedpreference
         */
        newguide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("isFirstUse",false);
                edit.commit();

                //??????????
                startActivity(new Intent(NewGuideActivity.this, MainActivity.class));
                finish();

            }
        });
    }


    class MyPageAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return imageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView image = new ImageView(NewGuideActivity.this);
            image.setBackgroundResource(imageIds[position]);
            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
