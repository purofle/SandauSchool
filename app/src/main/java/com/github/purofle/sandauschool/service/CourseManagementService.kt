package com.github.purofle.sandauschool.service

import com.github.purofle.sandauschool.model.CurrentTeachWeek
import com.github.purofle.sandauschool.model.Semester
import com.github.purofle.sandauschool.model.StudentTable
import com.github.purofle.sandauschool.network.json
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseManagementService {
    @GET("/student/home/get-login-count")
    suspend fun getLoginCount(): Int

    @GET("/student/home/get-current-teach-week")
    suspend fun getCurrentTeachWeek(): CurrentTeachWeek

    @GET("/student/for-std/course-table")
    suspend fun getCourseTableHtml(): Response<ResponseBody>

    @GET("student/for-std/course-table/semester/{semesterId}/print-data")
    suspend fun getCourseTable(
//        @Query("semesterId") semesterId: Int,
        @Path("semesterId") semesterId: Int,
    ): StudentTable

    fun getSemesterFromHtml(html: String): List<Semester> {
        val startIndex = html.lineSequence()
            .indexOfFirst { "var semesters = JSON.parse(" in it }

        if (startIndex == -1) return emptyList()

        val jsonLine = html.lineSequence()
            .drop(startIndex + 1)
            .firstOrNull { it.isNotBlank() }
            ?.trim()
            ?.removeSurrounding("'", "'")
            ?.replace("\\", "")

        return json.decodeFromString<List<Semester>>(jsonLine!!)
    }

}