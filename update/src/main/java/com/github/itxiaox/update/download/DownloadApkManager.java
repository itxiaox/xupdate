package com.github.itxiaox.update.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.itxiaox.update.UpdateManager;

import java.io.File;

/**
 * author: Created by lixiaotong on 2018/10/17
 * e-mail: 516030811@qq.com
 */
public class DownloadApkManager {
    private static final String TAG = "DownloadApkManager";
    private static DownloadApkManager instance;
    //下载器
    private DownloadManager downloadManager;
    //下载的ID
    private long downloadId;
    private String downloadPath;
    private Context mContext;

    public  static DownloadApkManager getInstances(Context context){
        if (instance==null){
            synchronized (DownloadApkManager.class){
                instance = new DownloadApkManager(context);
            }
        }
        return instance;
    }
    public DownloadApkManager(Context context) {
        mContext = context.getApplicationContext();
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private String title;
    public void setTitle(String title){
        this.title = title;
    }
    DownloadManager.Request request;
    String desc;

    public void setDownloadPath(String path){
        downloadPath = path;
    }
    //下载apk
    public void downloadAPK(String url, String name) {
        if (downloadId!=0){//如果当前Id已经存在了
            downloadManager.remove(downloadId);//先移除前一个任务防止重复下载
        }
        //创建下载任务
     request = new DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //隐藏下载通知
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        request.setTitle(TextUtils.isEmpty(title)?name:title);
         desc = name.replace(".apk","");
//        request.setDescription(String.format("%s 正在下载中",desc));
        request.setVisibleInDownloadsUi(true);

        //设置下载文件保存位置
        request.setDestinationUri(Uri.fromFile(new File(downloadPath)));
        //获取DownloadManager
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        downloadId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public String getDownloadPath(){
        return downloadPath;
    }

//    private String activityName;
//    public void setActivityName(String activityName){
//        this.activityName = activityName;
//    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    //检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
       // int total =  cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        // int downloadSize =  cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
//                    UpdateLog.d("下载%d/%d",downloadSize,total);
//                    request.setDescription(String.format("%s 正在下载中 %d/%d",desc,downloadSize/total));
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                   // UpdateLog.d("下载成功，开始安装：downloadSize=%d,downloadPath=%s",downloadSize, downloadPath);
                    UpdateManager.getInstance().install(downloadPath);
                    //下载完成安装APK
//                   ApkController.install(mContext,downloadPath);
//                    UpdateLog.d("下载成功，安装结果=%s,packageName=%s,activityName=%s",
//                            result,mContext.getPackageName(),activityName);
//                    LogUtils.writeToFile("安装结果："+result);
//                    if (result){
//                        LogUtils.writeToFile("开始启动App："+result);
//                       boolean  restart = ApkController.startApp(mContext.getPackageName(),
//                               activityName);
//                       UpdateLog.d("下载成功，启动结果="+restart);
//                    }
                    cursor.close();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
    }

//    private void installAPK() {
//        setPermission(pathstr);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //Android 7.0以上要使用FileProvider
//        if (Build.VERSION.SDK_INT >= 24) {
//            File file = (new File(pathstr));
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            Uri apkUri = FileProvider.getUriForFile(mContext, ContextUtils.getPackName()+".fileprovider", file);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS, name)),
//                    "application/vnd.android.package-archive");
//        }
//        mContext.startActivity(intent);
//    }

//    //修改文件权限
//    private void setPermission(String absolutePath) {
//        String command = "chmod " + "777" + " " + absolutePath;
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            runtime.exec(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(long downloadId) {
        try {
            if (downloadManager!=null)
            downloadManager.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }


}