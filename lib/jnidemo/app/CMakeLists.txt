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