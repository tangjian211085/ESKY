package com.puhui.lib.widgets.listener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/3/2.
 * Description: EditText控件的文字输入变化监听实现
 * Modified: by TangJian on 2017/3/2.
 */

public abstract class TextWatcherImpl implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        DMLog.e("beforeTextChanged", "beforeTextChanged = " + s.toString());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
