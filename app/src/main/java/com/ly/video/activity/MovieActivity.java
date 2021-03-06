package com.ly.video.activity;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.ly.video.R;
import com.ly.video.adapter.MovieAdapter;
import com.ly.video.bean.ConstantApi;
import com.ly.video.bean.MovieBean;
import com.ly.video.util.LogUtil;
import com.ly.video.util.Util;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MovieActivity extends AppCompatActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private EasyRecyclerView recyclerView;
    private MovieAdapter adapter;
    private WebView mWebView;
    private List<MovieBean> list = new ArrayList<MovieBean>();
    private int page = 1;
    private String mainPaths;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        initView();
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("视频选择");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(this, 0.5f), Util.dip2px(this, 72), 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);
        mWebView = (WebView) findViewById(R.id.notice_web_view);
        // 设置WebView的类型
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 支持javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mWebView.setDrawingCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override  //对网页中超链接按钮的响应
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        mWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                progressBar.setProgress(i);
                if (i == progressBar.getMax()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        adapter = new MovieAdapter(this);
        recyclerView.setAdapterWithProgress(adapter);
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                progressBar.setProgress(3);
                progressBar.setVisibility(View.VISIBLE);
                initMovieData(list.get(position).getMovie_api());
                toolbar.setTitle("正在播放" + "\"" + list.get(position).getMovie_title() + "\"");
                setSupportActionBar(toolbar);
            }
        });
        mainPaths = getIntent().getStringExtra("mainPaths");
        recyclerView.setRefreshListener(this);  //下拉刷新
//        adapter.addAll(list);
        onRefresh();


    }

    @Override
    public void onLoadMore() {
        adapter.stopMore();
    }

    @Override
    public void onRefresh() {
        page = 1;
        initData();

    }

    private void initData() {
        String path = getIntent().getStringExtra("url");
        LogUtil.m("播放地址" + path);
        try {
            OkHttpUtils
                    .get()
                    .url(path)
                    .build()
                    .execute(new StringCallback() {
                                 @Override
                                 public void onError(Call call, Exception e, int id) {
                                     if (page == 1) {
                                         adapter.clear();
                                         list.clear();

                                     } else {

                                         adapter.stopMore();
                                     }
                                 }

                                 @Override
                                 public void onResponse(String string, int id) {
                                     try {
                                         if (page == 1) {//暂无数据
                                             adapter.clear();
                                             list.clear();
                                         }

                                         Document doc = Jsoup.parse(string);
                                         switch (getIntent().getIntExtra("index", -1)) {

                                             case 1:
                                                 String link_url = doc.select("iframe").attr("src");
                                                 if (link_url.trim().startsWith("/")) {
                                                     link_url = ConstantApi.Play_Path + link_url;
                                                     //添加到html
                                                     doc.select("iframe").attr("src", link_url);

                                                 }
                                                 LogUtil.m("1 vip电视剧播放地址" + link_url);
                                                 mWebView.loadUrl(link_url);
                                                 Elements links = doc.select("div.plist").select("ul.list_tab_img");
                                                 Elements elements = links.select("li");
                                                 for (Element element : elements) {
                                                     MovieBean bean = new MovieBean();
                                                     LogUtil.m("视频地址" + mainPaths + element.select("a").attr("href"));
                                                     bean.setMovie_api(mainPaths + element.select("a").attr("href"));
                                                     String movieTitle = element.select("label").text();
                                                     LogUtil.m("视频名称" + movieTitle);
                                                     bean.setMovie_title(movieTitle);
                                                     list.add(bean);
                                                 }
//                                                 initMovieData(list.get(0).getMovie_api());
                                                 break;
                                             case 2:

                                                 Document docs = Jsoup.parse(string);
                                                 String linkss = docs.select("iframe").attr("src");
                                                 if (linkss.trim().startsWith("/")) {
                                                     linkss = ConstantApi.Play_Path + linkss;
                                                     //添加到html
                                                     docs.select("iframe").attr("src", linkss);

                                                 }
                                                 LogUtil.m("2 vip电影播放地址 " + linkss);
                                                 mWebView.loadUrl(linkss);

                                                 break;
                                             case 3:
                                                 Elements links1 = doc.select("DIV.c-box");
                                                 Elements elements1 = links1.select("LI");
                                                 for (Element element : elements1) {
                                                     MovieBean bean = new MovieBean();
                                                     bean.setMovie_title(element.select("A").attr("title"));
                                                     bean.setMovie_api(ConstantApi.Movie_Number_Path + element.select("A").attr("href"));
                                                     LogUtil.m("首页视频名称" + element.select("A").attr("title"));
                                                     LogUtil.m("首页视频接口" + ConstantApi.Movie_Number_Path + element.select("A").attr("href"));
                                                     list.add(bean);

                                                 }
                                                 LogUtil.m("3 首页播放地址 " + list.get(0).getMovie_api());
                                                 initMovieData(list.get(0).getMovie_api());

                                                 break;
                                             default:

                                                 break;

                                         }


                                         adapter.addAll(list);
                                     } catch (Exception e) {
                                         adapter.stopMore();
                                         e.printStackTrace();

                                     }


                                 }

                             }

                    );
        } catch (Exception e) {
            adapter.stopMore();
            e.printStackTrace();

        }
    }


    private void initMovieData(String path) {
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
                            Document doc = Jsoup.parse(string);
                            String links = doc.select("iframe").attr("src");

                            if (getIntent().getIntExtra("index", -1) == 3) { //首页数据
                                mWebView.loadUrl(links);

                            } else {  //其他vip数据

                                mWebView.loadUrl(ConstantApi.Play_Path + links);
                            }

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