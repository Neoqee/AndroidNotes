package com.neoqee.javalib;

import android.app.Application;

import com.billy.android.loading.Gloading;
import com.neoqee.commonlib.exception.ExceptionHandler;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.javalib
 * Create by 小孩 on 2020/6/27
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler.getInstance().init(this);
        Gloading.initDefault(new GlobalAdapter());
    }
}
