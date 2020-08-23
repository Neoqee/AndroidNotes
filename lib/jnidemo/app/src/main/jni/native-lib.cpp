#include "com_neoqee_jnidemo_JniUtil.h"
//#include "jni.h"

//Java_com_neoqee_jnidemo_JniUtil_printStringByJni
//JNIEXPORT jstring JNICALL Jave_com_neoqee_jnidemo_JniUtil_printStringByJni(
//        JNIEnv *env, jclass jclass){
//    return env->NewStringUTF("来自jni的字符串");
//}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_neoqee_jnidemo_JniUtil_printStringByJni(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("来自jni的字符串");
}

