package com.github.itxiaox.update.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.itxiaox.update.UpdateManager;
import com.github.itxiaox.update.apk.ApkController;
import com.github.itxiaox.update.apk.UpdateInfo;
import com.github.itxiaox.update.slient.SilentUpdateUtil;
import com.github.itxiaox.update.ui.UpdateDialog;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv_show);
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
//        String filePath = "/sdcard/new.apk";
//        ApkController.normalInstallApk(this,filePath);
        //UpdateManager.getInstance()
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setVersionCode(101);
        updateInfo.setVersionName("1.0.1");
        updateInfo.setDesc("1.新增功能 2.修复bug 3.提高性能");
        updateInfo.setDownloadUrl("http://www.baidu.com");
        updateInfo.setMd5("");
        UpdateDialog.showUpdateDialog(this, updateInfo);
    }
}