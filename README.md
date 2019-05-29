# ESKY
<ol>
  <li>NDK层实现进程保活（后续会完善到ndk使用双进程保活）；</li>
  <li>手写数据库框架(学习greenDao原理，持续完善中......)；</li>
  <li>项目dex分包、代码混淆和dex加密</li>
  <li>待续......</li>
</ol>

#### 进程保活目录
    \app\src\main\cpp\native-lib.cpp
    ( AS 3.2.1   创建新项目时，在第一步勾选 Include C++ support，在最后一步C++ Standard选择C++11，创建项目后写C代码会有提示效果。
    已有项目可将app/build.gradle文件下的cppFlags设置为: cppFlags "-std=c++11" )

#### 数据库代码目录
    \app\src\main\java\com\bhesky\app\utils\sqlite
    插入
        User user = new User("tangjian", 29, "421583199202116632", "123456");
        BaseDaoFactory.getInstance().getBaseDao(User.class).insert(user));
    查找
        List<User> all = BaseDaoFactory.getInstance().getBaseDao(User.class).findAll();

#### dex打包加密
    参看proxy_tool中Main.java以及proxy_core中ProxyApplication类
    ![logo](/images/dex_encrypt.png)