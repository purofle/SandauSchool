package com.github.purofle.sandauschool.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class StudentTable(
    val studentTableVms: List<StudentTableVm>
)

@Serializable
data class StudentTableVm(
    val activities: List<RemoteCourse>,
)

@Serializable
data class RemoteCourse(
    @SerialName("courseName") val name: String,
    val weekIndexes: List<Int>,
    val room: Int,
    val teachers: List<String>,
    val startTime: String,
    val endTime: String,
    val startUnit: Int,
    val endUnit: Int,
    val weekday: Int,
)