package com.xe.lzh.rfid.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.tree.Node;
import com.xe.lzh.rfid.tree.TreeListViewAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */

public class DossierAdpter<T> extends TreeListViewAdapter<T> {
    private SelectCallback selectCallback;

    public SelectCallback getSelectCallback() {
        return selectCallback;
    }

    public void setSelectCallback(SelectCallback selectCallback) {
        this.selectCallback = selectCallback;
    }

    public DossierAdpter(ListView mTree, Context context, List<T> datas, int defaultExpandLevel,SelectCallback selectCallback) throws IllegalArgumentException, IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
        this.selectCallback = selectCallback;
    }

    @Override
    public View getConvertView(final Node node, int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_dossier, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.id_treenode_icon);
            viewHolder.label = (TextView) convertView
                    .findViewById(R.id.id_treenode_label);
//            viewHolder.bt_select = (Button) convertView
//                    .findViewById(R.id.bt_select);
            convertView.setTag(viewHolder);

        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getIcon() == -1)
        {
            viewHolder.icon.setVisibility(View.INVISIBLE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectCallback.select(node.getId());
                }
            });
        } else
        {
            viewHolder.icon.setVisibility(View.VISIBLE);
            viewHolder.icon.setImageResource(node.getIcon());
        }
        viewHolder.label.setText(node.getName());
        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCallback.select(node.getId());
            }
        });
        return convertView;
    }
    private final class ViewHolder
    {
        ImageView icon;
        TextView label;
//        Button bt_select;
    }
    public interface SelectCallback {

        void select(int id) ;


    }
}
