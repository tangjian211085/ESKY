package com.puhui.lib.base;

public interface IBasePresenter<V extends IBaseView> {
    void attach(V view);

    void detach();
}
