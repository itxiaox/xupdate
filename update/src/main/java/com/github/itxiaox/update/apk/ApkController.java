package com.github.itxiaox.update.apk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.github.itxiaox.update.slient.SilentUpdateUtil;
import com.github.itxiaox.update.utils.UpdateLog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


public class ApkController {
    private static final String TAG = "ApkController";

    /**
     * 普通安装，需要手动点击下一步进行安装
     * 如果downloadAPk路径错误，或者没有访问权限(如sdcard读写权限)会出现解析包错误
     * @param context
     * @param downloadApk
     */
    public static  void normalInstallApk(Context context,String downloadApk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(downloadApk);
        UpdateLog.d("安装路径==%s",downloadApk);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {////判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(context,
                    context.getPackageName() +".provider", file);
            //这里需要读写权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //如果只有FLAG_GRANT_READ_URI_PERMISSION,则会出现解析软件包出现问题
            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }

    /**
     * 描述: 卸载
     */
    public static boolean uninstall(String packageName,Context context){
        if(hasRootPerssion()){
            // 有root权限，利用静默卸载实现
            return clientUninstall(packageName);
        }else{
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
            return true;
        }
    }

    /**
     * 判断手机是否有root权限
     */
    public static boolean hasRootPerssion(){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 静默安装
     */
    public static boolean clientInstall(String apkPath){
        PrintWriter PrintWriter = null;
        UpdateLog.d("静默安装：安装路径=%s",apkPath);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            UpdateLog.d("value=%d",value);
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    public static  void clientInstall(Context context,String apkPath){
        SilentUpdateUtil.installSilent(context, apkPath, new SilentUpdateUtil.SilentUpdateCallback() {
            @Override
            public void noRootPermission() {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed(int code) {

            }
        });
    }

    /**
     * 静默卸载
     */
    public static boolean clientUninstall(String packageName){
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("adb uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(process!=null){
                process.destroy();
            }
        }
        return false;
    }

    /**
     * 启动app
     * com.exmaple.client/com.exmaple.client.MainActivity
     */
    public static boolean startApp(String packageName,String activityName){
        boolean isSuccess = false;
        //am start -n com.xczt.dianzigui/MainActivity
        String cmd = "am start  " + packageName + "/." + activityName + " \n";
        UpdateLog.d("startApp:%s",cmd);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(process!=null){
                process.destroy();
            }
        }
        return isSuccess;
    }

    private static boolean returnResult(int value){
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }



    /*
　　@pararm apkPath 等待安装的app全路径，如：/sdcard/app/app.apk
**/
    public static boolean clientInstall2(String apkPath) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 " + apkPath);
            PrintWriter
                    .println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r " + apkPath);
            // PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            UpdateLog.d("静默安装返回值：%d",value);
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
            UpdateLog.e("安装apk出现异常：%s",e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

//    public static void startBroadcastReceiver(Context context){
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        //当网络发生变化的时候，系统广播会发出值为android.net.conn.CONNECTIVITY_CHANGE这样的一条广播
//        context.registerReceiver(new ReplaceAddRemoveBroadcastReceiver(),intentFilter);
//    }

    public static void execLinuxCommand(String packageName,String activityName) {
        String cmd = "sleep 120; am start -n " + packageName + "/" + activityName + " \n";
        //Runtime对象
        Runtime runtime = Runtime.getRuntime();
        try {
            Process localProcess = runtime.exec("su");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
//            int value = localProcess.waitFor();
            UpdateLog.d("设备准备重启");
        } catch (IOException e) {
            UpdateLog.e("设备重启失败，",e.getMessage());
        }
    }

//        public static void installAPK(String apkPath){
//            AutoInstaller installer = new AutoInstaller.Builder(ContextUtils.getContext())
//                    .setMode(AutoInstaller.MODE.ROOT_ONLY)
////                    .setCacheDirectory(CACHE_FILE_PATH)
//                    .build();
////            AutoInstaller installer = AutoInstaller.getDefault(ContextUtils.getContext());
//            installer.install(apkPath);
//            installer.setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
//                @Override
//                public void onStart() {
//                    // 当后台安装线程开始时回调
//                   LogUtils.d_foramt("后台开始安装");
//                }
//                @Override
//                public void onComplete() {
//                    // 当请求安装完成时回调
//                    LogUtils.d_foramt("安装完成");
//                }
//                @Override
//                public void onNeed2OpenService() {
//                    // 当需要用户手动打开 `辅助功能服务` 时回调
//                    // 可以在这里提示用户打开辅助功能
//                    LogUtils.d_foramt("请打开辅助功能");
//                }
//            });
//        }

}
