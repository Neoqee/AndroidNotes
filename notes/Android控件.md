# Android控件

## Text fields

> 使用方式：

```
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/textField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/label">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
    />

</com.google.android.material.textfield.TextInputLayout>
```

### 在文本字段中添加前导图标

```
<com.google.android.material.textfield.TextInputLayout
	...
	添加图标
	app:startIconDrawable="@drawable/ic_favorite_24dp"	
	描述(ps这个可以不用，只是google设计的时候希望有这个描述)
	app:startIconContentDescription="@string/content_description_end_icon"
	...
</com.google.android.material.textfield.TextInputLayout>
```

### 在文本字段中添加尾随图标

```
<com.google.android.material.textfield.TextInputLayout
    ...
    app:endIconMode="password_toggle">
    <com.google.android.material.textfield.TextInputEditText
        ...
        android:inputType="textPassword"
    />
</com.google.android.material.textfield.TextInputLayout>>
```

> 完整end icon modes：[完整IconMode](https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/textfield/res/values/attrs.xml#L149 "完整icon模式")

也可以进行自定义图标

```xml
<com.google.android.material.textfield.TextInputLayout
    ...
    app:endIconMode="custom"
    app:endIconDrawable="@drawable/ic_check_circle_24dp"
    app:endIconContentDescription="@string/content_description_end_icon">

    ...

</com.google.android.material.textfield.TextInputLayout>
```

然后在代码中实现监听，可选：

```java
textField.setEndIconOnClickListener {
  // Respond to end icon presses
    //在icon被按下时响应
}

textField.addOnEditTextAttachedListener {
  // If any specific changes should be done when the edit text is attached (and
  // thus when the trailing icon is added to it), set an
  // OnEditTextAttachedListener.
	//如果想要在编辑框被触摸的时候有任何提别的变化，设置一个监听
    
  // Example: The clear text icon's visibility behavior depends on whether the
  // EditText has input present. Therefore, an OnEditTextAttachedListener is set
  // so things like editText.getText() can be called.
    //比如：清除文本按钮是在有输入的情况下才显示的，那么可以在监听里监听是否有文本，如果有，就显示，没有就不	 //显示
}

textField.addOnEndIconChangedListener {
  // If any specific changes should be done if/when the endIconMode gets
  // changed, set an OnEndIconChangedListener.

  // Example: If the password toggle icon is set and a different EndIconMode
  // gets set, the TextInputLayout has to make sure that the edit text's
  // TransformationMethod is still PasswordTransformationMethod. Because of
  // that, an OnEndIconChangedListener is used.
    //大概是点击更换icon 比如密码的显示明文或星号
}
```



### 实现一个下拉菜单

被建议使用AutoCompleteTextView

```
<com.google.android.material.textfield.TextInputLayout
    ...
	style="@style/Widget.MaterialComponents.TextInputLayout.*.ExposedDropdownMenu">

    <AutoCompleteTextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="none"
    />

</com.google.android.material.textfield.TextInputLayout>
```

```kotlin
val items = listOf("Material", "Design", "Components", "Android")
val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
(textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
```

相当于实现一个列表

### 添加一个辅助文本到文本域

```
<com.google.android.material.textfield.TextInputLayout
    ...
    app:helperTextEnabled="true"
    app:helperText="@string/helper_text">

    ...

</com.google.android.material.textfield.TextInputLayout>
```

### 添加一个计数器到文本域

```
<com.google.android.material.textfield.TextInputLayout
    ...
    app:counterEnabled="true"
    app:counterMaxLength="20">

    ...

</com.google.android.material.textfield.TextInputLayout>
```

### 添加一个错误指示

```
<com.google.android.material.textfield.TextInputLayout
    ...
    app:errorEnabled="true">

    ...

</com.google.android.material.textfield.TextInputLayout>
```

```
// Set error text
passwordLayout.error = getString(R.string.error)
// Clear error text
passwordLayout.error = null
```

### 添加一个前缀/后缀文本

```
<com.google.android.material.textfield.TextInputLayout
    ...
    app:prefixText="@string/prefix"
    app:suffixText="@string/suffix">
    ...
</com.google.android.material.textfield.TextInputLayout>
```

### 使文本域可理解

Android的文本字段组件API同时支持标签文本和帮助文本以告知用户文本字段需要哪些内容。虽然是可选的，但是官方强烈建议使用。

#### 内容描述

当使用一个自定义按钮时，我们应该给他们设置一个文本描述，如此让屏幕阅读器比如TalkBack这些可以说明他们的目的或行动，如果有的话。

对于引导按钮，可以通过app:startIconContentDescription属性或者setStartIconContentDescription方法来设置。尾部按钮同理。

当设置的错误信息包含一些阅读器无法解析的特殊字符时，应该通过设置一个错误内容描述让他们可以描述。

#### 自定义编辑文本

如果你使用一个自定义的编辑文本作为TextInputLayout的子控件，而且你的文本字段与layout提供的支持有不同的可访问性支持，那么你可以通过setTextInputAccessibilityDelegate方法设置TextInputLayout.AccessibilityDelegate，使用此方法代替直接在EditText上提供AccessibilityDelegate。

### 填充文本样式

填充文本比边框文本有更多的视觉重点，让他们在被别的内容或控件包围时显得特别。

Note：这个是默认的样式

解析结构和属性

![Filled text field anatomy](https://material.io/components/images/content/ed861292f4ac5c2dc0b85b4bf3c161e6.png)

1. Container
2. Leading icon
3. Label
4. Input text
5. Trailing icon
6. Activation indicator
7. Helper/error/counter text
8. Prefix/suffix/placeholder

Note：所有的属性都应该设置在TextInputLayout，要输入的文本属性除外，这些应该设置在EditText中。

#### 1.包容器属性

- #### Color	

  app:boxBackgroundColor 设置该盒子的背景颜色

- #### Shape    

  app:shapeAppearance 形状，没用过，不清楚

- #### Text field enabled    

  android:enabled 应该是设置是否可用

#### 2.引导按钮属性（就是前面的按钮）

- #### Icon 

  app:startIconDrawable

  设置图片

- #### Content description

  app:startIconContentDescription

  设置内容描述

- #### Color

  app:startIconTint

  设置颜色

- #### Checkable

  app:startIconCheckable

  设置是否可选择

#### 3.标签属性

- #### Text

  android:hint

  设置提示文本

- #### Color

  android:textColorHint

  提示文本的字体颜色

- #### Collapsed （floating） color

  app:hintTextColor

  折叠颜色（悬浮颜色），即选中后，该提示会悬浮在左上角

- #### Typography

  app:hintTextAppearance

  应该是文本样式

Note:hint属性应该总是设置在layout而不是edittext，为了避免意外

#### 4.输入文本属性

- #### Input text

  android:text

  设置输入的文本

- #### Typography

  android:textAppearance

  文本样式

- #### Input text color

  android:textColor

  输入文本的字体颜色

- #### Cursor color

  光标颜色，该颜色来自主题的 ?attr/colorControlActivated

Note:这些属性应该设置在EditText中

#### 5.尾部按钮属性

- #### Mode

  app:endIconMode

  模式，比如密码显示、清除、下拉、自定义等。

- #### Color

  app:endIconTint

  颜色

- #### Custom icon

  app:endIconDrawable

  自定义的icon图标

- #### Custom icon content description

  app:endIconContentDescription

  自定义icon的内容描述

- #### Custom icon checkable

  app:endIconCheckable

  自定义icon可选择性，默认为可以，猜测是可点击

- #### Error icon

  app:errorIconDrawable

  错误提示的icon

#### 6.活动指示器属性

|                   | 属性                      | 描述               |
| ----------------- | ------------------------- | ------------------ |
| **Color**         | app:boxStrokeColor        | 边框线颜色         |
| **Error color**   | app:boxStrokeErrorColor   | 边框错误提示颜色   |
| **Width**         | app:boxStrokeWidth        | 边框线宽度         |
| **Focused width** | app:boxStrokeWidthFocused | 获取焦点时边线宽度 |

#### 7.帮助/错误/计数 文本属性

|                            | 属性                                                         | 描述                                                 |
| -------------------------- | ------------------------------------------------------------ | ---------------------------------------------------- |
| **Helper text enabled**    | app:helperTextEnabled                                        | 是否开启文本帮助器，默认为false                      |
| **Helper text**            | app:helperText                                               | 设置帮助提示文本                                     |
| **Helper text color**      | app:helperTextColor                                          | 帮助文本字体颜色                                     |
| **Helper text typography** | app:helperTextAppearance                                     | 帮助文本字体样式                                     |
| **Error text enabled**     | app:errorEnabled                                             | 是否开启错误提示，默认为false                        |
| **Error text**             |                                                              | null                                                 |
| **Error text color**       | app:errorTextColor                                           | 错误文本字体颜色                                     |
| **Error text typography**  | app:errorTextAppearance                                      | 错误文本样式                                         |
| **Counter text enabled**   | app:counterEnabled                                           | 开启计数器，默认为false                              |
| **Counter text length**    | app:counterMaxLength                                         | 最大计数长度，根据输入文本多少，会提示超出，默认为-1 |
| **Counter typography**     | app:counterTextAppearance<br />app:counterOverFlowTextAppearance | 文本样式，未超出和超出时                             |
| **Counter text color**     | app:counterTextColor<br />app:counterOverFlowTextColor       | 字体颜色，同上                                       |

#### 8.前缀/后缀属性

|                       | 属性                     | 描述                 |
| --------------------- | ------------------------ | -------------------- |
| **Prefix**            | app:prefixText           | 前缀文本，默认为null |
| **Prefix color**      | app:prefixTextColor      | 前缀文本字体颜色     |
| **Prefix typography** | app:prefixTextAppearance | 文本样式             |
| **Suffix**            | app:suffixText           | 后缀文本，默认为null |
| **Suffix color**      | app:suffixTextColor      | 后缀文本字体颜色     |
| **Suffix typography** | app:suffixTextAppearance | 文本样式             |

#### 9.Styles

|                                       | Style                                                        | 描述               |
| ------------------------------------- | :----------------------------------------------------------- | ------------------ |
| **Default style**                     | Widget.MaterialComponents.TextInputLayout.FilledBox          | 默认样式           |
| **Dense style**                       | Widget.MaterialComponents.TextInputLayout.FilledBox.Dense    | 无边界             |
| **Exposed dropdown menu style**       | Widget.MaterialComponents.TextInputLayout.FilledBox.ExposedDropdownMenu | 展开下拉框菜单样式 |
| **Dense exposed dropdown menu style** | Widget.MaterialComponents.TextInputLayout.FilledBox.Dense.ExposedDropdownMenu | 无边界下拉菜单样式 |

### 带边框文本域 Outlined text field

比默认的少了视觉重点，当他们出现在像表单数据这样的布局中时，他们减少的强度会简化布局。

基本属性和默认的一样。

#### Styles

|                                       | Style                                                        | 描述               |
| ------------------------------------- | :----------------------------------------------------------- | ------------------ |
| **Default style**                     | Widget.MaterialComponents.TextInputLayout.OutlinedBox        | 默认样式           |
| **Dense style**                       | Widget.MaterialComponents.TextInputLayout.OutlinedBox.Dense  | 无边界             |
| **Exposed dropdown menu style**       | Widget.MaterialComponents.TextInputLayout.OutlinedBox.ExposedDropdownMenu | 展开下拉框菜单样式 |
| **Dense exposed dropdown menu style** | Widget.MaterialComponents.TextInputLayout.OutlinedBox.Dense.ExposedDropdownMenu | 无边界下拉菜单样式 |



## Bottom navigation

### 使用

1. 创建一个带有5个导航目标的菜单资源（BottomNavigationView 不支持超过5个项目）
2. 放置你的BottomNavigationView在你的内容下面
3. 在BottomNavigationView的app：menu属性上设置菜单资源
4. 使用setOnNavigationItemSelectedListener（）监听选择事件。

可以和角标联动交互

## Badges（角标）

已确定可以和BottomNavigationView和TabLayout交互

### BadgeDrawable属性

|                   | 属性                                        | 描述                                                         |
| ----------------- | ------------------------------------------- | ------------------------------------------------------------ |
| **Color**         | app:backgroundColor<br />app:badgeTextColor | 背景颜色和角标字体颜色                                       |
| **Label**         | app:number                                  | 标签，假如要显示为数字，这就是设置为数字的属性               |
| **Label Length**  | app:maxCharacterCount                       | 标签长度，大概是可以显示的最大长度的字符                     |
| **Badge Gravity** | app:badgeGravity                            | 角标的位置，分为左上，右上，左下，右下：TOP_END、TOP_START、BOTTOM_START、BOTTOM_START，默认为右上 |



### 与底部导航栏的交互

```java
BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
badge.setVisible(true);
```

不适用的时候记得移除

```
bottomNavigationView.removeBadge(menuItemId);
```

### 与TabLayout交互

```xml
BadgeDrawable badge = tablayout.getTabAt(0).getOrCreateBadge();
badge.setVisible(true);
// Optionally show a number.
badge.setNumber(99);
```

移除

```java
tablayout.getTabAt(0).removeBadge();
```

### 设置icon的大小

```java
BottomNavigationMenuView menuView = (BottomNavigationMenuView) binding.bottomNavigationView.getChildAt(0);
        try {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                ImageView imageView = item.findViewById(R.id.icon);
//                imageView.getLayoutParams().width = 36;
//                imageView.getLayoutParams().height = 36;
                item.setIconSize(48);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//需要添加@SuppressLint("RestrictedApi")注释，因为setIconSize这个方法只能在同一个库里的包才能使用。
```



## TextView

### 设置最大字数并省略

​	感觉应该要按顺序设置：

​		先设置最大字数：android:maxEms="10"

​		再设置省略模式：android:ellipsize="end"

​		最后设置行数：android:singleLine="true"（已废弃，但还生效）

​									android:maxLines=1	(这个不确定生不生效)

​	多行，省略号不显示。

```xml
android:maxEms="20"
android:ellipsize="end"
android:maxLines=2
```

maxEms不生效



### 动态设置文本的颜色

java->使用html的方式

```java
String textStr = "测试啊本月已成功邀请 <font color=\"#FF0000\">" + 100 + "</font>人"; mTvTest.setText(Html.fromHtml(textStr));
//不能对Html.fromHtml(textStr)这个进行拼接，只能提前拼好字符串，使用这方法转换
```



## Toolbar

### 显示Toolbar的返回按钮

其实是左边的home键

在代码中：

```java
//这里设置toolbar关联为actionBar
	setSupportActionBar(binding.toolbar);
	ActionBar actionBar = getSupportActionBar();
	if (actionBar!=null){
   		actionBar.setDisplayHomeAsUpEnabled(true);
	}
//监听事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //这里写相应要做的逻辑
                finish();
                break;
        }
        return true;
    }
```

android:textColorSecondary：设置Toolbar中菜单项的颜色，需要在style中设置



## MaterialCardView-卡片布局

继承链：MaterialCardView < CardView < FrameLayout < ViewGroup

还实现了：Checkable, Shapeable

所以实际上是一个封装的FrameLayout

基本代码：

```xml
<com.google.android.material.card.MaterialCardView
        android:id="@+id/materialCardView2"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_margin="20dp"
        app:cardBackgroundColor="@android:color/white"
        app:cardCornerRadius="12dp"
        app:cardElevation="0dp"
        app:cardForegroundColor="@android:color/transparent"
        app:layout_constraintBottom_toTopOf="@+id/guideline"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="1.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/linearHorNews">

        <!-- 在这里写其他布局 -->
    </com.google.android.material.card.MaterialCardView>
```

属性说明：

| 属性                    | 作用                          | 使用                                                         |
| ----------------------- | ----------------------------- | ------------------------------------------------------------ |
| app:cardBackgroundColor | 设置布局的背景颜色            | app:cardBackgroundColor="@android:color/white"               |
| app:cardForegroundColor | 设置前景色                    | app:cardForegroundColor="@android:color/transparent"，使其透明 |
| app:cardCornerRadius    | 卡片四个角的圆弧角度          | app:cardCornerRadius="12dp"，表明是一个半径为12dp的1/4圆     |
| app:cardElevation       | 层级度，大概解释为在Z轴的高度 | app:cardElevation="0dp"，设置大于0的数，在z轴产生高度，可以有阴影效果 |



## DialogFragment

官方推荐使用DialogFragment来替代直接创建Dialog。

两种创建方式：

1. 重写onCreateDialog方法，在这里创建一个Dialog对象，然后配置相应属性。
2. 重写onCreateView方法，以Fragment的使用方式创建，相当于Dialog使用自定义View的方式创建布局。

```java
	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(true);//设置是否可以取消
        requireDialog().setCanceledOnTouchOutside(true);//设置点击外部取消
        Window window = requireDialog().getWindow();
        //设置窗口背景颜色为透明
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //设置窗口弹出时的动画，在直接创建Dialog的方式中，可以将其设置在一个style中传进去给dialog，在这里setStyle方法传进去的话，不是预料的效果
        window.setWindowAnimations(R.style.anim_bottom_dialog);
        //因为默认是有padding的，所以在这里要重新设置为0
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams attributes = window.getAttributes();
        //位置：底部
        attributes.gravity = Gravity.BOTTOM;
        //宽高
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        binding = DialogBottomSelectorBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
```

```xml
//正常创建dialog时，可以这样写一个样式
<style name="dialogBottomStyle" parent="@android:style/Theme.Translucent.NoTitleBar">
	<item name="android:backgroundDimEnabled">true</item>
	<item name="android:backgroundDimAmount">0</item>
<!--        <item name="android:windowAnimationStyle">@style/anim_bottom_dialog</item>-->
</style>
//设置动画
<style name="anim_bottom_dialog">
	<item name="android:windowEnterAnimation">@anim/dialog_bottom_enter</item>
	<item name="android:windowExitAnimation">@anim/dialog_bottom_exit</item>
</style>
//进入动画
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="400"
        android:fromYDelta="100%"
        android:toYDelta="0"
        />
    <alpha
        android:duration="400"
        android:fromAlpha="0"
        android:toAlpha="1.0"
        />
</set>
//退出动画
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="400"
        android:fromYDelta="0"
        android:toYDelta="100%"
        />
    <alpha
        android:duration="400"
        android:fromAlpha="1"
        android:toAlpha="0.3"
        />
</set>
```

对动画的属性的大概解读：坐标以左上角为原点，向左为x轴，向下为y轴，所以进入时，因为是从底部滑出来的，fromYDelta="100%"即表示y轴最底下，android:toYDelta="0"即到最顶点。按照语义解读大概就是从y轴的fromYDelta到toYDelta。



## RecyclerView

执行ListAdapter.submitList(list)该方法时，会判断提交的列表是否是当前提交的列表

```java
ListAdapter
	public void submitList(@Nullable List<T> list) {
        mDiffer.submitList(list);
    }
AsyncListDiffer
    @Nullable
    private List<T> mList;
	public void submitList(@Nullable final List<T> newList,
            @Nullable final Runnable commitCallback) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        final int runGeneration = ++mMaxScheduledGeneration;
		//判断新提交的是否是上一次提交的List对象，如果是，直接返回，不做改变
        if (newList == mList) {
            // nothing to do (Note - still had to inc generation, since may have ongoing work)
            if (commitCallback != null) {
                commitCallback.run();
            }
            return;
        }
        final List<T> previousList = mReadOnlyList;

        // fast simple remove all
        if (newList == null) {
            //noinspection ConstantConditions
            int countRemoved = mList.size();
            mList = null;
            mReadOnlyList = Collections.emptyList();
            // notify last, after list is updated
            mUpdateCallback.onRemoved(0, countRemoved);
            onCurrentListChanged(previousList, commitCallback);
            return;
        }

        //当mList为null时，说明是第一次进来，直接将新提交的赋值给它
        // fast simple first insert
        if (mList == null) {
            mList = newList;
            mReadOnlyList = Collections.unmodifiableList(newList);
            // notify last, after list is updated
            mUpdateCallback.onInserted(0, newList.size());
            onCurrentListChanged(previousList, commitCallback);
            return;
        }
        
        //当两个列表既不相等，也都不为null时，进行两个列表的比较，从这里可以看出mList就是旧的列表
    	final List<T> oldList = mList;
        mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return oldList.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return newList.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        T oldItem = oldList.get(oldItemPosition);
                        T newItem = newList.get(newItemPosition);
                        if (oldItem != null && newItem != null) {
                            //这个callback就是每次创建adapter时需要传入的DiffUtil.ItemCallback
                            return mConfig.getDiffCallback().areItemsTheSame(oldItem, newItem);
                        }
                        //都为null的时候，认为它们是相同的
                        // If both items are null we consider them the same.
                        return oldItem == null && newItem == null;
                    }

                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        T oldItem = oldList.get(oldItemPosition);
                        T newItem = newList.get(newItemPosition);
                        if (oldItem != null && newItem != null) {
                            return mConfig.getDiffCallback().areContentsTheSame(oldItem, newItem);
                        }
                        if (oldItem == null && newItem == null) {
                            return true;
                        }
                        // There is an implementation bug if we reach this point. Per the docs, this
                        // method should only be invoked when areItemsTheSame returns true. That
                        // only occurs when both items are non-null or both are null and both of
                        // those cases are handled above.
                        throw new AssertionError();
                    }

                    @Nullable
                    @Override
                    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                        T oldItem = oldList.get(oldItemPosition);
                        T newItem = newList.get(newItemPosition);
                        if (oldItem != null && newItem != null) {
                            return mConfig.getDiffCallback().getChangePayload(oldItem, newItem);
                        }
                        // There is an implementation bug if we reach this point. Per the docs, this
                        // method should only be invoked when areItemsTheSame returns true AND
                        // areContentsTheSame returns false. That only occurs when both items are
                        // non-null which is the only case handled above.
                        throw new AssertionError();
                    }
                });

                mMainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mMaxScheduledGeneration == runGeneration) {
                            latchList(newList, result, commitCallback);
                        }
                    }
                });
            }
        });
	}
```

### ItemDecoration

设置间距：通过重写getItemOffsets方法，通过parent.getChildAdapterPosition(view)获得当前view的位置，然后通过rectOut设置间距

```java
@Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 || position == 3 || position == 6){
            outRect.top = specTop;
        }
    }
```

重写其他两个方法可以另外画其他view到视图上



## WebView

基本配置使用