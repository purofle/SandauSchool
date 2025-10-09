package com.github.purofle.sandauschool.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.model.RemoteCourse
import com.github.purofle.sandauschool.network.courseManagementService
import com.github.purofle.sandauschool.network.json
import com.github.purofle.sandauschool.utils.retry
import com.github.purofle.sandauschool.utils.retryLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CourseTableRepository(
    val context: Context,
) {
    fun getCourseTableFlow(): Flow<List<RemoteCourse>> = flow {
        loadCourseTableLocal()?.let { emit(it) }

        val remoteCourseTable = loadCourseTableRemote()
        emit(remoteCourseTable)
        saveCourseTableLocal(remoteCourseTable)
    }

    suspend fun loadCourseTableLocal(): List<RemoteCourse>? {
        val courseTableJson = context.dataStore.data
            .map { it[Preference.courseTable] }
            .first() ?: return null

        return json.decodeFromString(courseTableJson)
    }

    fun getCurrentTeachWeekFlow(): Flow<Int> = flow {
        emit(context.dataStore.data.map { it[Preference.currentTeachWeek] }.first()?.toInt() ?: 2)

        emit(courseManagementService.getCurrentTeachWeek().weekIndex)
    }

    suspend fun saveCourseTableLocal(courseTable: List<RemoteCourse>) {
        context.dataStore.edit {
            it[Preference.courseTable] = json.encodeToString(courseTable)
        }
    }

    suspend fun loadCourseTableRemote(): List<RemoteCourse> {
        retry(onError = { attempt, exception ->
            retryLogin(attempt, exception, context)
        }) {
            val semesterId = courseManagementService.getCourseTableHtml().let {
                courseManagementService.getSemesterFromHtml(
                    it.body()?.string() ?: throw Exception("courseTable is null")
                )
                    .first()
            }.id

            return courseManagementService.getCourseTable(semesterId).studentTableVms.flatMap { it.activities }
        }
    }
}