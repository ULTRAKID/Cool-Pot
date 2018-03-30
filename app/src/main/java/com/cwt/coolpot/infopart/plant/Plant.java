package com.cwt.coolpot.infopart.plant;

import java.io.Serializable;
import java.util.List;

/**
 * 外层为图片识别后的一些属性
 * 内层为植物类ResultBean
 *      名称
 *      拉丁名
 *      外号
 *      科名
 *      属名
 *      可能性
 *      图片链接
 *      植物百科信息获取接口需要的代号
 * Created by 曹吵吵 on 2017/10/16 0016.
 */

public class Plant implements Serializable {

    public final static String SER_KEY="com.cwt.coolpot.infopart.plant";
    private static final long serialVersionUID = 7068555515952324858L;
    public int Status;
    public String Message;
    public List<ResultBean> Result;
    public final static String STATUS_1001="无法获取图片信息";
    public final static String STATUS_1002="图片过大，上传失败";
    public final static String STATUS_1003="植物识别失败";

    public class ResultBean implements Serializable{
        private static final long serialVersionUID = 7133954966402008497L;
        public String Name;
        public String LatinName;
        public String AliasName;
        public String AliasList[]; //可能需要改为List
        public String Family;
        public String Genus;
        public double Score;
        public String ImageUrl;
        public String InfoCode;

    }

}
