package com.cwt.coolpot.infopart.article;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cwt.coolpot.R;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

/**
 * 文字搜索结果的RecyclerView的适配器
 * Created by 曹吵吵 on 2017/11/29 0029.
 */

public class ArticleBriefAdapter extends RecyclerView.Adapter<ArticleBriefAdapter.ViewHolder> {
    private List<ArticleBrief> articleBriefList;
    private View view;

    public ArticleBriefAdapter(List<ArticleBrief> articleBriefList){
        this.articleBriefList=articleBriefList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.article_brief_result,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.articleBriefView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=viewHolder.getAdapterPosition();
                ArticleBrief articleBrief=articleBriefList.get(position);
                String url=articleBrief.articleUrl;
                String title=articleBrief.articleTitle;
                Intent intent=new Intent(view.getContext(),PlantArticle.class);
                intent.putExtra("ArticleUrl",url);
                intent.putExtra("ArticleTitle",title);
                view.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleBrief articleBrief=articleBriefList.get(position);
        String url=articleBrief.picUrl;
        if (url=="") {
            Glide.with(view.getContext()).load(R.drawable.no_pic).into(holder.imageView);
        } else {
            Glide.with(view.getContext()).load(url).into(holder.imageView);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            holder.titleText.setText(Html.fromHtml("<b>"+articleBrief.articleTitle+"</b>",FROM_HTML_MODE_COMPACT));
            holder.infoText.setText(Html.fromHtml("<p>&nbsp;&nbsp;"+articleBrief.briefInfo+"</p>",FROM_HTML_MODE_COMPACT));
        }else {
            holder.infoText.setText(Html.fromHtml("<p>&nbsp;&nbsp;"+articleBrief.briefInfo+"</p>"));
            holder.titleText.setText(Html.fromHtml("<b>"+articleBrief.articleTitle+"</b>"));
        }

    }

    @Override
    public int getItemCount() {
        return articleBriefList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView titleText,infoText;
        View articleBriefView;

        public ViewHolder(View itemView) {
            super(itemView);
            articleBriefView=itemView;
            imageView=(ImageView)itemView.findViewById(R.id.article_brief_pic);
            titleText=(TextView)itemView.findViewById(R.id.article_brief_title);
            infoText=(TextView)itemView.findViewById(R.id.article_brief_info);
        }
    }
}
