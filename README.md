# 大头秀 安卓版

## 核心代码文档

### 1. 输入

1. 路路径: app/java/activity/NavigatorActivity
2. NavigatorActivity 就是输⼊入 url 的界面
3. 跳转代码:

```java
Intent intent = new Intent();
// 指定视频的 url
intent.putExtra(MixtureKeys.KEY_VIDEO_PATH, mVideoUrl.getText().toString());
// 指定头像数据的 url
intent.putExtra(MixtureKeys.KEY_DATA_PATH, mDataUrl.getText().toString());
intent.setClass(NavigatorActivity.this, MainActivity.class);
startActivity(intent);
```

### 2. 输出
1. 路路径: app/java/activity/MainActivity
2. 第 683 ⾏代码 backToNavActivity ⽅方法
3. 只要退出当前⻚面,⽆论是什么情况,都会触发 backToNavActivity ⽅法
4. 关键代码:

```java
Intent intent = new Intent();
// 当前的状态
intent.putExtra(MixtureKeys.KEY_MIXTURE_STATE, mMixtureResult.state);
// 返回的提示信息
intent.putExtra(MixtureKeys.KEY_MIXTURE_MESSAGE, mMixtureResult.message);
// 合成完成后视频保存的地址
intent.putExtra(MixtureKeys.KEY_MIXTURE_VIDEO_PATH, mMixtureResult.videoUrl);
intent.setClass(MainActivity.this, NavigatorActivity.class);
startActivity(intent);
```

### 3. 解释

1. mMixtureResult.state 是当前的状态有以下几种:

|返回宏|解释|
| --- | :-: |
|DOWNLOADERROR|下载出错|
|SUCCESS|合成成功|
|EXCEPTION|异常出错|
|CANCEL|点击右上⻆退出按钮|
|NOCAMERA|无相机|
|NOCAMERAPERMISSION|⽆相机许可|

2. mMixtureResult.message 是返回的提示信息
3. mMixtureResult.videoUrl 是合成功后的视频保存的路径
