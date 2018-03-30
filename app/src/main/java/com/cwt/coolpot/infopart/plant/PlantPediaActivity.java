package com.cwt.coolpot.infopart.plant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cwt.coolpot.R;

import java.util.List;

public class PlantPediaActivity extends AppCompatActivity {

    private PlantPedia.resultBean plantPedia;
    RecyclerView imagesRv;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_pedia);
        init();
    }

    private void init(){
        scrollView=(ScrollView)findViewById(R.id.plant_pedia_scrollView);
        TextView nameStd=(TextView)findViewById(R.id.nameStdPedia_text),
                familyGenusCn=(TextView)findViewById(R.id.family_genusPedia_text),
                alias=(TextView)findViewById(R.id.aliasPedia_text),
                description=(TextView)findViewById(R.id.descriptionPedia_text),
                xgsc=(TextView)findViewById(R.id.xgscPedia_text),
                jzgy=(TextView)findViewById(R.id.jzgyPedia_text),
                hyyy=(TextView)findViewById(R.id.hyyyPedia_text),
                fbdq=(TextView)findViewById(R.id.fbdqPedia_text),
                mcll=(TextView)findViewById(R.id.mcllPedia_text),
                yhjs=(TextView)findViewById(R.id.yhjsPedia_text),
                bxtz=(TextView)findViewById(R.id.bxtzPedia_text),
                hksj=(TextView)findViewById(R.id.hksjPedia_text);
        imagesRv =(RecyclerView)findViewById(R.id.imagesPedia_RV);
        PlantPedia buf=(PlantPedia)getIntent().getSerializableExtra(PlantPedia.SER_KEY);
        plantPedia=buf.result;
        PlantPedia.resultBean.infoBean infoPedia=plantPedia.info;
        nameStd.setText(dealStr(plantPedia.nameStd));
        familyGenusCn.setText(dealStr(plantPedia.familyCn)+" "+dealStr(plantPedia.genusCn));
        alias.setText(dealStr(plantPedia.alias));
        description.setText("\t\t"+dealStr(plantPedia.description));
        xgsc.setText(dealStr(infoPedia.xgsc));
        jzgy.setText("\t\t"+dealStr(infoPedia.jzgy));
        hyyy.setText("\t\t"+dealStr(infoPedia.hyyy));
        fbdq.setText(dealStr(infoPedia.fbdq));
        mcll.setText("\t\t"+dealStr(infoPedia.mcll));
        yhjs.setText("\t\t"+dealStr(infoPedia.yhjs));
        bxtz.setText("\t\t"+dealStr(infoPedia.bxtz));
        hksj.setText(dealStr(infoPedia.hksj));
        List<String> imagesUrl=plantPedia.images;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imagesRv.setLayoutManager(layoutManager);
        ImagesAdapter adapter=new ImagesAdapter(imagesUrl);
        imagesRv.setAdapter(adapter);
    }

    private String dealStr(String info){
        if (info==""||info.isEmpty())
            return "暂无";
        return info;
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>{
        List<String> images;
        View img_item_view;

        public ImagesAdapter(List<String> images){
            this.images=images;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            img_item_view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pedia_images_item,parent,false);
            ViewHolder holder=new ViewHolder(img_item_view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String url=images.get(position);
            if (url==""||url.isEmpty())
                Glide.with(holder.view.getContext()).load(R.drawable.no_pic).into(holder.imageView);
            else
                Glide.with(holder.view.getContext()).
                        load(url).
                        into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            if (images.size()==0)
                return 1;
            return images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view=itemView;
                imageView=(ImageView)view.findViewById(R.id.pedia_image);
            }
        }
    }
}
