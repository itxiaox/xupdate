package com.github.itxiaox.update;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.itxiaox.update.apk.ApkController;
import com.github.itxiaox.update.apk.UpdateInfo;
import com.github.itxiaox.update.download.DownloadApkManager;
import com.github.itxiaox.update.slient.SilentUpdateUtil;
import com.github.itxiaox.update.utils.UpdateLog;
import com.github.itxiaox.update.utils.UpdateUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Author:xiao
 * Time: 2020/6/18 17:34
 * Description:This is UpdateManager
 */
public class UpdateManager {
    private static Context mContext;
    private static String downloadDir;
    private static DownloadApkManager downloadApkManager;
    private static UpdateManager instance;
    private static String mActivityName;

    public static UpdateManager getInstance(){
        synchronized (UpdateManager.class){
            if (instance==null){
                instance = new UpdateManager();
            }
        }
        return instance;
    }

    /**
     * 下载初始化
     * @param context
     * @param appName， 应用名
     */
    public static void init(Context context,String appName,String path,String activityName){
        mContext = context.getApplicationContext();
        mActivityName = activityName;
        downloadDir = path;
        if (TextUtils.isEmpty(downloadDir)){
            downloadDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        downloadApkManager = DownloadApkManager.getInstances(mContext);
        downloadApkManager.setTitle(appName);
    }

    public void update(String downloadUrl,String md5){
        if (TextUtils.isEmpty(downloadDir)){
            throw new RuntimeException("下载路径为null，请先设置下载路径");
        }
        if (existApk(md5,getFileName(downloadUrl))){//apk存在，直接安装，不用下载
            String fileName = getFileName(downloadUrl);
            File file = new File(downloadDir,fileName);
            //安装,
            install(file.getAbsolutePath());
        }else {
            update(downloadUrl);
        }
    }
    public void update(@NonNull  UpdateInfo updateInfo){
        if (updateInfo==null){
            throw new NullPointerException("updateInfo is null");
        }
        if (TextUtils.isEmpty(updateInfo.getDownloadUrl())){
            throw new RuntimeException("下载路径为null，请先设置下载路径");
        }
        if (existApk(updateInfo.getMd5(),getFileName(updateInfo.getDownloadUrl()))){//apk存在，直接安装，不用下载
            String fileName = getFileName(updateInfo.getDownloadUrl());
            File file = new File(downloadDir,fileName);
            //安装,
            install(file.getAbsolutePath());
        }else {
            update(updateInfo.getDownloadUrl());
        }
    }

    private String getFileName(String downloadUrl){
        String[] strings = downloadUrl.split("/");
        String fileName = strings[strings.length-1];
        return fileName;
    }

    private void update(String url){
        String fileName = getFileName(url);
        File file = new File(downloadDir,fileName);
        downloadApkManager.setDownloadPath(file.getAbsolutePath());
        downloadApkManager.downloadAPK(url,fileName);
    }

    public void install(String apkPath){
        if (SilentUpdateUtil.checkRootPermission()){
            //已经root了，采用静默安装
            silentInstall(mContext,apkPath);
        }else {
            ApkController.normalInstallApk(mContext,apkPath);
        }
    }

    public void silentInstall(Context context,String apkPath){
        // 有root权限，利用静默安装实现
        SilentUpdateUtil.installSilent(context, apkPath, new SilentUpdateUtil.SilentUpdateCallback() {
            @Override
            public void noRootPermission() {
                UpdateLog.d("静默安装: noRootPermission");
            }

            @Override
            public void onSuccess() {
                UpdateLog.d("静默安装：success");
//                ApkController.startApp(context.getPackageName(),mActivityName);
//                ApkController.startBroadcastReceiver(context);
            }

            @Override
            public void onFailed(int code) {

            }
        });
        ApkController.startApp(context.getPackageName(),mActivityName);
    }

    private boolean existApk(String md5,String fileName){
        if (TextUtils.isEmpty(md5)){
            return false;
        }
        if (TextUtils.isEmpty(downloadDir)){
            return false;
        }
        File file = new File(downloadDir,fileName);
        if (!file.exists()){
            return false;
        }
        try {
            String nativeFile = UpdateUtils.getMd5ByFile(file);
            if (TextUtils.equals(md5,nativeFile)){
                return true;
            }else{
                file.delete();
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void silentInstallApkByReflect(PackageManager packageManager,String apkPath) {
        Class<?> pmClz = packageManager.getClass();
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Class<?> aClass = Class.forName("android.app.PackageInstallObserver");
                Constructor<?> constructor = aClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object installObserver = constructor.newInstance();
                Method method =
                        pmClz.getDeclaredMethod("installPackage", Uri.class, aClass, int.class, String.class);
                method.setAccessible(true);
                method.invoke(packageManager, Uri.fromFile(new File(apkPath)), installObserver, 2, null);
            } else {
                Method method = pmClz.getDeclaredMethod("installPackage", Uri.class,
                        Class.forName("android.content.pm.IPackageInstallObserver"), int.class, String.class);
                method.setAccessible(true);
                method.invoke(packageManager, Uri.fromFile(new File(apkPath)), null, 2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
