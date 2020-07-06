package com.github.itxiaox.update.apk;

/**
 * Author:xiao
 * Time: 2020/6/24 13:57
 * Description:This is ApkInfo
 */
public class UpdateInfo {
    private int versionCode;
    private String versionName;
    private String desc;//版本描述
    private String downloadUrl;
    private String md5;
    private boolean isForce;//是否强制更新
    private boolean isShowDialog; //是否显示更新对话框

    public UpdateInfo() {
    }

    public UpdateInfo(int versionCode, String desc, String downloadUrl) {
        this.versionCode = versionCode;
        this.desc = desc;
        this.downloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }
}
