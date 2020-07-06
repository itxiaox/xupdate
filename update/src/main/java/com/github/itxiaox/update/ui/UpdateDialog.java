package com.github.itxiaox.update.ui;

import android.app.Activity;
import android.app.AlertDialog;
import com.github.itxiaox.update.R;
import com.github.itxiaox.update.UpdateManager;
import com.github.itxiaox.update.apk.UpdateInfo;

/**
 * Author:xiao
 * Time: 2020/6/24 14:29
 * Description:This is UpdateDialog
 */
public class UpdateDialog {

    public static void showUpdateDialog(Activity activity, UpdateInfo updateInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(String.format(activity.getString(R.string.update_title),
                updateInfo.getVersionName()));
        builder.setMessage(showDesc(updateInfo.getDesc()));
        builder.setPositiveButton(R.string.update_sure, (dialog, which) -> {
            //有root权限直接下载
            UpdateManager.getInstance().update(updateInfo.getDownloadUrl(),
                    updateInfo.getMd5());
        });
        if (!updateInfo.isForce()){
            builder.setNegativeButton(R.string.update_cancel,
                    (dialog, which) -> dialog.dismiss());
        }else{
            builder.setCancelable(false);
            builder.setOnKeyListener((dialog, keyCode, event) -> true);
        }
        builder .create().show();
    }

    /**
     * 格式化更新内容的排版
     * 思路：碰到数字就换行
     * 如 1.增加版本更新功能 2.修复一些bug 3.提高程序性能
     * 格式化后：
     *  1.增加版本更新功能
     *  2.修复一些bug
     *  3.提高程序性能
     * @param desc
     * @return
     */
    private static String showDesc(String desc){
        String result = desc;
        try{
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < desc.length(); i++) {
                if (Character.isDigit(desc.charAt(i))) {
                    buffer.append("\n");
                }
                buffer.append(desc.charAt(i));
            }
            result = buffer.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
