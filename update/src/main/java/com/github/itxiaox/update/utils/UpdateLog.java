package com.github.itxiaox.update.utils;

import android.util.Log;

/**
 * Author:xiao
 * Time: 2020/6/18 17:41
 * Description:This is UpdateLog
 */
public class UpdateLog {

    private static final String TAG = "UpdateLog";
    public static boolean enableLog = true;

    public static void d(String format,Object... objects){
        if (enableLog)
        Log.d(TAG, String.format(format,objects));
    }
    public static void e(String format,Object... objects){
        if (enableLog)
        Log.e(TAG,String.format(format,objects));
    }
}
