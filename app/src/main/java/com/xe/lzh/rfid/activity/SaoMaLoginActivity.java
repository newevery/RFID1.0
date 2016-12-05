package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.adpter.RfidAdpter1;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户扫码登陆页面
 * Created by Administrator on 2016/10/10 0010.
 */
@ContentView(R.layout.activity_saoma)
public class SaoMaLoginActivity extends BaseActivity {
    @ViewInject(R.id.lv_data)
    ListView lv_data;
    @ViewInject(R.id.bt_queding)
    Button queing;

    public TextView tv_count;
    private boolean startFlag = false;
    private boolean runflag = false;
    private ArrayList<EpcModel> listEPC;
    public UhfReader uhfReader;
    private RfidAdpter1 rfidAdpter1;
    private Thread inventoryThread;
    private int num;

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "saoma");
        intent.setClass(this, SheZhiActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        listEPC = new ArrayList<EpcModel>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.headview1, null);
        lv_data.addHeaderView(view, null, false);
        lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userid = listEPC.get(position - 1).getDataID();
                String username = listEPC.get(position - 1).getDH();
                Intent intent = new Intent();
                SpUtils.put(getApplicationContext(), "username", username);
                SpUtils.put(getApplicationContext(), "userid", userid);
                SaoMaLoginActivity.this.setResult(1, intent);
                intent.setClass(SaoMaLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        num = (int) SpUtils.get(this, "saoma", 25);
        rfidAdpter1 = new RfidAdpter1(listEPC, this);
        lv_data.setAdapter(rfidAdpter1);
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



    }



    @Event(value = {R.id.bt_guanbi, R.id.bt_start, R.id.bt_shezhi, R.id.bt_stop})
    private void Click(View view) {
        switch (view.getId()) {

            case R.id.bt_guanbi:
                finish();
                break;
            case R.id.bt_start:
                Toast.makeText(SaoMaLoginActivity.this, "点击了开始", Toast.LENGTH_SHORT).show();
//                num = (int) SpUtils.get(getApplicationContext(), "num", "");

                startFlag = true;
                inventoryThread = new InventoryThread();
                inventoryThread.start();
                break;
            case R.id.bt_stop:
                Toast.makeText(SaoMaLoginActivity.this, "点击了停止", Toast.LENGTH_SHORT).show();
                startFlag = false;
                break;
//            case R.id.bt_shezhi:
//                Intent intent = new Intent();
//                intent.putExtra("falg", "saoma");
//                intent.setClass(SaoMaLoginActivity.this,SheZhiActivity.class);
//                startActivity(intent);
//                break;


        }
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
                        byte[] epcByte = uhfReader.readFrom6C(3, 0, 2, Tools.HexString2Bytes("00000000"));
                        if (epcByte != null) {
                            StringBuffer sbuf = new StringBuffer();
                            for (int i = 0; i < epcByte.length; i++) {
                                sbuf.append(epcByte[i]);
                            }
                            System.out.println("SSSSSS"+sbuf.toString());

                            epcList.add(epcByte);
                            if (epcList != null && !epcList.isEmpty()) {
                                //播放提示音
//                        Util.play(1, 0);
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (uhfReader != null) {
            runflag = false;
            uhfReader.powerOff();
            uhfReader.close();
        }
    }
    private void addToList(final List<EpcModel> list, final String epc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String userid = null;
                String username = null;
                String epcStr = null;
                try {
                    //由于epc由档号+groupid+dataid组成，故截取出档号
                    epcStr = new String(Tools.HexString2Bytes(epc), "ascii");
                    String Seperator = ";";
                    String[] Resources = epcStr.split(Seperator);
                    StringBuffer stringBuffer = new StringBuffer();
                    if (Resources.length > 1) {
                        userid = Resources[0];
                        username = Resources[1];
                    } else {
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }//第二个参数指定编码方式
                //第一次读入数据
                EpcModel epcTag = new EpcModel();
                epcTag.setEPC(epcStr);
                epcTag.setDataID(userid);
                epcTag.setDH(username);
                if (list.isEmpty()) {
                    epcTag.setCount(1);
                    list.add(epcTag);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        EpcModel mEPC = list.get(i);
                        //list中有此EPC
                        if (userid.equals(mEPC.getDataID())) {
                            epcTag.setCount(mEPC.getCount() + 1);
                            list.set(i, epcTag);
                            break;
                        } else if (i == (list.size() - 1)) {
                            epcTag.setCount(1);
                            list.add(epcTag);
                            break;
                        }
                    }
                }
                Collections.sort(list, RFIDUtils.comp);
                rfidAdpter1.notifyDataSetChanged();

//                tv_count.setText(String.valueOf(listEPC.size()));
            }
        });
    }

    @Override
    public void setdata(String data, int requestcode) {

    }
}
