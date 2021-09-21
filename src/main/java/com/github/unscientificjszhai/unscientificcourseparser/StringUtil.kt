@file:JvmName("StringUtil")
package com.github.unscientificjszhai.unscientificcourseparser

import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime

/**
 * 去掉HTML标签。只能去掉简单标签（防止错误识别）。
 *
 * @return 去掉HTML标签后的字符串。
 * @author UnscientificJsZhai
 */
internal fun String.removeHtmlTags() = this
    .replace("<[a-z]+>".toRegex(), "")
    .replace("</[a-z]+>".toRegex(), "")

/**
 * 将周几、星期几的描述转为[ClassTime]中描述星期的数字。
 *
 * @return 描述周几上课的数字。0代表周日，6代表周六。无法解析则返回-1。
 */
internal fun String.textToDayOfWeek(): Int {
    return when (this) {
        "星期一", "周一" -> 1
        "星期二", "周二" -> 2
        "星期三", "周三" -> 3
        "星期四", "周四" -> 4
        "星期五", "周五" -> 5
        "星期六", "周六" -> 6
        "星期日", "周日" -> 0
        else -> -1
    }
}