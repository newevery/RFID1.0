package com.xe.lzh.rfid.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GsonUtils {

    private static final String TAG = GsonUtils.class.getSimpleName();

    /**
     * GSON解析json字符串返回List集合
     */
    public static <T> List<T> getBeansFromGson(String jsonData,
                                               TypeToken<ArrayList<T>> token) {
        ArrayList<T> list = new ArrayList<T>();
        Gson gson = new Gson();
        try {
            list = gson.fromJson(jsonData, token.getType());
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return list;
    }
}
