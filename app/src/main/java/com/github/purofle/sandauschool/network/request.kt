package com.github.purofle.sandauschool.network

import com.github.purofle.sandauschool.service.AuthService
import com.github.purofle.sandauschool.service.CourseManagementService
import com.github.purofle.sandauschool.service.NewEHallService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val client = OkHttpClient.Builder()
    .cookieJar(PersistentCookieJar())
    .build()

val json = Json {

}

val newEHallRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://newehall.sandau.edu.cn/")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

val authRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://authserver.sandau.edu.cn/")
    .build()

val courseManagementRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://jxgl.sandau.edu.cn/")
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

val newEHallService: NewEHallService = newEHallRetrofit.create(NewEHallService::class.java)
val authService: AuthService = authRetrofit.create(AuthService::class.java)
val courseManagementService: CourseManagementService = courseManagementRetrofit.create(
    CourseManagementService::class.java
)