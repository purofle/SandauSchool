package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.purofle.sandauschool.model.RemoteCourse

@Composable
fun TimeTableScreenUI(vm: MainViewModel = viewModel()) {
    val courseTable by vm.courseTable.collectAsStateWithLifecycle()
    val currentTeachWeek by vm.currentTeachWeek.collectAsStateWithLifecycle()

    // 过滤出本周的课程
    val timeTable = courseTable
        .filter { currentTeachWeek in it.weekIndexes }
        .groupBy { it.weekday }
        .toSortedMap()

    val weekdayNames = mapOf(
        1 to "周一",
        2 to "周二",
        3 to "周三",
        4 to "周四",
        5 to "周五",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {

        val totalWidth = maxWidth
        val leftColumnWidth = 20.dp
        val rightWidth = totalWidth - leftColumnWidth
        val columnCount = timeTable.keys.size
        val eachColumnWidth = rightWidth / columnCount

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(leftColumnWidth)
                    .fillMaxHeight()
            ) {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height((80 / 2).dp))
                    }
                    items((1..12).toList()) {
                        Box(
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(it.toString())
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {

                timeTable.toList().forEach { (weekday, courses) ->
                    Column(
                        modifier = Modifier
                            .width(eachColumnWidth)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${weekdayNames[weekday]}",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Column {
                            var currentUnit = 1
                            val sortedCourses = courses.sortedBy { it.startUnit }

                            while (currentUnit <= 12) {
                                val course =
                                    sortedCourses.find { currentUnit in it.startUnit..it.endUnit }
                                if (course != null) {
                                    CourseCard(course)
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                    )
                                }
                                currentUnit++
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: RemoteCourse) {
    Card(
        modifier = Modifier
            .height(120.dp)
            .width(120.dp)
    ) {
        Text(course.name)
        Text("${course.room}教室")
        Text(course.teachers.joinToString(","))
    }
}