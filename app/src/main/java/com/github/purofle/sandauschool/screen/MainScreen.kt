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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.repository.CourseTableRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreenUI() {

    val context = LocalContext.current
    val courseTableRepository = CourseTableRepository(context)

    val courseTable by courseTableRepository.getCourseTableFlow().collectAsState(emptyList())
    val timeTable = courseTable.groupBy { it.weekday }

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

        val pattern = stringResource(R.string.date_pattern)
        DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
//        val dateText = dateTime.format(formatter)

        Text(
            stringResource(text) + ",",
            style = TextStyle(fontSize = 28.sp),
            fontWeight = FontWeight.Bold
        )
        Text("日落果", style = TextStyle(fontSize = 28.sp))
        Spacer(modifier = Modifier.height(16.dp))
        val todayTimeTable = timeTable[dateTime.dayOfWeek.value]

        todayTimeTable?.forEach { course ->
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
                    Text("${course.room}教室")
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