package com.github.purofle.sandauschool

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object Preference {

    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")

    val courseTable = stringPreferencesKey("courseTable")

    val currentTeachWeek = intPreferencesKey("currentTeachWeek")

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userdata")
}