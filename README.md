# XUpdate Android 版本更新功能
[![](https://jitpack.io/v/itxiaox/XUpdate.svg)](https://jitpack.io/#itxiaox/XUpdate)
#### 介绍
版本更新模块，将项目常见的版本更新功能封装成一个通用模块，便于以后在项目中复用， 提供普通更新安装，和静默安装功能（需要Root），静默升级针对售货机、电子柜等智能终端提供远程升级功能。


#### 使用说明

1.  在项目的根build.gradle文件中添加如下代码：
```
allprojects {
	repositories {
			...
			maven { url 'https://jitpack.io' }
	}
}
```

2.  在需要使用的module中添加依赖

```
dependencies {
	          implementation 'com.github.itxiaox:XUpdate:1.0.2'
	}
```

3.  代码中使用
```         
 	//UpdateInfo信息可以通过版本更新接口获取
        UpdateInfo updateInfo = new UpdateInfo();
	updateInfo.setVersionCode(versionCode);
	updateInfo.setVersionName(versionName);
	updateInfo.setDownloadUrl(apkUrl);
	updateInfo.setMd5(md5);
	updateInfo.setDesc(apkDesc);
	if (ApkController.hasRootPerssion()){
	    //有root权限直接下载
	    UpdateManager.getInstance().update(updateInfo);
	}else {//没有root权限，弹窗提示
	    UpdateDialog.showUpdateDialog(mActivity,updateInfo);
	}
```
