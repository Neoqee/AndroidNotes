package com.neoqee.commonlib.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * ProjectName : JavaLib
 * PackageName : com.neoqee.commonlib.execption
 * Create by 小孩 on 2020/6/27
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = "ExceptionHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private Map<String, String> paramsMap = new HashMap<>();
    private static ExceptionHandler handler;

    private ExceptionHandler(){}

    public static ExceptionHandler getInstance(){
        if (null == handler){
            handler = new ExceptionHandler();
        }
        return handler;
    }

    public void init(Context context){
        mContext = context;

        //系统默认的UncaughtExceptionHandler
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        //设置该CrashHandler为系统默认的
       Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        if (!handlerException(throwable) && mDefaultHandler != null){
            //如果自己没处理就交给系统处理
            mDefaultHandler.uncaughtException(thread, throwable);
        }else {
            //自己处理
            //杀进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private boolean handlerException(Throwable throwable) {
        collectDeviceInfo(mContext);
        saveCrashInfo2File(throwable);
        return false;
    }

    /**
     * 收集当前App的版本信息以及手机设备的相关信息
     * */
    private void collectDeviceInfo(Context context){
        //获取当前App的版本信息
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (null != packageInfo){
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                paramsMap.put("versionName",versionName);
                paramsMap.put("versionCode",versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //获取所有系统信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                paramsMap.put(field.getName(),field.get(null).toString());
            } catch (IllegalAccessException e) {
                Log.e(TAG,"收集Crash信息时发生了一个错误",e);
//                e.printStackTrace();
            }
        }
    }

    /**
     * 保存收集的Crash信息
     * */
    private void saveCrashInfo2File(Throwable throwable){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();

        String result = writer.toString();
        sb.append(result);
        Log.e(TAG,sb.toString());
//        try {
//            //使用IO流将日志保存到文件
//        }catch (Exception e){
//
//        }

    }

}
