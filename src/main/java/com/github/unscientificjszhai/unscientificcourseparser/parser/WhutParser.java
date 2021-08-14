package com.github.unscientificjszhai.unscientificcourseparser.parser;

import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser;
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean;
import com.github.unscientificjszhai.unscientificcourseparser.StringUtility;
import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime;
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 武汉理工大学。
 *
 * @author UnscientificJsZhai
 */
@ParserBean(value = "whut", displayName = "武汉理工大学")
public final class WhutParser extends Parser {

    @NotNull
    @Override
    public String getUrl() {
        return "http://sso.jwc.whut.edu.cn/Certification/login.do";
    }

    @NotNull
    @Override
    public String getMessage() {
        return "请点击\"学期课表\"后再进行解析";
    }

    @NotNull
    @Override
    public List<Course> parse(@NotNull String htmlText) {
        ArrayList<Course> courseList = new ArrayList<>();
        final Document document = Jsoup.parse(htmlText);

        //第一轮识别：课程、学分。
        final HashMap<String, String> teacherMap = new HashMap<>();
        final Elements courses = document.getElementsByClass("table-box").get(2).select("tr");
        courses.remove(courses.get(1)); //移除空白。
        courses.remove(courses.get(0)); //移除表头。
        for (Element element : courses) {
            final String title = StringUtility.removeHtmlTags(element.child(0).toString());
            final double credit = Double.parseDouble(StringUtility.removeHtmlTags(element.child(1).toString()));
            final String teacher = StringUtility.removeHtmlTags(element.child(4).toString());
            teacherMap.put(title, teacher);
            courseList.add(new Course(title, credit, "", new ArrayList<>()));
        }

        //第二轮识别：添加上课时间。
        final Elements elements = document.getElementsByClass("table-class-even")
                .get(1)
                .select("td");
        for (int index = 1; index < elements.size(); index++) {
            final Element block = elements.get(index);
            if (block.childrenSize() == 0) {
                continue;
            }
            final Elements rawClassTimes = block.select("div");
            for (Element rawClassTime : rawClassTimes) {
                final Element time = rawClassTime.select("a").get(0);
                final String title = time.childNode(0).toString().trim();
                final ClassTime classTime = this.parseClassTime(time, teacherMap.get(title), index);
                for (Course course : courseList) {
                    if (course.getTitle().equals(title)) {
                        course.getClassTimes().add(classTime);
                        break;
                    }
                }
            }
        }

        return courseList;
    }

    /**
     * 解析上课时间。
     *
     * @param element Jsoup元素。
     * @return 上课时间。
     */
    @NotNull
    private ClassTime parseClassTime(final Element element, String teacherName, int tableIndex) {
        if (teacherName == null) {
            teacherName = "";
        }
        final String location = StringUtility.removeHtmlTags(element.childNode(1).toString()).substring(1);
        final String[] information = StringUtility.removeHtmlTags(element.childNode(3).toString())
                .replaceAll("\\D+", " ").trim().split(" ");
        int dayOfWeek = tableIndex % 8 - 1;
        if (dayOfWeek == -1) {
            dayOfWeek = 0;
        }
        //information: [03,17,1,2]
        final int startWeek = Integer.parseInt(information[0]);
        final int endWeek = Integer.parseInt(information[1]);
        final int from = Integer.parseInt(information[2]);
        final int to = Integer.parseInt(information[3]);
        return new ClassTime(dayOfWeek, from, to, startWeek, endWeek, ClassTime.SCHEDULE_MODE_DEFAULT, location, teacherName, null);
    }
}
