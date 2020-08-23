

## 编译头文件

进行JNI开发时，在java代码中声明好了native方法后需要使用javah指令生成.h头文件

javah -jni 包名+类名



## JNI编译错误：错误: 编码GBK的不可映射字符

**这是因为执行的类中包含了中文字符，需要去掉中文，或者*使用*UTF-8编码执行；出现这种错误的原因是由于JDK是国际版的，在编译的时候，如果我们没有用-encoding参数指定我们的JAVA源程序的编码格式，则javac.exe首先获得我们操作系统默认采用的编码格式。**

解决：在使用javah命令时，指明编码格式

比如:javah -jni -encoding UTF-8 后续命令



# JNI基础

## 1、配置ndk环境：

通过Android studio的SDK manager下载ndk和cmake，不进行细化选择会默认下载当前sdk的最新版本，下载完成可以在个人的Android sdk目录下找到，对应文件夹下有以版本为名的文件夹

如果是Android 4.0+的，下载完成即可，如果是以下的，需要根据版本添加配置

参考链接：https://developer.android.com/studio/projects/configure-agp-ndk?language=agp3-6

3.6+：

需要在local.properties文件下添加

```
cmake.dir=D\:\\Android\\SDK\\cmake\\3.10.2.4988404	//cmake的文件夹
ndk.dir=D\:\\Android\\SDK\\ndk\\21.3.6528147		//ndk文件夹
```

## 2、创建cpp源文件夹

在src/main下创建cpp文件夹，然后再创建c/cpp source file，文件名就相当于是native库，然后如果需要，还可以添加CMakeList.txt，名字固定，是CMake的编译脚本文件

其实这个文件的放置位置也可以在其他地方，只要后面在gradle中声明路径即可

CmakeList.txt：

```
    # Cmake编译脚本配置文件
    # Sets the minimum version of CMake required to build your native library.
    # This ensures that a certain set of CMake features is available to
    # your build.

    # 1. 标注需要支持的CMake最小版本
    cmake_minimum_required(VERSION 3.4.1)

    # Specifies a library name, specifies whether the library is STATIC or
    # SHARED, and provides relative paths to the source code. You can
    # define multiple libraries by adding multiple add_library() commands,
    # and CMake builds them for you. When you build your app, Gradle
    # automatically packages shared libraries with your APK.

    # 2. add_library 定义需要编译的代码库 名称, 类型, 包含的源码
    add_library( # Specifies the name of the library.
                 native-lib

                 # Sets the library as a shared library.
                 SHARED

                 # Provides a relative path to your source file(s).
                 src/main/jni/native-lib.cpp )

    # 3. find_library 定义当前代码库需要依赖的系统或者第三方库文件(可以写多个)
    find_library( # Defines the name of the path variable that stores the
                  # location of the NDK library.
                  log-lib

                  # Specifies the name of the NDK library that
                  # CMake needs to locate.
                  log )

    # 4. target_link_libraries设置最终编译的目标代码库
    # You need to link static libraries against your shared native library.
    target_link_libraries(
                    native-lib      # add_library 生成的
                    ${log-lib}      # find_library 找到的系统库
                    )
```

native-lib.cpp：

```c++
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_neoqee_toollib_MyBluetoothService_getByteStringByNative(JNIEnv *env, jobject thiz,
                                                                 jbyteArray bytes, jint len) {
    return env->NewStringUTF("");
}


```

方法名根据java类及定义的方法名不同而不同，使用包名+类名+方法名形成一种映射关系，从而达到相互访问

## 3、gradle配置

```groovy
android{
	defaultConfig{
		// 设置c++的一些配置
		externalNativeBuild {
            cmake {
                cppFlags ""
            }
        }
        // 设置abi
        ndk {
            // Specifies the ABI configurations of your native
            // libraries Gradle should build and package with your APK.
            abiFilters 'armeabi-v7a', 'arm64-v8a'
        }
	}
	// 这里也是设置c++的一些配置
	externalNativeBuild {
        cmake {
            path 'src/main/cpp/CMakeLists.txt'	// CMakeList.txt的文件路径
            version "3.10.2"					// Cmake的版本
        }
    }
}
```



# 后续

如何自动生成或者命令生成so库，不是太了解，还需后续了解