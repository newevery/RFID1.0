package com.example.yzyx.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgressDialog extends ProgressDialog {
  
    private AnimationDrawable mAnimation;
    private Context mContext;  
    private ImageView mImageView;
    private String mLoadingTip;  
    private TextView mLoadingTv;
    private int count = 0;  
    private String oldLoadingTip;  
    private int mResid;  
  
    public CustomProgressDialog(Context context, String content, int id) {
        super(context);  
        this.mContext = context;  
        this.mLoadingTip = content;  
        this.mResid = id;  
        setCanceledOnTouchOutside(true);
        initView();
        initData();
    }

    private void initData() {
  
        mImageView.setBackgroundResource(mResid);  
        // 通过ImageView对象拿到背景显示的AnimationDrawable  
        mAnimation = (AnimationDrawable) mImageView.getBackground();  
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一  
        mImageView.post(new Runnable() {  
            @Override  
            public void run() {  
                mAnimation.start();  
  
            }  
        });  
        mLoadingTv.setText(mLoadingTip);  
  
    }  
  
    public void setContent(String str) {  
        mLoadingTv.setText(str);  
    }  
  
    private void initView() {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.fragment_item, null);
        mLoadingTv = (TextView) inflate.findViewById(R.id.loadingTv);
        mImageView = (ImageView) inflate.findViewById(R.id.loadingIv);
    }  
  
}  