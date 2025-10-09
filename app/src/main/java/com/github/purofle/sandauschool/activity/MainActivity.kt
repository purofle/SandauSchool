package com.github.purofle.sandauschool.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.network.courseManagementService
import com.github.purofle.sandauschool.network.newEHallService
import com.github.purofle.sandauschool.screen.MainScreenUI
import com.github.purofle.sandauschool.service.LoginService
import com.github.purofle.sandauschool.utils.retry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data object MainScreen : NavKey

@Serializable
data object TimeTableScreen : NavKey

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {

                var selectedItem by remember { mutableIntStateOf(0) }
                val items = listOf("Home", "Timetable")
                val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.DateRange)
                val unselectedIcons =
                    listOf(Icons.Outlined.Home, Icons.Outlined.DateRange)

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
                                        }
                                    },
                                )
                            }
                        }
                    }
                ) { pd ->

                    val context = LocalContext.current
                    var cardBalance by remember { mutableDoubleStateOf(0.00) }
                    var loginCount by remember { mutableIntStateOf(0) }
                    var currentTeachWeek by remember { mutableIntStateOf(0) }
                    LaunchedEffect(Unit) {
                        try {
                            retry(onError = { attempt, err ->
                                err.printStackTrace()
                                Log.e(
                                    TAG,
                                    "failed to get data, try to login...(retry $attempt)"
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

                            }) {
                                newEHallService.getAppPage(7328727903036396) // get cookie
                                cardBalance = newEHallService.getYktData().data.balance
                                loginCount = courseManagementService.getLoginCount()
                                currentTeachWeek =
                                    courseManagementService.getCurrentTeachWeek().weekIndex

                                courseManagementService.getCourseTableHtml().body()?.string()
                                    .orEmpty()
                                    .let { html ->
                                        val semester =
                                            courseManagementService.getSemesterFromHtml(html)
                                                .first()
                                        Log.d(TAG, "course: $semester")
                                        val courseTable =
                                            courseManagementService.getCourseTable(semester.id)

                                        val timeTable = courseTable.studentTableVms
                                            .flatMap { it.activities }
                                            .groupBy { it.weekday }

                                        Log.d(TAG, "courseTable: $timeTable")
                                    }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry(MainScreen) {
                                MainScreenUI()
                            }

                            entry(TimeTableScreen) {

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
