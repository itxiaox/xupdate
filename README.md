# XUpdate Android 版本更新功能
[![](https://jitpack.io/v/itxiaox/XUpdate.svg)](https://jitpack.io/#itxiaox/XUpdate)
#### 介绍
版本更新模块，将项目常见的版本更新功能封装成一个通用模块，便于以后在项目中复用， 提供普通更新安装，和静默安装功能（需要Root）


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
	          implementation 'com.github.itxiaox:XUpdate:v1.0.1'
	}
```

3.  代码中使用

    待更新
