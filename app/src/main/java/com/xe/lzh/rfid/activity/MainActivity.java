package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.RFIDUtils;
import com.xe.lzh.rfid.Utils.SpUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @ViewInject(R.id.tv_jieyue)
    TextView tv_jieyue;
    @ViewInject(R.id.tv_guihuan)
    TextView tv_guihuan;
    @ViewInject(R.id.tv_ruku)
    TextView tv_ruku;
    @ViewInject(R.id.tv_pandian)
    TextView tv_pandian;

    @ViewInject(R.id.tv_zhuisu)
    TextView tv_zhuisu;
    @ViewInject(R.id.tv_tuichu)
    TextView tv_tuichu;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    String loginUser_id;
    String loginUser_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        loginUser_id = (String) SpUtils.get(this, "userid", "1");
        loginUser_name = (String) SpUtils.get(this,"username","操作人");
        tv_name.setText("欢迎您，"+loginUser_name);
        Date date = new Date();
        String datestr = RFIDUtils.sdf.format(date);
        tv_time.setText(datestr);
    }


    Intent intent = new Intent();
    @Event(value = {R.id.tv_jieyue,R.id.tv_guihuan,R.id.tv_ruku,R.id.tv_pandian,R.id.tv_zhuisu,R.id.tv_tuichu})
    private void onclik(View view){
       switch (view.getId()){
           case R.id.tv_jieyue:
               intent.setClass(MainActivity.this,JieYueActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_guihuan:
               intent.setClass(MainActivity.this,GuiHuanActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_ruku:
               intent.setClass(MainActivity.this,RukuActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_pandian:
               intent.setClass(MainActivity.this,PanDianActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_zhuisu:
               intent.setClass(MainActivity.this,ZhuiSuActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_tuichu:
               finish();
//               Intent intent =new Intent();
//               intent.setClass(MainActivity.this,LoginActivity.class);
//               startActivity(intent);
               break;
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        settext("RFID档案管理系统");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setdata(String data, int requestcode) {

    }
}
