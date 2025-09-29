package com.github.purofle.sandauschool.service

import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

val cookieManager = CookieManager().also { it.setCookiePolicy(CookiePolicy.ACCEPT_ALL) }

val client = OkHttpClient.Builder()
    .cookieJar(JavaNetCookieJar(cookieManager))
    .build()

val newEHallRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://newehall.sandau.edu.cn/")
    .addConverterFactory(Json {  }.asConverterFactory("application/json".toMediaType()))
    .build()

val authRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://authserver.sandau.edu.cn/")
    .build()

val courseManagementRetrofit: Retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://jxgl.sandau.edu.cn/")
    .addConverterFactory(Json {  }.asConverterFactory("application/json".toMediaType()))
    .build()

val newEHallService: NewEHallService = newEHallRetrofit.create(NewEHallService::class.java)
val authService: AuthService = authRetrofit.create(AuthService::class.java)
val courseManagementService: CourseManagementService = courseManagementRetrofit.create(CourseManagementService::class.java)