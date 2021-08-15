package com.github.unscientificjszhai.unscientificcourseparser.parser

import com.github.unscientificjszhai.unscientificcourseparser.StringUtility.removeHtmlTags
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
@ParserBean("nwpuold", "西北工业大学（教务系统）", true)
class NwpuOldParser : Parser() {

    override val url = "http://us.nwpu.edu.cn/eams/"

    override fun parse(htmlText: String): List<Course> {
        val courseList = ArrayList<Course>()
        val jsoupDoc = Jsoup.parse(htmlText)
        val tbody = jsoupDoc.getElementsByClass("gridtable").select("tbody")
        val rawCourses = tbody[1].select("tr")

        for (rawCourse in rawCourses) {
            val elements = rawCourse.select("tr").select("td")

            val course = Course(
                title = elements[3].toString().removeHtmlTags(),
                credit = elements[5].toString().removeHtmlTags().toDouble(),
                remark = elements[11].toString().removeHtmlTags().trim(),
                classTimes = parseClassTime(elements[7])
            )

            courseList.add(course)
        }

        return courseList
    }

    /**
     * 解析每个上课时间段的方法。
     *
     * @param rawClassTimes 原始数据。
     * @return 上课时间段的解析结果。
     */
    private fun parseClassTime(rawClassTimes: Element): ArrayList<ClassTime> {
        val classTimes = ArrayList<ClassTime>()

        val stringList = rawClassTimes.toString().split("<br>")
        for (part in stringList) {
            val rawClassTime = part.removeHtmlTags().split(" ")

            val fromTo = rawClassTime[2].split("-")
            val from = fromTo[0].toInt()
            val to = fromTo[fromTo.lastIndex].toInt()

            val (startWeek, endWeek, scheduleMode) = parseWeekData(rawClassTime[3])

            classTimes.add(
                ClassTime(
                    day = rawClassTime[1].textToDayOfWeek(),
                    from = from,
                    to = to,
                    location = rawClassTime[4],
                    teacher = rawClassTime[0],
                    startWeek = startWeek,
                    endWeek = endWeek,
                    scheduleMode = scheduleMode
                )
            )
        }

        return classTimes
    }

    /**
     * 内部数据类，用于解析上课周数。
     *
     * @param startWeek 同[ClassTime.startWeek]。
     * @param endWeek 同[ClassTime.endWeek]。
     * @param scheduleMode 同[ClassTime.scheduleMode]。
     */
    private data class WeekData(
        val startWeek: Int,
        val endWeek: Int,
        val scheduleMode: Int
    )

    /**
     * 解析上课周数。
     *
     * @param value 包含上课周数的字符串，如果只上一周课则为一个数字。
     *              否则为方括号包裹的两个数字。方括号外可能有表示单双周上课的字符。
     * @return 解析后的数据类。
     */
    private fun parseWeekData(value: String): WeekData {
        val regex = Regex("[0-9]+")
        val numbers = regex.findAll(value).iterator()
        val startWeek = numbers.next().value.toInt()
        return if (numbers.hasNext()) {
            val endWeek = numbers.next().value.toInt()
            val scheduleMode = if (value.endsWith("单")) {
                ClassTime.SCHEDULE_MODE_ODD
            } else if (value.endsWith("双")) {
                ClassTime.SCHEDULE_MODE_EVEN
            } else {
                ClassTime.SCHEDULE_MODE_DEFAULT
            }
            WeekData(startWeek, endWeek, scheduleMode)
        } else {
            WeekData(startWeek, startWeek, ClassTime.SCHEDULE_MODE_DEFAULT)
        }
    }
}