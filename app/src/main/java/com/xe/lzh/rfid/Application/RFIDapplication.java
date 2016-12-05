package com.xe.lzh.rfid.Application;

import android.app.Application;

import com.android.hdhe.uhf.reader.UhfReader;

import org.xutils.x;

/**
 * Created by Administrator on 2016/8/30.
 */
public class RFIDapplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);

    }
}
