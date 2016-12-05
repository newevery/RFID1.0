package com.xe.lzh.rfid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.HttpUtils;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.Utils.Util;
import com.xe.lzh.rfid.Utils.WordUtils;
import com.xe.lzh.rfid.adpter.PLZhuiSuAdapter;
import com.xe.lzh.rfid.adpter.PanDianListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

public class PlzhuiSuFragment extends Fragment implements View.OnClickListener, HttpUtils.PostCallBack {
    private boolean startFlag = false;
    private boolean runflag = false;
    public UhfReader uhfReader=null;
    private Thread inventoryThread;
    private int num;
    private ListView pllistView;
    private PLZhuiSuAdapter plZhuiSuAdapter;
    private List<String> dhList;
private EditText etdrboxnum;
    private RadioGroup rgdaoru;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plzhui_su, null);
        rgdaoru= (RadioGroup) view.findViewById(R.id.rg_daoru);
        etdrboxnum= (EditText) view.findViewById(R.id.et_drboxnum);
        //点击其他部位隐藏软键盘
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        return view;
    }

    private ListView plZSJG;
    private PanDianListAdapter panDianListAdapter;
private String type="0";
    private Button btZhuiSu,btChaKu,btPLShanChu,btGuanBi;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view.findViewById(R.id.bt_qingkong).setOnClickListener(this);
        pllistView = (ListView) view.findViewById(R.id.pllistView);
        plZSJG = (ListView) view.findViewById(R.id.pllistViewjieguo);
        plZSJG.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.item_list2, null));
        plZSJG.setVisibility(View.GONE);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.item_list3, null, false);
        pllistView.addHeaderView(inflate);
        btZhuiSu= (Button) view.findViewById(R.id.bt_zhuisu);
       btChaKu= (Button) view.findViewById(R.id.bt_chaku);
        btPLShanChu= (Button) view.findViewById(R.id.bt_plshanchu);
        btGuanBi= (Button) view.findViewById(R.id.bt_guanbi);
        btPLShanChu.setOnClickListener(this);
        btGuanBi.setOnClickListener(this);
        btChaKu.setOnClickListener(this);
        btZhuiSu.setOnClickListener(this);

        btZhuiSu.setVisibility(View.GONE);
        btChaKu.setVisibility(View.VISIBLE);
        btPLShanChu.setVisibility(View.VISIBLE);
        btGuanBi.setVisibility(View.GONE);

        dhList = new ArrayList<>();
        plZhuiSuAdapter = new PLZhuiSuAdapter(dhList, getActivity());
        pllistView.setAdapter(plZhuiSuAdapter);

        panDianListAdapter = new PanDianListAdapter(getActivity(), listEPC);
        plZSJG.setAdapter(panDianListAdapter);

       rgdaoru .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.et_daoru) {
                    type = "1";
                    if (uhfReader != null) {
                        runflag = false;
                        uhfReader.close();
                    }
                    etdrboxnum.setVisibility(View.GONE);
                    btZhuiSu.setVisibility(View.GONE);
                    btChaKu.setVisibility(View.VISIBLE);
                    btPLShanChu.setVisibility(View.VISIBLE);
                    btGuanBi.setVisibility(View.GONE);
                    showFileChooser();
                    if (dhList.size() != 0) {
                        dhList.clear();
                        plZhuiSuAdapter.notifyDataSetChanged();
                    }
                    plZSJG.setVisibility(View.GONE);
                    pllistView.setVisibility(View.VISIBLE);

                } else {
                    type = "2";
                    if (uhfReader != null) {
                        runflag = false;
                        uhfReader.close();
                    }
                    etdrboxnum.setVisibility(View.GONE);
                    btZhuiSu.setVisibility(View.GONE);
                    btChaKu.setVisibility(View.VISIBLE);
                    btPLShanChu.setVisibility(View.VISIBLE);
                    btGuanBi.setVisibility(View.GONE);
                    etdrboxnum.setVisibility(View.VISIBLE);
listEPC.clear();
                }
            }
        });
    }
private JSONArray js;
    public void doNetWork(RequestParams params, int requestcode) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.setPostcallback(this);
        httpUtils.dopost(params, requestcode, getActivity());
        
        List<KeyValue> bodyParams = params.getBodyParams();
        for (KeyValue k : bodyParams) {
            System.out.println(" BaseActivity " + k.getValueStr() + "  " + k.key);
        }
        System.out.println("doNetWork " + params.toString());
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
    public void onPause() {
        super.onPause();
        if (uhfReader != null) {
            runflag = false;}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.bt_daoru:
//                btZhuiSu.setVisibility(View.GONE);
//                btChaKu.setVisibility(View.VISIBLE);
//                btPLShanChu.setVisibility(View.VISIBLE);
//                btGuanBi.setVisibility(View.GONE);
//                showFileChooser();
//                if (dhList.size() != 0) {
//                    dhList.clear();
//                    plZhuiSuAdapter.notifyDataSetChanged();
//                }
//                plZSJG.setVisibility(View.GONE);
//                pllistView.setVisibility(View.VISIBLE);
//
//
//                break;
//            case R.id.bt_zhuisu:
//                plZSJG.setVisibility(View.VISIBLE);
//                pllistView.setVisibility(View.GONE);
//                onSuccess("", 0);
//                if (dhList.size() > 0) {
//                    HashMap<Integer, Boolean> map = plZhuiSuAdapter.getHashMap();
//                    List<Integer> list = new ArrayList<Integer>();
//                    for (Integer key : map.keySet()) {
//                        if (map.get(key)) {
//                            list.add(key);
//                        }
//                    }
//                    List<String> dhs = new ArrayList<>();
//                    for (int i = list.size() - 1; i >= 0; i--) {
//                        dhs.add(dhList.get(list.get(i)));
//                    }
//                    RequestParams params = new RequestParams(RFIDUtils.ZHUISU);
//                    params.addBodyParameter("DH", dhs.toString());
//                    System.out.println("danghao " + dhs.toString());
//                    doNetWork(params, 0);
//                }

//                break;
            case R.id.bt_plshanchu:
                if (dhList.size() > 0) {
                    HashMap<Integer, Boolean> map = plZhuiSuAdapter.getHashMap();
                    List<Integer> list = new ArrayList<Integer>();
                    for (Integer key : map.keySet()) {
                        if (map.get(key)) {
                            list.add(key);
                        }
                    }

                    for (int i = list.size() - 1; i >= 0; i--) {
                        Integer integer = list.get(i);
                        dhList.remove(new Integer(integer).intValue());
                    }
                    plZhuiSuAdapter.initmap();
                    plZhuiSuAdapter.notifyDataSetChanged();
                }

                break;
//            case R.id.bt_qingkong:
//                dhList.clear();
//                System.out.println(dhList.size());
//                plZhuiSuAdapter.notifyDataSetChanged();
//                break;
            case R.id.bt_chaku:

                    btZhuiSu.setVisibility(View.VISIBLE);
                    btChaKu.setVisibility(View.GONE);
                    btPLShanChu.setVisibility(View.GONE);
                    btGuanBi.setVisibility(View.VISIBLE);
                    pllistView.setVisibility(View.GONE);
                    plZSJG.setVisibility(View.VISIBLE);
                    pllistView.setVisibility(View.GONE);
                if ("1".equals(type)){
                    List<String> list = new ArrayList<>();
                    if (dhList.size() > 0) {
                        HashMap<Integer, Boolean> map = plZhuiSuAdapter.getHashMap();
                        for (Integer key : map.keySet()) {
                            if (map.get(key)) {
                                list.add(dhList.get(key));
                            }
                        }
                    }else {
                        Toast.makeText(getActivity(),"请导入excel",Toast.LENGTH_SHORT).show();
                        etdrboxnum.setVisibility(View.GONE);
                        btZhuiSu.setVisibility(View.GONE);
                        btChaKu.setVisibility(View.VISIBLE);
                        btPLShanChu.setVisibility(View.VISIBLE);
                        btGuanBi.setVisibility(View.GONE);
                        etdrboxnum.setVisibility(View.VISIBLE);
                        return;
                    }
                    try {
                        js = new JSONArray();
                        for (int i = 0; i < list.size(); i++) {
                            js.put(new JSONObject().put("DH", list.get(i).toString()));
                        }
                        RequestParams params = new RequestParams(RFIDUtils.ZHUISU);
                        params.addBodyParameter("dhMap", js.toString());
                        System.out.println("danghao " + js.toString());

                        doNetWork(params, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if ("2".equals(type)){
                    String boxNum = etdrboxnum.getText().toString();
                    if (TextUtils.isEmpty(boxNum)) {
                        Toast.makeText(getActivity(), "请输入箱号", Toast.LENGTH_SHORT).show();
                        etdrboxnum.setVisibility(View.GONE);
                        btZhuiSu.setVisibility(View.GONE);
                        btChaKu.setVisibility(View.VISIBLE);
                        btPLShanChu.setVisibility(View.VISIBLE);
                        btGuanBi.setVisibility(View.GONE);
                        etdrboxnum.setVisibility(View.VISIBLE);

                    } else {
                        RequestParams params = new RequestParams(RFIDUtils.AXPANDIAN);
                        params.addBodyParameter("BOXNUM", String.valueOf(boxNum));
                        System.out.println("按箱盘点 该箱数据 " + String.valueOf(boxNum));
                        doNetWork(params, 0);
                    }
                }
                break;
            case R.id.bt_guanbi:
                if (uhfReader != null) {
                    runflag = false;
                    uhfReader.close();
                }
                break;
            case R.id.bt_zhuisu:


                Toast.makeText(getActivity(), "未入库", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < listEPC.size(); i++) {
                    if (Integer.decode(listEPC.get(i).getState()).intValue() == 1) {
                        System.out.println(listEPC.get(i));
                        epcss.add(listEPC.get(i));
                    }
                }
                listEPC.clear();
                for (int i = 0; i < epcss.size(); i++) {
                    listEPC.add(epcss.get(i));
                }
                System.out.println(listEPC.size());
                panDianListAdapter.notifyDataSetChanged();
                plZSJG.setVisibility(View.VISIBLE);
                pllistView.setVisibility(View.GONE);
//                onSuccess("", 0);
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
    List<EpcModel> epcss = new ArrayList<>();
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

    private String xlsPath;

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/vnd.ms-excel");
//        intent.setType("xls/*");
        intent.setType("application/vnd.ms-excel");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择要导入的Excel"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    xlsPath = WordUtils.getPath(getActivity(), uri);
                    System.out.println(xlsPath);
                    String end = xlsPath.substring(xlsPath.lastIndexOf(".") + 1, xlsPath.length()).toLowerCase();
                    System.out.println(end);
                    if (end.equals("xls")) {
                        dhList.clear();
                        try {
//                        InputStream is = getClass().getResourceAsStream("/assets/dh.xls");
                            InputStream is = new FileInputStream(xlsPath);
//           Workbook book = Workbook.getWorkbook(new File("mnt/sdcard/test.xls"));
                            Workbook book = Workbook.getWorkbook(is);
                            Sheet sheet = book.getSheet(0);
                            int Rows = sheet.getRows();
                            for (int j = 1; j < Rows; ++j) {
                                // getCell(Col,Row)获得单元格的值
                                String contents = sheet.getCell(1, j).getContents();
                                System.out.println(j + "    " + contents);
                                dhList.add(contents);
                            }
                            plZhuiSuAdapter.notifyDataSetChanged();
                            plZhuiSuAdapter.setmap();
                            book.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(String s, int requestcode) {
//        s = " {\"data\":[{\"STATE\":\"3\",\"EPC\":\"2015-DQ11-Y-1,193250,218265,\"},{\"STATE\":\"1\",\"EPC\":\"2015-DQ11-Y-2,193250,221064,\"},{\"STATE\":\"2\",\"EPC\":\"2015-DQ11-Y-4,193250,221064,\"},{\"STATE\":\"2\",\"EPC\":\"2015-DQ11-Y-3,5164396,341849,\"},{\"STATE\":\"3\",\"EPC\":\"2015-DR11-Y-5,193240,223064,\"}],\"resultcode\":\"200\"}";

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

    public void setdata(String data, int requestcod) {
        if (data != null && !"".equals(data)) {
            try {
                System.out.println("aaaaaaaa  "+data);
                listEPC.clear();
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if("无此数据".equals(jsonArray.getString(i))){
                        String dh = js.getJSONObject(i).getString("DH");
                        EpcModel epcTag=new EpcModel("",dh,"","","4");
                        listEPC.add(epcTag);
                    }else {
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
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
                        epcs.add(epc);
                    }
                    System.out.println("epcs  " + epcs.toString());
                    System.out.println("listEPC   "+listEPC.toString());
                }

                panDianListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "此库内无数据", Toast.LENGTH_LONG).show();
            etdrboxnum.setVisibility(View.GONE);
            btZhuiSu.setVisibility(View.GONE);
            btChaKu.setVisibility(View.VISIBLE);
            btPLShanChu.setVisibility(View.VISIBLE);
            btGuanBi.setVisibility(View.GONE);
            etdrboxnum.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailture(Throwable throwable, boolean b, int requestcode) {
Toast.makeText(getActivity(),"服务器异常",Toast.LENGTH_SHORT).show();
    }


}
