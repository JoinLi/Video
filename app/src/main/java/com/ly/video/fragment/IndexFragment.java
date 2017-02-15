package com.ly.video.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.ly.video.R;
import com.ly.video.activity.MovieActivity;
import com.ly.video.adapter.PersonAdapter;
import com.ly.video.bean.InforBean;
import com.ly.video.util.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @Auther: Joinli
 * @Date: 2016/7/9.
 * @description:
 */
public class IndexFragment extends BaseFragment implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<InforBean> list = new ArrayList<InforBean>();
    private int page;
    private String tvPaths, moviePaths, mainPaths;
    private String path;
    private TextView text_tv, text_movie;
    private int index = 3; //标志位，判断是电影还是电视剧

    public static IndexFragment newInstance(String tvPath, String moviePath, String mainPath) {
        IndexFragment fragment = new IndexFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tvPath", tvPath);
        bundle.putString("moviePath", moviePath);
        bundle.putString("mainPath", mainPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vip;
    }

    @Override
    protected void initView() {
        Bundle args = getArguments();
        if (args != null) {
            tvPaths = args.getString("tvPath");
            moviePaths = args.getString("moviePath");
            mainPaths = args.getString("mainPath");
        }
        recyclerView = findView(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
//        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(this, 0.5f), Util.dip2px(this, 72), 0);
//        itemDecoration.setDrawLastItem(false);
//        recyclerView.addItemDecoration(itemDecoration);
        text_tv = findView(R.id.text_tv);
        text_movie = findView(R.id.text_movie);
        adapter = new PersonAdapter(getContext());
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
                //index  1 vip电视剧   2 vip电影     3 首页电视剧电影
                Intent intent = new Intent(getContext(), MovieActivity.class);
                intent.putExtra("url", mainPaths + adapter.getAllData().get(position).getMovie_url());
                intent.putExtra("index", index);
                intent.putExtra("mainPaths", mainPaths);
                startActivity(intent);
            }
        });
        text_tv.setOnClickListener(this);
        text_movie.setOnClickListener(this);
        path = tvPaths;
        text_tv.setTextColor(getActivity().getColor(R.color.colorPrimary));
        recyclerView.setRefreshListener(this);  //下拉刷新

    }

    @Override
    protected void initData() {
        onRefresh();
    }


    @Override
    public void onLoadMore() {
        adapter.stopMore();
    }

    @Override
    public void onRefresh() {
        page = 1;
        getMovie();

    }

    private void getMovie() {
        recyclerView.showProgress();
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
                            Snackbar.make(getView(), getResources().getString(R.string.snackbar_err), Snackbar.LENGTH_LONG)
                                    .show();
                        }

                        @Override
                        public void onResponse(String string, int id) {
                            try {
                                if (page == 1) {//暂无数据
                                    adapter.clear();
                                    list.clear();
                                }

                                Document doc = Jsoup.parse(string);
                                Elements links = doc.select("div.plist").select("ul.list_tab_img");
                                Elements elements = links.select("li");
                                for (Element element : elements) {
                                    InforBean bean = new InforBean();
                                    bean.setMovie_url(element.select("a").attr("href"));
                                    bean.setImg_url(element.select("img").attr("src"));
                                    bean.setTitle(element.select("b").text());
                                    element.select("b").text();
                                    LogUtil.m("链接" + element.select("a").attr("href"));
                                    LogUtil.m("图片" + element.select("img").attr("src"));
                                    LogUtil.m("标题" + element.select("b").text());
                                    list.add(bean);

                                }
                                adapter.addAll(list);
                            } catch (Exception e) {
                                adapter.stopMore();
                                e.printStackTrace();

                            }


                        }

                    });

        } catch (Exception e) {
            adapter.stopMore();
            e.printStackTrace();

        }

        recyclerView.cancelLongPress();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_tv:
                text_tv.setTextColor(getActivity().getColor(R.color.colorPrimary));
                text_movie.setTextColor(getActivity().getColor(R.color.black_normal));
                index = 3;
                path = tvPaths;
                break;
            case R.id.text_movie:
                text_tv.setTextColor(getActivity().getColor(R.color.black_normal));
                text_movie.setTextColor(getActivity().getColor(R.color.colorPrimary));
                index = 3;
                path = moviePaths;
                break;
            default:

                break;
        }
        onRefresh();
    }
}
