package com.xe.lzh.rfid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.LogUtils;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.Utils.Util;
import com.xe.lzh.rfid.adpter.RfidAdpter;

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

/**
 * Created by Administrator on 2016/9/2.
 */

@ContentView(R.layout.activity_guihuan)
public class GuiHuanActivity extends BaseActivity {
    @ViewInject(R.id.lv_data)
    ListView lv_data;
    @ViewInject(R.id.bt_dengjiJYR)
    Button btjyr;
    @ViewInject(R.id.tv_jieyueren)
    EditText tv_jieyueren;
    @ViewInject(R.id.tv_count)
    TextView tv_count;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    @ViewInject(R.id.st_saoma)
    Switch st_saoma;
    private boolean startFlag = false;
    private boolean runflag = false;
    private ArrayList<EpcModel> listEPC;
    private UhfReader uhfReader = null;
    private RfidAdpter rfidAdpter;
    private String epc;
    private String userid;
    private String jieyueren;
    String loginUser_id;
    String loginUser_name;
    private int num;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        listEPC = new ArrayList<EpcModel>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.headview, null);
        lv_data.addHeaderView(view, null, false);

        loginUser_id = (String) SpUtils.get(this, "userid", "1");
        loginUser_name = (String) SpUtils.get(this, "username", "操作人");
        tv_name.setText("欢迎您，" + loginUser_name);

        st_saoma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
//                    Toast.makeText(GuiHuanActivity.this, "点击了开始", Toast.LENGTH_SHORT).show();

                    lv_data.setAdapter(rfidAdpter);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            num = (int) SpUtils.get(GuiHuanActivity.this, "guihuan", 25);
                            uhfReader = UhfReader.getInstance();
                            uhfReader.setOutputPower(num);
                            uhfReader.setWorkArea(1);
                            if (uhfReader != null) {
                                runflag = true;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Thread inventoryThread = new InventoryThread();
                            inventoryThread.start();
                            //初始化声音池
                            Util.initSoundPool(GuiHuanActivity.this);
                            startFlag = true;
                        }
                    }).start();

                } else {
//                    Toast.makeText(GuiHuanActivity.this, "点击了停止", Toast.LENGTH_SHORT).show();
                    startFlag = false;
                }
            }
        });
    }

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "guihuan");
        intent.setClass(this, SheZhiActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        settext("归还");
        rfidAdpter = new RfidAdpter(listEPC, this);

    }

    class InventoryThread extends Thread {
        private List<byte[]> epcList = new ArrayList<byte[]>();

        @Override
        public void run() {
            super.run();
            while (runflag) {
                if (startFlag) {
//					reader.stopInventoryMulti()
//                    epcList = uhfReader.inventoryRealTime(); //实时盘存
                    byte[] epcByte = uhfReader.readFrom6C(1, 2, 20, Tools.HexString2Bytes("00000000"));
                    if (epcByte != null) {
                        epcList.add(epcByte);
                        if (epcList != null && !epcList.isEmpty()) {

                            for (byte[] epc : epcList) {
                                String epcStr = Tools.Bytes2HexString(epc, epc.length);
                                addToList(listEPC, epcStr);
                            }
                        }
                        epcList.clear();
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void addToList(final List<EpcModel> list, final String epc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String dhStr = null;
                String khstr = null;
                String idstr = null;
                String epcStr = null;
                try {
                    //由于epc由档号+groupid+dataid组成，故截取出档号
                    epcStr = new String(Tools.HexString2Bytes(epc), "ascii");
                    String Seperator = ",";
                    String[] Resources = epcStr.split(Seperator);
                    StringBuffer stringBuffer = new StringBuffer();
                    if (Resources.length > 2) {
                        dhStr = Resources[0];
                        khstr = Resources[1];
                        idstr = Resources[2];
                    } else {
                        return;
                    }
                    epcStr = dhStr + "," + khstr + "," + idstr + ",";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }//第二个参数指定编码方式
                //第一次读入数据
                EpcModel epcTag = new EpcModel();
                epcTag.setEPC(epcStr);
                epcTag.setDataID(idstr);
                epcTag.setDH(dhStr);
                epcTag.setGroupID(khstr);

                //保证每个EPC只显示一次
                if (list.isEmpty()) {
                    epcTag.setCount(1);
                    list.add(epcTag);
                    Util.play(1, 0);
                } else {
                    for (int i = 0; i < list.size(); i++) {//遍历此List中是否已有此EPC
                        EpcModel mEPC = list.get(i);
                        //list中有此EPC
                        if (epcStr.equals(mEPC.getEPC())) {
                            epcTag.setCount(mEPC.getCount() + 1);
                            list.set(i, epcTag);
                            System.out.println(i + "  !!  " + epcTag);
                            break;
                        } else if (i == (list.size() - 1)) {
                            epcTag.setCount(1);
                            list.add(epcTag);
                            Util.play(1, 0);
                            break;
                        }
                    }
                }
                Collections.sort(list, RFIDUtils.comp);
                rfidAdpter.notifyDataSetChanged();
                rfidAdpter.initmap();
                tv_count.setText(String.valueOf(listEPC.size()));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (uhfReader != null) {
            runflag = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (uhfReader != null) {
            runflag = false;
            uhfReader.close();
        }
    }

    JSONObject jsonObjectJYR = new JSONObject();

    @Event(value = {R.id.bt_shanchu, R.id.bt_queding, R.id.bt_dengjiJYR, R.id.bt_qingkong})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.bt_queding:

                if (TextUtils.isEmpty(jsonObjectJYR.toString())) {
                    Toast.makeText(GuiHuanActivity.this, "请先登记借阅人信息", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (listEPC.size() > 0) {
                            HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
                            List<Integer> list = new ArrayList<Integer>();
                            JSONArray jsonArray = new JSONArray();
                            for (Integer key : map.keySet()) {
                                if (map.get(key)) {
                                    list.add(key);
                                    LogUtils.d(key + "");
                                }
                            }
                            for (int i = list.size() - 1; i >= 0; i--) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("EPC", listEPC.get(list.get(i)).getEPC());
//                            jsonObject.put("EPC","2015-DQ11-Y-3,211832,218688,");
//                                jsonObject.put("JYR", jieyueren);
                                jsonObject.put("CREATOR", loginUser_id);
                                jsonObject.put("STATE", "1");
                                if (jsonObjectJYR.length() == 0 || TextUtils.isEmpty(jsonObjectJYR.getString("JYRNAME"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRNUM"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRSEC"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRPHONE"))) {
                                    Toast.makeText(GuiHuanActivity.this, "请登记完整借阅人信息", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                jsonObject.put("JYRNAME", jsonObjectJYR.getString("JYRNAME"));
                                jsonObject.put("JYRNUM", jsonObjectJYR.getString("JYRNUM"));
                                jsonObject.put("JYRSEC", jsonObjectJYR.getString("JYRSEC"));
                                jsonObject.put("JYRPHONE", jsonObjectJYR.getString("JYRPHONE"));
                                jsonArray.put(jsonObject);
//                                jsonObject.put("JYR", userid);
                            }
                            System.out.println("GuiHuanActivity " + jsonArray.toString());
                            RequestParams params = new RequestParams(RFIDUtils.RUKU);
                            params.addBodyParameter("data", jsonArray.toString());
                            doNetWork(params, 0);
                            shanchu();
                        } else {
                            Toast.makeText(this, "选择归还档案", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.bt_dengjiJYR:
                AlertDialog.Builder jyrBuilder = new AlertDialog.Builder(this, R.style.Theme_Transparent);
//                builder.setIcon(R.drawable.ic_launcher);
//                jyrBuilder.setTitle("归还人登记");
                LayoutInflater inflater = LayoutInflater.from(this);
                View viewJYR = inflater.inflate(R.layout.item_jyr, null);
                final EditText etJYRNAME = (EditText) viewJYR.findViewById(R.id.et_jyrname);
                final EditText etJYRNUM = (EditText) viewJYR.findViewById(R.id.et_jyrnum);
                final EditText etJYRSEC = (EditText) viewJYR.findViewById(R.id.et_jyrsec);
                final EditText etJYRPHONE = (EditText) viewJYR.findViewById(R.id.et_jyrphone);
                jyrBuilder.setView(viewJYR);
                jyrBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTitle("您点击的是确定按钮!");
                        String jyrName = etJYRNAME.getText().toString();
                        String jyrNum = etJYRNUM.getText().toString();
                        String jyrSec = etJYRSEC.getText().toString();
                        String jyrPhone = etJYRPHONE.getText().toString();
                        btjyr.setText(jyrName);
                        try {
                            jsonObjectJYR.put("JYRNAME", jyrName + "");
                            jsonObjectJYR.put("JYRNUM", jyrNum + "");
                            jsonObjectJYR.put("JYRSEC", jyrSec + "");
                            jsonObjectJYR.put("JYRPHONE", jyrPhone + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                jyrBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTitle("您点击的是取消按钮!");
                    }
                });
                jyrBuilder.show();
                break;
            case R.id.bt_shanchu:
                if (listEPC.size() > 0) {
                    HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
                    List<Integer> list = new ArrayList<Integer>();
                    for (Integer key : map.keySet()) {
                        if (map.get(key)) {
                            list.add(key);
                        }
                    }

                    for (int i = list.size() - 1; i >= 0; i--) {
                        int index = list.get(i).intValue();
                        System.out.println(index);
                        listEPC.remove(index);
                    }

                    rfidAdpter.notifyDataSetChanged();
                    rfidAdpter.initmap();
                    tv_count.setText(String.valueOf(listEPC.size()));
                } else {
                    Toast.makeText(this, "请先扫码获得档案", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_qingkong:
//               listEPC.clear();
                if (listEPC.size() > 0) {
                    if (!st_saoma.isChecked()) {
                        rfidAdpter.selectmap();
                        rfidAdpter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            userid = data.getExtras().getString("userid");//得到新Activity关闭后返回的数据
            jieyueren = data.getExtras().getString("username");//得到新Activity关闭后返回的数据
            tv_jieyueren.setText(jieyueren);
        } else {

        }

    }

    private void shanchu() {
        if (listEPC.size() > 0) {
            HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
            List<Integer> list = new ArrayList<Integer>();
            for (Integer key : map.keySet()) {
                if (map.get(key)) {
                    list.add(key);
                }
            }

            for (int i = list.size() - 1; i >= 0; i--) {
                int isc = list.get(i);
                listEPC.remove(isc);
            }
            rfidAdpter.notifyDataSetChanged();
            rfidAdpter.initmap();
            tv_count.setText(String.valueOf(listEPC.size()));
        }
    }

    @Override
    public void setdata(String data, int requestcode) {
        if (data != null && !"".equals(data))
            Toast.makeText(GuiHuanActivity.this, data + "条数据已归还！", Toast.LENGTH_LONG).show();
        shanchu();
    }
}
