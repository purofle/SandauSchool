package com.github.purofle.sandauschool.utils

import android.content.Context
import android.util.Log
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.service.LoginService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

suspend fun retryLogin(attempt: Int, exception: Throwable, context: Context) {
    exception.printStackTrace()
    Log.e(
        "retryLogin",
        "failed to get data, try to login...(retry $attempt): ${exception.localizedMessage}"
    )
    val username =
        context.dataStore.data.map { it[Preference.USERNAME] }
            .first()
    val password =
        context.dataStore.data.map { it[Preference.PASSWORD] }
            .first()
    username?.let {
        LoginService.login(username, password.orEmpty())
    }
}