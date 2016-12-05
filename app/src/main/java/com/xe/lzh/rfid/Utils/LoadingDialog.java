package com.xe.lzh.rfid.Utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.xe.lzh.rfid.R;

/**
 * Created by Administrator on 2016/9/2.
 */
public class LoadingDialog extends Dialog {
    private static LoadingDialog loadingDialog = null;
    private Context context = null;

    public LoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static LoadingDialog createDialog(Context context) {
        loadingDialog = new LoadingDialog(context,R.style.CustomProgressDialog);
        loadingDialog.setContentView(R.layout.item_loading);
        loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return loadingDialog;
    }
}
