package com.github.itxiaox.update.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.itxiaox.update.UpdateManager;
import com.github.itxiaox.update.apk.ApkController;
import com.github.itxiaox.update.slient.SilentUpdateUtil;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_show);
        textView.setText("new apk");
    }

    public void isRoot(View view) {
        boolean isRoot = SilentUpdateUtil.checkRootPermission();
        textView.setText(isRoot ? "已经Root了" : "没有Root");
    }

    public void installed(View view) {
        String filePath = "/sdcard/new.apk";
        UpdateManager.getInstance().silentInstall(this, filePath);
    }

    public void normalInstall(View view) {
        String filePath = "/sdcard/new.apk";
        ApkController.normalInstallApk(this,filePath);
    }
}