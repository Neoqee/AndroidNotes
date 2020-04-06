package com.neoqee.timelinedemo;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static List<DataBean> getData(){
        List<DataBean> dataBeans = new ArrayList<>();
        dataBeans.add(new DataBean("第一次"));
        dataBeans.add(new DataBean("第二次"));
        dataBeans.add(new DataBean("第三次"));
        return dataBeans;
    }
}
