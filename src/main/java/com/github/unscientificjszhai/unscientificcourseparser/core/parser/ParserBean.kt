package com.github.unscientificjszhai.unscientificcourseparser.core.parser

/**
 * 解析器前置注解。
 *
 * @param value 识别名。
 * @param displayName 解析器在GUI中的显示名称。
 * @author UnscientificJsZhai
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParserBean(
    val value: String,
    val displayName: String
)