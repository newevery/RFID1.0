package com.xe.lzh.rfid.Utils;


import com.xe.lzh.rfid.Model.EpcModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/8/31.
 * 日期工具类，请求服务器地址
 */
public class
RFIDUtils {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    public static Comparator comp = new Comparator() {
        public int compare(Object o1, Object o2) {
            EpcModel p1 = (EpcModel) o1;
            EpcModel p2 = (EpcModel) o2;
            if (p1.getCount() > p2.getCount())
                return -1;
            else if (p1.getCount() == p2.getCount())
                return 0;
            else if (p1.getCount() < p2.getCount())
                return 1;
            return 0;
        }
    };

    //接口
//    public static String ip = "http://172.23.39.9:8080/android/";
    public static String ip = "http://192.168.0.124:8080/android/";
    public static String FINDDETAIL = ip + "findDetail.do";//查看详细的EPC信息
    public static String ALLKU = ip + "allKU.do";//从服务器读取所有的档案信息  按库盘点
    public static String SELECT = ip + "searchId.do";//查找指定档案号的档案   盘点时选择库号
    public static String ZHUISU = ip + "traceBack.do";//追溯某档案
    public static String RUKU = ip + "updateData.do";//修改档案状态
    public static String DENGLU = ip + "userlogin.do";//用户登陆
    public static String AKPANDIAN = ip + "akPanDian.do";
    public static String AXPANDIAN = ip + "axPanDian.do";
    public static String UPDATEBOX = ip + "updateDataBoxNum.do";
}
