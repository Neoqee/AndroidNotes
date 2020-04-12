# Android

## 多渠道配置

可以设置buildConfig下的所有属性

### 流程

#### 设置维度：flavorDimensions

```
flavorDimensions "name1","name2",...
```

#### 设置不同版本：productFlavors

```
productFlavors{
	版本1{
		属性设置
	}
	...
	版本n{
		最重要的是每个版本都需要设置维度
		dimension "维度名"
	}
}
```

#### 设置源集

在不同版本里，可能存在差异的数据，可以在src文件夹下创建对应的源集。

##### 源集

> Android Studio 按逻辑关系将每个模块的源代码和资源分组为源集。模块main/源集包含其所有版本变体共用的代码和资源。其他源集目录是可选的，在配置新的版本变体时，Android Studio 不会自动创建这些目录。

###### src/main/

> 此源集包含所有版本变体共用的代码和资源。

###### src/buildType/

> 创建此源集以纳入特定版本类型专用的代码和资源。

###### src/productFlavor/

>  创建此源集以纳入特定产品变种专用的代码和资源。

> **注意**：如果将版本配置为[组合多个产品变种](https://developer.android.google.cn/studio/build/build-variants?hl=zh-cn#flavor-dimensions)，则可以为变种维度之间的每个产品变种组合创建源集目录：`src/productFlavor1ProductFlavor2/`

###### src/productFlavorBuildType/

> 创建此源集以纳入特定版本变体类型专用的代码和资源。

> 如果不同源集包含同一文件的不同版本，Gradle 将按以下优先顺序决定使用哪一个文件（左侧源集替换右侧源集的文件和设置）：
>
> 版本变体 > 版本类型 > 产品变种 > 主源集 > 库依赖项

清单文件合并需要在存在冲突时需要添加合并规则标记

参考链接：

- [配置您的版本](https://developer.android.google.cn/studio/build/index.html?hl=zh-cn#sourcesets)
- [合并多个清单文件](https://developer.android.google.cn/studio/build/manifest-merge?hl=zh-cn)
- [配置构建变体](https://developer.android.google.cn/studio/build/build-variants?hl=zh-cn#sourcesets)



## 网络安全配置

> Google表示，为保证用户数据和设备的安全，针对下一代 Android 系统(Android P) 的应用程序，将要求默认使用加密连接，这意味着 Android P 将禁止 App 使用所有未加密的连接，因此运行 Android P 系统的安卓设备无论是接收或者发送流量，未来都不能明码传输，需要使用下一代(Transport Layer Security)传输层安全协议，而 Android Nougat 和 Oreo 则不受影响。

在API28及以上时，默认关闭了明文网络传输，即http请求。

### 解决方案

- app改用https请求

- targetSdkVersion降到27及以下

- 配置网络安全文件

  在res下创建xml目录，并新建network-security-config.xml文件，内容如下

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <network-security-config>
      <base-config cleartextTrafficPermitted="true" />
  </network-security-config>
  ```

  详情参考Android官网 [网络安全配置](https://developer.android.com/training/articles/security-config#network-security-config)及[blog](https://android-developers.googleblog.com/2018/04/protecting-users-with-tls-by-default-in.html)

## IDEA控制台输出乱码问题

> 打开Help>Edit Custom VM Options
>
> 添加
>
> ```
> -Dfile.encoding=UTF-8
> ```

[参考链接](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360004976119-idea-build-console-display-Chinese-in-garbled-code)

## 配置Log

## 获取Activity

{activity

​	fragment{

​		viewPager{

​			fragment1...3 	这里的fragment怎么获取activity的viewModel？

​		}

​	}

}

代码如下：

```java
viewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(HiddenDangerViewModel.class);
```

