package com.xe.lzh.rfid.Dao;

import android.content.Context;

import com.xe.lzh.rfid.Utils.HttpUtils;

/**
 * Created by Administrator on 2016/9/14.
 */
public class DetailDao extends BaseDao implements HttpUtils.PostCallBack {
    private Context context;
    private String EPC;
    public DetailDao(Context context,String EPC) {
        super(context);
        this.context = context;
        this.EPC = EPC;

    }

    @Override
    protected void do_baseWork(String json) {

    }

    @Override
    public void do_business(String json) {
        super.do_business(json);
    }

    @Override
    public void do_network() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.setPostcallback(this);
        super.do_network();
    }

    @Override
    public void onSuccess(String s, int requestcode) {

    }

    @Override
    public void onFailture(Throwable throwable, boolean b, int requestcode) {

    }
}
