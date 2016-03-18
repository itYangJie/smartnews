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
    private int downY;//����ʱy����
    private View headerView;
    private OnRefreshingListener listener = null;
    private ImageView iv_arrow;
    private ProgressBar pb_rotate;
    private TextView tv_state, tv_time;
    private View footerView;
    private final int PULL_REFRESH = 0;//����ˢ�µ�״̬
    private final int RELEASE_REFRESH = 1;//�ɿ�ˢ�µ�״̬
    private final int REFRESHING = 2;//����ˢ�µ�״̬
    private int currentState = PULL_REFRESH;
    //����Ч��
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
     * Ϊlistview ���headerview
     */
    private void initView() {
        initHeader();
        initFooterView();
        setOnScrollListener(this);
    }

    /**
     * ��ʼ��headerview
     */
    private void initHeader() {
        //�Ӳ����ļ��м���headerview
        headerView = View.inflate(getContext(), R.layout.header_lrefreshlist, null);

        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        pb_rotate = (ProgressBar) headerView.findViewById(R.id.pb_rotate);
        tv_state = (TextView) headerView.findViewById(R.id.tv_state);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);
        //֪ͨϵͳ����
        headerView.measure(0, 0);
        //������headerview�ĸ߶�
        headerViewHeight = headerView.getMeasuredHeight();
        //Ĭ������Ӱ��headerview
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        this.addHeaderView(headerView);
    }

    /**
     * �����¼�����
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //��¼����ʱ��������
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //����״̬���ܻ���
                if (currentState == REFRESHING) {
                    break;
                }
                //�����ƶ��ľ���
                int moveY = (int) (ev.getY()) - downY;
                //�����paddingTopֵ
                int paddingTop = moveY - headerViewHeight;
                //����headerview��ȫ���غ���Ȼ���������ƶ�
                if (paddingTop > -headerViewHeight && getFirstVisiblePosition() == 0) {
                    headerView.setPadding(0, paddingTop, 0, 0);
                    if (paddingTop > 0 && currentState == PULL_REFRESH) {
                        //�ı�״̬Ϊ����ˢ��
                        currentState = RELEASE_REFRESH;
                        changeHeaderView();
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                        //�ı�״̬Ϊ����ˢ��
                        currentState = PULL_REFRESH;
                        changeHeaderView();
                    }

                    return true;//����TouchMove������listview����ô�move�¼�,�����listview�޷�����
                }

                break;

            case MotionEvent.ACTION_UP:
                //�ж�״̬
                if (currentState == PULL_REFRESH) {
                    //������ˢ��״̬������ ��ȫ����
                    headerView.setPadding(0, -headerViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    //������ˢ��״̬�����֣��������״̬
                    currentState = REFRESHING;
                    //��ȫ��ʾheaderview
                    headerView.setPadding(0, 0, 0, 0);
                    changeHeaderView();
                }

                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * ��ʼ����ת����
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
     * �ı�headerview��ʾЧ��
     */
    private void changeHeaderView() {
        switch (currentState) {
            case PULL_REFRESH:
                tv_state.setText("����ˢ��");
                iv_arrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                tv_state.setText("�ɿ�ˢ��");
                iv_arrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                iv_arrow.clearAnimation();//��Ϊ���ϵ���ת�����п���û��ִ����
                iv_arrow.setVisibility(View.INVISIBLE);
                pb_rotate.setVisibility(View.VISIBLE);
                tv_state.setText("����ˢ��...");

                //�ص� ֪ͨ ���������������
                if (listener != null) {
                    listener.onRefreshing();
                }
                break;
        }
    }

    /**
     * �˺����������ã����ݸ�����Ϻ�ı�״̬
     */
    public void changeStateToStart() {
        if (isLoadingMore) {
            //����footerView״̬
            footerView.setPadding(0, -footerViewHeight, 0, 0);
            isLoadingMore = false;
        } else {
            //����headerView״̬
            headerView.setPadding(0, -headerViewHeight, 0, 0);
            currentState = PULL_REFRESH;
            pb_rotate.setVisibility(View.INVISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);
            tv_state.setText("����ˢ��");
            tv_time.setText("���ˢ�£�" + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
        }
    }
    /**
     * ����ˢ�¼�����
     *
     * @param listener
     */
    public void setRefreshingListener(OnRefreshingListener listener) {
        this.listener = listener;
    }

    /**
     * ��¶�����Ľӿ�
     */
    public interface OnRefreshingListener {
        public void onRefreshing();
        public void onLoadingMore();
    }


    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.layout_footer, null);
        footerView.measure(0, 0);//����֪ͨϵͳȥ������view;
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        addFooterView(footerView);
    }

    /**
     * SCROLL_STATE_IDLE:����״̬��������ָ�ɿ�
     * SCROLL_STATE_TOUCH_SCROLL����ָ�������������ǰ���������
     * SCROLL_STATE_FLING�����ٻ������ɿ�
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE
                && getLastVisiblePosition()==(getCount()-1) &&!isLoadingMore){
            isLoadingMore = true;

            footerView.setPadding(0, 0, 0, 0);//��ʾ��footerView
            setSelection(getCount());//��listview���һ����ʾ����

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
