package com.github.unscientificjszhai.unscientificcourseparser.core.factory

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner

/**
 * 工厂类。用于提供解析器。
 *
 * @param scanner 类扫描器。
 * @author UnscientificJsZhai
 */
class ParserFactory(scanner: TypeScanner) {

    /**
     * 默认类扫描器，不在Android上工作。
     */
    private class DefaultScanner : TypeScanner {

        override fun scan(): Map<String, Class<out Parser>> {
            val map = HashMap<String, Class<out Parser>>()
            val reflections =
                Reflections("com.github.unscientificjszhai.unscientificcourseparser.parser", SubTypesScanner())
            val parserClassSet = reflections.getSubTypesOf(Parser::class.java)
            for (parserClass in parserClassSet) {
                try {
                    val beanName = parserClass.getAnnotation(ParserBean::class.java).value
                    map[beanName] = parserClass
                } catch (e: NullPointerException) {
                    continue
                }
            }
            return map
        }
    }

    private val parserMap: Map<String, Class<out Parser>> = scanner.scan()

    /**
     * 默认构造方法，使用自带扫描器。不支持在Android上使用。
     */
    constructor() : this(DefaultScanner())

    /**
     * 获取要查找的解析器。
     *
     * @param beanName 解析器上注解[ParserBean]的参数[ParserBean.value]。
     * @return 要查找的解析器。
     * @throws ClassNotFoundException 对应的解析器找不到时，抛出此异常。
     */
    @Throws(ClassNotFoundException::class)
    operator fun get(beanName: String): Parser {
        val parserClass = this.parserMap[beanName]
        if (parserClass == null) {
            throw ClassNotFoundException("Can not find parser class: \"$beanName\"")
        } else {
            val constructors = parserClass.constructors
            for (constructor in constructors) {
                if (constructor.parameters.isEmpty()) {
                    return constructor.newInstance() as Parser
                }
            }
            throw ClassNotFoundException("Can not initialize parser class: \"$beanName\"")
        }
    }

    /**
     * 获取一个包含所有解析器的字典。
     *
     * @param cloud 是否只显示仅限云解析的解析器。
     * @return 键是解析器的显示名称，可用于显示在GUI中。
     * 值是解析器的beanName，用于在[get]方法中获取这个解析器。
     */
    fun parserList(cloud: Boolean = false): Map<String, String> {
        val map = HashMap<String, String>()
        for (beanName in this.parserMap.keys) {
            val annotation = this.parserMap[beanName]!!.getAnnotation(ParserBean::class.java)
            val displayName = annotation.displayName
            if (cloud == annotation.cloud) {
                map[displayName] = beanName
            }
        }
        return map
    }
}