package com.ly.video;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.ly.video.activity.SearchActivity;
import com.ly.video.activity.SettingActivity;
import com.ly.video.activity.Updae_MainActivity;
import com.ly.video.adapter.ViewPagerAdapter;
import com.ly.video.bean.ConstantApi;
import com.ly.video.fragment.IndexFragment;
import com.ly.video.fragment.SouhuFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Updae_MainActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;
    protected SearchHistoryTable historyTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        SharedPreferences share = getSharedPreferences("setting", MODE_PRIVATE);
        boolean isRem = share.getBoolean("checked", false);
        if (isRem) {
            CheckNewestVersion();
        }

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSearchView();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //将tabLayout与viewpager连起来
        tabLayout.setupWithViewPager(viewPager);

    }

    protected void setSearchView() {
        historyTable = new SearchHistoryTable(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        customSearchView(true);
        if (searchView != null) {
            searchView.setHint(R.string.search_hint);
            searchView.setVoice(false); //隐藏语音搜索
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    getData(query, 0);

                    searchView.close(true);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });


            if (searchView.getAdapter() == null) {
                List<SearchItem> suggestionsList = new ArrayList<>();

                SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
                searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                        String query = textView.getText().toString();
                        getData(query, position);
                        searchView.close(true);
                    }
                });
                searchView.setAdapter(searchAdapter);
            }

        }
    }

    /**
     * 隐藏搜索框
     *
     * @param menuItem
     */
    protected void customSearchView(boolean menuItem) {
        if (searchView != null) {
            if (menuItem) {
                searchView.setVersion(SearchView.VERSION_MENU_ITEM);
                searchView.setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM);
                searchView.setTheme(SearchView.THEME_LIGHT);
            } else {
                searchView.setVersion(SearchView.VERSION_TOOLBAR);
                searchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
                searchView.setTheme(SearchView.THEME_LIGHT);
            }
        }
    }

    protected void getData(String text, int position) {
        historyTable.addItem(new SearchItem(text));

        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra("context", text);
//        intent.putExtra(EXTRA_KEY_VERSION, SearchView.VERSION_TOOLBAR);
//        intent.putExtra(EXTRA_KEY_VERSION_MARGINS, SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
//        intent.putExtra(EXTRA_KEY_THEME, SearchView.THEME_LIGHT);
//        intent.putExtra(EXTRA_KEY_TEXT, text);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.action_search:
                searchView.open(true, item);
                break;

            default:

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(IndexFragment.newInstance(ConstantApi.Tv_Other_Path, ConstantApi.Movie_Other_Path, ConstantApi.Movie_Number_Path), "首页");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Sh_Path, ConstantApi.Movie_Sh_Path, ConstantApi.Sh_Path), "搜狐");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Qy_Path, ConstantApi.Movie_Qy_Path, ConstantApi.Qy_Path), "爱奇艺");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Ls_Path, ConstantApi.Movie_Ls_Path, ConstantApi.Play_Path), "乐视");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Mg_Path, ConstantApi.Movie_Mg_Path, ConstantApi.Mg_Path), "芒果");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    /**
     * 点击两次退出
     */
    boolean isExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        isExit = false;

                    }

                }, 2000);

                return false;
            }

        }

        return super.onKeyDown(keyCode, event);
    }

}
