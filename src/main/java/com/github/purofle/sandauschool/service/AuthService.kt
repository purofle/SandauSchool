package com.github.purofle.sandauschool.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @GET("/authserver/login")
    suspend fun getLoginPage(): ResponseBody

    @FormUrlEncoded
    @POST("/authserver/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("_eventId") eventId: String = "submit",
        @Field("cllt") cllt: String = "userNameLogin",
        @Field("dllt") dllt: String = "generalLogin",
        @Field("execution") execution: String,
        @Field("captcha") captcha: String = "",
        @Field("lt") lt: String = "",
        @Query("service") service: String = "https://newehall.sandau.edu.cn/ywtb-portal/official/index.html",
    ): Response<ResponseBody>
}