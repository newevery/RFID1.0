package com.xe.lzh.rfid.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.activity.XiangqingActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class RfidAdpter extends BaseAdapter {
    private List<EpcModel> data = null;
    private Context context = null;
    private HashMap<Integer, Boolean> hashMap;

    public RfidAdpter(List<EpcModel> list, Context context) {
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
        final EpcModel epcModel = data.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            viewHolder.XH = (TextView) convertView.findViewById(R.id.tv_xuhao);
            viewHolder.tv_epc = (TextView) convertView.findViewById(R.id.tv_epc);
            viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.XH.setText(String.valueOf(position + 1));
//            viewHolder.tv_epc.setText(epcModel.getEPC());
        viewHolder.tv_epc.setText(epcModel.getDH());
//            viewHolder.tv_count.setText(String.valueOf(epcModel.getCount()));
//        viewHolder.tv_count.setText(epcModel.getDataID());
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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Activity) context, XiangqingActivity.class);
                intent.putExtra("EPC", epcModel.getEPC());
                context.startActivity(intent);
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
    public void selectmap() {
        hashMap = new HashMap<Integer, Boolean>();
        for (int i = 0; i < data.size(); i++) {
            hashMap.put(i, true);
        }
    }

    class ViewHolder {
        TextView XH;
        TextView tv_epc;
        TextView tv_count;
        CheckBox cb;
    }

}
