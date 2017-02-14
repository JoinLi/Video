package com.ly.video;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ly.video.activity.SettingActivity;
import com.ly.video.activity.Updae_MainActivity;
import com.ly.video.adapter.ViewPagerAdapter;
import com.ly.video.bean.ConstantApi;
import com.ly.video.fragment.IndexFragment;
import com.ly.video.fragment.SouhuFragment;

public class MainActivity extends Updae_MainActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        CheckNewestVersion();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //将tabLayout与viewpager连起来
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(IndexFragment.newInstance(), "首页");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Sh_Path,ConstantApi.Movie_Sh_Path,ConstantApi.Sh_Path), "搜狐");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Qy_Path,ConstantApi.Movie_Qy_Path,ConstantApi.Qy_Path), "爱奇艺");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Ls_Path,ConstantApi.Movie_Ls_Path,ConstantApi.Ls_Path), "乐视");
        adapter.addFragment(SouhuFragment.newInstance(ConstantApi.Tv_Mg_Path,ConstantApi.Movie_Mg_Path,ConstantApi.Mg_Path), "芒果");
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
