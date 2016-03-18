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
    private String[] items = {"�����", "���", "����", "С��", "��С��"};
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
             * ��ҳ��ʼ����
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbProgress.setVisibility(View.VISIBLE);
            }

            /**
             * ��ҳ���ؽ���
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbProgress.setVisibility(View.GONE);
            }

            /**
             * ������ת�����Ӷ����ڴ˷����лص�
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // tel:110
                view.loadUrl(url);
                return true;
                // return super.shouldOverrideUrlLoading(view, url);
            }
        });
        //������ҳ
        mWebView.loadUrl(url);
        settings = mWebView.getSettings();
        //��ʼ�������С
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

        settings.setJavaScriptEnabled(true);// ��ʾ֧��js
        settings.setBuiltInZoomControls(true);// ��ʾ�Ŵ���С��ť
        settings.setUseWideViewPort(true);// ֧��˫������
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
     * ��������Ի���
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ѡ������");
        int defaultId = sp.getInt("font", 2);
        builder.setSingleChoiceItems(items, defaultId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentChoice = which;
            }
        });
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
                //��������
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("font", currentChoice);
                editor.commit();
            }
        });
        builder.show();
    }

    /**
     * һ������
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //��������
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        //�ر�sso��Ȩ
        oks.disableSSOWhenAuthorize();
        // ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
        oks.setTitle(getString(R.string.share));
        // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
        oks.setTitleUrl("http://sharesdk.cn");
        // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
        oks.setText("����һ�°�");
        // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
        oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
        // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
        oks.setUrl("http://sharesdk.cn");
        // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
        oks.setComment("����һ����");
        // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
        oks.setSite(getString(R.string.app_name));
        // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
        oks.setSiteUrl("http://sharesdk.cn");
        // ��������GUI
        oks.show(this);
    }
}
