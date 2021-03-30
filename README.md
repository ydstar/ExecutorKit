# ExecutorKit

## YdKit通用组件库
YdKit 是一组功能丰富的 Android 通用组件。

* [LogKit](https://github.com/ydstar/LogKit) — 轻量级的 Android 日志系统。
* [RestfulKit](https://github.com/ydstar/RestfulKit) — 简洁但不简单的 Android 网络组件库。
* [StorageKit](https://github.com/ydstar/StorageKit) — 高性能 Android 离线缓存框架。
* [ExecutorKit](https://github.com/ydstar/ExecutorKit) — 简洁易用的 Android 多线程操作框架。
* [CrashKit](https://github.com/ydstar/CrashKit) — 简洁易用的 Android Crash日志捕捉组件。
* [PermissionKit](https://github.com/ydstar/PermissionKit) — 简洁易用的 Android 权限请求组件。
* [RefreshKit](https://github.com/ydstar/RefreshKit) — 简洁易用的 Android 下拉刷新和上拉加载组件。
* [AdapterKit](https://github.com/ydstar/AdapterKit) — 简洁易用的 Android 列表组件。
* [BannerKit](https://github.com/ydstar/BannerKit) — 简洁易用的 Android 无限轮播图组件。
* [TabBottomKit](https://github.com/ydstar/TabBottomKit) — 简洁易用的 Android 底部导航组件。

## 简介
简洁易用多线程操作框架

## 导入方式

仅支持`AndroidX`
```
dependencies {
     implementation 'com.android.ydkit:executor-kit:1.0.0'
}
```

## 使用方法

#### 开启线程池
```java

ExecutorKit.execute(runnable = Runnable {
    //开启子线程做耗时操作,比如数据的读写
})

```


#### 异步任务结果回调主线程
```java
ExecutorKit.execute(0,object : ExecutorKit.Callable<String?>() {

     //任务执行前_主线程
     override fun onPrepare() {
       //可以转菊花

     }

     //任务执行中_子线程
     override fun onBackground(): String? {

     }

     //任务执行结束_主线程
     override fun onCompleted(s: String?) {

     }
})
```

#### 暂停线程池

```
ExecutorKit.pause()

```

#### 恢复线程池
```java
ExecutorKit.resume()

```



## License
```text
Copyright [2021] [ydStar]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
