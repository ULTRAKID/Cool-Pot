package com.cwt.coolpot.infopart.article;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cwt.coolpot.network.HttpUtil;
import com.cwt.coolpot.R;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
/**
 * 文字搜索后显示的结果界面，由RecyclerView实现
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class NameSearchResultActivity extends AppCompatActivity {
    private List<ArticleBrief> articleBriefList;
    private Handler handler=null;
    private Elements searchResultElements=null;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_text_result);
        init();
    }

    private void init(){
        initData();
        initButton();
    }

    private void initButton(){
        Button button=(Button)findViewById(R.id.ptr_back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData(){
        Intent intent=getIntent();
        final String keyword=intent.getStringExtra("SearchKeyword");
        initHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    searchResultElements= HttpUtil.search_zw3e(keyword);
                    Message message=new Message();
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(NameSearchResultActivity.this).setMessage("搜索出错！");
                }
            }
        }).start();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("搜索中");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void initHandler(){
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                try {
                    articleBriefList=HttpUtil.getArticlesBrief(searchResultElements);
                    initRecyclerView();
                } catch (IOException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(NameSearchResultActivity.this).setMessage("搜索出错！");
                }
                progressDialog.dismiss();
                return true;
            }
        });
    }

    private void initRecyclerView(){
        recyclerView=(RecyclerView)findViewById(R.id.article_brief_recycler_view);
        ImageView imageView=(ImageView)findViewById(R.id.no_result_img);
        if (articleBriefList.isEmpty()){
            imageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }
        imageView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ArticleBriefAdapter adapter=new ArticleBriefAdapter(articleBriefList);
        recyclerView.setAdapter(adapter);
    }
}
