package com.github.purofle.sandauschool.service

import com.github.purofle.sandauschool.model.YktData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewEHallService {
    @GET("/psfw/sys/pubdzshsdgrxxjk/api/getYktData.do")
    suspend fun getYktData(): YktData

    @GET("/jsonp/ywtb/info/getUserInfoAndSchoolInfo")
    suspend fun getUserInfoAndSchoolInfo()

    @GET("/appShow")
    suspend fun getAppPage(@Query("appId") appId: Long): Response<ResponseBody>
}