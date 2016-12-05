package com.xe.lzh.rfid.Dao;

import android.content.Context;

import com.google.gson.Gson;
import com.xe.lzh.rfid.Utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class ZhuiSudao extends BaseDao implements HttpUtils.PostCallBack{
private Context context;
    private String danghao;

    public ZhuiSudao(Context context , String danghao) {
        super(context);
        this.context=context;
        this.danghao=danghao;

    }
    @Override
    public void do_business(String s) {
        Gson gson = new Gson();
//        List list = GsonUtils.getBeansFromGson(s,new TypeToken<ArrayList>(model));
    }

    @Override
    public void do_network() {
        HttpUtils httpUtils =new HttpUtils();
        httpUtils.setPostcallback(this);
        RequestParams params = new RequestParams("url");
        params.addBodyParameter("danghao",danghao);


        httpUtils.dopost(params, 1, context);

    }


    @Override
    public void onSuccess(String s, int requestcode) {
        switch (requestcode){
            case 1:
//                do_baseWork(s);
                Gson gson = new Gson();


                try {
                    JSONObject jsonObject = new JSONObject(s);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailture(Throwable throwable, boolean b, int requestcode) {
        switch(requestcode) {
            case 1:
                break;
        }
    }
}
