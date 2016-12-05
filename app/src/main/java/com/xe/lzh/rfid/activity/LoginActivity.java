package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xe.lzh.rfid.Model.UserModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2016/9/7 0007.
 */

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
//    @ViewInject(R.id.tv_saoma)
//    TextView saoma;

    @ViewInject(R.id.tv_login)
    TextView denglu;
    @ViewInject(R.id.et_username)
    EditText user;
    @ViewInject(R.id.et_dlmm)
    EditText dlmm;

    private String userid;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Event(value = {R.id.tv_login})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                donet();
                break;
//            case R.id.tv_saoma:
//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, SaoMaLoginActivity.class);
//                startActivity(intent);
//
//                break;

        }
    }

    private void donet() {

        userid = user.getText().toString().trim();
        String password = dlmm.getText().toString().trim();
        if (TextUtils.isEmpty(userid) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            RequestParams params = new RequestParams(RFIDUtils.DENGLU);
            params.addBodyParameter("username", userid);
            params.addBodyParameter("password", password);
            doNetWork(params, 0);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data != null) {
//            userid = data.getExtras().getString("userid");//得到新Activity关闭后返回的数据
//            username = data.getExtras().getString("username");//得到新Activity关闭后返回的数据
//
//        } else {
//            Toast.makeText(LoginActivity.this, "您没有选择操作人！", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    public void setdata(String data, int requestcode) {
        System.out.println("LoginActivity "+data);
        try {
            if (data != null && !data.equals("")) {
                Gson gson = new Gson();
                JSONObject jsonObject = new JSONObject(data);
                String state = jsonObject.getString("state");
                if (!state.equals("1")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    UserModel userModel = gson.fromJson(jsonObject.getString("user"), UserModel.class);
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    SpUtils.put(this, "username", userModel.getUSERNAME());
                    SpUtils.put(this, "userid", userModel.getID());
                    startActivity(intent);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
