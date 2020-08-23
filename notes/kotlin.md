# Kotlin内联函数

## 1、let

定义：一个作用域函数

作用：定义一个变量在一个特定的作用域范围内，避免写一些判断null的操作

应用场景：明确一个变量所处特定的作用域范围内可用，针对一个可null的对象统一做判空处理

```kotlin
// 注：返回值 = 最后一行 / return的表达式
object.let{
    //定义一个作用域
    it.todo()
}
object?.let{
    //表示object不为null的条件下，才会去执行let函数体
}
// 使用Java
if( mVar != null ){
    mVar.function1();
    mVar.function2();
    mVar.function3();
}
// 使用kotlin（无使用let函数）
mVar?.function1()
mVar?.function2()
mVar?.function3()
// 使用kotlin（使用let函数）
// 方便了统一判空的处理 & 确定了mVar变量的作用域
mVar?.let {
       it.function1()
       it.function2()
       it.function3()
}
```



## 2、also

与let函数类似，区别在于返回值为传入的对象本身

```kotlin
// let函数
var result = mVar.let {
               it.function1()
               it.function2()
               it.function3()
               999
}
// 最终结果 = 返回999给变量result

// also函数
var result = mVar.also {
               it.function1()
               it.function2()
               it.function3()
               999//这里不返回999
    			//而是返回it，即mVar本身
}
// 最终结果 = 返回一个mVar对象给变量result
```



## 3、with

调用同一个对象的多个方法 / 属性时，可以省去对象名重复，直接调用方法名 / 属性即可

```kotlin
// 返回值 = 函数块的最后一行 / return表达式
// 此处要调用people的name 和 age属性
// kotlin
val people = People("carson", 25)
with(people) {
println("my name is $name, I am $age years old")
}

// Java
User peole = new People("carson", 25);
String var1 = "my name is " + peole.name + ", I am " + peole.age + " years old";
System.out.println(var1);
```



## 4、run

结合了let、with两个函数的作用，即：

1. 调用同一个对象的多个方法 / 属性时，可以省去对象名重复，直接调用方法名 / 属性即可
2. 定义一个变量在特定作用域内
3. 统一做判空处理

```kotlin
// 返回值 = 函数块的最后一行 / return表达式
// 此处要调用people的name 和 age属性，且要判空
// kotlin
val people = People("carson", 25)
people?.run{
    println("my name is $name, I am $age years old")
}

// Java
User peole = new People("carson", 25);
String var1 = "my name is " + peole.name + ", I am " + peole.age + " years old";
System.out.println(var1);
```



## 5、apply

与run函数类似，但区别在于返回值：

- run函数返回最后一行的值 / 表达式
- apply函数返回传入的对象的本身

应用场景：对象实例初始化时需要对对象中的属性进行赋值 & 返回该对象

```kotlin
// run函数
val people = People("carson", 25)//name,age
val result = people?.apply{
    println("my name is $name, I am $age years old")
    age = 999
}
// 最终结果 = 返回people给变量result,且age修改为999
```

## 总结

所有函数都创建了一个关于自身的作用域，在这个作用域内使用it或者省略自身达成优化

let和also相似，返回值不一样，also返回自身，let返回最后一行或者return语句

run和apply相似，apply返回自身，run返回最后一行或者return语句



# 获取类对象

```kotlin
fun main() {
    val loginData = BaseResponse("0","成功", null)
    printObjectClass(loginData)
}

fun <T> printObjectClass(t: T){
    val className = (t as Any).javaClass.simpleName
    println(className)
}

data class BaseResponse<T>(
    var code: String,
    var msg: String,
    var data: T?
)
//输出：BaseResponse
```

对于没有明确表示继承Any的类，无法直接使用javaClass字段，需要先转为Any再使用



# 协程



# kts

在buildSrc目录下创建的build.gradle.kts需要添加一个仓库路径供下载

```groovy
plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
}
//这是由于Kotlin DSL 1.0的突破性变化：
//kotlin-dsl插件现在需要声明一个存储库
//使用Kotlin 1.2.60，驱动kotlin编译器的Kotlin Gradle插件需要额外的依赖关系，而Gradle Kotlin DSL脚本本身并不需要这些依赖关系，并且不会嵌入到Gradle中。
//这可以通过在应用kotlin-dsl插件的项目中添加包含Kotlin编译器依赖项的存储库来修复： repositories { jcenter() }
```

参考链接：[https://stackoom.com/question/3WRpL/%E7%94%B1%E4%BA%8E%E7%BC%BA%E5%B0%91%E4%BE%9D%E8%B5%96%E6%80%A7-Kotlin-buildSrc%E5%9C%A8Gradle-%E4%B8%8A%E5%A4%B1%E8%B4%A5](https://stackoom.com/question/3WRpL/由于缺少依赖性-Kotlin-buildSrc在Gradle-上失败)

从groovy迁移到kts：

参考链接：[https://edenxio.github.io/2019/02/01/Android%20%E6%9E%84%E5%BB%BA%E8%84%9A%E6%9C%AC%E4%BB%8EGroovy%E8%BF%81%E7%A7%BB%E5%88%B0Kotlin%20DSL/](https://edenxio.github.io/2019/02/01/Android 构建脚本从Groovy迁移到Kotlin DSL/)