package com.xe.lzh.rfid.fragment;


import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.HttpUtils;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.Utils.Util;
import com.xe.lzh.rfid.adpter.PanDianListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShouDongZSFragment extends Fragment implements View.OnClickListener, HttpUtils.PostCallBack {
    private LinearLayout shouDongZhuiSu;
    private int tv_xh = 1;
    private TextView tv_xuhao;
    private EditText et_epc;
    private TextView tv_tianjia;
    private List<String> epcShuRu;
    private List<String> dhList;
    private Button btZhuiSu, btChongShu, btGuaBi, btChaku;
    private boolean startFlag = false;
    private boolean runflag = false;
    public UhfReader uhfReader = null;
    private Thread inventoryThread;
    private int num;

    private String EPC;

    public ShouDongZSFragment() {
    }

    List<EditText> editTexts = new ArrayList<>();
    private ListView lvZhuiSu;
    private LinearLayout shouDongZS;
    private ScrollView svZhuiSu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_zhuisu, container, false);
        svZhuiSu = (ScrollView) inflate.findViewById(R.id.scrollview_zhuisu);
        dhList = new ArrayList<>();
        shouDongZS = (LinearLayout) inflate.findViewById(R.id.shoudongZS);
        lvZhuiSu = (ListView) inflate.findViewById(R.id.lv_zhuisu);
        lvZhuiSu.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_list2, null));
        lvZhuiSu.setVisibility(View.GONE);
        btZhuiSu = (Button) inflate.findViewById(R.id.bt_zhuisu);
        btZhuiSu.setOnClickListener(this);
        btChongShu = (Button) inflate.findViewById(R.id.bt_chongshu);
        btChongShu.setOnClickListener(this);
        btChaku = (Button) inflate.findViewById(R.id.bt_chaku);
        btChaku.setOnClickListener(this);
        btGuaBi = (Button) inflate.findViewById(R.id.bt_guanbi);
        btGuaBi.setOnClickListener(this);
        btChaku.setVisibility(View.VISIBLE);
        btChongShu.setVisibility(View.VISIBLE);
        btZhuiSu.setVisibility(View.GONE);
        btGuaBi.setVisibility(View.GONE);

        shouDongZhuiSu = (LinearLayout) inflate.findViewById(R.id.linearLayout_zhuisu);
        epcShuRu = new ArrayList<>();
        View viewTianJia = LayoutInflater.from(getActivity()).inflate(R.layout.item_zhuisu, null);
        shouDongZhuiSu.addView(viewTianJia);

        tv_xuhao = (TextView) viewTianJia.findViewById(R.id.textView2);
        et_epc = (EditText) viewTianJia.findViewById(R.id.editText);


        tv_tianjia = (TextView) viewTianJia.findViewById(R.id.textView3);
        tv_xuhao.setText(tv_xh++ + "");
        editTexts.add(et_epc);
        tv_tianjia.setOnClickListener(this);

        return inflate;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView3:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String str = editTexts.get(editTexts.size() - 1).getText().toString();
//                String str = et_epc.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    System.out.println(str);
                    v.setEnabled(false);
                    dhList.add(str);
                    View viewTianJia = LayoutInflater.from(getActivity()).inflate(R.layout.item_zhuisu, null);
                    shouDongZhuiSu.addView(viewTianJia);
                    tv_xuhao = (TextView) viewTianJia.findViewById(R.id.textView2);
                    et_epc = (EditText) viewTianJia.findViewById(R.id.editText);
                    tv_tianjia = (TextView) viewTianJia.findViewById(R.id.textView3);
                    tv_xuhao.setText(tv_xh++ + "");
                    editTexts.add(et_epc);
                    tv_tianjia.setOnClickListener(this);

                } else {
                    Toast.makeText(getActivity(), "档号不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_chaku:
                btChaku.setVisibility(View.GONE);
                btChongShu.setVisibility(View.VISIBLE);
                btZhuiSu.setVisibility(View.VISIBLE);
                btGuaBi.setVisibility(View.GONE);
//                onSuccess("", 0);//测试
                svZhuiSu.setVisibility(View.GONE);
                shouDongZS.setVisibility(View.GONE);
                shouDongZhuiSu.setVisibility(View.GONE);
                lvZhuiSu.setVisibility(View.VISIBLE);
//                获取本地输入的档号

                for (int i = 0; i < editTexts.size(); i++) {
                    String shuiruDh = editTexts.get(i).getText().toString();
                    dhList.add(shuiruDh);
                    System.out.println("输入 " + shuiruDh);
                }

                if (!TextUtils.isEmpty(dhList.get(0).toString())) {
                    RequestParams params = new RequestParams(RFIDUtils.ZHUISU);
                    params.addBodyParameter("DH", dhList.toString());
                    System.out.println("danghao " + dhList.toString());
                    doNetWork(params, 0);
                } else {
                    Toast.makeText(getActivity(), "输入为空，请点击重新输入", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_chongshu:
                btChaku.setVisibility(View.VISIBLE);
                btChongShu.setVisibility(View.VISIBLE);
                btZhuiSu.setVisibility(View.GONE);
                btGuaBi.setVisibility(View.GONE);
                if (uhfReader != null) {
                    runflag = false;
                    uhfReader.close();
                }
                dhList.clear();
                editTexts.clear();
                tv_xh = 1;
                shouDongZhuiSu.removeAllViews();
                View child = LayoutInflater.from(getActivity()).inflate(R.layout.item_zhuisu, null);
                shouDongZhuiSu.addView(child);
                TextView tv_xuhao2 = (TextView) child.findViewById(R.id.textView2);
                tv_xuhao2.setText(tv_xh++ + "");
                TextView tv_tianjia2 = (TextView) child.findViewById(R.id.textView3);
                et_epc = (EditText) child.findViewById(R.id.editText);
                tv_tianjia = (TextView) child.findViewById(R.id.textView3);
                tv_xuhao.setText(tv_xh++ + "");
                editTexts.add(et_epc);
                tv_tianjia2.setEnabled(true);
                tv_tianjia2.setOnClickListener(this);

                svZhuiSu.setVisibility(View.VISIBLE);
                shouDongZS.setVisibility(View.VISIBLE);
                shouDongZhuiSu.setVisibility(View.VISIBLE);
                lvZhuiSu.setVisibility(View.GONE);
                break;
            case R.id.bt_guanbi:
                if (uhfReader != null) {
                    runflag = false;
                    uhfReader.powerOff();
                    uhfReader.close();
                }
                break;
            case R.id.bt_zhuisu:
                btChaku.setVisibility(View.GONE);
                btChongShu.setVisibility(View.VISIBLE);
                btZhuiSu.setVisibility(View.GONE);
                btGuaBi.setVisibility(View.VISIBLE);
                List<EpcModel> epcs = new ArrayList<>();
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
                if (panDianListAdapter != null) {
                    panDianListAdapter.notifyDataSetChanged();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        num = (int) SpUtils.get(getActivity(), "saoma", 25);
                        uhfReader = UhfReader.getInstance();
                        uhfReader.setOutputPower(num);
                        uhfReader.setWorkArea(1);
                        if (uhfReader != null) {
                            runflag = true;
                            startFlag = true;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        inventoryThread = new InventoryThread();
                        inventoryThread.start();
                        Util.initSoundPool(getActivity());
                    }
                }).start();


                break;
        }

    }

    List<String> epcs = new ArrayList<>();

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
                                //播放提示音
//                        Util.play(1, 0);
                                for (byte[] epc : epcList) {
                                    final String epcStr = Tools.Bytes2HexString(epc, epc.length);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String epcAll;
                                            String dhStr = null;
                                            String khstr = null;
                                            String idstr = null;
                                            try {
                                                //由于epc由档号+groupid+dataid组成，故截取出档号
                                                epcAll = new String(Tools.HexString2Bytes(epcStr), "ascii");
                                                String Seperator = ",";
                                                String[] Resources = epcAll.split(Seperator);
                                                if (Resources.length > 2) {
                                                    dhStr = Resources[0];
                                                    khstr = Resources[1];
                                                    idstr = Resources[2];
                                                } else {
                                                    return;
                                                }
                                                epcAll = dhStr + "," + khstr + "," + idstr + ",";
                                                System.out.println("扫码得到的  " + epcAll);
                                                System.out.println(epcs.toString());
                                                if (epcs.contains(epcAll)) {
                                                    Util.play(1, 0);
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

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

    public void doNetWork(RequestParams params, int requestcode) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.setPostcallback(this);
        httpUtils.dopost(params, requestcode, getActivity());
        List<KeyValue> bodyParams = params.getBodyParams();
        for (KeyValue k : bodyParams) {
            System.out.println(" PLZS " + k.getValueStr() + "  " + k.key);
        }
        System.out.println("doNetWork " + params.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uhfReader != null) {
            runflag = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uhfReader != null) {
            runflag = false;
            uhfReader.close();
        }
    }

    @Override
    public void onSuccess(String s, int requestcode) {
        s = " {\"data\":[{\"STATE\":\"3\",\"EPC\":\"2015-DQ11-Y-1,193250,218265,\"},{\"STATE\":\"1\",\"EPC\":\"2015-DQ11-Y-2,193250,221064,\"},{\"STATE\":\"2\",\"EPC\":\"2015-DQ11-Y-4,193250,221064,\"},{\"STATE\":\"2\",\"EPC\":\"2015-DQ11-Y-3,5164396,341849,\"},{\"STATE\":\"3\",\"EPC\":\"2015-DR11-Y-5,193240,223064,\"}],\"resultcode\":\"200\"}";
        try {
            JSONObject jsonObject = new JSONObject(s);
            String resultcode = jsonObject.getString("resultcode");
            if (resultcode.equals("200")) {
                String data = jsonObject.getString("data");
                setdata(data, requestcode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<EpcModel> listEPC = new ArrayList<>();
    private PanDianListAdapter panDianListAdapter;

    public void setdata(String data, int requestcod) {
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
                    epcs.add(epc);
                    epcTag.setDataID(idstr);
                    epcTag.setDH(dhStr);
                    epcTag.setGroupID(khstr);
                    epcTag.setState(jsonObject.getString("STATE"));
                    listEPC.add(epcTag);
                }
                panDianListAdapter = new PanDianListAdapter(getActivity(), listEPC);
                lvZhuiSu.setAdapter(panDianListAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "服务器无数据", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onFailture(Throwable throwable, boolean b, int requestcode) {
        Toast.makeText(getActivity(), "输入档号有误", Toast.LENGTH_SHORT).show();
    }
}
