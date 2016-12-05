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
 * Created by Administrator on 2016/9/19.
 */
public class RfidAdpter1 extends BaseAdapter {
    private List<EpcModel> data = null;
    private Context context = null;

    public RfidAdpter1(List<EpcModel> list, Context context) {
        data = list;
        this.context = context;

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
            EpcModel epcModel = data.get(position);

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_saoma, null);
                viewHolder.XH = (TextView) convertView.findViewById(R.id.tv_xuhao);
                viewHolder.tv_epc = (TextView) convertView.findViewById(R.id.tv_epc);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.XH.setText(epcModel.getDataID());
            viewHolder.tv_epc.setText(epcModel.getDH());
          

        return convertView;
    }



    class ViewHolder {
        TextView XH;
        TextView tv_epc;

    }

}
