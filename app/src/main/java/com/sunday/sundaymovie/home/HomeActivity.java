package com.sunday.sundaymovie.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.sunday.sundaymovie.R;
import com.sunday.sundaymovie.activity.AboutActivity;
import com.sunday.sundaymovie.activity.BaseActivity;
import com.sunday.sundaymovie.activity.SearchActivity;
import com.sunday.sundaymovie.activity.SettingsActivity;
import com.sunday.sundaymovie.activity.StarsActivity;

public class HomeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener
        , NavigationView.OnNavigationItemSelectedListener {
    private String[] mTitles = new String[]{"正在热映", "即将上映"};
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private HomePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HomePagerAdapter(getSupportFragmentManager(), mTitles, this);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void initParams(Bundle bundle) {

    }

    @Override
    protected void initView(Context context) {
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        toggleSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                SearchActivity.startMe(this, null);
                break;
        }
        return true;
    }

    private void toggleSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                    , mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                ShowTimeFragment sf = (ShowTimeFragment) mAdapter.instantiateItem(mViewPager, 0);
                sf.smoothScrollToTop();
                break;
            case 1:
                ComingFragment cf = (ComingFragment) mAdapter.instantiateItem(mViewPager, 1);
                cf.smoothScrollToTop();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mTabLayout.removeOnTabSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_stars:
                StarsActivity.startMe(this);
                break;
            case R.id.menu_settings:
                SettingsActivity.startMe(this);
                break;
            case R.id.menu_about:
                AboutActivity.startMe(this);
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.START);
        return true;
    }
}