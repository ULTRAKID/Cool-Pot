package com.cwt.coolpot;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class InfoFragment extends Fragment {

    SearchView searchView;ImageView bg_pic;
    SharedPreferences preferences;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.info_fragment,container,false);
        initSearchView();
        return view;
    }

    private void initSearchView(){
        searchView=(SearchView)view.findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("输入植物名称");
        int id=searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView=(TextView)searchView.findViewById(id);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setHintTextColor(Color.parseColor("#000000"));

        try {
            Field mCursorDrawableRes=TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(textView, R.drawable.cursor_color);
        } catch (Exception e){
            e.printStackTrace();
        }
        bg_pic=(ImageView)view.findViewById(R.id.bg_bing_pic);
        preferences=PreferenceManager.getDefaultSharedPreferences(view.getContext());
        String bg_pic_string=preferences.getString("bg_pic",null);
        if (bg_pic_string!=null){
            Glide.with(this).load(bg_pic_string).into(bg_pic);
        } else {
            loadBingPic();
        }
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


}
