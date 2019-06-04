package com.bhesky.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bhesky.app.R;
import com.bhesky.app.bean.User;
import com.bhesky.app.utils.sqlite.DaoFactory;
import com.puhui.lib.base.BaseActivity;
import com.puhui.lib.utils.AppManager;
import com.puhui.lib.utils.DensityUtil;
import com.puhui.lib.utils.PHConstant;

import java.util.Random;

public class SplashActivity extends BaseActivity {
    private ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 点击安装包进行安装，安装结束后不点击完成，而是点击打开应用，应用启动后，再回到桌面，
        // 从桌面点击应用图标会造成反复重启应用的现象。
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            this.finish();
            return;
        }
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getAppManager().getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        boolean isUpdateDatabase = PHConstant.DataBase.needUpdate;  //是否需要升级数据库
        //sqliteVersion: 升级后数据库版本   clazz: 需要升级的数据库表
        DaoFactory.getInstance().init(this, PHConstant.DataBase.Version, User.class);

        boolean b = new Random().nextBoolean();
        if (b || isUpdateDatabase) {  //需要显示广告
            final View view = View.inflate(this, R.layout.activity_splash, null);
            setContentView(view);
            splashImage = findViewById(R.id.splash_image);
            startAnimate(splashImage);
        } else {  //不需要显示广告的时候
            redirectTo();
        }
    }

    @Override
    protected void setStatusBar() {
    }

    // 渐变展示启动屏
    private void startAnimate(View view) {
        AnimationSet as = new AnimationSet(true);

        AlphaAnimation aa = new AlphaAnimation(0.7f, 1.0f);
        aa.setDuration(1500);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) splashImage.getLayoutParams();
        int pivotY = (DensityUtil.getScreenHeight(this)
                - layoutParams.bottomMargin - splashImage.getPaddingBottom()) / 2;
        ScaleAnimation sa = new ScaleAnimation(
                0.7f, 1.0f, 0.7f, 1.0f,
                DensityUtil.getScreenWidth(this) / 2, pivotY);
        sa.setDuration(0);

        as.addAnimation(aa);
        as.addAnimation(sa);
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        view.startAnimation(as);
    }

    /**
     * 跳转页面
     */
    protected void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
