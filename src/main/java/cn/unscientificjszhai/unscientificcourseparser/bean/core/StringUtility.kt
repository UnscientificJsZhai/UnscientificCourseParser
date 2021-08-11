package cn.unscientificjszhai.unscientificcourseparser.bean.core

/**
 * 处理HTML字符时常用的方法。本对象中的方法建议添加[JvmStatic]注解方便Java语言调用。
 */
object StringUtility {

    /**
     * 去掉HTML标签。只能去掉简单标签（防止错误识别）。
     *
     * @return 去掉HTML标签后的字符串。
     * @author UnscientificJsZhai
     */
    @JvmStatic
    fun String.removeHtmlTags() = this
        .replace("<[a-z]+>".toRegex(), "")
        .replace("</[a-z]+>".toRegex(), "")
}