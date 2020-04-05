# 编译插桩技术学习

根据拉勾课程[Android 工程师进阶34讲](https://kaiwu.lagou.com/course/courseInfo.htm?courseId=67#/detail/pc?id=1858)第04讲：编译插桩操纵字节码，实现不可能完成的任务的课程学习

## Demo具体实现

### 创建ASMLifeCycleDemo

### 创建自定义gradle插件

1. 在demo项目中创建一个新的module，选择Android library类型，module名asm_lifecycle_plugin（随个人取）

2. 在plugin module中删除除build.gradle和main文件夹之外的所有内容，在main下创建groovy和java目录

3. 修改build.gradle的内容，全部替换为一下内容：

   ```groovy
   apply plugin: 'groovy'
   apply plugin: 'maven'
   
   dependencies {
       implementation fileTree(dir: 'libs', includes: ['*.jar'])
   
       compile gradleApi()
       compile localGroovy()
   
       compile 'com.android.tools.build:gradle:3.4.2'
       //asm相关依赖
       implementation 'org.ow2.asm:asm:7.1'
       implementation 'org.ow2.asm:asm-commons:7.1'
   }
   
   group='com.neoqee.plugin'
   version='1.0.0'
   
   uploadArchives {
       repositories {
           mavenDeployer {
               //本地的Maven地址设置
               repository(url: uri('../asm_lifecycle_repo'))
           }
       }
   }
   ```

   group和version在app module引用此插件时需要使用。插件需要部署到maven库中，具体部署到哪里根据repository属性配置。

4. 在groovy目录下创建com.neoqee.plugin目录（看个人取），并在该目录下创建LifeCyclePlugin.groovy文件。实现Plugin接口，重写apply方法，实现插件逻辑，这里打印一条日志。内容如下：

   ```groovy
   package com.neoqee.plugin
   
   import org.gradle.api.Plugin
   import org.gradle.api.Project
   
   public class LifeCyclePlugin implements Plugin<Project> {
       void apply(Project project){
           System.out.println("--LifeCyclePlugin gradle plugin--")
       }
   }
   ```

   当在app module build.gradle中使用此插件时，apply方法会被自动调用。

5. 创建properties文件：在plugin/src/main目录下创建resources/META-INF/gradle-plugins目录，在此目录下新建一个文件：neoqee.asm.lifecycle.properties（格式：插件名称.properties）。在该文件中指定我们自定义的插件类名LifeCyclePlugin，内容如下：

   ```properties
   implementation-class=com.neoqee.plugin.LifeCyclePlugin
   ```

6. 至此，自定义gradle插件基本完成，在右侧点击uploadArchives执行plugin的部署任务。构建成功后，会在Project的根目录下将会出现一个repo目录，里面就是插件目标文件。

   ![img](https://s0.lgstatic.com/i/image3/M01/81/65/Cgq2xl6FrD-AMDFKAAcAXIFLKA8851.png)

### 测试插件

测试插件是否可用，在app module中build.gradle中引用此插件

```groovy
apply plugin: 'com.android.application'

apply plugin: 'neoqee.asm.lifecycle'	//自定义gradle插件中properties的文件名（neoqee.asm.lifecycle）
buildscript{
    repositories{
        google()
        jcenter()
        //自定义插件maven地址
        maven { url '../asm_lifecycle_repo' }
    }
    dependencies{
        //加载自定义插件 group + module + version
        classpath 'com.neoqee.plugin:asm_lifecycle_plugin:1.0.0'	//这里就是添加插件的依赖：使用到group module version
    }
}

android {...}
```

之后在命令行中执行gradlew构建命令，查看是或打印在插件中定义的log

![img](https://s0.lgstatic.com/i/image3/M01/81/65/Cgq2xl6FrD-Af1TcAADOeFj_5qk929.png)

### 自定义Transform，实现遍历.class文件

在plugin目录下新建LifeCycleTransform.groovy，并继承Transform类，内容如下：

```groovy
package com.neoqee.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.neoqee.asm.LifecycleClassVisitor
import groovy.io.FileType
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter


public class LifeCycleTransform extends Transform {
    @Override
    public String getName() {
        return "LifeCycleTransform"
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    @Override
    public boolean isIncremental() {
        return false
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //拿到所有的class文件
        Collection<TransformInput> transformInputs = transformInvocation.inputs

        transformInputs.each { TransformInput transformInput ->
            // directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            // 比如我们手写的类以及R.class、BuildConfig.class以及MainActivity.class等
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                File dir = directoryInput.file
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File file ->
                            System.out.println("find class: " + file.name)
                    }
                }
            }
        }
    }
}
```

#### 将自定义的Transform注册到gradle插件中

在LifeCyclePlugin中添加如下代码：

```groovy
	apply{
    	...
		def android = project.extensions.getByType(AppExtension)
        println '----- registering AutoTrackTransform -----'
        LifeCycleTransform transform = new LifeCycleTransform()
        android.registerTransform(transform)
        }
```

再次在命令行中执行build命令，可以看到检索的所有.class文件。

![img](https://s0.lgstatic.com/i/image3/M01/08/4F/Ciqah16FrECALf2oAAMhJPoxXUY597.png)

### 使用ASM，插入字节码到Activity文件

#### 创建自定义ASM Visitor类

在plugin module中的java目录下创建包com.neoqee.asm，分别创建LifecycleClassVisitor.java和LifecycleMethodVisitor.java。代码如下：

#### LifecycleClassVisitor.java

```java
package com.neoqee.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleClassVisitor extends ClassVisitor {

    private String className;
    private String superName;

    public LifecycleClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        super.visit(i, i1, s, s1, s2, strings);
        this.className = s;
        this.superName = s2;
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        System.out.println("ClassVisitor visitMethod name -----> " + s + ", supperName is " + superName);
        MethodVisitor mv = cv.visitMethod(i, s, s1, s2, strings);

        if (superName.equals("androidx/appcompat/app/AppCompatActivity")) {
            if (s.startsWith("onCreate")){
                return new LifecycleMethodVisitor(mv,className,s);
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}

```

#### LifecycleMethodVisitor.java

```java
package com.neoqee.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleMethodVisitor extends MethodVisitor {

    private String className;
    private String methodName;

    public LifecycleMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
        super(Opcodes.ASM5,methodVisitor);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("MethodVisitor visitCode -----");

        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + "----->" + methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }
}

```

#### 修改LifeCycleTransform的transform方法，使用ASM：

```groovy
@Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //拿到所有的class文件
        Collection<TransformInput> transformInputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        transformInputs.each { TransformInput transformInput ->
            // directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            // 比如我们手写的类以及R.class、BuildConfig.class以及MainActivity.class等
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                File dir = directoryInput.file
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File file ->
                            System.out.println("find class: " + file.name)
                            //对Class文件进行读取与解析
                            ClassReader classReader = new ClassReader(file.bytes)
                            //对class文件的写入
                            ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS)
                            //访问class文件相应的内容，解析到某一个结构就会通知到ClassVisitor的相应方法
                            ClassVisitor classVisitor = new LifecycleClassVisitor(classWriter)
                            //依次调用 ClassVisitor接口的各个方法
                            classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES)
                            //toByteArray方法会将最终修改的字节码以byte数组形式返回
                            byte[] bytes = classWriter.toByteArray()
                            //通过文件流写入方式覆盖掉原先的内容，实现class文件的改写
                            FileOutputStream outputStream = new FileOutputStream(file.path)
                            outputStream.write(bytes)
                            outputStream.close()
                    }
                }
                //处理完输入文件后把输出传给下一个文件
                def dest = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file,dest)
            }
        }
    }
```

#### 重新部署自定义Gradle插件，并运行主项目

重新部署时，需要将app module的build.gradle中插件依赖注释，否则可能报错。

点击uploadArchives重新部署插件，完成后重新添加插件依赖，运行app

![img](https://s0.lgstatic.com/i/image3/M01/08/4F/Ciqah16FrEGAJlXtAADxyHXMnAI728.png)