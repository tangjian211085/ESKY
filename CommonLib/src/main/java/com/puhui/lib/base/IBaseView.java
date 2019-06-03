package com.puhui.lib.base;

import android.content.Context;

public interface IBaseView {
    void showToast(String content);

    Context getContext();

    BaseActivity getActivity();

    void startLoading();

    void stopLoading();
}
