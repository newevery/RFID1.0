package com.xe.lzh.rfid.adpter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.activity.XiangqingActivity;

public class PanDianListAdapter extends BaseAdapter {
    private List<EpcModel> objects;

    private Context context;
    private LayoutInflater layoutInflater;

    public PanDianListAdapter(Context context, List<EpcModel> objects) {
        this.context = context;
        this.objects = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public EpcModel getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list2, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((EpcModel) getItem(position), (ViewHolder) convertView.getTag(), position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Activity) context, XiangqingActivity.class);
                intent.putExtra("EPC",objects.get(position).getEPC());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private void initializeViews(EpcModel object, ViewHolder holder, int p) {
        holder.tvId.setText(p + 1 + "");
        holder.tvDh.setText(object.getDH());
        holder.tvKh.setText(object.getGroupID());
        holder.tvDataid.setText(object.getDataID());
        Integer decode = Integer.decode(object.getState());
        if (decode==-1){
            holder.tvState.setText("未打印标签");
        }else if(decode==0){
            holder.tvState.setText("已打印标签");
        }else  if(decode==1){
            holder.tvState.setText("已入库");
        }else if (decode==2){
            holder.tvState.setText("已出库");
        }else  if (decode==3){
            holder.tvState.setText("已出库");
        }else{
            holder.tvState.setText("无此档案");
        }

    }


    class ViewHolder {
        private TextView tvId;
        private TextView tvDh;
        private TextView tvKh;
        private TextView tvDataid;
        private TextView tvState;

        public ViewHolder(View view) {
            tvId = (TextView) view.findViewById(R.id.tv_1id);
            tvDh = (TextView) view.findViewById(R.id.tv_1dh);
            tvKh = (TextView) view.findViewById(R.id.tv_1kh);
            tvDataid = (TextView) view.findViewById(R.id.tv_1dataid);
            tvState = (TextView) view.findViewById(R.id.tv_1state);
        }
    }
}
