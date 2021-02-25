package com.tsingning.attendancelibrary;

public class DetectStatus {

    public static final int CHECK_START = 0x10;
    public static final int CHECK_END = 0x11;

    public static final int MODE_FAST = 0x20;
    public static final int MODE_ACCURATE = 0x21;
    public static final int MODE_UN_QUALITY = 0x22;
    public static final int MODE_FEATURE = 0x23;

    private static int status = 0;
    private static int mode = MODE_FAST;

    public static boolean isCheckStart(){
        return status == CHECK_START;
    }

    public static void setStatus(int s){
        status = s;
    }

    public static int getStatus(){
        return status;
    }

    public static void setMode(int m){
        mode = m;
    }

    public static int getMode(){
        return mode;
    }

}
