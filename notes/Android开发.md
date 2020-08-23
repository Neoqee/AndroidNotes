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

//在manifest文件中的application属性中添加：
      <?xml version="1.0" encoding="utf-8"?>
      <manifest ... >
          <application android:networkSecurityConfig="@xml/network_security_config"
                          ... >
              ...
          </application>
      </manifest>

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

## 在viewPager里获取Activity的viewModel

{activity

​	fragment{

​		viewPager{

​			fragment1...3 	这里的fragment怎么获取activity的viewModel？

​		}

​	}

}

代码如下：

​```java
viewModel = new ViewModelProvider(requireParentFragment().requireActivity()).get(HiddenDangerViewModel.class);
  ```



## 打包时曾遇到的问题

​	Could not resolve all task dependencies for configuration ':app:lintClassPath'.

 	Could not resolve com.android.tools.lint:lint-gradle:26.6.1. 

​	Required by: project :app No cached version of com.android.tools.lint:lint-gradle:26.6.1 available for 		offline mode. 

No cached version of com.android.tools.lint:lint-gradle:26.6.1 available for offline mode.

解决方案：https://stackoverflow.com/questions/60611785/could-not-determine-the-dependencies-of-task-applintvitalrelease-error-on-re

在module中加入：

```groovy
lintOptions {

    checkReleaseBuilds false
    abortOnError false

}
```



## Gradle配置

### 给debug和release配置不同的applicationId

```groovy
buildTypes {
	...
	debug{
		applicationIdSuffix ".debug"	//给debug版本的applicationId添加后缀
	}
}
```

### 设置manifestPlaceholders

```groovy
//再配置不同渠道或者不同版本的块里设置
debug{
	...
	manifestPlaceholders = [ key1:value1,key2:value2... ]	//key-value格式，以逗号分隔
}
productFlavors {
    demo{
        ...
        manifestPlaceholders = [ key1:value1,key2:value2... ]	//key-value格式，以逗号分隔
    }
}
```

在AndroidManifest.xml文件里使用时：

```xml
//使用的地方 属性名=${key}
android:label="${APP_NAME}"		//APP_NAME就是在manifestPlaceholders里设置的key
```



## 打开Android官网时，最好后面添加hl=en指向美国

Android官网的文档想要看最新版的得是英文的，或者添加hl=en

## 常用依赖：

[最新版本库：https://developer.android.com/jetpack/androidx/versions?hl=en](https://developer.android.com/jetpack/androidx/versions?hl=en)

```groovy

```



## I/O流

### 读取文件：

参考文章：https://juejin.im/post/5ce260465188252f5e019e1e

```java
public class ReadFromFile {
  /**
   * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
   */
  public static void readFileByBytes(String fileName) {
    File file = new File(fileName);
    InputStream in = null;
    try {
      System.out.println("以字节为单位读取文件内容，一次读一个字节：");
      // 一次读一个字节
      in = new FileInputStream(file);
      int tempbyte;
      while ((tempbyte = in.read()) != -1) {
        System.out.write(tempbyte);
      }
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    try {
      System.out.println("以字节为单位读取文件内容，一次读多个字节：");
      // 一次读多个字节
      byte[] tempbytes = new byte[100];	//临时数组，表示一次读取的字节大小，相当于存储读取的字节
      int byteread = 0;	//读取的字节长度
      in = new FileInputStream(fileName);
      ReadFromFile.showAvailableBytes(in);
      // 读入多个字节到字节数组中，byteread为一次读入的字节数
      while ((byteread = in.read(tempbytes)) != -1) {
        String s = new String(tempbytes,0,byteread);
        System.out.write(tempbytes, 0, byteread);
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e1) {
        }
      }
    }
  }
 
  /**
   * 以字符为单位读取文件，常用于读文本，数字等类型的文件
   */
  public static void readFileByChars(String fileName) {
    File file = new File(fileName);
    Reader reader = null;
    try {
      System.out.println("以字符为单位读取文件内容，一次读一个字节：");
      // 一次读一个字符
      reader = new InputStreamReader(new FileInputStream(file));
      int tempchar;
      while ((tempchar = reader.read()) != -1) {
        // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
        // 但如果这两个字符分开显示时，会换两次行。
        // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
        if (((char) tempchar) != '\r') {
          System.out.print((char) tempchar);
        }
      }
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      System.out.println("以字符为单位读取文件内容，一次读多个字节：");
      // 一次读多个字符
      char[] tempchars = new char[30];
      int charread = 0;
      reader = new InputStreamReader(new FileInputStream(fileName));
      // 读入多个字符到字符数组中，charread为一次读取字符数
      while ((charread = reader.read(tempchars)) != -1) {
        // 同样屏蔽掉\r不显示
        if ((charread == tempchars.length)
            && (tempchars[tempchars.length - 1] != '\r')) {
          System.out.print(tempchars);
        } else {
          for (int i = 0; i < charread; i++) {
            if (tempchars[i] == '\r') {
              continue;
            } else {
              System.out.print(tempchars[i]);
            }
          }
        }
      }
 
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }
 
  /**
   * 以行为单位读取文件，常用于读面向行的格式化文件
   */
  public static void readFileByLines(String fileName) {
    File file = new File(fileName);
    BufferedReader reader = null;
    try {
      System.out.println("以行为单位读取文件内容，一次读一整行：");
      reader = new BufferedReader(new FileReader(file));
      String tempString = null;
      int line = 1;
      // 一次读入一行，直到读入null为文件结束
      while ((tempString = reader.readLine()) != null) {
        // 显示行号
        System.out.println("line " + line + ": " + tempString);
        line++;
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e1) {
        }
      }
    }
  }
 
  /**
   * 随机读取文件内容
   */
  public static void readFileByRandomAccess(String fileName) {
    RandomAccessFile randomFile = null;
    try {
      System.out.println("随机读取一段文件内容：");
      // 打开一个随机访问文件流，按只读方式
      randomFile = new RandomAccessFile(fileName, "r");
      // 文件长度，字节数
      long fileLength = randomFile.length();
      // 读文件的起始位置
      int beginIndex = (fileLength > 4) ? 4 : 0;
      // 将读文件的开始位置移到beginIndex位置。
      randomFile.seek(beginIndex);
      byte[] bytes = new byte[10];
      int byteread = 0;
      // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
      // 将一次读取的字节数赋给byteread
      while ((byteread = randomFile.read(bytes)) != -1) {
        System.out.write(bytes, 0, byteread);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (randomFile != null) {
        try {
          randomFile.close();
        } catch (IOException e1) {
        }
      }
    }
  }
 
  /**
   * 显示输入流中还剩的字节数
   */
  private static void showAvailableBytes(InputStream in) {
    try {
      System.out.println("当前字节输入流中的字节数为:" + in.available());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
 
  public static void main(String[] args) {
    String fileName = "C:/temp/newTemp.txt";
    ReadFromFile.readFileByBytes(fileName);
    ReadFromFile.readFileByChars(fileName);
    ReadFromFile.readFileByLines(fileName);
    ReadFromFile.readFileByRandomAccess(fileName);
  }
}
```



### 写文件：

参考文章：https://juejin.im/post/5c997a29f265da611846b83d#heading-0

BufferedWriter 提供高效的读写字符，字符串，数组。

PrintWriter 写入格式化文字

FileOutputStream 写入二进制流

DataOutputStream 写primary类型

RandomAccessFile 随机读写文件，在指定的位置编辑文件

FileChannel 写入大文件

```java
BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
FileOutputStream outputStream = new FileOutputStream(fileName);
        // 需要将String转换为bytes
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
```



### Android提供的方法：

```java
fileOutputStream = context.openFileOutput(logFile.getName(), Context.MODE_APPEND);
fileInputStream = context.openFileInput(filename);
```



## 文档注释换行

在要换行的地方添加<br/>

原理：因为javaDoc其实就是一个html页面

```java
/**
 * 我想这行之后换行,所以在后面加“<br/>”<br/>
 * 这样在查看的时候就换行了
 */
```

## 软键盘操作

```java
//显示
InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	if (null != imm){
		imm.showSoftInput(binding.searchEdit,0);
    }
    
//隐藏
imm.hideSoftInputFromWindow(currentFocus.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
```

### 处理EditText焦点问题

点击外部时，隐藏软键盘并清除焦点

```java
//需要重写该方法，处理用户的触摸事件	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            View currentFocus = getCurrentFocus();
            if (isClearEditTextFocus(currentFocus,ev)){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm){
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    binding.searchEdit.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);//继续传递事件
        }
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);//继续传递事件，防止其他view获取不到
    }

//是否清除EditText焦点，比较当前焦点的view是不是EditText的类型，如果是，则比较当前触摸事件是否在外部，如果是则返回true，否则返回false 
    private boolean isClearEditTextFocus(View view,MotionEvent event){
        if ((view instanceof TextInputEditText)){
            int[] leftTop = {0,0};
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getY() > top && event.getY() < bottom && event.getX() > left && event.getX() < right);
        }
        return false;
    }
```



## AndroidManifext.xml collided问题

Execution failed for task ':app:packageDebug'.

> A failure occurred while executing com.android.build.gradle.internal.tasks.Workers$ActionFacade
> Entry name 'AndroidManifest.xml' collided

在gradle.properties中添加：

```
android.useNewApkCreator=false
```



## 修改SearvhView的光标

使用反射的时候，在targetSDK >28时，会报错

调用非SDK接口的报错：

Accessing hidden field Landroid/widget/TextView;->mCursorDrawableRes:I (greylist-max-p, reflection, denied)

java.lang.NoSuchFieldException: No field mCursorDrawableRes in class Landroid/widget/TextView; (declaration of 'android.widget.TextView' appears in /system/framework/framework.jar!classes3.dex)

```java
SearchView.SearchAutoComplete searchTextView = searchView.findViewById(R.id.search_src_text);
try {
	Field[] declaredFields = TextView.class.getDeclaredFields();
	LogUtil.i(Arrays.toString(declaredFields));
	Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
	mCursorDrawableRes.setAccessible(true);
	mCursorDrawableRes.set(searchTextView,R.drawable.cursor_searchview);
} catch (NoSuchFieldException | IllegalAccessException e) {
	e.printStackTrace();
}
```

查找资料，大概该属性在AutoCompleteTextView上，所以在xml中使用样式来进行修改，定义一个样式，继承自AutoCompleteTextView，然后可以主题中使其生效

```xml
<style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
	...
	<item name="autoCompleteTextViewStyle">@style/searchView</item>
</style>
<style name="searchView" parent="Widget.AppCompat.AutoCompleteTextView">
	<item name="android:textCursorDrawable">@drawable/cursor_searchview</item>
</style>
```



## 适配通过SAF获取文件上传

uri为content开头的格式时，只能操作文件，无法获取文件对象，所以将获取到的文件重新保存再当前App的私有目录下，然后再拿到文件路劲。

```java
	private String getPathByUri(Context context,Uri uri){
        String path = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                File file = new File(context.getExternalCacheDir(),displayName);
                if (file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteRead;
                while (-1 != (byteRead = inputStream.read(buffer))){
                    fileOutputStream.write(buffer,0,byteRead);
                }
                inputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
                path = file.getAbsolutePath();
                LogUtils.i("path = " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return path;
    }
```



## 通过SAF保存图片到相册

```java
    private void save2Album(String path){
        File file = new File(path);
        String name = file.getName();
        Log.i(TAG,"name="+name);
//        Uri fileUri = FileProvider.getUriForFile(this,
//                BuildConfig.APPLICATION_ID + ".FileProvider",
//                file);
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        Log.i(TAG,"extension="+extension);
        if (extension != null){
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Log.i(TAG,"type="+type);
            if (type != null){
                Log.i(TAG,"开始保存");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
                values.put(MediaStore.Images.Media.MIME_TYPE, type);
                values.put(MediaStore.Images.Media.TITLE, name);
//                values.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DCIM+"/Camera");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Camera");//保存路径
                    values.put(MediaStore.MediaColumns.IS_PENDING, true);
                }
//                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                Uri external = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                Log.i(TAG,"external="+external);
                Log.i(TAG,"getScheme="+external.getScheme());
                Log.i(TAG,"getAuthority="+external.getAuthority());
                ContentResolver resolver = getContentResolver();

                Uri insertUri = resolver.insert(external, values);
                Log.i(TAG,"insertUri="+insertUri);

                OutputStream os = null;
                try {
                    if (insertUri != null) {
                        os = resolver.openOutputStream(insertUri);
                    }
                    if (os != null) {
//                        final Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
                        final Bitmap bitmap = BitmapFactory.decodeFile(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, os);
                        Log.i(TAG,"保存成功");
                        binding.image.setImageBitmap(bitmap);
                        // write what you want
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            values.clear();
                            values.put(MediaStore.MediaColumns.IS_PENDING,false);
                            resolver.update(insertUri,values,null,null);
                        }
                    }else {
                        Log.i(TAG,"os==null");
                    }
                } catch (IOException e) {
                    Log.i("PickPhotoActivity","fail: " + e.getCause());
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (IOException e) {
                        Log.i("PickPhotoActivity","fail in close: " + e.getCause());
                    }
                }

            }
        }
    }
```



## px，sp，dp转换

Android在代码中设置一些大小的时候，需要设置相应的单位，比如设置字体大小时，需要转成sp，设置长度、间距等需要换成dp，而通过一些api获取这些值的时候通常返回的时候是px，或者接收的参数是px，就需要将想要的值换成相应的值

在自定义View时：

```java
TypedArray typedArray = context.obtainStyledAttributes(attrs,styleableRes);
float px = typedArray.getDimension(styleable,defValue);//这个api返回的是px的值
textView.setTextSize(size);
//setTextSize(int unit, float size)，unit为单位，size为要设置的大小，这个单位默认为sp，这个方法里面执行的是TypedValue.applyDimension(unit,value,metrics),将这个值转换为对应的单位，比如这里会将这个值转换为sp
//大体流程是，通过getDimension将sp值转换为px，这时候需要将px重新转换为sp，然后传进去，里面再进行sp转px，如果不提前转为sp，就是一个px单位的sp值再进行px的转换
	public static int dp2px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale + 0.5f);
    }

    public static int sp2Px(Context context, float sp){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(sp * scale + 0.5f);
    }

    public static int px2sp(Context context, float px){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(px / scale + 0.5f);
    }
```



## 动态设置margin

这个属性在ViewGroup里面，所以需要根据view所在的viewGroup里，创建对应的LayoutParams，然后再设置进相应的view



# Retrofit2

## baseUrl设置

```java
/**
     * Set the API base URL.
     * <p>
     * The specified endpoint values (such as with {@link GET @GET}) are resolved against this
     * value using {@link HttpUrl#resolve(String)}. The behavior of this matches that of an
     * {@code <a href="">} link on a website resolving on the current URL.
     * <p>
     * <b>Base URLs should always end in {@code /}.</b>
     * <p>
     * A trailing {@code /} ensures that endpoints values which are relative paths will correctly
     * append themselves to a base which has path components.
     * <p>
     * <b>Correct:</b><br>
     * Base URL: http://example.com/api/<br>
     * Endpoint: foo/bar/<br>
     * Result: http://example.com/api/foo/bar/
     * <p>
     * <b>Incorrect:</b><br>
     * Base URL: http://example.com/api<br>
     * Endpoint: foo/bar/<br>
     * Result: http://example.com/foo/bar/
     * <p>
     * This method enforces that {@code baseUrl} has a trailing {@code /}.
     * <p>
     * <b>Endpoint values which contain a leading {@code /} are absolute.</b>
     * <p>
     * Absolute values retain only the host from {@code baseUrl} and ignore any specified path
     * components.
     * <p>
     * Base URL: http://example.com/api/<br>
     * Endpoint: /foo/bar/<br>
     * Result: http://example.com/foo/bar/
     * <p>
     * Base URL: http://example.com/<br>
     * Endpoint: /foo/bar/<br>
     * Result: http://example.com/foo/bar/
     * <p>
     * <b>Endpoint values may be a full URL.</b>
     * <p>
     * Values which have a host replace the host of {@code baseUrl} and values also with a scheme
     * replace the scheme of {@code baseUrl}.
     * <p>
     * Base URL: http://example.com/<br>
     * Endpoint: https://github.com/square/retrofit/<br>
     * Result: https://github.com/square/retrofit/
     * <p>
     * Base URL: http://example.com<br>
     * Endpoint: //github.com/square/retrofit/<br>
     * Result: http://github.com/square/retrofit/ (note the scheme stays 'http')
     */
```

大概意思是，如果只包含域名的情况下，可以不以**'/'**结尾

在包含path的情况下，比如http://example.com/这后面还有path参数，如http://example.com/api/，必须以**’/‘**结尾。

如果在接口设置请求路径时以**’/‘**开头时，比如/foo/bar/，会直接拼接在域名后http://example.com/foo/bar/

如果在baseUrl中已经有设置path时，设置的path会被请求中的替换

所以想要让baseUrl中设置的path生效，需要以’/‘结尾，并且在请求中不以’/‘开头。

```java
 * Base URL: http://example.com/api/<br>
 * Endpoint: foo/bar/<br>
 * Result: http://example.com/api/foo/bar/
```

**结论**：baseUrl尽量以'/'结尾，然后请求不以’/‘开头



## Multipart

这个标签表示使用**multipart/form-data**格式传输数据

一般用来实现文件上传

当用来文件上传时，需要和**@Part MultipartBody.Part file**属性搭配使用

如果是其他参数，可以使用**@PartMap Map<String,RequestBody> partMap**

代码：

```java
接口中：
	@POST("请求路径")
    @Multipart
    Call<ResponseBody> uploadLog(@Part MultipartBody.Part file, @PartMap Map<String,RequestBody> partMap);

参数设置：
    File file = new File("test.txt");//获取一个文件对象
	RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
	//这是上传文件时必须对文件设置的					这里对应大概就是 key-value：file-file对象
    MultipartBody.Part body =					//参数名	文件名				请求体
                MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    Map<String,Object> params = new HashMap<>();//参数什么自由拼接
    params.put(key1，value1);
    params.put(key2，value2);
    params.put(key3，value3);
    Map<String,RequestBody> partMap = new HashMap<>();
    for (String s : params.keySet()) {
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), params.get(s) + "");//解析成"multipart/form-data"格式的请求体
        partMap.put(s,requestBody1);
    }
```



## multipart/form-data 是什么

http协议将请求分为3个部分：状态行，请求头，请求体。 而RESTFul风格请求更multipart又有些不同，具体的：

1. `multipart/form-data`的基础方法是post，也就是说是由post方法来组合实现的。
2. `multipart/form-data`与post方法的不同之处：请求头，请求体。
3. `multipart/form-data`的请求头必须包含一个特殊的头信息：Content-Type，且其值也必须规定为multipart/form-data，同时还需要规定一个内容分割符用于分割请求体中的多个post的内容，如文件内容和文本内容自然需要分割开来，不然接收方就无法正常解析和还原这个文件。

```
Content-Type: multipart/form-data; boundary=${bound}
```

其中${bound}是定义的分隔符，用于分割各项内容(文件,key-value对)。post格式如下：

```
--${bound}
Content-Disposition: form-data; name="Filename"
 
HTTP.pdf
--${bound}
Content-Disposition: form-data; name="file000"; filename="HTTP协议详解.pdf"
Content-Type: application/octet-stream
 
%PDF-1.5
file content
%%EOF
 
--${bound}
Content-Disposition: form-data; name="Upload"
 
Submit Query
--${bound}--
```

${bound}是Content-Type里boundary的值



# 图片旋转问题

图片在拍完照之后又exif信息保存在文件中，通过该接口查看信息。

```java
ExifInterface exifInterface = new ExifInterface(filePath);
            int attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            LogUtils.d("photo degree:" + attributeInt);
            Matrix matrix = new Matrix();
            matrix.reset();
            if (attributeInt == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            }
            if (attributeInt == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            }
            if (attributeInt == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
```



# 相机拍照不显示图片

le x620	6.0

/storage/emulated/0/DCIM/Camera/IMG_20200520_13300455.jpg

等待解决

# 判断文件类型

可以通过文件头部来判断文件的真实类型



# 在线预览文件参考方案

搜索关键字：Android 使用第三方预览文件

（1）文件下载到本地，使用第三方应用打开，退出后删除该文件。

（2）使用TBS（腾讯浏览服务）打开。

（3）使用微软提供的预览效果，使用webview打开

（4）使用百度云文档服务

https://www.jianshu.com/p/940ee2b4ac93

Android实现附件预览：https://blog.csdn.net/qq_19917239/article/details/76447616



# 蓝牙

参考：https://github.com/android/connectivity-samples

https://juejin.im/post/6844904024525766669

**注意：您仅能扫描蓝牙 LE 设备*或*传统蓝牙设备，正如[蓝牙概览](https://developer.android.google.cn/guide/topics/connectivity/bluetooth)中所述。您无法同时扫描蓝牙 LE 设备和传统蓝牙设备。**

## Ble

### 1、权限

```xml
	<!--蓝牙必须的权限-->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <!--位置权限，6.0以上时，使用蓝牙需要位置信息-->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<!--如果想声明只适用于ble，required=true，否则required=false-->
	<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
```

所以在Android6.0+时，需要先申请权限

```java
//required=false时，可以检查是否支持ble
if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    Toast.makeText(this, "不支持ble", Toast.LENGTH_SHORT).show();
    finish();
}
//权限申请
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0x11);
            }
        }
```

### 2、设置蓝牙

```java
//1、获取BluetoothAdapter
private BluetoothAdapter bluetoothAdapter;
...
// 初始化Bluetooth adapter.
final BluetoothManager bluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
bluetoothAdapter = bluetoothManager.getAdapter();
//2、启用蓝牙
// 确保设备存在蓝牙且已经打开，否则显示一个弹窗请求打开权限
if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
}
```

### 3、查找BLE设备

```java
/**
 * Activity for scanning and displaying available BLE devices.
 */
public class DeviceScanActivity extends ListActivity {

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;

    // 延时停止扫描：10s，因为扫描是一个很耗时的操作，所以需要提供一个时间给予扫描，然后关闭
    private static final long SCAN_PERIOD = 10000;
    ...
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // 10s后停止扫描
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            //startLeScan(UUID[], BluetoothAdapter.LeScanCallback)，可以用来查找特定类型的设备
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
        ...
    }
...
}
```



## 经典蓝牙

使用Handler来传递消息

```java
    /**
     * The Handler that gets information back from the BluetoothService
     * 从BluetoothService获取回调信息
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 消息的处理跟自己定义的消息传递有关
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
//                            setStatus("已连接：" + mConnectedDeviceName);
//                            requireDialog().dismiss();
                            break;
                        case BluetoothService.STATE_CONNECTING:
//                            setStatus("连接中");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
//                            setStatus("未连接");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    LogUtil.i("writeMsg:" + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    LogUtil.i(mConnectedDeviceName + ":" + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // 保存已连接的设备名
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    ToastUtil.show("Connected to " + mConnectedDeviceName);
                    break;
                case Constants.MESSAGE_TOAST:
                    ToastUtil.show(msg.getData().getString(Constants.TOAST));
                    break;
            }
        }
    };
```





## 蓝牙问题记录

- getBluetoothService() called with no BluetoothManagerCallback

