# add by Tony 编译本地库时我们需要的最小的cmake版本 (以下所有中文部分)
cmake_minimum_required(VERSION 3.4.1)

# 使用CMakeLists.txt同样可以指定so库的输出路径,但一定要在add_library之前设置，否则不会生效:
# set(CMAKE_LIBRARY_OUTPUT_DIRECTORY
# 	${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI}) #指定路径
# 生成的so库在和CMakeLists.txt同级目录下的libs文件夹下

# add by Tony 相当于Android.mk
# 第一个参数生成函数库的名称, 即libnative-lib.so或libnative-lib.a(lib和.so/.a默认缺省)
# 第二个参数生成库类型: 动态库为SHARED, 静态库为STATIC
# 第三个参数依赖的c/cpp文件(相对路径)
add_library( # Sets the name of the library.  设置编译生成本地库的名字
             native-lib

             # Sets the library as a shared library.  库的类型
             SHARED

             # Provides a relative path to your source file(s).  编译文件的路径
             src/main/cpp/native-lib.cpp )

# 添加一些我们在编译我们的本地库的时候需要依赖的一些库，这里是用来打log的库
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# 关联自己生成的库和一些第三方库或者系统库
target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )