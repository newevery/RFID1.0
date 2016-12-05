package com.xe.lzh.rfid.Dao;

import android.content.Context;

import com.xe.lzh.rfid.Model.BaseMessageModel;
import com.xe.lzh.rfid.activity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;
import java.util.logging.MemoryHandler;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class BaseDao implements IAsyncDao {
    protected Context context;
    protected Handler handler;
    public BaseDao(Context context,Handler handler){
        this.context = context;
        this.handler = handler;
    }
    public BaseDao(Context context){
        this.context=context;
    }

    protected void do_baseWork(String json){
        /**
         * 对json String 做外层处理
         */
        BaseMessageModel datebean = BaseDao.this.GetDataFromJson(json);
        if(datebean!=null){
            if(datebean.success){
                do_business(datebean.data);
            }
        }
    }

    /**
     * 解析success和data
     * @param jsonData
     * @return
     */

    private BaseMessageModel GetDataFromJson(String jsonData) {
        BaseMessageModel baseMessageModel = new BaseMessageModel();
        if(jsonData.contains("\"data\":")){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonData);
                baseMessageModel.success = jsonObject.getBoolean("success");
                baseMessageModel.data=jsonObject.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public void do_business(String json) {

    }
    @Override
    public void do_network() {

    }
}
