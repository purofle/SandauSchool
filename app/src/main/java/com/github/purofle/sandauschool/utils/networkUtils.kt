package com.github.purofle.sandauschool.utils

import android.content.Context
import android.util.Log
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.network.newEHallService
import com.github.purofle.sandauschool.service.LoginService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

suspend fun <T> fetchDataOrLogin(context: Context, getData: suspend () -> T): T {
    // 判断是否登录校园统一认证
    val appPageResponse = newEHallService.getAppPage(7328727903036396)
    Log.d("fetchDataOrLogin", "$appPageResponse")

    val url = appPageResponse.raw().request.url.toString()
    return if ("/authserver/login" in url) {
        Log.d("fetchDataOrLogin", "login status: not logged in, try to login...")
        val username =
            context.dataStore.data.map { it[Preference.USERNAME] }
                .first()
        val password =
            context.dataStore.data.map { it[Preference.PASSWORD] }
                .first()
        LoginService.login(username!!, password!!)
        fetchDataOrLogin(context, getData)
    } else {
        Log.d("fetchDataOrLogin", "login status: logged in: $url")
        getData()
    }
}