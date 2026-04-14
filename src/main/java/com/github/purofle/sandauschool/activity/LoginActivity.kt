package com.github.purofle.sandauschool.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.github.purofle.sandauschool.Preference
import com.github.purofle.sandauschool.Preference.dataStore
import com.github.purofle.sandauschool.R
import com.github.purofle.sandauschool.service.LoginService
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
                    modifier = Modifier
                        .padding(pd)
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(modifier = Modifier.padding(32.dp, 0.dp)) {
                        OutlinedTextField(
                            username,
                            { username = it },
                            label = { Text(stringResource(R.string.input_student_id)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentType = ContentType.Username }
                        )

                        OutlinedTextField(
                            password,
                            { password = it },
                            label = { Text(stringResource(R.string.input_password)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentType = ContentType.Password }
                        )
                    }

                    Button({
                        scope.launch {
                            saveUsernameAndPassword(context, username, password)
                            val resp = LoginService.login(username, password)
                            if (resp.isSuccessful) {
                                finish()
                            } else {
                                println(resp.errorBody()?.string())
                            }
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