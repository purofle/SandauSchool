package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        .groupBy { it.weekday } // weekday: 1-7

    val weeks = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val totalDays = 7
    val totalUnits = 12 // 假设一天最多12节课

    Column(modifier = Modifier.fillMaxSize()) {
        // 星期栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(40.dp)
            ) // 左上角空白
            weeks.forEach {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(totalDays + 1), // +1 表示左边时间列
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true,
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(4.dp)
        ) {
            // 每个时间段
            items(totalUnits * (totalDays + 1)) { index ->
                val row = index / (totalDays + 1)
                val col = index % (totalDays + 1)

                if (col == 0) {
                    // 左边时间栏
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .width(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "第${row + 1}节",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    // 课程格子
                    val coursesInThisCell = timeTable[col]?.filter {
                        row + 1 in it.startUnit..it.endUnit
                    } ?: emptyList()

                    if (coursesInThisCell.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .height(80.dp)
                                .border(0.5.dp, Color.LightGray)
                        )
                    } else {
                        val course = coursesInThisCell.first()
                        CourseCard(course)
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: RemoteCourse) {
    val bgColor = remember(course.name) {
        // 课程名决定颜色，保证每门课颜色一致
        val colors = listOf(
            Color(0xFFE3F2FD),
            Color(0xFFFCE4EC),
            Color(0xFFE8F5E9),
            Color(0xFFFFF3E0),
            Color(0xFFEDE7F6)
        )
        colors[course.name.hashCode().mod(colors.size)]
    }

    Card(
        modifier = Modifier
            .padding(2.dp)
            .clickable { /* TODO: 弹出课程详情 */ }
            .height(80.dp)
            .width(80.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(course.name, fontWeight = FontWeight.Bold)
            Text("${course.room}教室", style = MaterialTheme.typography.bodySmall)
            Text(
                course.teachers.joinToString(),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}
