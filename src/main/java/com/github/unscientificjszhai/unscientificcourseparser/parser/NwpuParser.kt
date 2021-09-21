package com.github.unscientificjszhai.unscientificcourseparser.parser

import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import com.github.unscientificjszhai.unscientificcourseparser.textToDayOfWeek
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

    private val numberRegex = "[0-9]+".toRegex()
    private val weekRegex = "第.+?周".toRegex()
    private val classRegex = "[0-9]+-[0-9]+节".toRegex()
    private val dayOfWeekRegex = "周[一二三四五六日]".toRegex()

    override fun parse(htmlText: String): List<Course> {
        val titleMap = HashMap<String, Course>()

        val doc = Jsoup.parse(htmlText)
        val courseElements = doc.getElementsByClass("course-content")
        for (element in courseElements) {
            val title = element.getElementsByClass("name")[0].childNode(0).toString()
            if (title !in titleMap.keys) {
                //新建课程
                val remark = element.getElementsByClass("number")[0].childNode(0).toString()
                val classTimes = ArrayList<ClassTime>()

                //解析上课时间
                val classTimeElements = element.getElementsByClass("course-item-list")
                for (classTimeElement in classTimeElements) {
                    parseClassTime(classTimeElement, classTimes)
                }

                titleMap[title] = Course(
                    title = title,
                    credit = 0.0, //不解析学分
                    remark = remark,
                    classTimes = classTimes
                )
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
    private fun parseClassTime(element: Element, classTimeList: ArrayList<ClassTime>) {
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
            .childNode(0).toString()

        val scheduleMode = if (timeString.contains("双周")) {
            ClassTime.SCHEDULE_MODE_EVEN
        } else if (timeString.contains("单周")) {
            ClassTime.SCHEDULE_MODE_ODD
        } else {
            ClassTime.SCHEDULE_MODE_DEFAULT
        }

        val classString = classRegex.findAll(timeString).iterator().next().value
        val classNumbers = numberRegex.findAll(classString).iterator()
        val from = classNumbers.next().value.toInt()
        val to = if (classNumbers.hasNext()) {
            classNumbers.next().value.toInt()
        } else {
            from
        }

        val day = dayOfWeekRegex.findAll(timeString).iterator().next().value.textToDayOfWeek()

        val originalWeekStrings = weekRegex.findAll(timeString).iterator().next().value.split(" ")
        for (weekStringResult in originalWeekStrings) {
            val weekNumbers = numberRegex.findAll(weekStringResult).iterator()
            val startWeek = weekNumbers.next().value.toInt()
            val endWeek = if (weekNumbers.hasNext()) {
                weekNumbers.next().value.toInt()
            } else {
                startWeek
            }

            classTimeList.add(
                ClassTime(
                    day = day,
                    from = from,
                    to = to,
                    location = location,
                    teacher = teacherName,
                    startWeek = startWeek,
                    endWeek = endWeek,
                    scheduleMode = scheduleMode
                )
            )
        }
    }
}