package com.bhesky.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bhesky.app.bean.User;
import com.bhesky.app.bean.UserDao;
import com.bhesky.app.utils.sqlite.DaoFactory;
import com.puhui.lib.utils.DMLog;
import com.puhui.lib.utils.SharedPreferenceUtils;
import com.puhui.lib.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SqliteActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private UserDao<User> mBaseDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        mUnbinder = ButterKnife.bind(this);

        //模拟数据库升级
        SharedPreferenceUtils.put(this, SharedPreferenceUtils.ACCESS_COOKIE, 1);
        mBaseDao = DaoFactory.getInstance().getDao(UserDao.class, User.class);
    }

    @OnClick({R.id.insert, R.id.insertAll, R.id.query, R.id.queryAll, R.id.update, R.id.updateAll, R.id.delete, R.id.deleteAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.insert:
                insertOne();
                break;
            case R.id.insertAll:
                insertAll();
                break;
            case R.id.query:
                queryOne();
                break;
            case R.id.queryAll:
                queryAll();
                break;
            case R.id.update:
                updateOne();
                break;
            case R.id.updateAll:
                updateAll();
                break;
            case R.id.delete:
                deleteOne();
                break;
            case R.id.deleteAll:
                deleteAll();
                break;
        }
    }

    private void showToast(String content) {
        ToastUtil.getInstant().show(this, content);
    }

    private void insertOne() {
        User user = newUser(0);
        user.photo = "photoUrl".getBytes();
        mBaseDao.insert(newUser(0));
    }

    private void insertAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> users = new ArrayList<>();
                for (int i = 1; i < 100; i++) {
                    users.add(newUser(i));
                }
                mBaseDao.insertAll(users);
            }
        }).start();
    }

    /**
     * userId,  realName,  age,  idCardNum,  height,  password,  tradePassword,  remainAmount,  totalAmount,
     * double investAmount,  emailAddress,  qqNumber,  wechatAccount,
     * nickName,  school, byte[] photo,  homeAddress,  wordAddress,  bankCardNum, boolean isVip
     */
    private User newUser(int index) {
        User user = new User(index + "", "汤健" + index, 29 + index, "123456789" + index, 165, "123456", "123456",
                65535.65, 65535.65, 65535.65, "790548744@qq.com", "790548744", "tj211095", "冰神殿", "三峡大学",
                "中国湖北武汉汉阳", "中国湖北武汉汉阳", "12345789" + index, index % 2 == 0);
        user.photo = "photoUrl".getBytes();
        return user;
    }

    private void queryOne() {
        User user = mBaseDao.findFirst();
        if (null != user) {
            byte[] bytes1 = "photoUrl".getBytes();
            StringBuilder byte2 = new StringBuilder();
            for (byte b : bytes1) {
                byte2.append(b);
            }

            StringBuilder bytes = new StringBuilder();
            for (byte b : user.photo) {
                bytes.append(b);
            }
            DMLog.e(this.getClass().getCanonicalName(), "存入时photo内容：" + byte2.toString());
            DMLog.e(this.getClass().getCanonicalName(), "photo内容：" + bytes.toString());
            showToast(user.getHomeAddress() + user.isVip());
        }
    }

    private void queryAll() {
        int totalCount = mBaseDao.queryTotalCount();
        showToast("数据库里有" + totalCount + "条数据");
    }

    private void updateOne() {

    }

    private void updateAll() {

    }

    private void deleteOne() {

    }

    private void deleteAll() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }
}
