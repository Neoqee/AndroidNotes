package com.neoqee.commonlib.http;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit mRetrofit;

    private static RetrofitManager instance = null;

    private static class Holder{
        private static RetrofitManager instance = new RetrofitManager();
    }

    public static RetrofitManager getInstance(){
        if (instance == null){
            instance = Holder.instance;
        }
        return instance;
    }

    private RetrofitManager(){
        mRetrofit = new Retrofit
                .Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    public <T> T createService(Class<T> cls){
        return mRetrofit.create(cls);
    }

}
