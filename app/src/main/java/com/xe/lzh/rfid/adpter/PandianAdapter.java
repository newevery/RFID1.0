package com.xe.lzh.rfid.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xe.lzh.rfid.Model.EpcModel;
import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.activity.XiangqingActivity;
import com.xe.lzh.rfid.activity.XiangxixinxiActivity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.content.ContextCompat.startActivities;

/**
 * Created by Administrator on 2016/9/19.
 */
public class PandianAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private List<EpcModel> data = null;
    private Context context = null;
    public String i;
    private HashMap<Integer, Boolean> hashMap;

    public PandianAdapter(List<EpcModel> list, Context context) {
        data = list;
        this.context = context;
        hashMap = new HashMap<Integer, Boolean>();
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
        EpcModel epcModel = data.get(position);

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list2, null);
            viewHolder.XH = (TextView) convertView.findViewById(R.id.tv_1id);
            viewHolder.DH = (TextView) convertView.findViewById(R.id.tv_1dh);
            viewHolder.KH = (TextView) convertView.findViewById(R.id.tv_1kh);
            viewHolder.ID = (TextView) convertView.findViewById(R.id.tv_1dataid);
//            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(context,XiangxixinxiActivity.class);
//
//
//                context.startActivity(intent);
//
//            }
//        });
        viewHolder.XH.setText(String.valueOf(position+1));
        viewHolder.DH.setText(epcModel.getDH());
        viewHolder.KH.setText(epcModel.getGroupID());
        viewHolder.ID.setText(epcModel.getDataID());
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
        for (int i = 0; i < data.size(); i++) {
            hashMap.put(i, false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent((Activity) context, XiangqingActivity.class);
        intent.putExtra("EPC",data.get(position).getEPC());
        context.startActivity(intent);

    }


    class ViewHolder {
        TextView XH;
        TextView DH;
        TextView KH;
        TextView ID;
        CheckBox cb;
    }

}
