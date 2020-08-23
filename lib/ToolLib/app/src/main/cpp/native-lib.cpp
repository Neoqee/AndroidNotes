#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_neoqee_toollib_MyBluetoothService_getByteStringByNative(JNIEnv *env, jobject thiz,
                                                                 jbyteArray bytes, jint len) {
    return env->NewStringUTF("");
}

