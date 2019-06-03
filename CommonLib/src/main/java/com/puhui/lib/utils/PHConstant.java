package com.puhui.lib.utils;

import android.os.Environment;

import java.io.File;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/3/2.
 * Description:
 * Modified: by TangJian on 2017/3/2.
 */
public interface PHConstant {
    //线上环境
    String BASE_API_URL = "https://www.ydyilu.com/app/";  //外网域名
    //服务器apk名字：  app-guanfang-release.1.2.0.apk

    interface Config {

        /**
         * 默认存放文件下载的路径
         */
        String DEFAULT_SAVE_FILE_PATH = Environment.getExternalStorageDirectory()
                + File.separator + "puhui" + File.separator + "download";

        //版本更新时APK的名字
        String APK_NAME = "tou_hao_qian_zhuang.apk";


        boolean isNeedUploadErrorInfo = true;  //是否上传错误日志

        boolean isUseLog = true;  //测试时，该值为true；上线改为false

    }

    interface HTTP_STATUS {
        String SUCCESS = "000000";
    }

    interface DataBase {
        int Version = 2;  //需要升级的时候，将该值+1
        boolean needUpdate = false;  //是否需要升级
    }

}
