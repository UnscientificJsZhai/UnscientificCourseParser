package cn.unscientificjszhai.unscientificcourseparser.bean.factory

import cn.unscientificjszhai.unscientificcourseparser.bean.core.DisplayName
import cn.unscientificjszhai.unscientificcourseparser.bean.core.Parser
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.stereotype.Component

/**
 * 工厂类。用于提供解析器。
 *
 * @param context Spring的上下文。已经有默认配置，一般不需要手动添加和修改。
 * @author UnscientificJsZhai
 */
class ParserFactory(
    private val context: ApplicationContext = ClassPathXmlApplicationContext("parsers.xml")
) {

    /**
     * 获取要查找的解析器。
     *
     * @param beanName 解析器上注解[Component]的参数。
     * @return 要查找的解析器。
     * @throws NoSuchBeanDefinitionException 对应的解析器找不到时，抛出此异常。
     */
    @Throws(NoSuchBeanDefinitionException::class)
    fun get(beanName: String) = this.context.getBean(beanName) as Parser

    /**
     * 获取一个包含所有解析器的字典。
     *
     * @return 键是解析器的显示名称，可用于显示在GUI中。
     * 值是解析器的beanName，用于在[get]方法中获取这个解析器。
     */
    fun parserList(): Map<String, String> {
        val map = HashMap<String, String>()
        for (beanName in this.context.getBeanNamesForType(Parser::class.java)) {
            val parserClass = this.context.getType(beanName)
            val displayName = parserClass.getAnnotation(DisplayName::class.java).value

            map[displayName] = beanName
        }
        return map
    }
}