package com.github.unscientificjszhai.unscientificcourseparser.core.factory;

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser;
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean;

import java.util.HashMap;
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

    /**
     * 空白扫描器。
     */
    class EmptyScanner implements TypeScanner {

        @Override
        public Map<String, Class<? extends Parser>> scan() {
            return new HashMap<>();
        }
    }
}