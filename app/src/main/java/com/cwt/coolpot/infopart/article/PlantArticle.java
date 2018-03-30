package com.cwt.coolpot.infopart.article;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cwt.coolpot.R;

/**
 * 文章详情界面，直接用WebView来展示
 * Created by 曹吵吵 on 2017/8/20 0020.
 */

public class PlantArticle extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView titleTextView;
    private Button backButton;
    private String url,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_article);
        init();
    }

    private void init(){
        initData();
        initView();
    }

    private void initView(){
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100){
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView.loadUrl(url);
        titleTextView = (TextView)findViewById(R.id.article_title);
        titleTextView.setText(title);
        backButton = (Button)findViewById(R.id.article_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData(){
        Intent intent=getIntent();
        url=intent.getStringExtra("ArticleUrl");
        title=intent.getStringExtra("ArticleTitle");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
        {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
