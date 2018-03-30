package com.cwt.coolpot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.cwt.coolpot.conpart.ConFragment;
import com.cwt.coolpot.infopart.InfoFragment;
import com.cwt.coolpot.mainpart.MainFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationBar navigationBar;
    List<Fragment> fragments;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >=21) {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        initNavigationBar();
        initViewPager();
        //initToolBar();
    }

    private void initNavigationBar(){
        navigationBar=(BottomNavigationBar)findViewById(R.id.bottom_navigation_bar);
        BottomNavigationItem mainInfo,conInfo,plantsInfo;
        mainInfo=new BottomNavigationItem(android.R.drawable.btn_star_big_on,"主页");
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
        navigationBar.addItem(plantsInfo).addItem(mainInfo).addItem(conInfo)
                .setBarBackgroundColor(android.R.color.white)
                .setInActiveColor(android.R.color.holo_blue_dark)
                .setActiveColor(android.R.color.holo_red_light)
                .initialise();
        navigationBar.setAutoHideEnabled(true);

    }

    private void initViewPager(){
        fragments=new ArrayList<>();
        fragments.add(new InfoFragment());
        fragments.add(new MainFragment());
        fragments.add(new ConFragment());
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        MPagerAdapter adapter=new MPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
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
        /*Bundle bundle=new Bundle();
        bundle.putSerializable("MPagerAdapter",adapter);
        fragments.get(0).setArguments(bundle);*/
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String isToBase64(InputStream is){
        byte[] data = null;
        String result = null;
        try {
            data = new byte[is.available()];
            is.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //写入数组

        //用默认的编码格式进行编码
        result = Base64.encodeToString(data,Base64.NO_WRAP);
        return result;
    }

    /*private void initToolBar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }*/
}
