package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.github.purofle.sandauschool.res.Res
import com.github.purofle.sandauschool.res.app_name
import com.github.purofle.sandauschool.res.nav_debug
import com.github.purofle.sandauschool.res.nav_home
import com.github.purofle.sandauschool.res.nav_timetable
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import com.github.purofle.sandauschool.icons.adb as adbIcon
import com.github.purofle.sandauschool.icons.calendar_month as calendarMonthIcon
import com.github.purofle.sandauschool.icons.home as homeIcon

sealed class Screen {
    object Home : Screen()
    object TimeTable : Screen()
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        expressiveLightColorScheme()
    }

    MaterialExpressiveTheme(colorScheme, content = content)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun MainScreen() {

    val backStack = remember {
        mutableStateListOf<Screen>(Screen.Home)
    }

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.app_name)) }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = backStack.first() == Screen.Home,
                        icon = { Icon(homeIcon, "Home") },
                        onClick = {},
                        label = { Text(stringResource(Res.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = false,
                        icon = { Icon(calendarMonthIcon, "nav_timetable") },
                        onClick = {},
                        label = { Text(stringResource(Res.string.nav_timetable)) }
                    )
                    NavigationBarItem(
                        selected = false,
                        icon = { Icon(adbIcon, "debug") },
                        onClick = {},
                        label = { Text(stringResource(Res.string.nav_debug)) }
                    )
                }
            }
        ) { pd ->

            NavDisplay(
                backStack = backStack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                },
                entryProvider = entryProvider {
                    entry(Screen.Home) {
                        HomeScreen()
                    }
                },
                modifier = Modifier.padding(pd)
            )
        }
    }
}