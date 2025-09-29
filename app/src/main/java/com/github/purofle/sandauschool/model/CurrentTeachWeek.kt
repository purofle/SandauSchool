package com.github.purofle.sandauschool.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentTeachWeek(
    val currentSemester: String,
    val dayIndex: Int,
    val isInSemester: Boolean,
    val weekIndex: Int,
)
