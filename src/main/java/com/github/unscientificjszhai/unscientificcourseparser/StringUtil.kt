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
    .replace("<\\w+?>".toRegex(), "")
    .replace("</\\w+?>".toRegex(), "")

/**
 * 将周几、星期几的描述转为[ClassTime]中描述星期的数字。
 *
 * @return 描述周几上课的数字。0代表周日，6代表周六。无法解析则返回-1。
 * @author UnscientificJsZhai
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

/**
 * 移除首尾的括号。
 *
 * @return 移除一对括号后的字符串。中英文都可以被移除（但是要对应）。如果首尾任意一处括号缺失或者类型错误，则原样返回。
 * @author UnscientificJsZhai
 */
internal fun String.removeParentheses(): String {
    if (this.length < 2) {
        return this
    }

    val type = //false表示半角
        if (this.startsWith("(")) {
            false
        } else if (this.startsWith("（")) {
            true
        } else {
            return this
        }

    return if (
        (!type && this.endsWith(")")) || (type && this.endsWith("）"))
    ) {
        this.substring(1, this.lastIndex)
    } else {
        this
    }
}

/**
 * 闭合没有闭合的括号。半角或是全角，每次只能闭合其中的一种。
 *
 * @param type 是否是要补全全角括号。false即为补全半角括号。
 * @return 在尾部填充对应的括号。
 * @author UnscientificJsZhai
 */
internal fun String.fixIncompleteParentheses(type: Boolean = false): String {
    val head = if (type) {
        '（'
    } else {
        '('
    }
    val tail = if (type) {
        '）'
    } else {
        ')'
    }

    var hasHead = 0
    for (index in 0..this.lastIndex) {
        if (this[index] == head) {
            hasHead += 1
        } else if (this[index] == tail) {
            if (hasHead > 0) {
                hasHead -= 1
            }
        }
    }

    return this + tail.toString().repeat(hasHead)
}