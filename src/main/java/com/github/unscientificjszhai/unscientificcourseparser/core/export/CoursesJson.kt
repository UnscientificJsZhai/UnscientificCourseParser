package com.github.unscientificjszhai.unscientificcourseparser.core.export

import com.github.unscientificjszhai.unscientificcourseparser.core.data.ClassTime
import com.github.unscientificjszhai.unscientificcourseparser.core.data.Course
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Json生成器。将解析结果生成Json文件。
 * 直接对此对象调用[toString]方法即可获得Json字符串。
 *
 * @param courses 解析后的课程列表。
 * @param serializer 自定义的Json序列化实现。
 * @author UnscientificJsZhai
 */
class CoursesJson(private val courses: List<Course>, private val serializer: JsonSerializer<CoursesJson>) :
    List<Course> by courses {

    companion object {

        /**
         * 将输出结果直接转为Json的方法。
         *
         * @return 返回的CoursesJson对象。
         */
        @JvmStatic
        fun List<Course>.json() = CoursesJson(this)

        /**
         * 反序列化。
         *
         * @param jsonString 输入Json字符串。
         * @param deserializer 反序列化接口。
         * @return 反序列化的结果。
         */
        @JvmStatic
        fun jsonToCourse(
            jsonString: String,
            deserializer: JsonDeserializer<CoursesJson>
        ): List<Course> {
            val gson = GsonBuilder().registerTypeAdapter(CoursesJson::class.java, deserializer).create()

            return gson.fromJson(jsonString, CoursesJson::class.java)
        }

        /**
         * 使用默认提供接口反序列化。
         *
         * @param jsonString 输入Json字符串。
         */
        @JvmStatic
        @Suppress("unused")
        fun jsonToCourse(jsonString: String) = jsonToCourse(jsonString, CourseListDeserializer())
    }

    /**
     * 默认使用的构造函数。使用内置实现Json序列化课程列表。
     *
     * @param courses 解析后的课程列表。
     */
    constructor(courses: List<Course>) : this(courses, CourseListSerializer())

    /**
     * 课程表数据Json序列化实现。
     *
     * @author UnscientificJsZhai
     */
    private class CourseListSerializer : JsonSerializer<CoursesJson> {

        override fun serialize(src: CoursesJson, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            val root = JsonArray()
            for (course in src) {
                val courseElement = JsonObject()
                courseElement.addProperty("title", course.title)
                courseElement.addProperty("credit", course.credit)
                courseElement.addProperty("remark", course.remark)
                courseElement.add("class_times", JsonArray().apply {

                    for (classTime in course.classTimes) {
                        val classTimeElement = JsonObject()
                        classTimeElement.addProperty("day_of_week", classTime.day)

                        if (classTime.specificTime != null && classTime.from == 0 && classTime.to == 0) {
                            classTimeElement.addProperty("time", classTime.specificTime)
                        } else {
                            classTimeElement.addProperty("from", classTime.from)
                            classTimeElement.addProperty("to", classTime.to)
                        }

                        classTimeElement.addProperty("start_week", classTime.startWeek)
                        classTimeElement.addProperty("end_week", classTime.endWeek)
                        classTimeElement.addProperty("schedule_mode", classTime.scheduleMode)
                        classTimeElement.addProperty("location", classTime.location)
                        classTimeElement.addProperty("teacher", classTime.teacher)
                        this.add(classTimeElement)
                    }
                })
                root.add(courseElement)
            }
            return root
        }
    }

    /**
     * 课程表数据Json反序列化实现。
     *
     * @author UnscientificJsZhai
     */
    private class CourseListDeserializer : JsonDeserializer<CoursesJson> {

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): CoursesJson {
            val list = ArrayList<Course>()

            val courseArray = if (json.isJsonArray) {
                json.asJsonArray
            } else return CoursesJson(list)
            for (courseElement in courseArray) {
                if (courseElement is JsonObject) {
                    val title = courseElement.get("title").asString
                    val credit = courseElement.get("credit").asDouble
                    val remark = courseElement.get("remark").asString
                    val classTimeJsonArray = courseElement.get("class_times").asJsonArray
                    val classTimes = ArrayList<ClassTime>()

                    for (classTimeElement in classTimeJsonArray) {
                        if (classTimeElement is JsonObject) {
                            val dayOfWeek = classTimeElement.get("day_of_week").asInt
                            val startWeek = classTimeElement.get("start_week").asInt
                            val endWeek = classTimeElement.get("end_week").asInt
                            val scheduleMode = classTimeElement.get("schedule_mode").asInt
                            val location = classTimeElement.get("location").asString
                            val teacherName = classTimeElement.get("teacher").asString

                            if (classTimeElement.has("time")) {
                                classTimes.add(
                                    ClassTime(
                                        day = dayOfWeek,
                                        startWeek = startWeek,
                                        endWeek = endWeek,
                                        scheduleMode = scheduleMode,
                                        location = location,
                                        teacher = teacherName,
                                        from = 0,
                                        to = 0,
                                        specificTime = classTimeElement.get("time").asString
                                    )
                                )
                            } else {
                                classTimes.add(
                                    ClassTime(
                                        day = dayOfWeek,
                                        startWeek = startWeek,
                                        endWeek = endWeek,
                                        scheduleMode = scheduleMode,
                                        location = location,
                                        teacher = teacherName,
                                        from = classTimeElement.get("from").asInt,
                                        to = classTimeElement.get("to").asInt
                                    )
                                )
                            }
                        } else {
                            continue
                        }
                    }

                    list.add(
                        Course(
                            title = title,
                            credit = credit,
                            remark = remark,
                            classTimes = classTimes
                        )
                    )
                } else {
                    continue
                }
            }
            return CoursesJson(list)
        }
    }

    override fun toString(): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(CoursesJson::class.java, this.serializer)
            .setPrettyPrinting()
            .create()
        return gson.toJson(this)
    }
}