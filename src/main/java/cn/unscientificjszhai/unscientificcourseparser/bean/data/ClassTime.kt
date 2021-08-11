package cn.unscientificjszhai.unscientificcourseparser.bean.data

/**
 * 上课时间数据类。
 *
 * @param day 周几上课。取值范围0-6，0代表周日，1代表周一，6代表周六。
 * @param from 从第几节课开始。
 * @param to 到第几节课结束。
 * @param startWeek 起始周。
 * @param endWeek 结束周。
 * @param scheduleMode 排课规则，见伴生对象中的常量。
 * @param location 上课地点。比如教室房间号。
 * @param teacher 教师姓名。
 * @param specificTime 特定的上课时间。如果[from]和[to]都为0的时候，此项即表示特定的上课时间。
 * @author UnscientificJsZhai
 */
data class ClassTime(
    val day: Int,
    val from: Int,
    val to: Int,
    val startWeek: Int,
    val endWeek: Int,
    val scheduleMode: Int,
    val location: String,
    val teacher: String,
    val specificTime: String? = null //规则：8位数字，前4位表示上课时间，后四位表示下课时间。例如08301010代表8:30-10:10上课。
) {

    companion object {

        /**
         * 全周上课。
         */
        const val SCHEDULE_MODE_DEFAULT = 0

        /**
         * 单周上课。
         */
        const val SCHEDULE_MODE_ODD = 1

        /**
         * 双周上课。
         */
        const val SCHEDULE_MODE_EVEN = 2
    }
}
