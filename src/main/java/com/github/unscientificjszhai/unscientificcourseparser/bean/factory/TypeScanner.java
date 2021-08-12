package com.github.unscientificjszhai.unscientificcourseparser.bean.factory;

import com.github.unscientificjszhai.unscientificcourseparser.bean.core.Parser;
import com.github.unscientificjszhai.unscientificcourseparser.bean.core.ParserBean;

import java.util.Map;

/**
 * 类扫描器，用于获取所有解析器的类。
 *
 * @author UnscientificJsZhai
 */
public interface TypeScanner {

    /**
     * 获取解析器的方法。
     *
     * @return 键为 {@link ParserBean}的value，值为对应的解析器的Class对象。
     */
    Map<String, Class<? extends Parser>> scan();
}