package com.cwt.coolpot.infopart.plant;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;

import com.cwt.coolpot.R;

import java.util.List;
/**
 * 植物图片识别成功后的界面，即ViewPager所在的活动
 * Created by 曹吵吵 on 2017/8/20 0020.
 */
public class PlantImgResultActivity extends AppCompatActivity {

    private List<Plant.ResultBean> plantList;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_img_result);
        init();

    }

    private void init(){
        /*RecyclerView recyclerView=(RecyclerView)findViewById(R.id.plant_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Plant plant=(Plant)getIntent().getSerializableExtra(Plant.SER_KEY);
        PlantAdapter adapter=new PlantAdapter(plant.Result);
        recyclerView.setAdapter(adapter);*/
        viewPager=(ViewPager)findViewById(R.id.plant_viewPager);
        Plant plant=(Plant)getIntent().getSerializableExtra(Plant.SER_KEY);
        plantList=plant.Result;
        PlantPagerAdapter adapter=new PlantPagerAdapter(this,plantList);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                48, getResources().getDisplayMetrics()));
        viewPager.setPageTransformer(false,new ScaleTransformer(this));

    }


}
