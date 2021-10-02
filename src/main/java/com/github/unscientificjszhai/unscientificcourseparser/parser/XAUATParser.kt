package com.github.unscientificjszhai.unscientificcourseparser.parser

import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import com.github.unscientificjszhai.unscientificcourseparser.removeParentheses
import org.jsoup.Jsoup

/**
 * 西安建筑科技大学。
 *
 * @author UnscientificJsZhai
 */
@ParserBean("xauat", "西安建筑科技大学")
class XAUATParser : Parser() {

    override val url = "http://swjw.xauat.edu.cn/student/for-std/course-table"

    override val message = "点击\"我的课表\"，\"全部周次\""

    override fun parse(htmlText: String): List<Course> {
        val map = HashMap<String, Course>()

        val document = Jsoup.parse(htmlText)
        val cardContents = document.getElementsByClass("card-content-info")

        for (cardContent in cardContents) {
            val nodes = cardContent.childNodes()
            val titleSource = nodes[0].toString()
            val title = titleSource.substring(0, titleSource.lastIndexOf("&nbsp"))
            val course = if (map.containsKey(title)) {
                map[title]!!
            } else {
                val course = Course(
                    title = title,
                    credit = 0.0,
                    remark = ""
                )
                map[title] = course
                course
            }

            val classTimeSource = nodes[3].toString().split(" ?&nbsp;".toRegex())
            val location = classTimeSource[0].trim()
            val dayOfWeek = classTimeSource[2].trim().toInt()
            val fromTo = classTimeSource[3].trim().removeParentheses().split(',')
            val from = fromTo[0].toInt()
            val to = fromTo[fromTo.lastIndex].toInt()

            val weeks = classTimeSource[1].trim().removeParentheses().split(',')
            val numberRangeRegex = "[0-9]+~[0-9]+".toRegex()
            val numberRegex = "[0-9]+".toRegex()
            for (week in weeks) {
                val regex = if (week.contains('~')) {
                    numberRangeRegex
                } else { //仅上课一周的情况
                    numberRegex
                }
                val numberRange = regex.find(week)?.value?.split('~') ?: continue
                course.classTimes.add(
                    ClassTime(
                        location = location,
                        day = dayOfWeek,
                        from = from,
                        to = to,
                        teacher = "",
                        startWeek = numberRange[0].toInt(),
                        endWeek = numberRange[numberRange.lastIndex].toInt(),
                        scheduleMode = if (week.contains("单")) {
                            ClassTime.SCHEDULE_MODE_ODD
                        } else if (week.contains("双")) {
                            ClassTime.SCHEDULE_MODE_EVEN
                        } else {
                            ClassTime.SCHEDULE_MODE_DEFAULT
                        }
                    )
                )
            }
        }
        return map.values.toList()
    }
}