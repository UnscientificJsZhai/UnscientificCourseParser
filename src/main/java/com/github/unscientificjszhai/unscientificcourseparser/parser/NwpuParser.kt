package com.github.unscientificjszhai.unscientificcourseparser.parser

import com.github.unscientificjszhai.unscientificcourseparser.StringUtility.textToDayOfWeek
import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * 西北工业大学。
 *
 * @author UnscientificJsZhai
 */
@ParserBean("nwpu", "西北工业大学")
class NwpuParser : Parser() {

    override val url = "https://students-schedule.nwpu.edu.cn/ui/#/courseTable"

    override fun parse(htmlText: String): List<Course> {
        val titleMap = HashMap<String, Course>()

        val doc = Jsoup.parse(htmlText)
        val courseElements = doc.getElementsByClass("course-content")
        for (element in courseElements) {
            val title = element.getElementsByClass("name")[0].childNode(0).toString()
            if (title in titleMap.keys) {
                //追加到重复的课程中
                titleMap[title]!!.classTimes.add(parseClassTime(element))
            } else {
                //新建课程
                val remark = element.getElementsByClass("number")[0].childNode(0).toString()
                titleMap[title] = Course(
                    title = title,
                    credit = 0.0, //不解析学分
                    remark = remark
                ).apply {
                    classTimes.add(parseClassTime(element))
                }
            }
        }

        return titleMap.values.toList()
    }

    /**
     * 解析上课时间。
     *
     * @param element HTML元素。
     * @return 上课时间。
     */
    private fun parseClassTime(element: Element): ClassTime {
        val location = try {
            val addressElement = element.getElementsByClass("address")[0]
            val contentElement = addressElement.getElementsByClass("content")[0]
            contentElement.childNode(0).toString().split(" ")[1]
        } catch (e: IndexOutOfBoundsException) {
            ""
        }

        val teacherName = try {
            val teacherElement = element.getElementsByClass("teacher")[0]
            val contentElement = teacherElement.getElementsByClass("content")[0]
            contentElement.childNode(0).toString()
        } catch (e: IndexOutOfBoundsException) {
            ""
        }

        //必须包含时间元素
        val timeString = element.getElementsByClass("time")[0]
            .getElementsByClass("content")[0]
            .childNode(0).toString().split(" ")
        val numberRegex = "[0-9]+".toRegex()

        var numbers = numberRegex.findAll(timeString[0]).iterator()
        val startWeek = numbers.next().value.toInt()
        val endWeek = if (numbers.hasNext()) {
            numbers.next().value.toInt()
        } else {
            startWeek
        }
        val scheduleMode = if (timeString[0].endsWith("双周")) {
            ClassTime.SCHEDULE_MODE_EVEN
        } else if (timeString[0].endsWith("单周")) {
            ClassTime.SCHEDULE_MODE_ODD
        } else {
            ClassTime.SCHEDULE_MODE_DEFAULT
        }

        numbers = numberRegex.findAll(timeString[2]).iterator()
        val from = numbers.next().value.toInt()
        val to = if (numbers.hasNext()) {
            numbers.next().value.toInt()
        } else {
            from
        }

        return ClassTime(
            day = timeString[1].textToDayOfWeek(),
            from = from,
            to = to,
            location = location,
            teacher = teacherName,
            startWeek = startWeek,
            endWeek = endWeek,
            scheduleMode = scheduleMode
        )
    }
}