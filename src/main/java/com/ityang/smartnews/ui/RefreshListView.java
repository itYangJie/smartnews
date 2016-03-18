package com.ityang.smartnews.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ityang.smartnews.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/25.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private int headerViewHeight;
    private int downY;//按下时y坐标
    private View headerView;
    private OnRefreshingListener listener = null;
    private ImageView iv_arrow;
    private ProgressBar pb_rotate;
    private TextView tv_state, tv_time;
    private View footerView;
    private final int PULL_REFRESH = 0;//下拉刷新的状态
    private final int RELEASE_REFRESH = 1;//松开刷新的状态
    private final int REFRESHING = 2;//正在刷新的状态
    private int currentState = PULL_REFRESH;
    //动画效果
    private Animation upAnimation;
    private Animation downAnimation;
    private int footerViewHeight;
    private boolean isLoadingMore=false;

    public RefreshListView(Context context) {
        super(context);
        initView();
        initRotateAnimation();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initRotateAnimation();
    }

    /**
     * 为listview 添加headerview
     */
    private void initView() {
        initHeader();
        initFooterView();
        setOnScrollListener(this);
    }

    /**
     * 初始化headerview
     */
    private void initHeader() {
        //从布局文件中加载headerview
        headerView = View.inflate(getContext(), R.layout.header_lrefreshlist, null);

        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        pb_rotate = (ProgressBar) headerView.findViewById(R.id.pb_rotate);
        tv_state = (TextView) headerView.findViewById(R.id.tv_state);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        //通知系统测量
        headerView.measure(0, 0);
        //测量出headerview的高度
        headerViewHeight = headerView.getMeasuredHeight();
        //默认设置影藏headerview
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        this.addHeaderView(headerView);
    }

    /**
     * 触摸事件处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //记录按下时的纵坐标
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //更新状态不能滑动
                if (currentState == REFRESHING) {
                    break;
                }
                //计算移动的距离
                int moveY = (int) (ev.getY()) - downY;
                //计算出paddingTop值
                int paddingTop = moveY - headerViewHeight;
                //避免headerview完全隐藏后任然继续往上移动
                if (paddingTop > -headerViewHeight && getFirstVisiblePosition() == 0) {
                    headerView.setPadding(0, paddingTop, 0, 0);
                    if (paddingTop > 0 && currentState == PULL_REFRESH) {
                        //改变状态为松手刷新
                        currentState = RELEASE_REFRESH;
                        changeHeaderView();
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                        //改变状态为下拉刷新
                        currentState = PULL_REFRESH;
                        changeHeaderView();
                    }

                    return true;//拦截TouchMove，不让listview处理该次move事件,会造成listview无法滑动
                }

                break;

            case MotionEvent.ACTION_UP:
                //判断状态
                if (currentState == PULL_REFRESH) {
                    //在下拉刷新状态下松手 完全隐藏
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    //在松手刷新状态下松手，进入更新状态
                    currentState = REFRESHING;
                    //完全显示headerview
                    headerView.setPadding(0, 0, 0, 0);
                    changeHeaderView();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 初始化旋转动画
     */
    private void initRotateAnimation() {
        upAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);
        downAnimation = new RotateAnimation(-180, -360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
    }

    /**
     * 改变headerview显示效果
     */
    private void changeHeaderView() {
        switch (currentState) {
            case PULL_REFRESH:
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                iv_arrow.clearAnimation();//因为向上的旋转动画有可能没有执行完
                iv_arrow.setVisibility(View.INVISIBLE);
                pb_rotate.setVisibility(View.VISIBLE);
                tv_state.setText("正在刷新...");

                //回调 通知 向服务器请求数据
                if (listener != null) {
                    listener.onRefreshing();
                }
                break;
        }
    }

    /**
     * 此函数由外界调用，数据更新完毕后改变状态
     */
    public void changeStateToStart() {
        if (isLoadingMore) {
            //重置footerView状态
            footerView.setPadding(0, -footerViewHeight, 0, 0);
            isLoadingMore = false;
        } else {
            //重置headerView状态
            headerView.setPadding(0, -headerViewHeight, 0, 0);
            currentState = PULL_REFRESH;
            pb_rotate.setVisibility(View.INVISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            tv_state.setText("下拉刷新");
            tv_time.setText("最后刷新：" + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
        }
    }
    /**
     * 设置刷新监听器
     *
     * @param listener
     */
    public void setRefreshingListener(OnRefreshingListener listener) {
        this.listener = listener;
    }

    /**
     * 暴露给外界的接口
     */
    public interface OnRefreshingListener {
        public void onRefreshing();
        public void onLoadingMore();
    }


    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.layout_footer, null);
        footerView.measure(0, 0);//主动通知系统去测量该view;
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        addFooterView(footerView);
    }

    /**
     * SCROLL_STATE_IDLE:闲置状态，就是手指松开
     * SCROLL_STATE_TOUCH_SCROLL：手指触摸滑动，就是按着来滑动
     * SCROLL_STATE_FLING：快速滑动后松开
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE
                && getLastVisiblePosition()==(getCount()-1) &&!isLoadingMore){
            isLoadingMore = true;

            footerView.setPadding(0, 0, 0, 0);//显示出footerView
            setSelection(getCount());//让listview最后一条显示出来

            if(listener!=null){
                listener.onLoadingMore();
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    OnItemClickListener mItemClickListener;
    @Override
    public void setOnItemClickListener(
            android.widget.AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener(this);

        mItemClickListener = listener;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(parent, view, position
                    - getHeaderViewsCount(), id);
        }
    }


}
