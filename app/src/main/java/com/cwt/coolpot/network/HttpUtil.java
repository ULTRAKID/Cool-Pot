package com.cwt.coolpot.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.cwt.coolpot.infopart.article.ArticleBrief;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 曹吵吵 on 2017/8/22 0022.
 */

public class HttpUtil {

    final static String appcode="2ea82190bf2f4a1581c5032e074f56f7";
    final static String zw3e_website="http://www.zw3e.com/bk/s/";

    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static Response handlePlantImg(String img_base64){
        /*Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("img_base64", img_base64);*/
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                //.add("Authorization", "APPCODE " + appcode)
                //.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .add("img_base64",img_base64)
                .build();
        Request request=new Request.Builder()
                .url("http://plantgw.nongbangzhu.cn/plant/recognize2")
                .header("Authorization", "APPCODE " + appcode)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                //.header("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response=call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception","call execute failed\n");
        }
        return null;
    }

    public static Response getPlantPedia(String ID){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                //.add("Authorization", "APPCODE " + appcode)
                //.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .add("code",ID)
                .build();
        Request request=new Request.Builder()
                .url("http://plantgw.nongbangzhu.cn/plant/info")
                .header("Authorization", "APPCODE " + appcode)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        Response response;
        try {
            response=call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception","call execute failed\n");
        }
        return null;
    }

    public static String testPlantRecUI(){
        String jsonStr="{\"Status\":0,\"Message\":\"OK\",\"Result\":[{\"Score\":\"59.46\",\"AliasList\":[],\"Genus\":\"金琥属\",\"InfoCode\":\"aVo5awEfQw0TjYlg\",\"AliasName\":\"\",\"Family\":\"仙人掌科\",\"ImageUrl\":\"https://static.nongbangzhu.cn/samples_v4/p11k/p11k-watermark/%E9%87%91%E7%90%A5%E4%BB%99%E4%BA%BA%E7%90%83/10c7afa3d303a86e.jpg\",\"LatinName\":\"Echinocactus grusonii\",\"Name\":\"金琥\"},{\"Score\":\"15.51\",\"AliasList\":[],\"Genus\":\"仙人球属\",\"InfoCode\":\"N68n8ZufiJOKL52R\",\"AliasName\":\"\",\"Family\":\"仙人掌科\",\"ImageUrl\":\"https://static.nongbangzhu.cn/samples_v4/p11k/p11k-watermark/%E4%BB%99%E4%BA%BA%E7%90%83/102bec333acd1b50.jpg\",\"LatinName\":\"Echinopsis tubiflora\",\"Name\":\"仙人球\"},{\"Score\":\"6.37\",\"AliasList\":[],\"Genus\":\"锦绣玉属\",\"InfoCode\":\"4Qr3p509nhN7SX8U\",\"AliasName\":\"\",\"Family\":\"仙人掌科\",\"ImageUrl\":\"https://static.nongbangzhu.cn/samples_v4/p11k/p11k-watermark/%E8%8B%B1%E5%86%A0%E7%8E%89/1094bd157174dd04.jpg\",\"LatinName\":\"Parodia magnifica\",\"Name\":\"英冠玉\"},{\"Score\":\"4.02\",\"AliasList\":[],\"Genus\":\"乳突球属\",\"InfoCode\":\"AX1tJWp1Nb8YvSRW\",\"AliasName\":\"\",\"Family\":\"仙人掌科\",\"ImageUrl\":\"https://static.nongbangzhu.cn/samples_v4/p11k/p11k-watermark/%E9%87%91%E6%89%8B%E6%8C%87/114987fb740a1924.jpg\",\"LatinName\":\"Mammillaria elongata\",\"Name\":\"金手指\"},{\"Score\":\"3.82\",\"AliasList\":[],\"Genus\":\"星球属\",\"InfoCode\":\"WcLFuSRun8sn734l\",\"AliasName\":\"\",\"Family\":\"仙人掌科\",\"ImageUrl\":\"https://static.nongbangzhu.cn/samples_v4/p11k/p11k-watermark/%E6%98%9F%E7%90%83/1010c2efa196d874.jpg\",\"LatinName\":\"Astrophytum asterias\",\"Name\":\"星球\"}]}";
        return jsonStr;
    }

    public static String testPlantPediaUI(){
        String jsonStr="{\"status\":0,\"message\":\"OK\",\"result\":{\"nameStd\":\"金琥\",\"nameLt\":\"Echinocactus grusonii\",\"familyCn\":\"仙人掌科\",\"genusCn\":\"金琥属\",\"alias\":\"\",\"description\":\"野生的金琥是极度濒危的稀有植物。金琥球体浑圆碧绿，刺色金黄，刚硬有力，为强刺类品种的代表种。\",\"info\":{\"xgsc\":\"\",\"jzgy\":\"盆栽可长成规整的大型标本球，点缀厅堂，更显金碧辉煌，为室内盆栽植物中的佳品。\",\"hyyy\":\"金琥有避邪、镇宅的寓意。也有人叫“金虎”，寓意“老虎看家”，金琥是抗击逆境的勇者，他有坚硬的外刺，可以抵挡一切外来的袭击，有化煞的作用。\",\"fbdq\":\"中国南方、北方均有引种栽培。\",\"mcll\":\"\",\"yhjs\":\"金琥仙人球喜高温，干燥环境，冬季室温白天要保持在20度以上，夜间温度不低于10度，温度过低容易造成根系腐烂。要求阳光充足，但在夏季不能强光暴晒，需要适当遮荫。宜于排水良好的沙质土壤生长。\",\"bxtz\":\"茎圆球形，单生或成丛，高1.3米，直径80厘米或更大。球顶密被金黄色绵毛。有棱21-37，显著。刺座很大，密生硬刺，刺金黄色，后变褐，有辐射刺8-10，3厘米长，中刺3-5，较粗，稍弯曲，5厘米长。花生于球顶部绵毛丛中，钟形，4-6厘米，黄色，花筒被尖鳞片。 \",\"hksj\":\"6-10月开花。\"},\"images\":[\"https://api.aiplants.cn/resource/1/金琥/2953623.jpg\",\"https://api.aiplants.cn/resource/1/金琥/1530688.jpg\",\"https://api.aiplants.cn/resource/1/金琥/2770833.jpg\",\"https://api.aiplants.cn/resource/1/金琥/2988153.jpg\"]}}";
        return jsonStr;
    }

    public static Elements search_zw3e(String keyword) throws IOException {
        Document document= Jsoup.connect(zw3e_website+keyword).get();
        Elements searchResultElements=document.select("div.zw-list");
        return searchResultElements;
    }

    public static List<ArticleBrief> getArticlesBrief(Elements searchResultElements) throws IOException {
        List<ArticleBrief> articleBriefList=new ArrayList<>();
        for (Element element:searchResultElements){
            Elements info=element.select("div.zw-list-map")
                    .select("a[href]");
            Elements briefText=element.select("div.zw-list-msg")
                    .select("p.zw-list-desc");
            ArticleBrief articleBrief=new ArticleBrief();
            articleBrief.articleTitle=info.attr("title");
            articleBrief.briefInfo=briefText.text()+"......";
            articleBrief.articleUrl=info.attr("href");
            articleBrief.picUrl=info.select("img").attr("src");
            //Log.e("Article", articleBrief.articleTitle+"\n"+articleBrief.articleUrl);
            articleBriefList.add(articleBrief);
        }
        Log.e("Article", "返回结果："+articleBriefList.size());
        return articleBriefList;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private static String makeUA() {
        final String ua= Build.BRAND + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE;
        Log.e("User-agent", "makeUA: "+ua);
        return ua;
    }
}
