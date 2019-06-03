package com.bhesky.app.ui;

import android.os.Bundle;

import com.bhesky.app.R;
import com.bhesky.app.mvp.presenter.impl.LoginPresenter;
import com.bhesky.app.mvp.view.ILoginView;

public class LoginActivity extends BaseMvpActivity<LoginPresenter> implements ILoginView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }
}
