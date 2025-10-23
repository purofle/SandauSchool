package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val weekdayNames = listOf("周一", "周二", "周三", "周四", "周五")

    val leftListState = rememberLazyListState()
    val rightListState = rememberLazyListState()

    // Observe scroll changes in the RIGHT column
    LaunchedEffect(rightListState) {
        snapshotFlow { rightListState.firstVisibleItemIndex to rightListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                leftListState.scrollToItem(index, offset)
            }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {

        val totalWidth = maxWidth
        val leftColumnWidth = 20.dp
        val rightWidth = totalWidth - leftColumnWidth
        val columnCount = 5
        val eachColumnWidth = rightWidth / columnCount

        val courseHeight = 120.dp

        Row {
            LazyColumn(state = leftListState) {
                items((1..12).toList()) {
                    Box(
                        modifier = Modifier
                            .height(courseHeight)
                            .width(leftColumnWidth)
                            .padding(top = courseHeight / 2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(it.toString())
                    }
                }
            }

            LazyColumn(state = rightListState) {
                stickyHeader {
                    Row {
                        weekdayNames.forEach { weekday ->
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 4.dp, start = 2.dp, end = 2.dp)
                                    .width(eachColumnWidth - 4.dp)
                            ) {
                                Text(
                                    weekday,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items((1..12).toList()) { currentUnit ->
                    Row {
                        val courses = timeTable.filter { currentUnit in it.startUnit..it.endUnit }
                        var currentWeekDay = 1
                        while (currentWeekDay <= 5) {
                            val currentCourses = courses.filter { it.weekday == currentWeekDay }
                            if (currentCourses.isNotEmpty()) {
                                CourseCard(
                                    currentCourses.first(),
                                    modifier = Modifier
                                        .height(courseHeight)
                                        .width(eachColumnWidth)
                                        .padding(2.dp)
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .width(eachColumnWidth)
                                        .height(courseHeight)
                                )
                            }
                            currentWeekDay++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: RemoteCourse, modifier: Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp, 2.dp)
        ) {
            Text(course.name, fontSize = 14.sp, textAlign = TextAlign.Center)
            Text("${course.room}教室", fontSize = 12.sp)
        }
    }
}
