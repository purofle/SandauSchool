package com.github.purofle.sandauschool.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.network.courseManagementService
import com.github.purofle.sandauschool.network.newEHallService

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) }
                        )
                    }
                ) { pd ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pd),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val context = LocalContext.current
                        var cardBalance by remember { mutableDoubleStateOf(0.00) }
                        var loginCount by remember { mutableIntStateOf(0) }
                        var currentTeachWeek by remember { mutableIntStateOf(0) }

                        LaunchedEffect(Unit) {
                            try {
                                newEHallService.getAppPage(7328727903036396) // get cookie
                                cardBalance = newEHallService.getYktData().data.balance
                                loginCount = courseManagementService.getLoginCount()
                                currentTeachWeek = courseManagementService.getCurrentTeachWeek().weekIndex
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        Text("校园一卡通余额：$cardBalance")
                        Text("教务系统登录次数：$loginCount")
                        Text("当前是第 $currentTeachWeek 教学周")

                        Button({
                            startActivity(Intent(context, LoginActivity::class.java))
                        }) {
                            Text(stringResource(R.string.login))
                        }
                    }
                }
            }
        }
    }
}
