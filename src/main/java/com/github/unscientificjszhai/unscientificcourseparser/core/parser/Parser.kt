package com.github.unscientificjszhai.unscientificcourseparser.core.parser

import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.github.unscientificjszhai.unscientificcourseparser.parser.NWPUParser
import com.github.unscientificjszhai.unscientificcourseparser.parser.WHUTParser

/**
 * 解析器基类。Kotlin示例见西北工业大学解析。Java示例见武汉理工大学解析。
 *
 * @see NWPUParser
 * @see WHUTParser
 * @author UnscientificJsZhai
 */
abstract class Parser {

    /**
     * 要打开的教务系统的网页。
     */
    abstract val url: String

    /**
     * 解析前可能会在调用方的界面上显示一条消息。
     */
    open val message: String = ""

    /**
     * 解析方法。子类需要重写此方法。
     *
     * @param htmlText 输入的HTML字符串。
     * @throws Exception 执行过程中可能会抛出任何类型的异常，需要做好异常处理。
     * @return 解析结果。
     */
    @Throws(Exception::class)
    abstract fun parse(htmlText: String): List<Course>

    /**
     * 获取显示名称。
     *
     * @return 解析器的显示名称。
     * @throws NullPointerException 如果解析器没有设置显示名称则会抛出此异常。
     */
    @Suppress("unused")
    fun getDisplayName() = this::class.java.getAnnotation(ParserBean::class.java).displayName
}