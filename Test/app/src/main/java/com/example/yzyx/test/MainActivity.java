package com.example.yzyx.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomProgressDialog dialog=new CustomProgressDialog(this,"",R.anim.progress_round);
        dialog.show();
    }
}
