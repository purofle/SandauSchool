package com.github.purofle.sandauschool.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.repository.CourseTableRepository
import com.github.purofle.sandauschool.screen.MainScreenUI
import com.github.purofle.sandauschool.screen.TimeTableScreenUI
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object MainScreen : NavKey

@Serializable
data object TimeTableScreen : NavKey

@Serializable
data object DebugScreen : NavKey

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val colorScheme = when (isSystemInDarkTheme()) {
                true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicDarkColorScheme(this)
                } else {
                    darkColorScheme()
                }

                false -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicLightColorScheme(this)
                } else {
                    lightColorScheme()
                }
            }

            MaterialTheme(colorScheme) {

                var selectedItem by remember { mutableIntStateOf(0) }
                val items = listOf("Home", "Timetable", "Debug")
                val selectedIcons =
                    listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.Build)
                val unselectedIcons =
                    listOf(Icons.Outlined.Home, Icons.Outlined.DateRange, Icons.Outlined.Build)

                val backStack = rememberNavBackStack(MainScreen)

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                            contentDescription = item,
                                        )
                                    },
                                    label = { Text(item) },
                                    selected = selectedItem == index,
                                    onClick = {
                                        selectedItem = index
                                        backStack.removeLastOrNull()
                                        when (selectedItem) {
                                            0 -> backStack.add(MainScreen)
                                            1 -> backStack.add(TimeTableScreen)
                                            2 -> backStack.add(DebugScreen)
                                        }
                                    },
                                )
                            }
                        }
                    }
                ) { pd ->

                    val scope = rememberCoroutineScope()

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry(MainScreen) {
                                MainScreenUI()
                            }

                            entry(TimeTableScreen) {
                                TimeTableScreenUI()
                            }

                            entry(DebugScreen) {
                                Column {
                                    Button(onClick = {
                                        CookieManager.getInstance().removeAllCookies {
                                            Log.d(TAG, "remove all cookies")
                                        }
                                    }) { Text("Clean cookie") }

                                    Button(onClick = {
                                        scope.launch {
                                            CourseTableRepository(this@MainActivity).refreshCourseTable()
                                        }
                                    }) { Text("刷新课程表") }
                                }
                            }
                        },
                        modifier = Modifier.padding(pd)
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
