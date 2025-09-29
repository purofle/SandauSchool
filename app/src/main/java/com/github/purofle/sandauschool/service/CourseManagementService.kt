package com.github.purofle.sandauschool.service

import com.github.purofle.sandauschool.model.CurrentTeachWeek
import retrofit2.http.GET

interface CourseManagementService {
    @GET("/student/home/get-login-count")
    suspend fun getLoginCount(): Int

    @GET("/student/home/get-current-teach-week")
    suspend fun getCurrentTeachWeek(): CurrentTeachWeek
}