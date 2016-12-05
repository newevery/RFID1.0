package com.xe.lzh.rfid.Utils;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Toast;

import com.xe.lzh.rfid.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class HttpUtils {
    private PostCallBack postcallback;
    private int irequestcode;
    private LoadingDialog loadingDialog = null;

    public PostCallBack getPostcallback() {
        return postcallback;
    }

    public void setPostcallback(PostCallBack postcallback) {
        this.postcallback = postcallback;
    }

    public interface PostCallBack {
        void onSuccess(String s, int requestcode);

        void onFailture(Throwable throwable, boolean b, int requestcode);
    }

    public void dopost(final RequestParams params, int requestcode, final Context context) {
        loadingDialog =LoadingDialog.createDialog(context);
        loadingDialog.show();
        this.irequestcode = requestcode;

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                System.out.println("服务器返回 " + s);
                postcallback.onSuccess(s, irequestcode);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

                postcallback.onFailture(throwable, b, irequestcode);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                    Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
