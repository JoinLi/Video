package com.ly.video.fragment;


import android.content.Intent;
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
public class SouhuFragment extends BaseFragment implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<InforBean> list = new ArrayList<InforBean>();
    private int page;
    private static String tvPaths, moviePaths,mainPaths;
    private String path;
    private TextView text_tv, text_movie;

    /**
     *
     * @param tvPath  电视剧接口
     * @param moviePath 电影接口
     * @param mainPath  观看接口
     * @return
     */
    public static SouhuFragment newInstance(String tvPath, String moviePath,String mainPath) {
        SouhuFragment fragment = new SouhuFragment();
        tvPaths = tvPath;
        moviePaths = moviePath;
        mainPaths=mainPath;
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vip;
    }

    @Override
    protected void initView() {
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
                Intent intent = new Intent(getContext(), MovieActivity.class);
                intent.putExtra("url", mainPaths+adapter.getAllData().get(position).getMovie_url());
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
        page++;
        list.clear();
        getMovie();
    }

    @Override
    public void onRefresh() {
        page = 1;
        getMovie();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_tv:
                text_tv.setTextColor(getActivity().getColor(R.color.colorPrimary));
                text_movie.setTextColor(getActivity().getColor(R.color.black_normal));
                path = tvPaths;
                break;
            case R.id.text_movie:
                text_tv.setTextColor(getActivity().getColor(R.color.black_normal));
                text_movie.setTextColor(getActivity().getColor(R.color.colorPrimary));
                path = moviePaths;
                break;
            default:

                break;
        }
        onRefresh();
    }

    private void getMovie() {
//        recyclerView.showProgress();
//        String path = ConstantApi.Movie_Qy_Path+page;
        try {
            if (page == 1) {
                recyclerView.showProgress();
            }
            LogUtil.m("请求地址"+path + page);
            OkHttpUtils
                    .get()
                    .url(path + page)
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
                            LogUtil.m("出错"+call);
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
        if (page == 1) {
            recyclerView.cancelLongPress();
        }
//        recyclerView.cancelLongPress();
    }


}
