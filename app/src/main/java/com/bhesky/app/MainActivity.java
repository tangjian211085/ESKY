package com.bhesky.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bhesky.app.bean.User;
import com.bhesky.app.utils.sqlite.DaoFactory;
import com.puhui.lib.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaoFactory.checkUpdate(this, 2, User.class);
//        startService(new Intent(this, ProcessService.class));
    }

    public void sqliteFrame(View view) {
        startActivity(new Intent(this, SqliteActivity.class));
//        DaoFactory.getInstance().getBaseDao(User.class).insert(
//                new User("tangjian", 29, "421583199202116632", "123456"));
    }

    public void query(View view) {
        ToastUtil.getInstant().show(this, "dex加密成功");
//        User user = DaoFactory.getInstance().getBaseDao(User.class).findFirst();
//        if (null != user) {
//            DMLog.e(this.getClass().getCanonicalName(), user.getIdCardNum());
//        }
    }
}
