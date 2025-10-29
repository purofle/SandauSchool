package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.purofle.sandauschool.MyApp
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.repository.CourseTableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.util.Locale

class MainViewModel() : ViewModel() {
    // context 似乎不是最佳方案
    val repo = CourseTableRepository(MyApp.context)
    val courseTable = repo.getCourseTableFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val currentTeachWeek = repo.getCurrentTeachWeekFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)
}

@Composable
fun MainScreenUI(vm: MainViewModel = viewModel()) {
    val courseTable by vm.courseTable.collectAsStateWithLifecycle()
    val currentTeachWeek by vm.currentTeachWeek.collectAsStateWithLifecycle()

    val timeTable = courseTable.filter { currentTeachWeek in it.weekIndexes }
        .groupBy { it.weekday }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        val dateTime = LocalDateTime.now()
        val text = when (dateTime.hour) {
            in 0..4 -> R.string.good_evening
            in 5..11 -> R.string.good_morning
            in 12..17 -> R.string.good_afternoon
            in 18..23 -> R.string.good_evening
            else -> error("why are you here: ${dateTime.hour}?")
        }

        Text(
            stringResource(text) + ",",
            style = TextStyle(fontSize = 28.sp),
            fontWeight = FontWeight.Bold
        )
        Text(stringResource(R.string.purofle), style = TextStyle(fontSize = 28.sp))
        Spacer(modifier = Modifier.height(16.dp))
        val todayTimeTable = timeTable[dateTime.dayOfWeek.value]

        Text(
            stringResource(
                R.string.today_is_week,
                dateTime.dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    Locale.getDefault()
                ),
                currentTeachWeek
            )
        )

        if (todayTimeTable.isNullOrEmpty()) {
            Text(stringResource(R.string.no_courses_today))
        }

        todayTimeTable?.sortedBy { course ->
            // Parse non-zero padded time (e.g., "9:00" or "13:30") to comparable integer
            // Convert "H:MM" to minutes since midnight for efficient sorting
            val parts = course.startTime.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                hours * 60 + minutes
            } else {
                0
            }
        }?.forEach { course ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 12.dp, 12.dp, 0.dp)
                ) {
                    Text("${course.name} (${course.teachers.joinToString(",")})")
                    Text("${course.room}${stringResource(R.string.classroom_suffix)}")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 0.dp, 12.dp, 12.dp)
                ) {
                    Text(course.startTime)
                    Text(course.endTime)
                }
            }
        }
    }
}

@Composable
@Preview
fun MainScreenUIPreview() {
    MainScreenUI()
}