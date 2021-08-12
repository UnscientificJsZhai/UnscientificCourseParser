package com.github.unscientificjszhai.unscientificcourseparser.bean.core

/**
 * 解析器前置注解。
 *
 * @param value 解析器在GUI中的显示名称。
 * @author UnscientificJsZhai
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParserBean(val value: String, val displayName: String)