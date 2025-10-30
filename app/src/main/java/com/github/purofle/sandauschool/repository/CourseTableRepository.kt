package com.github.purofle.sandauschool.repository

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.model.RemoteCourse
import com.github.purofle.sandauschool.network.courseManagementService
import com.github.purofle.sandauschool.network.json
import com.github.purofle.sandauschool.utils.fetchDataOrLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CourseTableRepository(
    val context: Context,
) {
    fun getCourseTableFlow(): Flow<List<RemoteCourse>> = flow {
        val courseTable = loadCourseTableLocal()
        if (courseTable != null) {
            emit(courseTable)
            return@flow
        }

        emit(refreshCourseTable())
        return@flow
    }

    suspend fun refreshCourseTable(): List<RemoteCourse> {
        val remoteCourseTable = fetchDataOrLogin(context) {
            loadCourseTableRemote()
        }

        context.dataStore.edit {
            it[Preference.courseTable] = json.encodeToString(remoteCourseTable)
        }

        return remoteCourseTable
    }

    suspend fun loadCourseTableLocal(): List<RemoteCourse>? {
        val courseTableJson = context.dataStore.data
            .map { it[Preference.courseTable] }
            .first() ?: return null

        return json.decodeFromString(courseTableJson)
    }

    fun getCurrentTeachWeekFlow(): Flow<Int> = flow {
        val currentTeachWeek =
            context.dataStore.data.map { it[Preference.currentTeachWeek] }.first()

        if (currentTeachWeek != null) {
            emit(currentTeachWeek)
        }

        try {
            val remoteCourseTable = fetchDataOrLogin(context) {
                courseManagementService.getCurrentTeachWeek().weekIndex
            }
            emit(remoteCourseTable)
            context.dataStore.edit {
                it[Preference.currentTeachWeek] = remoteCourseTable
            }
        } catch (e: Exception) {
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Need to login first, recommended to use [fetchDataOrLogin]
     */
    suspend fun loadCourseTableRemote(): List<RemoteCourse> {
        val semesterId = courseManagementService.getCourseTableHtml().let {
            courseManagementService.getSemesterFromHtml(
                it.body()?.string() ?: throw Exception("courseTable is null")
            )
                .first()
        }.id

        return courseManagementService.getCourseTable(semesterId).studentTableVms.flatMap { it.activities }
    }
}