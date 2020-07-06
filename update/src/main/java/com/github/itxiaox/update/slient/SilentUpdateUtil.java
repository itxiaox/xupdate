package com.github.itxiaox.update.slient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Carl on 2018/5/6 006.
 */

public class SilentUpdateUtil {

    /**
     * 检查是否有root权限
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return ShellUtils.checkRootPermission();
    }

    /**
     * 执行静默更新
     * @param context
     * @param apkPath 待安装apk完整路径
     * @param callback
     */
    public static void installSilent(Context context, String apkPath, final SilentUpdateCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (ShellUtils.checkRootPermission()) {
            final int resultCode = PackageUtils.installSilent(context, apkPath);
            if (resultCode != PackageUtils.INSTALL_SUCCEEDED) {
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(resultCode);
                        }
                    });
                }
            } else {
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    });
                }
            }
        } else {
            if (callback != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.noRootPermission();
                    }
                });
            }
        }
    }

    public interface SilentUpdateCallback {
        void noRootPermission();

        void onSuccess();

        void onFailed(int code);
    }


}
