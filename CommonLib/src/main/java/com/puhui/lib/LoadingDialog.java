package com.puhui.lib;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 描    述:  <描述>
 * 修 改 人:  tangjian
 * 修改时间:  2016/6/20
 */
@SuppressWarnings("unused")
public class LoadingDialog {
    private Dialog dialog;
    private TextView textView;
    private AnimationDrawable ad;

    public static LoadingDialog getInstance(Context context) {
        return new LoadingDialog(new WeakReference<>(context));
    }

    private LoadingDialog(WeakReference<Context> context) {
        if (context.get() != null) {
            View view = View.inflate(context.get(), R.layout.loading_dialog, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.load_text);
            textView.setText("加载中...");

            ad = (AnimationDrawable) context.get()
                    .getResources().getDrawable(R.drawable.loading_progress_round);
            imageView.setImageDrawable(ad);
            ad.start();

            dialog = new Dialog(context.get(), R.style.loading_dialog);// 加入样式
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            if (null != window) {
                window.setGravity(Gravity.CENTER);
                window.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    public void show() {
        if (null != dialog && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void show(String content) {
        if (null != dialog && !dialog.isShowing()) {
            textView.setText(content);
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            if (ad.isRunning()) {
                ad.stop();
            }
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setCancelable(boolean canCancel) {
        if (null != dialog) {
            dialog.setCancelable(canCancel);
        }
    }
}
