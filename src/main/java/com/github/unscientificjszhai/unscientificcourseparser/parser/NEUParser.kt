package com.github.unscientificjszhai.unscientificcourseparser.parser

import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.Parser
import com.github.unscientificjszhai.unscientificcourseparser.core.parser.ParserBean
import com.github.unscientificjszhai.unscientificcourseparser.fixIncompleteParentheses
import com.github.unscientificjszhai.unscientificcourseparser.removeHtmlTags
import com.github.unscientificjszhai.unscientificcourseparser.removeParentheses
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node

/**
 * 东北大学
 *
 * @author UnscientificJsZhai
 */
@ParserBean("neu", "东北大学")
class NEUParser : Parser() {

    companion object {

        const val MAX_CLASS_PER_DAY = 12
    }

    override val url = "http://219.216.96.4/eams/courseTableForStd!courseTable.action"

    override val message = "请在校园网内访问"

    private val parenthesesRegex = "\\(.+?\\)".toRegex()
    private val numberRegex = "[0-9]+".toRegex()

    override fun parse(htmlText: String): List<Course> {
        val courseTitleMap = HashMap<String, Course>()

        val document = Jsoup.parse(htmlText)
        val table = document.getElementById("manualArrangeCourseTable") ?: return ArrayList()
        val blocks = table.getElementsByClass("infoTitle")
        for (block in blocks) {
            val informationPairs = getClassInformationPair(block.childNodes())
            for (informationPair in informationPairs) {
                val firstIterator = parenthesesRegex.findAll(informationPair.first).iterator()
                val description = firstIterator.next().value
                val teacher = firstIterator.next().value
                val courseTitle = informationPair.first.substring(0, informationPair.first.indexOf(description))

                //确认当前课程标题是否已经创建了Course对象
                val course = if (courseTitleMap.containsKey(courseTitle)) {
                    courseTitleMap[courseTitle]!!
                } else {
                    val newCourse = Course(
                        title = courseTitle,
                        credit = 0.0,
                        remark = "课程序号 " + description.removeParentheses()
                    )
                    courseTitleMap[courseTitle] = newCourse
                    newCourse
                }

                //分析表ID获得上课是第几周第几节课等信息
                val rowId = block.attributes().get("id")
                val idNumber = rowId.substring(2, rowId.indexOf('_')).toInt()
                val dayOfWeek: Int = idNumber / MAX_CLASS_PER_DAY + 1 //7代表周日
                val from = idNumber % MAX_CLASS_PER_DAY + 1
                val to = from - 1 + try {
                    block.attributes().get("rowspan").toInt()
                } catch (e: Exception) {
                    1
                }

                //创建ClassTime
                val secondSubstrings = informationPair.second.removeParentheses().split(",", limit = 2)
                val location = secondSubstrings.getOrElse(1) { "" }
                val weekSource = secondSubstrings[0].split(" ")
                for (result in weekSource) {
                    val numberIterator = numberRegex.findAll(result).iterator()
                    val start = numberIterator.next().value.toInt()
                    val end = if (numberIterator.hasNext()) {
                        numberIterator.next().value.toInt()
                    } else {
                        start
                    }
                    val scheduleMode = when (result.last()) {
                        '单' -> ClassTime.SCHEDULE_MODE_ODD
                        '双' -> ClassTime.SCHEDULE_MODE_EVEN
                        else -> ClassTime.SCHEDULE_MODE_DEFAULT
                    }
                    course.classTimes.add(
                        ClassTime(
                            startWeek = start,
                            endWeek = end,
                            location = location.fixIncompleteParentheses(),
                            teacher = teacher.removeParentheses(),
                            scheduleMode = scheduleMode,
                            day = if (dayOfWeek > 6) {
                                0
                            } else {
                                dayOfWeek
                            },
                            from = from,
                            to = to
                        )
                    )
                }
            }
        }

        return secondScan(courseTitleMap, document)
    }

    /**
     * 通过td标签内容获取关联的（连续的）两段描述同一节课的文字。
     *
     * @param nodes td标签内的内容。
     * @return Pair对象。
     */
    private fun getClassInformationPair(nodes: List<Node>): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        var currentA: String? = null

        for (node in nodes) {
            if (node.toString().contains("<br>")) {
                continue
            } else {
                currentA = if (currentA == null) {
                    node.toString()
                } else {
                    list.add(Pair(currentA, node.toString()))
                    null
                }
            }
        }
        return list
    }

    /**
     * 第二次扫描，获取学分信息。
     *
     * @param courseTitleMap 第一次扫描中创建的HashMap对象。
     * @param htmlDocument 要扫描的HTML文档。
     * @return 最后结果。如果二次扫描中出现异常，则直接返回不含学分信息的课程对象列表。
     */
    private fun secondScan(courseTitleMap: HashMap<String, Course>, htmlDocument: Document): List<Course> {
        val originalList = courseTitleMap.values.toList()
        try {
            val table = htmlDocument.getElementById("tasklesson")?.getElementsByClass("gridtable")!!
            val bodies = table[0].getElementsByTag("tbody")[0].getElementsByTag("tr")
            for (body in bodies) {
                val title = body.child(2).toString().removeHtmlTags()
                val credit = body.child(3).toString().removeHtmlTags().toDouble()

                val originalCourse = courseTitleMap[title]
                if (originalCourse?.title == title) {
                    courseTitleMap[title] = Course(
                        title = originalCourse.title,
                        credit = credit,
                        remark = originalCourse.remark,
                        classTimes = originalCourse.classTimes
                    )
                }
            }
            return courseTitleMap.values.toList()
        } catch (e: Exception) {
            return originalList
        }
    }
}