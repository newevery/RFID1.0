package com.xe.lzh.rfid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.HttpUtils;
import com.xe.lzh.rfid.Utils.RFIDUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/30.
 */
public abstract class BaseActivity extends FragmentActivity implements HttpUtils.PostCallBack {
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.tv_name)
    TextView tb_name;
    @ViewInject(R.id.tv_time)
    TextView tv_time;
    Context content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "当前无网络", Toast.LENGTH_LONG).show();
        }
    }

    public void back(View view) {
        finish();
    }

    public void setting(View view) {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        //      Toast.makeText(getApplicationContext(), "操作ing", Toast.LENGTH_SHORT)
        //              .show();
        //       Log.i(TAG, "操作ing");
        resetTime();
        return super.dispatchTouchEvent(ev);
    }

    private int SHOW_ANOTHER_ACTIVITY = 1;

    private void resetTime() {
        // TODO Auto-generated method stub
        mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);//從消息隊列中移除
        Message msg = mHandler.obtainMessage(SHOW_ANOTHER_ACTIVITY);
        mHandler.sendMessageDelayed(msg, 1000 * 60 * 30);//無操作30分钟后進入屏保
    }

    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == SHOW_ANOTHER_ACTIVITY) {
                //跳到activity
                //               Log.i(TAG, "跳到activity");
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    public void settext(String str) {

        Date date = new Date();
        String datestr = RFIDUtils.sdf.format(date);
        System.out.println(str + datestr);
        tv_time.setText(datestr);
        tv_title.setText(str);
    }

    public void doNetWork(RequestParams params, int requestcode) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.setPostcallback(this);
        httpUtils.dopost(params, requestcode, this);
        List<KeyValue> bodyParams = params.getBodyParams();
        for (KeyValue k : bodyParams) {
            System.out.println(" BaseActivity " + k.getValueStr() + "  " + k.key);
        }
        System.out.println("doNetWork " + params.toString());
    }

    @Override
    public void onSuccess(String s, int requestcode) {
        try {
            if (s != null && !"".equals(s)) {
                JSONObject jsonObject = new JSONObject(s);
                String resultcode = jsonObject.getString("resultcode");
                if (resultcode.equals("200")) {
                    String data = jsonObject.getString("data");
                    setdata(data, requestcode);
                }
            } else {
                Toast.makeText(this, "服务器无数据", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailture(Throwable throwable, boolean b, int requestcode) {

    }

    public abstract void setdata(String data, int requestcode);
}
