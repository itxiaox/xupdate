package com.github.itxiaox.update.download;

public interface DownloadApkListener {
    void onStart();
    void onProgress(long downloadOfSize,long totalSize);
    void onFinish(String path);
    void onError(String msg);
}