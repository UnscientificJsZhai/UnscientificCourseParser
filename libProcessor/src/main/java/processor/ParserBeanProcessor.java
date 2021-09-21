package processor;

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.TreeSet;

/**
 * 注解处理器。用于在编译期处理{@link ParserBean}注解生成一个类扫描器。
 *
 * @author UnscientificJsZhai
 */
public class ParserBeanProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        this.messager.printMessage(Diagnostic.Kind.ERROR, "Initial Processing");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, "Start Processing");
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ParserBean.class);
        for (Element element : elements) {
            messager.printMessage(Diagnostic.Kind.ERROR, element.getSimpleName().toString());
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new TreeSet<>();
        types.add("com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean");
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
