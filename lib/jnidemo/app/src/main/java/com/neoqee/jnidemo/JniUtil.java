package com.neoqee.jnidemo;

public class JniUtil {

    static {
        // native-lib 后面新建的.c 或者.cpp 文件名  在这里可以先注释掉
        System.loadLibrary("native-lib");
    }

    public static native String printStringByJni();

}
