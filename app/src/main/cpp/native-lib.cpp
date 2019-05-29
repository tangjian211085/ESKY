#include <jni.h>
#include <string>
#include "native_lib.h"


const char *PATH="/data/data/com.bhesky.app/my.sock";
int m_child;
const char *userId;

void child_do_work(){
    //开启socket 服务端
    if(child_create_channel()){
        child_listen_msg();
    }
}

/**
* 服务端读取信息
* 客户端是apk进程
*/
int child_create_channel(){
    //ip  端口  文件
    int listenFd = socket(AF_LOCAL, SOCK_STREAM, 0);
    // addr 内存区域
    struct sockaddr_un addr;

    unlink(PATH);
    //清空内存
    memset(&addr, 0, sizeof(sockaddr_un));

    addr.sun_family = AF_LOCAL;

    strcpy(addr.sun_path, PATH);

    if(bind(listenFd, (const sockaddr *) &addr, sizeof(sockaddr_un)) < 0){
        LOGE("绑定错误");
        return 0;
    }

    int connFd = 0;
    listen(listenFd, 5);
    // 保证宿主进程（客户端）连接成功
    while(1){
        //返回值  客户端地址  阻塞式函数
        if((connFd = accept(listenFd, NULL, NULL)) < 0){
            if(errno == EINTR){
                continue;
            } else {
                LOGE("读取错误");
            }
            return 0;
        }

        m_child = connFd;
        LOGE("apk 父进程连接上了 %d", m_child);
        break;
    }

    return 1;
}

/**
* 创建服务端的socket
*/
void child_listen_msg(){
    fd_set rfds;
    struct timeval timeout = {3, 0};
    while(1){
        //清空内容
        FD_ZERO(&rfds);
        FD_SET(m_child, &rfds);
        //4个客户端
        int r = select(m_child + 1, &rfds, NULL, NULL, &timeout);
        LOGE("读取消息前  %d", r);
        sleep(2);
        if(r > 0){
            //缓冲区
            char pkg[256] = {0};

            //保证所读到的信息是  指定apk客户端
            if(FD_ISSET(m_child, &rfds)){
                //阻塞式函数  其实这里什么都不用读
                int result = read(m_child, pkg, sizeof(pkg));

                LOGE("userId is %s", userId);
                execlp("am", "am", "startservice", "--user", userId,
                 "com.bhesky.app/com.bhesky.app.ProcessService", (char*)NULL);
                return;
            }
        }
    }
}


extern "C" JNIEXPORT void JNICALL
Java_com_bhesky_app_Watcher_connectMonitor(JNIEnv *env, jobject instance){
    //客户端
    int socked;
    // addr 内存区域
    struct sockaddr_un addr;

    while(1){
        LOGE("客户端，父进程开始连接");
        socked = socket(AF_LOCAL, SOCK_STREAM, 0);
        if(socked < 0){
            LOGE("连接失败");
            return;
        }

        //清空内存
        memset(&addr, 0, sizeof(sockaddr));

        addr.sun_family = AF_LOCAL;

        strcpy(addr.sun_path, PATH);

        if(connect(socked, (const sockaddr *) &addr, sizeof(sockaddr_un)) < 0){
            LOGE("connect 连接失败");
            close(socked);
            sleep(2);
            continue;  //睡眠两秒尝试下一次连接
        }

        LOGE("连接成功");
        break;
    }

}


extern "C" JNIEXPORT void JNICALL
Java_com_bhesky_app_Watcher_createWatcher(JNIEnv *env, jobject instance, jstring userId_){
    userId = env->GetStringUTFChars(userId_, 0);

    //开启双进程
    pid_t pid = fork();  //fork函数会有两个返回值  fork下面的代码会执行两次
    if(pid < 0){

    }else if(pid == 0){
        //该代码运行在子进程中  守护进程
        child_do_work();
    }else if(pid > 0){
        //该代码运行在父进程中
    }

    env->ReleaseStringUTFChars(userId_, userId);
}
