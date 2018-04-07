package com.cwt.coolpot.infopart.plant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cwt.coolpot.R;
import com.cwt.coolpot.infopart.article.NameSearchResultActivity;
import com.cwt.coolpot.network.HttpUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Response;

/**
 * ViewPager的适配器
 * Created by 曹吵吵 on 2017/11/6 0006.
 */

public class PlantPagerAdapter extends PagerAdapter {
    private List<Plant.ResultBean> plantList;
    private Context context;
    private LayoutInflater inflater;
    private String pediaResult="";
    private PlantPedia plantPedia;
    private ProgressDialog progressDialog;
    private Handler handler;
    public static final int GOT_PLANT_PEDIA=65;

    public PlantPagerAdapter(Context context,List<Plant.ResultBean> plantList){
        this.context=context;
        this.plantList=plantList;
        inflater=LayoutInflater.from(context);
        initHandler();
    }

    @Override
    public int getCount() {
        return plantList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View resultView = inflater.inflate(R.layout.plant_item, container, false);
        TextView plantAliasName=(TextView)resultView.findViewById(R.id.plant_alias_name);
        TextView plantFamily=(TextView)resultView.findViewById(R.id.plant_family);
        TextView plantGenus=(TextView)resultView.findViewById(R.id.plant_genus);
        TextView plantLatinName=(TextView)resultView.findViewById(R.id.plant_latin_name);
        TextView plantName=(TextView)resultView.findViewById(R.id.plant_name);
        TextView plantScore=(TextView)resultView.findViewById(R.id.plant_score);
        ImageView plantExamplePic=(ImageView)resultView.findViewById(R.id.plant_example_pic);
        plantName.setText("名称："+dealStr(plantList.get(position).Name));
        plantLatinName.setText("拉丁名："+dealStr(plantList.get(position).LatinName));
        plantAliasName.setText("别名："+dealStr(plantList.get(position).AliasName));
        plantFamily.setText("科名："+dealStr(plantList.get(position).Family));
        plantGenus.setText("属名："+dealStr(plantList.get(position).Genus));
        plantScore.setText("可能性："+new DecimalFormat("0.00").format(plantList.get(position).Score)+"%");
        String url=plantList.get(position).ImageUrl;
        if (url=="")
            Glide.with(resultView.getContext()).load(R.drawable.no_pic).into(plantExamplePic);
        else
            Glide.with(resultView.getContext()).load(url).into(plantExamplePic);
        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID=plantList.get(position).InfoCode;
                Log.e("Log_CoolPot","植物ID:"+ID);
                getPlantPedia(ID);
            }
        });
        container.addView(resultView);
        return resultView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void getPlantPedia(final String ID){
        plantPedia=null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = HttpUtil.getPlantPedia(ID);
                if (response==null)
                    Log.e("Error","response is null\n");
                pediaResult = response.toString();
                //Log.e("Error",rec_result+"\n");
                try {
                    String jsonStr=response.body().string();
                    Log.e("Log_CoolPot", pediaResult + "\n" + jsonStr);
                    plantPedia=new Gson().fromJson(jsonStr,PlantPedia.class);
                    Log.e("Log_CoolPot",plantPedia.result.nameStd);
                    Message message=new Message();
                    message.what=GOT_PLANT_PEDIA;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("正在加载植物信息");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case GOT_PLANT_PEDIA:
                        Intent intent=new Intent(context,PlantPediaActivity.class);
                        intent.putExtra(PlantPedia.SER_KEY,plantPedia);
                        context.startActivity(intent);
                        progressDialog.dismiss();
                        break;
                }
                return true;
            }
        });
    }

    private String dealStr(String info){
        if (info==""||info.isEmpty())
            return "暂无";
        return info;
    }
}
