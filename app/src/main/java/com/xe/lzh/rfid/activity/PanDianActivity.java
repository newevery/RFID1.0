package com.xe.lzh.rfid.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xe.lzh.rfid.Application.RFIDapplication;
import com.xe.lzh.rfid.Model.DeatailModel;
import com.xe.lzh.rfid.Model.DossierModel;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.GsonUtils;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.adpter.DossierAdpter;
import com.xe.lzh.rfid.adpter.PanDianListAdapter;
import com.xe.lzh.rfid.tree.TreeListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2.
 */
@ContentView(R.layout.activity_pandian)
public class PanDianActivity extends BaseActivity implements DossierAdpter.SelectCallback {
    @ViewInject(R.id.lv_pandian)
    ListView lv_pandian;
    @ViewInject(R.id.tv_count)
    TextView tv_count;
    @ViewInject(R.id.tv_kuhao)
    EditText tv_kuhao;
    @ViewInject(R.id.tv1)
    TextView tv1;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    @ViewInject(R.id.st_saoma)
    Switch st_saoma;
    @ViewInject(R.id.pandianStyle)
    RelativeLayout linearLayout;
    String loginUser_id;
    String loginUser_name;
    private List<EpcModel> listEPC;
    private ArrayList<EpcModel> listWeitEPC;
    private String type = "1";
    private Boolean Flag = true;
    private int num;
    private String boxNum;
    private PanDianListAdapter panDianListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        settext("盘点");
        listEPC = new ArrayList<EpcModel>();
        listWeitEPC = new ArrayList<EpcModel>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_list2, null);
        lv_pandian.addHeaderView(view, null, false);
        panDianListAdapter = new PanDianListAdapter(PanDianActivity.this, listEPC);
        lv_pandian.setAdapter(panDianListAdapter);
        tv_kuhao.setEnabled(false);
        loginUser_id = (String) SpUtils.get(this, "userid", "1");
        loginUser_name = (String) SpUtils.get(this, "username", "操作人");
        tv_name.setText("欢迎您，" + loginUser_name);
        RadioGroup rg;
        rg = (RadioGroup) findViewById(R.id.radiogroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radio_0) {

                    type = "1";
                    tv1.setText("库号:");
                    tv1.setFocusable(true);
                    listEPC.clear();
                    linearLayout.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    tv_kuhao.setText("");
                    tv_kuhao.setEnabled(true);
                    tv_kuhao.setHint("点击输入或双击选择库");

                }
                if (checkedId == R.id.radio_2) {
                    type = "2";
                    linearLayout.setVisibility(View.GONE);

                }
                if (checkedId == R.id.radio_3) {
                    type = "3";
                    listEPC.clear();
                    panDianListAdapter.notifyDataSetChanged();
                    linearLayout.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    tv_kuhao.setText("");
                    tv_kuhao.setHint("请输入箱号");
                    tv_kuhao.setEnabled(true);
                    tv1.setText("箱号：");
                }
            }

        });
    }


    @Override
    public void select(int id) {
        if (dialog != null) {
            dialog.dismiss();
        }
        tv_kuhao.setText(String.valueOf(id));
//        RequestParams params = new RequestParams(RFIDUtils.SELECT);
//        params.addBodyParameter("PGROUP",String.valueOf(id));
//        doNetWork(params, 0);
    }

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "pandian");
        intent.setClass(this, SheZhiActivity.class);
        startActivity(intent);
    }

    @Event(value = {R.id.tv_kuhao, R.id.bt_weiruku, R.id.bt_queding})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.tv_kuhao:
                if (type == "1") {
                    RequestParams params = new RequestParams(RFIDUtils.ALLKU);
                    doNetWork(params, 1);
                    System.out.println("按库盘点 库信息" + params);
                }

                break;
            case R.id.bt_queding:
                if (type == "1") {
                    if (TextUtils.isEmpty(tv_kuhao.getText().toString())) {
                        Toast.makeText(PanDianActivity.this, "请输入库号", Toast.LENGTH_SHORT).show();
                    } else {
                    RequestParams params = new RequestParams(RFIDUtils.AKPANDIAN);
                    params.addBodyParameter("GROUPID", tv_kuhao.getText().toString());
                    params.addBodyParameter("FLAG", "1");
                    System.out.println("按库盘点 库内数据 " + String.valueOf(num));
                    doNetWork(params, 0);
                    Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show();}
                } else if (type == "3") {
                    boxNum = tv_kuhao.getText().toString();
                    if (TextUtils.isEmpty(boxNum)) {
                        Toast.makeText(PanDianActivity.this, "请输入箱号", Toast.LENGTH_SHORT).show();
                    } else {
                        RequestParams params = new RequestParams(RFIDUtils.AXPANDIAN);
                        params.addBodyParameter("BOXNUM", String.valueOf(boxNum));
                        System.out.println("按箱盘点 该箱数据 " + String.valueOf(boxNum));
                        doNetWork(params, 0);
                        Toast.makeText(this, "开始盘点", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    RequestParams params = new RequestParams(RFIDUtils.AKPANDIAN);
                    params.addBodyParameter("GROUPID", String.valueOf(0));
                    params.addBodyParameter("FLAG", "0");
                    System.out.println("按库盘点 库内数据 " + String.valueOf(0));
                    doNetWork(params, 0);
                }


                break;
            case R.id.bt_weiruku:
                List<EpcModel> epcs = new ArrayList<>();
                Toast.makeText(this, "未入库", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < listEPC.size(); i++) {
                    if (Integer.decode(listEPC.get(i).getState()).intValue() != 1) {
                        System.out.println(listEPC.get(i));
                        epcs.add(listEPC.get(i));
                    }
                }
                listEPC.clear();
                for (int i = 0; i < epcs.size(); i++) {
                    listEPC.add(epcs.get(i));
                }
                System.out.println(listEPC.size());
                panDianListAdapter.notifyDataSetChanged();
                break;
        }
    }


    TreeListViewAdapter dossierAdpter;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    public void setdata(String data, int requestcod) {
        try {
            Gson gson = new Gson();
            switch (requestcod) {
                case 0:
                    if (data != null && !"".equals(data)) {
                        try {

                            listEPC.clear();
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String epc = jsonObject.getString("EPC");
                                String Seperator = ",";
                                String[] Resources = epc.split(Seperator);
                                String dhStr;
                                String khstr;
                                String idstr;
                                if (Resources.length > 2) {
                                    dhStr = Resources[0];
                                    khstr = Resources[1];
                                    idstr = Resources[2];
                                } else {
                                    return;
                                }
                                EpcModel epcTag = new EpcModel();
                                epcTag.setEPC(epc);
                                epcTag.setDataID(idstr);
                                epcTag.setDH(dhStr);
                                epcTag.setGroupID(khstr);
                                epcTag.setState(jsonObject.getString("STATE"));
                                listEPC.add(epcTag);
                            }

                            panDianListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if ("1".equals(type)){
                            Toast.makeText(PanDianActivity.this, "此库内无数据", Toast.LENGTH_LONG).show();
                        }else if ("3".equals(type)){
                            Toast.makeText(PanDianActivity.this, "此箱内无数据", Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
                case 1:
                    List<DossierModel> listDossier = gson.fromJson(data, new TypeToken<List<DossierModel>>() {
                    }.getType());
                    builder = new AlertDialog.Builder(this, R.style.Theme_Transparent);
                    View view = LayoutInflater.from(this).inflate(R.layout.dialog_listview, null);
                    ListView listView = (ListView) view.findViewById(R.id.lv_data);
                    dossierAdpter = new DossierAdpter<DossierModel>(listView, PanDianActivity.this, listDossier, 0, this);
                    listView.setAdapter(dossierAdpter);
                    builder.setView(view);
                    dialog = builder.show();
                    dialog.setCanceledOnTouchOutside(false);
                    break;
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
