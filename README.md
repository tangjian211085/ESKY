# ESKY
NDK层实现进程保活（后续会完善到ndk使用双进程保活）、手写数据库框架(学习greenDao原理，持续完善中......)

#### 进程保活目录
    \app\src\main\cpp\native-lib.cpp

#### 数据库代码目录
    \app\src\main\java\com\bhesky\app\utils\sqlite
    插入
        User user = new User("tangjian", 29, "421583199202116632", "123456");
        BaseDaoFactory.getInstance().getBaseDao(User.class).insert(user));
    查找
        List<User> all = BaseDaoFactory.getInstance().getBaseDao(User.class).findAll();