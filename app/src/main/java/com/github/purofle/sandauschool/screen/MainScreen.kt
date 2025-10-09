package com.github.purofle.sandauschool.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.purofle.sandauschool.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreenUI() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        val dateTime = LocalDateTime.now()
        val text = when (dateTime.hour) {
            in 0..4 -> R.string.good_evening
            in 5..11 -> R.string.good_morning
            in 12..17 -> R.string.good_afternoon
            in 20..23 -> R.string.good_evening
            else -> error("why are you here?")
        }

        val pattern = stringResource(R.string.date_pattern)
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        val dateText = dateTime.format(formatter)

        Text(
            stringResource(text) + ",",
            style = TextStyle(fontSize = 28.sp),
            fontWeight = FontWeight.Bold
        )
        Text("日落果", style = TextStyle(fontSize = 28.sp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("今天是 $dateText，今日无剩余课程")
    }
}

@Composable
@Preview
fun MainScreenUIPreview() {
    MainScreenUI()
}