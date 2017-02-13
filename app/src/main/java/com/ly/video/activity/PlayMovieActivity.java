package com.ly.video.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ly.video.R;
import com.ly.video.bean.ConstantApi;
import com.ly.video.view.CircleProgressView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.Call;

/**
 * 播放视频
 */
public class PlayMovieActivity extends AppCompatActivity {
    private WebView mWebView;
    private CircleProgressView circle_progress;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_jingjia_context);
        initView();

    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("播放视频");
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            //设置返回按钮
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        circle_progress = (CircleProgressView) findViewById(R.id.circle_progress);
        showProgress();
        mWebView = (WebView) findViewById(R.id.notice_web_view);
        // 设置WebView的类型
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 支持javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mWebView.setDrawingCacheEnabled(true);
//        mWebView.setWebViewClient(new MyWebViewClient());
        initData(ConstantApi.Movie_Number_Path + getIntent().getStringExtra("url"));
//        mWebView.loadUrl(ConstantApi.Movie_Number_Path + getIntent().getStringExtra("url"));
    }

    private void showProgress() {

        circle_progress.setVisibility(View.VISIBLE);
        circle_progress.spin();//旋转
    }

    public void hideProgress() {

        circle_progress.setVisibility(View.GONE);
        circle_progress.stopSpinning();

    }


    private void initData(String path) {
        OkHttpUtils
                .get()
                .url(path)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        hideProgress();
                    }

                    @Override
                    public void onResponse(String string, int id) {
                        try {
//                            String htmlData = subString(string);
//                            hideProgress();
                            Document doc = Jsoup.parse(string);
                            String links = doc.select("iframe").attr("src");
//                            mWebView.loadData(string, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
                            mWebView.loadUrl(links);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }

                });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
