package com.bhesky.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhesky.app.R;
import com.bhesky.app.ui.test.SqliteActivity;
import com.puhui.lib.base.BaseActivity;
import com.puhui.lib.utils.AppManager;
import com.puhui.lib.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.title_text)
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //这里调用会影响启动页跳转到MainActivity的效果
//        startService(new Intent(getApplicationContext(), LocalService.class));

    }

    public void sqliteFrame(View view) {
        startActivity(new Intent(this, SqliteActivity.class));
//        DaoFactory.getInstance().getBaseDao(User.class).insert(
//                new User("tangjian", 29, "421583199202116632", "123456"));
    }

    public void query(View view) {
        ToastUtil.getInstance().show(this, "dex加密成功");
//        User user = DaoFactory.getInstance().getBaseDao(User.class).findFirst();
//        if (null != user) {
//            DMLog.e(this.getClass().getCanonicalName(), user.getIdCardNum());
//        }
    }

    private long lastClickTime;  //保存上一次点击返回按钮的时间，用来判断是否退出应用

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime <= 2000) {
                AppManager.getAppManager().AppExit();
            } else {
                lastClickTime = clickTime;
                ToastUtil.getInstance().show(this, "再按一次退出");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
