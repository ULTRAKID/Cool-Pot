package com.cwt.coolpot.infopart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cwt.coolpot.MainActivity;
import com.cwt.coolpot.R;
import com.cwt.coolpot.infopart.plant.PlantPedia;
import com.cwt.coolpot.infopart.plant.PlantPediaActivity;
import com.cwt.coolpot.network.HttpUtil;
import com.cwt.coolpot.infopart.plant.Plant;
import com.cwt.coolpot.infopart.plant.PlantImgResultActivity;
import com.cwt.coolpot.infopart.article.NameSearchResultActivity;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 百科界面
 *      图片识别
 *      文字搜索
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class InfoFragment extends Fragment{
    SearchView searchView;ImageView bg_pic;FloatingActionButton imgChoose;Button testRecUI,testPediaUI;
    //SharedPreferences preferences;
    View view;
    String img_base64="",rec_result="";
    Plant plant;
    private String imagepath = "/storage/emulated/0/DCIM/CP ";
    private String imagepath2 = "/storage/emulated/0/DCIM/test_null.jpg";
    private Uri imageUri;
    private Handler handler=null;
    private ProgressDialog progressDialog;
    private final static int TAKE_PHOTO=1,CHOOSE_PHOTO=2,
            SUCCESS=0,FAILURE=1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.info_fragment,container,false);
        init();
        //testRecUI.setVisibility(View.INVISIBLE);
        return view;
    }

    private void init(){
        initView();
        initHandler();
    }

    private void initView(){
        initSearchView();
        initButton();
    }

    private void initSearchView(){
        searchView=(SearchView)view.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("输入植物名称");
        searchView.setSubmitButtonEnabled(true);
        int id=searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView=(TextView)searchView.findViewById(id);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setHintTextColor(Color.argb(100,00,00,00));

        try {
            Field mCursorDrawableRes=TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(textView, R.drawable.cursor_color);
        } catch (Exception e){
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (HttpUtil.isNetworkConnected(view.getContext())){
                    Intent intent=new Intent(view.getContext(),NameSearchResultActivity.class);
                    intent.putExtra("SearchKeyword",s);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("错误")
                            .setMessage("请检查网络连接！")
                            .show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        /*bg_pic=(ImageView)view.findViewById(R.id.bg_bing_pic);
        preferences=PreferenceManager.getDefaultSharedPreferences(view.getContext());
        String bg_pic_string=preferences.getString("bg_pic",null);
        if (bg_pic_string!=null){
            Glide.with(InfoFragment.this).load(bg_pic_string).into(bg_pic);
        } else {
            loadBingPic();
        }*/

    }

    private void initButton(){
        final String[] items={"拍摄图片","相册选取"};
        testRecUI =(Button)view.findViewById(R.id.testRecUI_button);
        testRecUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonStr=HttpUtil.testPlantRecUI();
                plant=new Gson().fromJson(jsonStr,Plant.class);
                Intent intent=new Intent(view.getContext(),PlantImgResultActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(Plant.SER_KEY,plant);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        testPediaUI=(Button)view.findViewById(R.id.testPediaUI_button);
        testPediaUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsonStr=HttpUtil.testPlantPediaUI();
                PlantPedia plantPedia=new Gson().fromJson(jsonStr,PlantPedia.class);
                Intent intent=new Intent(view.getContext(),PlantPediaActivity.class);
                intent.putExtra(PlantPedia.SER_KEY,plantPedia);
                startActivity(intent);
            }
        });
        imgChoose=(FloatingActionButton) view.findViewById(R.id.search_pic);
        imgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(view.getContext());
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                choosePhoto();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();

            }
        });
    }

    private void choosePhoto(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    private void takePhoto(){
        Date now=new Date();
        now.getTime();
        imagepath="/storage/emulated/0/DCIM/CP ";
        imagepath+=now.toString()+".jpg";
        File outputImage=new File(imagepath);
        //File outputImage=new File(view.getContext().getExternalCacheDir(),"plants.jpg");
        //File outputImage2=new File(imagepath2);
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT>=24){
            imageUri= FileProvider.getUriForFile(view.getContext(),"com.cwt.coolpot.ccc",outputImage);
        } else {
            imageUri=Uri.fromFile(outputImage);
            Log.e("data_null",imageUri.toString());
        }
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bing_pic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit();
                editor.putString("bg_pic",bing_pic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(InfoFragment.this).load(bing_pic).into(bg_pic);
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int state=SUCCESS;
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode== Activity.RESULT_OK) {
                    state=recognizePlant(imageUri);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode== Activity.RESULT_OK){
                    imageUri=data.getData();
                    state=recognizePlant(imageUri);
                }
                break;
        }
        if (state==FAILURE){
            if (plant!=null){
                switch (plant.Status){
                    case 1001:
                        Toast.makeText(getContext(),Plant.STATUS_1001,Toast.LENGTH_SHORT).show();
                        break;
                    case 1002:
                        Toast.makeText(getContext(),Plant.STATUS_1002,Toast.LENGTH_SHORT).show();
                        break;
                    case 1003:
                        Toast.makeText(getContext(),Plant.STATUS_1003,Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(getContext(),"图片上传失败",Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.arg1){
                    case 1:
                        if (plant==null||plant.Status!=0){
                            if (plant!=null){
                                switch (plant.Status){
                                    case 1001:
                                        Toast.makeText(getContext(),Plant.STATUS_1001,Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1002:
                                        Toast.makeText(getContext(),Plant.STATUS_1002,Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1003:
                                        Toast.makeText(getContext(),Plant.STATUS_1003,Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(getContext(),"图片上传失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                        Intent intent=new Intent(view.getContext(),PlantImgResultActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable(Plant.SER_KEY,plant);
                        intent.putExtras(bundle);
                        progressDialog.dismiss();
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    private int recognizePlant(Uri photoUri){
        Log.e("data", photoUri.toString());
        plant=null;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(photoUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
                if (bitmap != null) {
                    //bg_pic.setImageBitmap(bitmap);
                    img_base64 = MainActivity.bitmapToBase64(bitmap);
                } else {
                    Log.e("Log_CoolPot", "bitmap is null");
                    return FAILURE;
                }
                //img_base64=MainActivity.isToBase64(inputStream);
                //img_base64= URLEncoder.encode(img_base64,"GBK");
            } else {
                Log.e("Log_CoolPot", "inputStream is null");
                return FAILURE;
            }
            inputStream.close();
        } catch (IOException e) {
            img_base64 = "";
            e.printStackTrace();
            return FAILURE;
        }
        Log.e("Log_CoolPot", "img_base64:\n" + img_base64);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = HttpUtil.handlePlantImg(img_base64);
                Log.e("Log_CoolPot","response value:"+response.toString());
                if (response==null)
                    Log.e("Error","response is null\n");
                rec_result = response.toString();
                //Log.e("Error",rec_result+"\n");
                try {
                    String jsonStr=response.body().string();
                    Log.e("Log_CoolPot", rec_result + "\n" + jsonStr);
                    plant=new Gson().fromJson(jsonStr,Plant.class);
                    Log.e("Log_CoolPot",plant.Result.get(0).Name);
                    Message message=new Message();
                    message.arg1=1;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                //Toast.makeText(view.getContext(),rec_result,Toast.LENGTH_LONG).show();
            }
        }).start();
        progressDialog=new ProgressDialog(view.getContext());
        progressDialog.setMessage("图片识别中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return SUCCESS;
    }
}
