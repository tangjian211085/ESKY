package com.puhui.lib.base;

import java.lang.ref.WeakReference;

public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {
    protected WeakReference<V> mBaseView;

    @Override
    public void attach(V view) {
        mBaseView = new WeakReference<>(view);
    }

    @Override
    public void detach() {
        mBaseView = null;
    }
}
