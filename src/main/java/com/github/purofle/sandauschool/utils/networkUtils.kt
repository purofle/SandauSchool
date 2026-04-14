package com.github.purofle.sandauschool.utils

import android.content.Context
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.network.newEHallService
import com.github.purofle.sandauschool.service.LoginService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody
import retrofit2.Response

val loginMutex = Mutex()
var loginTask: Deferred<Response<ResponseBody>>? = null

suspend fun <T> fetchDataOrLogin(context: Context, getData: suspend () -> T): T {
    return try {
        getData()
    } catch (e: SerializationException) {
        e.printStackTrace()

        val appPageResponse = newEHallService.getAppPage(7328727903036396)
        val url = appPageResponse.raw().request.url.toString()

        if ("/authserver/login" in url) {
            getLoginTask(context).await()
            getData()
        } else {
            getData()
        }
    }
}

suspend fun getLoginTask(context: Context): Deferred<Response<ResponseBody>> {
    return loginMutex.withLock {
        val currentTask = loginTask
        if (currentTask != null && currentTask.isActive) {
            return currentTask
        }

        val newLoginTask = CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
            val prefs = context.dataStore.data.first()
            LoginService.login(prefs[Preference.USERNAME]!!, prefs[Preference.PASSWORD]!!)
            newEHallService.getAppPage(7328727903036396)
        }

        loginTask = newLoginTask

        newLoginTask
    }
}