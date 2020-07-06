package com.github.itxiaox.update.slient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReplaceAddRemoveBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "silentUpdate";

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())|| Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
//            Uri data = intent.getData();
//            if (data != null && context.getPackageName().equals(data.getEncodedSchemeSpecificPart())) {
//
//                LogUtils.writeToFile("更新安装成功.....,");
//                // 重新启动APP
//                Intent intentToStart = context.getPackageManager()
//                        .getLaunchIntentForPackage(context.getPackageName());
//                context.startActivity(intentToStart);
//            }
//        }

        // 重新启动APP
        Intent intentToStart = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intentToStart);
    }


}