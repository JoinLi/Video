package com.ly.video.fragment;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.ly.video.R;
import com.ly.video.activity.MovieActivity;
import com.ly.video.adapter.PersonAdapter;
import com.ly.video.bean.InforBean;
import com.ly.video.bean.ConstantApi;
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
public class IndexFragment extends BaseFragment implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TITLE = "首页";
    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<InforBean> list = new ArrayList<InforBean>();
    private EditText mClearEditText;
    private String context = "鬼吹灯";
    private ImageView ic_search;
    private int page;

    public static IndexFragment newInstance() {
        IndexFragment fragment = new IndexFragment();
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initView() {
        mClearEditText = findView(R.id.filter_edit_qd);
        recyclerView = findView(R.id.recyclerView);
        ic_search = findView(R.id.ic_search);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
//        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(this, 0.5f), Util.dip2px(this, 72), 0);
//        itemDecoration.setDrawLastItem(false);
//        recyclerView.addItemDecoration(itemDecoration);
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
        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edContext = mClearEditText.getText().toString().trim();
                filterData(edContext.toString());
            }
        });

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), MovieActivity.class);
                intent.putExtra("url", ConstantApi.Movie_Number_Path+adapter.getAllData().get(position).getMovie_url());
                startActivity(intent);
            }
        });
        list.clear();
        adapter.clear();
        adapter.addAll(list);
        recyclerView.setRefreshListener(this);  //下拉刷新

    }

    @Override
    protected void initData() {

    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        LogUtil.m("执行了");

        if (TextUtils.isEmpty(filterStr)) {

            Snackbar.make(getView(), getResources().getString(R.string.snackbar_context), Snackbar.LENGTH_LONG)
                    .show();


        } else {
            context = filterStr;
            page = 1;
            onRefresh();

        }


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
        String path = ConstantApi.Movie_Sou_Path + context;
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


}
