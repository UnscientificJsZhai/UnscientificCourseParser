package com.github.unscientificjszhai.unscientificcourseparser.core.processor;

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * 注解处理器。用于在编译期处理{@link ParserBean}注解生成一个类扫描器。
 *
 * @author UnscientificJsZhai
 */
public class ParserBeanProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    private final ArrayList<Element> elements = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {

            FileGenerator.generateJavaFile(filer, elements);

        } else {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ParserBean.class);
            for (Element element : elements) {
                messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName().toString());
                this.elements.add(element);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new TreeSet<>();
        types.add(ParserBean.class.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
