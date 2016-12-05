package com.xe.lzh.rfid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
 * Created by Administrator on 2016/8/30.
 */
@ContentView(R.layout.activity_jieyue)
public class JieYueActivity extends BaseActivity {
    @ViewInject(R.id.tv_count)
    TextView tv_count;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    @ViewInject(R.id.tv_time)
    TextView tv_time;
    @ViewInject(R.id.bt_dengjiJYR)
    Button btJYR;
    @ViewInject(R.id.tv_yongtu)
    TextView tv_yongtu;
    @ViewInject(R.id.radiogroup)
    RadioGroup radiogroup;
    @ViewInject(R.id.st_saoma)
    Switch st_saoma;
    @ViewInject(R.id.lv_data)
    ListView lv_data;
    private boolean startFlag = false;
    private boolean runflag = false;
    private ArrayList<EpcModel> listEPC;
    private UhfReader uhfReader = null;
    private RfidAdpter rfidAdpter;

    private String state = "-10";
    private String userid;
    private String jieyueren;
    private int num;
    private String epc;
    private String[] stryt = new String[]{"外借", "查阅", "复印", "拍摄", "拷贝"};
    String loginUser_id;
    String loginUser_name;

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "jieyue");
        intent.setClass(JieYueActivity.this, SheZhiActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                    rfidAdpter = new RfidAdpter(listEPC, JieYueActivity.this);
                    lv_data.setAdapter(rfidAdpter);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            num = (int) SpUtils.get(JieYueActivity.this, "guihuan", 25);
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
                            Util.initSoundPool(JieYueActivity.this);
//                    Toast.makeText(JieYueActivity.this, "点击了开始", Toast.LENGTH_SHORT).show();
                            startFlag = true;
                        }
                    }).start();

                } else {
//                    Toast.makeText(JieYueActivity.this, "点击了停止", Toast.LENGTH_SHORT).show();
                    startFlag = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settext("借阅");

//        lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                EpcModel epcModel = (EpcModel) parent.getAdapter().getItem(position);
//                String epc = epcModel.getEPC();
//                Intent intent = new Intent(JieYueActivity.this, XiangqingActivity.class);
//                intent.putExtra("EPC", epc);
//                startActivity(intent);
//            }
//        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (uhfReader != null) {
            runflag = false;
            uhfReader.close();
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
                        byte[] epcByte = uhfReader.readFrom6C(1, 2, 20, Tools.HexString2Bytes("00000000"));
                        if (epcByte != null) {
                            epcList.add(epcByte);
                            if (epcList != null && !epcList.isEmpty()) {

//                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.read);
//                            mediaPlayer.start();
//                            mediaPlayer.setVolume(50.0f,50.0f);

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
//                    System.out.println("epcStr  "+epcStr);
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
                    Util.play(1, 0);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        EpcModel mEPC = list.get(i);
                        //list中有此EPC
                        if (epcStr.equals(mEPC.getEPC())) {
                            epcTag.setCount(mEPC.getCount() + 1);
                            list.set(i, epcTag);//暂时未知用途
//                            list.add(epcTag);//测试用数据
                            break;
                        } else if (i == (list.size() - 1)) {
                            epcTag.setCount(1);
                            list.add(epcTag);
                            break;
                        }
                    }
                }
                Collections.sort(list, RFIDUtils.comp);//对于扫码获得的EEPC进行排序

                rfidAdpter.notifyDataSetChanged();
                rfidAdpter.initmap();
                tv_count.setText(String.valueOf(listEPC.size()));
            }
        });
    }

    //    点击空白区域 自动隐藏软键盘
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    JSONObject jsonObjectJYR = new JSONObject();

    @Event(value = {R.id.bt_queding, R.id.bt_dengjiJYR, R.id.bt_shanchu, R.id.tv_yongtu, R.id.bt_qingkong})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.bt_queding:
                if (TextUtils.isEmpty(jsonObjectJYR.toString())) {
                    Toast.makeText(JieYueActivity.this, "请先登记借阅人信息", Toast.LENGTH_SHORT).show();
                } else if (listEPC.size() == 0) {
                    Toast.makeText(this, "请先扫码借阅", Toast.LENGTH_SHORT).show();
                } else {
//                    System.out.println(jsonObjectJYR.toString());
                    try {
                        if (listEPC.size() > 0) {
                            HashMap<Integer, Boolean> map = rfidAdpter.getHashMap();
                            List<Integer> list = new ArrayList<Integer>();
                            JSONArray jsonArray = new JSONArray();
                            for (Integer key : map.keySet()) {
                                if (map.get(key)) {
                                    list.add(key);
                                }
                            }
                            for (int i = list.size() - 1; i >= 0; i--) {
                                System.out.println(list.size());
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("EPC", listEPC.get(list.get(i)).getEPC());
                                jsonObject.put("STATE", 3);
                                jsonObject.put("CREATOR", loginUser_id);
                                if (state == "-10" || "-10".equals(state)) {
                                    Toast.makeText(JieYueActivity.this, "请选择借阅的用途", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (jsonObjectJYR.length() == 0 || TextUtils.isEmpty(jsonObjectJYR.getString("JYRNAME"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRNUM"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRSEC"))
                                        || TextUtils.isEmpty(jsonObjectJYR.getString("JYRPHONE"))) {
                                    Toast.makeText(JieYueActivity.this, "请登记完整借阅人信息", Toast.LENGTH_SHORT).show();
                                    return;
                                }
//                            jsonObject.put("JYR", jieyueren);
                                jsonObject.put("JYRNAME", jsonObjectJYR.getString("JYRNAME"));
                                jsonObject.put("JYRNUM", jsonObjectJYR.getString("JYRNUM"));
                                jsonObject.put("JYRSEC", jsonObjectJYR.getString("JYRSEC"));
                                jsonObject.put("JYRPHONE", jsonObjectJYR.getString("JYRPHONE"));
                                jsonArray.put(jsonObject);
                            }
                            RequestParams params = new RequestParams(RFIDUtils.RUKU);
                            System.out.println("JieYueActivity " + jsonArray.toString());
                            params.addBodyParameter("data", jsonArray.toString());
                            doNetWork(params, 0);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.bt_dengjiJYR:
//                startActivityForResult(new Intent(JieYueActivity.this, SaoMaActivity.class), 2);

                AlertDialog.Builder jyrBuilder = new AlertDialog.Builder(this, R.style.Theme_Transparent);

//                builder.setIcon(R.drawable.ic_launcher);
//                jyrBuilder.setTitle("借阅人登记");
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
                        btJYR.setText(jyrName);
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
                        listEPC.remove(list.get(i).intValue());
                    }
                    rfidAdpter.notifyDataSetChanged();
                    rfidAdpter.initmap();
                    tv_count.setText(String.valueOf(listEPC.size()));
                }
                break;
            case R.id.tv_yongtu:
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);
                builder.setItems(stryt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = String.valueOf(which + 1);
                        tv_yongtu.setText(stryt[which]);
                        dialog.dismiss();
                    }
                });
                builder.show();
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

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data != null) {
//            userid = data.getExtras().getString("userid");//得到新Activity关闭后返回的数据
//            String username = data.getExtras().getString("username");//得到新Activity关闭后返回的数据
////            tv_jieyueren.setText(username);
//        } else {
////            Toast.makeText(JieYueActivity.this,"您没有选择操作人！",Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (uhfReader != null) {
            runflag = false;
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
            Toast.makeText(JieYueActivity.this, data + "条数据已借阅！", Toast.LENGTH_LONG).show();
        shanchu();
    }
}
