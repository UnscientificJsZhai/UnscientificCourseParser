@file:JvmName("FileGenerator")

package com.github.unscientificjszhai.unscientificcourseparser.core.processor

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * 生成扫描器的Java代码。
 *
 * @param filer 注解处理器的环境对象。
 * @param classes 扫描到的类。
 */
internal fun generateJavaFile(
    filer: Filer,
    classes: List<Element>
) {
    //成员：map
    val parserBaseTypeName =
        ClassName.get("com.github.unscientificjszhai.unscientificcourseparser.core.parser", "Parser")
    val classOfParser = ParameterizedTypeName.get(
        ClassName.get("java.lang", "Class"),
        WildcardTypeName.subtypeOf(parserBaseTypeName)
    ) // Class<? extends Parser>
    val mapTypeName = ParameterizedTypeName.get(
        ClassName.get("java.util", "HashMap"),
        ClassName.get("java.lang", "String"),
        classOfParser
    )
    val initializerBlock = CodeBlock.builder().add("new HashMap<>()").build()
    val mapField = FieldSpec.builder(mapTypeName, "map", Modifier.PRIVATE, Modifier.FINAL)
        .initializer(initializerBlock)
        .build()

    //构造函数
    val constructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .apply {
            for (element in classes) {
                val type = ClassName.get(element as TypeElement)
                addStatement("submit(\$T.class)", type)
            }
        }.build()

    //注册方法
    val submitMethod = MethodSpec.methodBuilder("submit")
        .addModifiers(Modifier.PRIVATE)
        .addParameter(classOfParser, "parser")
        .addStatement(
            "String beanName = parser.getAnnotation(\$T.class).value()",
            ClassName.get(ParserBean::class.java)
        )
        .addStatement("map.put(beanName, parser)")
        .build()

    //实现方法
    val mapReturnTypeName = ParameterizedTypeName.get(
        ClassName.get("java.util", "Map"),
        ClassName.get("java.lang", "String"),
        classOfParser
    )
    val implementMethod = MethodSpec.methodBuilder("scan")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override::class.java)
        .returns(mapReturnTypeName)
        .addStatement("return this.map")
        .build()

    //类
    val scannerInterfaceTypeName = ClassName.get(
        "com.github.unscientificjszhai.unscientificcourseparser.core.factory",
        "TypeScanner"
    )
    val typeSpec = TypeSpec.classBuilder("Scanner_Impl")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addSuperinterface(scannerInterfaceTypeName)
        .addField(mapField)
        .addMethod(constructor)
        .addMethod(submitMethod)
        .addMethod(implementMethod)
        .build()

    //文件
    val file = JavaFile.builder("com.github.unscientificjszhai.unscientificcourseparser.core.factory", typeSpec)
        .build()

    file.writeTo(filer)
}