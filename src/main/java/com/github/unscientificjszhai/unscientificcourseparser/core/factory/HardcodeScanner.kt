package com.github.unscientificjszhai.unscientificcourseparser.core.factory

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import com.github.unscientificjszhai.unscientificcourseparser.parser.NwpuOldParser
import com.github.unscientificjszhai.unscientificcourseparser.parser.NwpuParser
import com.github.unscientificjszhai.unscientificcourseparser.parser.WhutParser

/**
 * 硬编码式类扫描器实现。（这都不叫扫描了）
 * （暂时的备选方案，希望以后能有更好的解决方案）
 */
class HardcodeScanner : TypeScanner {

    private val map = HashMap<String, Class<out Parser>>()

    override fun scan(): Map<String, Class<out Parser>> = this.map

    private fun HashMap<String, Class<out Parser>>.submit(parser: Class<out Parser>) {
        val beanName = parser.getAnnotation(ParserBean::class.java).value
        this[beanName] = parser
    }

    init {
        this.map.apply {
            //在这里注册
            submit(NwpuOldParser::class.java)
            submit(NwpuParser::class.java)
            submit(WhutParser::class.java)
        }
    }
}