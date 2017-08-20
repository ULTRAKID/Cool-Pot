package com.cwt.coolpot;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.design.internal.BottomNavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationBar navigationBar;
    List<Fragment> fragments;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        initNavigationBar();
        initViewPager();
    }

    private void initNavigationBar(){
        navigationBar=(BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        BottomNavigationItem mainInfo,conInfo,plantsInfo;
        mainInfo=new BottomNavigationItem(android.R.drawable.btn_star_big_off,"主页");
        conInfo=new BottomNavigationItem(android.R.drawable.ic_menu_share,"连接");
        plantsInfo=new BottomNavigationItem(android.R.drawable.ic_menu_info_details,"百科");
        navigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        navigationBar.addItem(conInfo).addItem(mainInfo).addItem(plantsInfo)
                .setBarBackgroundColor(R.color.colorPrimary)
                .setActiveColor("#FF2320")
                .initialise();
        navigationBar.setAutoHideEnabled(true);

    }

    private void initViewPager(){
        fragments=new ArrayList<>();
        fragments.add(new ConFragment());
        fragments.add(new MainFragment());
        fragments.add(new InfoFragment());
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(new MPagerAdapter(getSupportFragmentManager(),fragments));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigationBar.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }
}
