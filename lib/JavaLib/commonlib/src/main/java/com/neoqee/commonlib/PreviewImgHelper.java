package com.neoqee.commonlib;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib
 * Create by 小孩 on 2020/6/21
 */
public class PreviewImgHelper {

    private PreviewImgHelper(){}

    public volatile static PreviewImgHelper instance = null;

    public static PreviewImgHelper getInstance(){
        if (null == instance){
            synchronized (PreviewImgHelper.class){
                if (null == instance){
                    instance = new PreviewImgHelper();
                }
            }
        }
        return instance;
    }

    public void preview(Context context, String url){
        List<String> urls = new ArrayList<>();
        urls.add(url);
        preview(context,urls,0);
    }

    public void preview(Context context, List<String> urls, int startPosition){
        GalleryActivity.startAction(context, (ArrayList<String>) urls,startPosition);
    }

}
