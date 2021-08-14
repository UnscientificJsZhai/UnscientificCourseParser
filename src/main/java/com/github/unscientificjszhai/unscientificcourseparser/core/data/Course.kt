package com.github.unscientificjszhai.unscientificcourseparser.core.data

/**
 * 课程数据。
 *
 * @param title 课程标题。
 * @param credit 学分。0表示值不存在（未定义）。
 * @param remark 课程备注。
 * @param classTimes 上课时间。默认情况下会创建一个空的[ArrayList]作为容器。
 * @see ClassTime
 * @author UnscientificJsZhai
 */
data class Course(
    val title: String,
    val credit: Double,
    val remark: String,
    val classTimes: ArrayList<ClassTime> = ArrayList()
) : List<ClassTime> by classTimes {

    /**
     * 对于只有一个上课时间段的课程的构造方法。
     *
     * @param title 课程标题。
     * @param credit 学分。0表示值不存在（未定义）。
     * @param remark 课程备注。
     * @param classTime 上课时间。
     */
    constructor(
        title: String,
        credit: Double,
        remark: String,
        classTime: ClassTime
    ) : this(title, credit, remark, arrayListOf(classTime))
}