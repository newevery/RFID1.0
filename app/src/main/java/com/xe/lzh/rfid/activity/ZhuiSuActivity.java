package com.xe.lzh.rfid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.xe.lzh.rfid.R;
import com.xe.lzh.rfid.Utils.SpUtils;
import com.xe.lzh.rfid.fragment.PlzhuiSuFragment;
import com.xe.lzh.rfid.fragment.ShouDongZSFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_zhui_su)
public class ZhuiSuActivity extends BaseActivity {
    @ViewInject(R.id.vPager)
    ViewPager vPager;
    @ViewInject(R.id.textView2)
    TextView textView2;
    @ViewInject(R.id.textView3)
    TextView textView3;
    @ViewInject(R.id.tv_name)
    TextView tv_name;
    @ViewInject(R.id.tv_time)
    TextView tv_time;

    @Override
    public void setting(View view) {
        super.setting(view);
        Intent intent = new Intent();
        intent.putExtra("falg", "zhuisu");
        intent.setClass(ZhuiSuActivity.this, SheZhiActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        String loginUser_name = (String) SpUtils.get(this, "username", "操作人");
        tv_name.setText("欢迎您，"+loginUser_name);
        settext("追溯");

        textView3.setBackgroundColor(Color.BLUE);
        textView2.setBackgroundColor(Color.GRAY);
        vPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int index)//直接创建fragment对象并返回
            {
                switch (index) {
                    case 0:

                        return new ShouDongZSFragment();
                    case 1:

                        return new PlzhuiSuFragment();
                }
                return null;
            }
        });
        vPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    textView3.setBackgroundColor(Color.BLUE);
                    textView2.setBackgroundColor(Color.GRAY);
                } else {
                    textView2.setBackgroundColor(Color.BLUE);
                    textView3.setBackgroundColor(Color.GRAY);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }

    @Event(value = {R.id.textView2, R.id.textView3})
    private void Click(View view) {
        switch (view.getId()) {
            case R.id.textView2:
                vPager.setCurrentItem(1);
                break;
            case R.id.textView3:
                vPager.setCurrentItem(0);
                break;
        }
    }



    @Override
    public void setdata(String data, int requestcode) {

    }
}
