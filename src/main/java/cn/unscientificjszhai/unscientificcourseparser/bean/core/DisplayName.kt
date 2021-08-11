package cn.unscientificjszhai.unscientificcourseparser.bean.core

/**
 * 显示名称注解。
 *
 * @param value 解析器在GUI中的显示名称。
 * @author UnscientificJsZhai
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val value: String)
