package com.cwt.coolpot.infopart.plant;

import java.io.Serializable;
import java.util.List;

/**
 * 【注】各字段含义说明：
 nameStd      中文名
 nameLt       拉丁名
 familyCn     中文科名
 genusCn      中文属名
 alias        别名
 description  植物简要介绍
 info         其他信息对象
 xgsc         相关诗词
 jzgy         价值功用
 hyyy         花语寓意
 fbdq         分布地区
 mcll         名称来历
 yhjs         养护技术
 bxtz         表型特征
 hksj         花开时节
 imagesRv       几张该植物的代表图
 * Created by 曹吵吵 on 2018/3/14 0014.
 */

public class PlantPedia implements Serializable {
    private static final long serialVersionUID = 2501841339129491564L;
    public final static String SER_KEY="com.cwt.coolpot.infopart.plantpedia";
    public int Status;
    public String Message;
    public resultBean result;

    public class resultBean implements Serializable{
        private static final long serialVersionUID = -4408795222488969082L;
        public String nameStd;
        public String nameLt;
        public String familyCn;
        public String genusCn;
        public String alias;
        public String description;      //缩进处理
        public infoBean info;
        public List<String> images;

        public class infoBean implements Serializable{
            private static final long serialVersionUID = 4011596679112990277L;
            public String xgsc;
            public String jzgy;      //缩进处理
            public String hyyy;      //缩进处理
            public String fbdq;
            public String mcll;      //缩进处理
            public String yhjs;      //缩进处理
            public String bxtz;      //缩进处理
            public String hksj;
        }
    }
}
