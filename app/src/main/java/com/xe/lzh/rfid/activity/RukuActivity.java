package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
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
 * 入库界面
 */
@ContentView(R.layout.activity_ruku)
public class RukuActivity extends BaseActivity {
    @ViewInject(R.id.lv_data)
    ListView lv_data;
    @ViewInject(R.id.rg_ruku)
    RadioGroup rgRuKu;
    @ViewInject(R.id.et_boxnum)
    EditText etBoxNum;
    @ViewInject(R.id.tv_count)
    TextView tv_count;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    @ViewInject(R.id.st_saoma)
    Switch st_saoma;
    String loginUser_id;
    String loginUser_name;
    private boolean startFlag = false;
    private boolean runflag = false;
    private ArrayList<EpcModel> listEPC;
    private UhfReader uhfReader = null;
    private RfidAdpter rfidAdpter;
    private Thread inventoryThread;
    private int num;

    private String type;

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

        rgRuKu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_xinjia) {
                    Toast.makeText(RukuActivity.this, "新加入库", Toast.LENGTH_SHORT).show();
                    type = "1";
                    etBoxNum.setVisibility(View.GONE);
                } else {
                    Toast.makeText(RukuActivity.this, "整箱入库", Toast.LENGTH_SHORT).show();
                    type = "2";
                    etBoxNum.setVisibility(View.VISIBLE);
                }
            }
        });

        st_saoma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            num = (int) SpUtils.get(RukuActivity.this, "ruku", 25);
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
                            inventoryThread = new InventoryThread();
                            inventoryThread.start();

                            startFlag = true;
                            //初始化声音池
                            Util.initSoundPool(RukuActivity.this);
                        }
                    }).start();

                } else {
                    startFlag = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settext("入库");

        rfidAdpter = new RfidAdpter(listEPC, this);
        lv_data.setAdapter(rfidAdpter);

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (uhfReader != null) {
//            runflag = false;
//            uhfReader.powerOff();
//            uhfReader.close();
//        }
//    }

    @Event(value = {R.id.bt_shanchu, R.id.bt_queding, R.id.bt_qingkong})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.bt_queding:
                try {
                    if (listEPC.size() > 0) {

                        if ("1".equals(type)) {
                            HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
                            List<Integer> list = new ArrayList<Integer>();
                            JSONArray jsonArray = new JSONArray();
                            for (Integer key : map.keySet()) {
                                if (map.get(key)) {
                                    list.add(key);
                                }
                            }
                            for (int i = list.size() - 1; i >= 0; i--) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("EPC", listEPC.get(list.get(i)).getEPC());
                                jsonObject.put("CREATOR", loginUser_id);
                                jsonObject.put("STATE", "1");
                                jsonArray.put(jsonObject);
                            }
                            RequestParams params = new RequestParams(RFIDUtils.RUKU);
                            params.addBodyParameter("data", jsonArray.toString());
                            System.out.println("RuKuActivity  " + jsonArray.toString());
                            doNetWork(params, 0);

                        } else {
                            String boxnum = etBoxNum.getText().toString();
                            if (boxnum == null || "".equals(boxnum)) {
                                Toast.makeText(RukuActivity.this, "箱号不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
                                List<Integer> list = new ArrayList<Integer>();
                                JSONArray jsonArray = new JSONArray();
                                for (Integer key : map.keySet()) {
                                    if (map.get(key)) {
                                        list.add(key);
                                    }
                                }
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("EPC", listEPC.get(list.get(i)).getEPC());
                                    jsonObject.put("CREATOR", loginUser_id);
                                    jsonObject.put("BOXNUM", boxnum);
                                    jsonArray.put(jsonObject);
                                }
                                RequestParams params = new RequestParams(RFIDUtils.UPDATEBOX);
                                params.addBodyParameter("data", jsonArray.toString());
                                System.out.println("RuKuActivity  " + jsonArray.toString());
                                doNetWork(params, 0);

                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_shanchu:
                shanchu();
                break;
            case R.id.bt_qingkong:
                if (listEPC.size() > 0) {
                    if (!st_saoma.isChecked()) {
                        rfidAdpter.selectmap();
                        rfidAdpter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (uhfReader != null) {
            runflag = false;
        }

    }

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "ruku");
        intent.setClass(this, SheZhiActivity.class);
        startActivity(intent);
    }

    @Override
    public void setdata(String data, int requestcode) {
        Toast.makeText(RukuActivity.this, data + "条数据已入库", Toast.LENGTH_LONG).show();
        shanchu();
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
                    if (uhfReader != null) {
                        byte[] epcByte = uhfReader.readFrom6C(1, 2, 20, Tools.HexString2Bytes("00000000"));
                        if (epcByte != null) {
                            epcList.add(epcByte);
                            if (epcList != null && !epcList.isEmpty()) {

                                for (byte[] epc : epcList) {
                                    String epcStr = Tools.Bytes2HexString(epc, epc.length);
                                    System.out.println("epcStr " + epcStr);
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
                if (list.isEmpty()) {
                    epcTag.setCount(1);
                    list.add(epcTag);
//                                播放提示音
                    Util.play(1, 0);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        EpcModel mEPC = list.get(i);
                        //list中有此EPC
                        if (epcStr.equals(mEPC.getEPC())) {
                            epcTag.setCount(mEPC.getCount() + 1);
                            list.set(i, epcTag);
                            break;
                        } else if (i == (list.size() - 1)) {
                            epcTag.setCount(1);
                            list.add(epcTag);
                            //                                播放提示音
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

}

