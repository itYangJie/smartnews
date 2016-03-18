package com.ityang.smartnews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

public class NewsReadActivity extends Activity implements View.OnClickListener {

    private WebView mWebView;
    private ImageButton btnBack;
    private ImageButton btnSize;
    private ImageButton btnShare;
    private ProgressBar pbProgress;
    private SharedPreferences sp;
    private String[] items = {"超大号", "大号", "正常", "小号", "超小号"};
    private int currentChoice = 3;
    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initViews();
        initData();
    }

    private void initViews() {
        setContentView(R.layout.activity_news_read);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        mWebView = (WebView) findViewById(R.id.wv_web);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnSize = (ImageButton) findViewById(R.id.btn_size);
        btnSize.setOnClickListener(this);

        btnShare = (ImageButton) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
    }

    private void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        mWebView.setWebViewClient(new WebViewClient() {
            /**
             * 网页开始加载
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbProgress.setVisibility(View.VISIBLE);
            }

            /**
             * 网页加载结束
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbProgress.setVisibility(View.GONE);
            }

            /**
             * 所有跳转的链接都会在此方法中回调
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // tel:110
                view.loadUrl(url);
                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }
        });
        //加载网页
        mWebView.loadUrl(url);
        settings = mWebView.getSettings();
        //初始化字体大小
        switch ( sp.getInt("font", 2)) {
            case 0:
                settings.setTextSize(WebSettings.TextSize.LARGEST);
                break;
            case 1:
                settings.setTextSize(WebSettings.TextSize.LARGER);
                break;
            case 2:
                settings.setTextSize(WebSettings.TextSize.NORMAL);
                break;
            case 3:
                settings.setTextSize(WebSettings.TextSize.SMALLER);
                break;
            case 4:
                settings.setTextSize(WebSettings.TextSize.SMALLEST);
                break;
        }

        settings.setJavaScriptEnabled(true);// 表示支持js
        settings.setBuiltInZoomControls(true);// 显示放大缩小按钮
        settings.setUseWideViewPort(true);// 支持双击缩放
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_size:
                showDialog();
                break;
            case R.id.btn_share:
                showShare();
                break;
        }
    }

    /**
     * 调整字体对话框
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择字体");
        int defaultId = sp.getInt("font", 2);
        builder.setSingleChoiceItems(items, defaultId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentChoice = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (currentChoice) {
                    case 0:
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        break;
                    case 1:
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4:
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                }
                //保存数据
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("font", currentChoice);
                editor.commit();
            }
        });
        builder.show();
    }

    /**
     * 一键分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //设置主题
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("分享一下吧");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论一下呗");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }
}
