package com.renj.mvp.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.renj.common.utils.ResUtils;
import com.renj.common.weight.MyViewPager;
import com.renj.daggersupport.DaggerSupportActivity;
import com.renj.mvp.R;
import com.renj.mvp.utils.MyCommonUtils;
import com.renj.mvp.view.fragment.HomeFragment;
import com.renj.mvp.view.fragment.MyFragment;
import com.renj.mvp.view.fragment.NewsFragment;
import com.renj.mvpbase.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends DaggerSupportActivity {
    @BindView(R.id.view_pager)
    MyViewPager viewPager;
    @BindView(R.id.bottom_tab)
    BottomNavigationBar bottomTab;

    private List<BaseFragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        setPageBack(false, false, null);
        initTitles();
        initFragments();
        initViewPager();
        initNavigationBar();
    }

    private void initTitles() {
        titles.add(ResUtils.getString(R.string.main_tab_1));
        titles.add(ResUtils.getString(R.string.main_tab_2));
        titles.add(ResUtils.getString(R.string.main_tab_3));
        setPageTitle(titles.get(0));
    }

    private void initFragments() {
        fragments.add(HomeFragment.Companion.newInstance());
        fragments.add(NewsFragment.Companion.newInstance());
        fragments.add(MyFragment.Companion.newInstance());
    }

    private void initViewPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }

    private void initNavigationBar() {
        bottomTab
                .setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(new BottomNavigationItem(R.mipmap.home_s, titles.get(0))
                        .setInactiveIconResource(R.mipmap.home_n)
                        .setActiveColor(ResUtils.getColor(R.color.app_main))
                        .setInActiveColor(ResUtils.getColor(R.color.color_gray)))
                .addItem(new BottomNavigationItem(R.mipmap.found_s, titles.get(1))
                        .setInactiveIconResource(R.mipmap.found_n)
                        .setActiveColor(ResUtils.getColor(R.color.app_main))
                        .setInActiveColor(ResUtils.getColor(R.color.color_gray)))
                .addItem(new BottomNavigationItem(R.mipmap.me_s, titles.get(2))
                        .setInactiveIconResource(R.mipmap.me_n)
                        .setActiveColor(ResUtils.getColor(R.color.app_main))
                        .setInActiveColor(ResUtils.getColor(R.color.color_gray)))
                .initialise();

        bottomTab.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                setPageTitle(titles.get(position));
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        // 放在后面才能生效
        MyCommonUtils.setBottomNavigationItem(bottomTab,6,24,12);
    }
}
