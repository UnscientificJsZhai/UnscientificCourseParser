# 不科学的课程表解析

## 简介

本项目是基于Jsoup的课程表解析库，可用于课程表开发。采用IoC模式，使得更容易使用。 仍在开发中，正式版即将上线。  
相关项目：[时间管理大师](https://github.com/UnscientificJsZhai/TimeManager)

## 适配说明

想让本项目支持你自己的学校教务？那就自己来动手吧！ 在开始前，你需要：

1. 掌握Java或者Kotlin语言。（推荐使用Kotlin）
2. 安装IntelliJ IDEA。（或者其它顺手的IDE，需要支持Gradle）
3. 掌握Jsoup基础。（当然现学也来得及）

### 开发指南

1. Fork项目，打开项目，在.parser包中建立自己的解析器子类，继承.bean.core.Parser抽象类。
2. 给你的解析器子类添加这个注解。  
   `com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean`
   注解的第一个参数相当于是你的解析器的标识符，要求使用小写字母，最好是学校英文简称。第二个参数相当于显示名称，即适配器在使用中显示出的名称。 使用中文全称即可。
3. 查看Course类和ClassTime类的文档，了解输出数据类的结构。
4. 重写`url`属性，这个属性是教务系统的网址。  
   对于Java用户，则是重写`getUrl():String`方法。
5. 重写`parse(String):List<Course>`方法，实现你自己的解析器类。
6. 在.bean.factory.HardcodeScanner中的init代码块注册你的解析器类。请仅注册final类（Kotlin中的非open）。  
   请按照文件名顺序排序注册代码。注册方式一看就懂。
7. 测试，提交，PR。

### 注意事项

1. 请按下面的编码要求要求书写代码。（强迫症警告）
2. 如果不了解怎么写可以参考西北工业大学、武汉理工大学的解析器，作为示例。这两个解析器分别使用了Kotlin和Java语言。当然也欢迎大家帮我捉虫。
3. 提交时请勿提交测试类和测试用HTML文件。小心隐私泄露。
4. 关于多个学校共享相同教务系统：请在parser包下新建一个包，包名为教务系统名简称。然后创建一个抽象类继承Parser，实现解析方法`parse(String):List<Course>`
   。不要给这个抽象类添加上述的两个注解。然后创建若干个该抽象类的子类并实现方法`getUrl():String`，给每个子类添加上述两个注解。

### 编码要求

1. 每个类（包括对象、接口、内部类，不包括匿名类和伴生对象）、**非重写**方法都必须写JavaDoc（或者KDoc）。 Java中所有重写方法必须添加`@Override`
   注解。文档中每句话必须有句号。文档中如果出现参数、返回等字段，需要空一行再写。
2. 每个类的第一行必须空行，包括对象、接口、内部类、匿名类和（仅Kotlin）伴生对象。
3. 完成后需要格式化代码。 如果你是用的是IntelliJ IDEA，在Windows下按<kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>L</kbd>、 在macOS中按<kbd>option</kbd>
   +<kbd>command</kbd>+<kbd>L</kbd>格式化代码。
4. 对于Kotlin用户，创建数据类Course和ClassTime、调用其构造方法时请使用命名参数，这样的代码具有自描述性。例如：
   ```kotlin
   val course = Course(
       title = "课程标题",
       credit = 1.0,
       remark = "备注"
   )
   ```
5. 每个解析器类的JavaDoc（KDoc）第一行要用中文写出对应学校的全名。
6. 每个解析器的作者都请用`@author`署名。你们的努力该被所有人看到。

## 接入说明

在Gradle脚本中添加如下依赖。当前版本：  
[![](https://jitpack.io/v/UnscientificJsZhai/UnscientificCourseParser.svg)](https://jitpack.io/#UnscientificJsZhai/UnscientificCourseParser)

```groovy
allprojects {
    repositories {

        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.UnscientificJsZhai:UnscientificCourseParser:Tag'
}
```

需要把“Tag”改为当前版本号。

### 使用说明

通过ParserFactory类查找获取解析器。这个类需要一个TypeScanner对象作为参数。TypeScanner是查找解析器的接口，默认（即无入参）采用Reflections反射扫描对象，还提供一个硬编码的对象列表。Android用户无法使用反射扫描的实现，可以选择硬编码实现，也可以自己实现TypeScanner。

通过ParserFactory查找到想要的解析器后，调用解析器的`parse(String):List<Course>`方法，传入一个HTML字符串，即可获得解析结果。