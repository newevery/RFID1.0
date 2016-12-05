package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.adpter.RfidAdpter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
@ContentView(R.layout.activity_shezhi)
public class SheZhiActivity extends BaseActivity {
    @ViewInject(R.id.tv_num)
    TextView tv_num;
    @ViewInject(R.id.tv_plus)
    TextView tv_plus;
    @ViewInject(R.id.tv_minus)
    TextView tv_minus;
    private int num;
    String flag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        flag = getIntent().getStringExtra("falg");
        num = (int) SpUtils.get(this, flag, 25);
        tv_num.setText(String.valueOf(num));
    }

    @Event(value = {R.id.tv_minus, R.id.tv_plus, R.id.bt_queding})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.tv_minus:
                num = Integer.parseInt(tv_num.getText().toString().trim());
                if (16 <= num && num <= 50) {
                    tv_num.setText(--num + "");
                }
                break;
            case R.id.tv_plus:
                num = Integer.parseInt(tv_num.getText().toString().trim());
                if (16 <= num && num <= 50) {
                    tv_num.setText(++num + "");
                }
                break;
            case R.id.bt_queding:
                SpUtils.put(SheZhiActivity.this, flag, num);
                finish();
        }
    }

    @Override
    public void setdata(String data, int requestcode) {

    }
}