package com.github.purofle.sandauschool.activity

import android.content.Context
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.edit
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.R
import kotlinx.coroutines.launch

class LoginActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            LoginPage()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginPage() {
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.login)) }
                    )
                }
            ) { pd ->

                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.padding(pd).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    OutlinedTextField(
                        username,
                        { username = it },
                        label = { Text(stringResource(R.string.input_student_id)) }
                    )
                    OutlinedTextField(
                        password,
                        { password = it },
                        label = { Text(stringResource(R.string.input_password)) }
                    )

                    Button({
                        scope.launch {
                            saveUsernameAndPassword(context, username, password)
                        }
                    }) {
                        Text(stringResource(R.string.login))
                    }
                }
            }
        }
    }

    suspend fun saveUsernameAndPassword(context: Context, username: String, password: String) {
        context.dataStore.edit {
            it[Preference.USERNAME] = username
            it[Preference.PASSWORD] = password
        }
    }

    @Preview
    @Composable
    fun LoginPagePreview() {
        LoginPage()
    }
}