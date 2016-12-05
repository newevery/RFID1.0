package com.xe.lzh.rfid.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class PLZhuiSuAdapter extends BaseAdapter {
    private List<String> data = null;
    private Context context = null;
    private HashMap<Integer, Boolean> hashMap;

    public PLZhuiSuAdapter(List<String> list, Context context) {
        data = list;
        this.context = context;
        initmap();

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            viewHolder.XH = (TextView) convertView.findViewById(R.id.tv_xuhao);
            viewHolder.tv_epc = (TextView) convertView.findViewById(R.id.tv_epc);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.XH.setText(String.valueOf(position + 1));
//            viewHolder.tv_epc.setText(epcModel.getEPC());
        viewHolder.tv_epc.setText(data.get(position));
        if (hashMap.get(position)) {
            viewHolder.cb.setChecked(true);
        } else {
            viewHolder.cb.setChecked(false);
        }
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hashMap.put(position, isChecked);
            }
        });
        return convertView;
    }

    public HashMap<Integer, Boolean> getHashMap() {
        return hashMap;
    }

    public void initmap() {
        hashMap = new HashMap<Integer, Boolean>();
        for (int i = 0; i < data.size(); i++) {
            hashMap.put(i, false);
        }
    }
    public void setmap() {
        hashMap = new HashMap<Integer, Boolean>();
        for (int i = 0; i < data.size(); i++) {
            hashMap.put(i, true);
        }
    }

    class ViewHolder {
        TextView XH;
        TextView tv_epc;
        CheckBox cb;
    }


}
