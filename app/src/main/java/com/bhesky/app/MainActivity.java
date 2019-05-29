package com.bhesky.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bhesky.app.bean.User;
import com.bhesky.app.utils.sqlite.BaseDaoFactory;
import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.ToastUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, ProcessService.class));
    }

    public void insert(View view) {
        ToastUtil.getInstant().show(this, "dex加密成功");
        BaseDaoFactory.getInstance().getBaseDao(User.class).insert(
                new User("tangjian", 29, "421583199202116632", "123456"));
    }

    public void query(View view) {
//        User user = BaseDaoFactory.getInstance().getBaseDao(User.class).findFirst();
//        if (null != user) {
//            DMLog.e(this.getClass().getCanonicalName(), user.getIdCardNum());
//        }

        List<User> all = BaseDaoFactory.getInstance().getBaseDao(User.class).findAll();
        DMLog.e(this.getClass().getCanonicalName(), "all.size() = " + all.size());
    }

    public native String getString();
}
