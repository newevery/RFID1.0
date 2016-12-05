package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xe.lzh.rfid.Model.DeatailModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.Utils.ZhuangTaiUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 暂时无用
 */

@ContentView(R.layout.activity_xiangxixinxi)
public class XiangxixinxiActivity extends BaseActivity{
    @ViewInject(R.id.tv_danghao)
    TextView danghao;
    @ViewInject(R.id.tv_zhuangtai)
    TextView zhuangtai;
    @ViewInject(R.id.tv_jieyueren)
    TextView jieyueren;
    @ViewInject(R.id.tv_caozuoren)
    TextView caozuoren;
    @ViewInject(R.id.tv_shijian)
    TextView shijian;
    @ViewInject(R.id.bt_guanbi)
    Button guanbi;
    @ViewInject(R.id.tv_ztm)
    Button tv_ztm;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    String loginUser_id;
    String loginUser_name;
    private String EPC;
    private DeatailModel deatailModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent intent = this.getIntent();
        EPC= (String) intent.getSerializableExtra("EPC");
        doNet();
        loginUser_id = (String) SpUtils.get(this, "userid", "1");
        loginUser_name = (String) SpUtils.get(this, "username", "操作人");
        tv_name.setText("欢迎您，"+loginUser_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        settext("详细信息");

    }

    @Event(value = {R.id.bt_guanbi})
    private void Click(View v) {
        switch (v.getId()) {
            case R.id.bt_guanbi:
                finish();
                break;
        }
    }
    private void doNet(){
        RequestParams params = new RequestParams(RFIDUtils.FINDDETAIL);
        params.addBodyParameter("EPC",EPC);
        doNetWork(params,0);
    }


    @Override
    public void setdata(String data, int requestcode) {
        switch (requestcode){
            case 0:

                Gson gson = new Gson();
                deatailModel = gson.fromJson(data,DeatailModel.class);
                danghao.setText(deatailModel.getDH());
                zhuangtai.setText(ZhuangTaiUtils.transOrderStatusCode(Integer.parseInt(deatailModel.getState())));
                tv_ztm.setText(deatailModel.getZTM());

                break;
        }
    }

}
