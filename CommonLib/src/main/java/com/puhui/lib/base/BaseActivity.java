package com.puhui.lib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.puhui.lib.LoadingDialog;
import com.puhui.lib.R;
import com.puhui.lib.utils.AppManager;
import com.puhui.lib.utils.ToastUtil;
import com.puhui.lib.widgets.statusbar.StatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {
    private LoadingDialog mDialog;
    private SparseArray<Long> clickedViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mDialog = LoadingDialog.getInstance(this);
        clickedViews = new SparseArray<>();
    }

    /**
     * 判断是否快速连续点击
     *
     * @param viewId 控件id
     */
    protected boolean checkClick(int viewId) {
        if (System.currentTimeMillis() - clickedViews.get(viewId) < 800) {
            clickedViews.put(viewId, System.currentTimeMillis());
            return false;
        }
        return true;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setStatusBar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBar();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.main_color), 0);
    }

    @Override
    public void showToast(String content) {
        ToastUtil.getInstance().show(this, content);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public void startLoading() {
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void stopLoading() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
